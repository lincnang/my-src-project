package com.lineage.server.datatables.sql;

import com.lineage.server.datatables.storage.DwarfForVIPStorage;
import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 角色物品贖回物件清單（最小實作，使用記憶體儲存）
 */
public class DwarfForVIPTable implements DwarfForVIPStorage {

    private static final Map<String, CopyOnWriteArrayList<L1ItemInstance>> ITEM_LIST = new ConcurrentHashMap<String, CopyOnWriteArrayList<L1ItemInstance>>();

    @Override
    public void load() {
        // no-op for in-memory implementation
    }

    @Override
    public Map<String, CopyOnWriteArrayList<L1ItemInstance>> allItems() {
        return ITEM_LIST;
    }

    @Override
    public CopyOnWriteArrayList<L1ItemInstance> loadItems(final String char_name) {
        return ITEM_LIST.get(char_name);
    }

    @Override
    public void delUserItems(final String char_name) {
        ITEM_LIST.remove(char_name);
    }

    @Override
    public boolean getUserItems(final String char_name, final int objid, final int count) {
        final CopyOnWriteArrayList<L1ItemInstance> list = ITEM_LIST.get(char_name);
        if (list == null || list.isEmpty()) {
            return false;
        }
        for (L1ItemInstance item : list) {
            if (item.getId() == objid && item.getCount() >= count) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void insertItem(final String char_name, final L1ItemInstance item) {
        CopyOnWriteArrayList<L1ItemInstance> list = ITEM_LIST.get(char_name);
        if (list == null) {
            list = new CopyOnWriteArrayList<L1ItemInstance>();
            ITEM_LIST.put(char_name, list);
        }
        if (!list.contains(item)) {
            list.add(item);
        }
    }

    @Override
    public void updateItem(final L1ItemInstance item) {
        // no-op for in-memory implementation (item is mutated in place)
    }

    @Override
    public void deleteItem(final String char_name, final L1ItemInstance item) {
        final CopyOnWriteArrayList<L1ItemInstance> list = ITEM_LIST.get(char_name);
        if (list != null) {
            list.remove(item);
        }
    }
}

//package com.lineage.server.datatables.sql;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import com.lineage.DatabaseFactory;
//import com.lineage.server.datatables.InnKeyTable;
//import com.lineage.server.datatables.ItemTable;
//import com.lineage.server.datatables.lock.CharacterQuestReading;
//import com.lineage.server.datatables.storage.DwarfForVIPStorage;
//import com.lineage.server.model.Instance.L1ItemInstance;
//import com.lineage.server.templates.L1Item;
//import com.lineage.server.utils.PerformanceTimer;
//import com.lineage.server.utils.SQLUtil;
//import com.lineage.server.world.World;
//
///**
// * 角色物品贖回物件清單
// *
// * @author juonena
// *
// */
//public class DwarfForVIPTable implements DwarfForVIPStorage {
//
//	private static final Log _log = LogFactory.getLog(DwarfForVIPTable.class);
//
//	// 角色物品贖回物件清單 (角色名稱) (物品清單)
//	private static final Map<String, CopyOnWriteArrayList<L1ItemInstance>> _itemList = new ConcurrentHashMap<String, CopyOnWriteArrayList<L1ItemInstance>>();
//
//	/**
//	 * 預先加載
//	 */
//	@Override
//	public void load() {
//		final PerformanceTimer timer = new PerformanceTimer();
//		int i = 0;
//		Connection co = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try {
//			co = DatabaseFactory.get().getConnection();
//			ps = co.prepareStatement("SELECT * FROM `kzy_GiveBack`");
//			rs = ps.executeQuery();
//
//			while (rs.next()) {
//				final int objid = rs.getInt("id");
//				final String char_name = rs.getString("char_name");
//
//				// if (CharObjidTable.get().charObjid(char_name) != 0) {
//				final int item_id = rs.getInt("item_id");
//				final long count = rs.getLong("count");
//				final int enchantlvl = rs.getInt("enchantlvl");
//				final int is_id = rs.getInt("is_id");
//				final int durability = rs.getInt("durability");
//				final int charge_count = rs.getInt("charge_count");
//				final int remaining_time = rs.getInt("remaining_time");
//				final Timestamp last_used = rs.getTimestamp("last_used");
//				int bless = rs.getInt("bless");
//				int attr_enchant_kind = rs.getInt("attr_enchant_kind");
//				int attr_enchant_level = rs.getInt("attr_enchant_level");
//		 	    final int itemAttack = rs.getInt("itemAttack");
//	            final int itemBowAttack = rs.getInt("itemBowAttack");
//	            final int itemReductionDmg = rs.getInt("itemReductionDmg");
//	            final int itemSp = rs.getInt("itemSp");
//	            final int itemprobability = rs.getInt("itemprobability");
//	            final int itemStr = rs.getInt("itemStr");
//	            final int itemDex = rs.getInt("itemDex");
//	            final int itemInt = rs.getInt("itemInt");
//	            final int itemHp = rs.getInt("ItemHp");
//	            final int itemMp = rs.getInt("ItemMp");
//	            final int itemCon = rs.getInt("itemCon");
//	            final int itemCha = rs.getInt("itemCha");
//	            final int itemWis = rs.getInt("itemWis");
//	            final String racegamno = rs.getString("racegamno");
//				final L1ItemInstance item = new L1ItemInstance();
//				item.setId(objid);
//
//				final L1Item itemTemplate = ItemTable.get()
//						.getTemplate(item_id);
//				if (itemTemplate == null) {
//					// 無該物品資料 移除
//					errorItem(objid);
//					continue;
//				}
//				item.setItem(itemTemplate);
//				item.setCount(count);
//				item.setEquipped(false);
//				item.setEnchantLevel(enchantlvl);
//				item.setIdentified(is_id != 0 ? true : false);
//				item.set_durability(durability);
//				item.setChargeCount(charge_count);
//				item.setRemainingTime(remaining_time);
//				item.setLastUsed(last_used);
//				item.setBless(bless);
//				item.setAttrEnchantKind(attr_enchant_kind);
//				item.setAttrEnchantLevel(attr_enchant_level);
//		  	    item.setItemAttack(itemAttack);
//	            item.setItemBowAttack(itemBowAttack);
//	            item.setItemReductionDmg(itemReductionDmg);
//	            item.setItemSp(itemSp);
//	            item.setItemprobability(itemprobability);
//	            item.setItemStr(itemStr);
//	            item.setItemDex(itemDex);
//	            item.setItemInt(itemInt);
//	            item.setItemHp(itemHp);
//	            item.setItemMp(itemMp);
//	            item.setItemCon(itemCon);
//	            item.setItemCha(itemCha);
//	            item.setItemWis(itemWis);
//	            item.setraceGamNo(racegamno);
//				/** [原碼] 怪物對戰系統 */
//				item.setGamNo(rs.getInt("gamNo"));
//				item.setGamNpcId(rs.getInt("gamNpcId"));
//				/** [原碼] 大樂透系統 */
//				item.setStarNpcId(rs.getString("starNpcId"));
//				// 登入鑰匙紀錄
//				if (item.getItem().getItemId() == 40312) {
//					InnKeyTable.checkey(item);
//				}
//				if (item.getItem().getItemId() == 82503) {// 訓練所鑰匙
//					InnKeyTable.checkey(item);
//				}
//				if (item.getItem().getItemId() == 82504) {// 龍門憑證
//					InnKeyTable.checkey(item);
//				}
//				addItem(char_name, item);
//				i++;
//
//				// } else {
//				// deleteItem(char_name);
//				// }
//			}
//
//		} catch (final SQLException e) {
//			_log.error(e.getLocalizedMessage(), e);
//
//		} finally {
//			SQLUtil.close(rs);
//			SQLUtil.close(ps);
//			SQLUtil.close(co);
//		}
//		_log.info("讀取->角色物品贖回物件清單資料數量: " + _itemList.size() + "/" + i + "("
//				+ timer.get() + "ms)");
//	}
//
//	/**
//	 * 刪除錯誤物品資料
//	 *
//	 * @param objid
//	 */
//	private static void errorItem(int objid) {
//		Connection co = null;
//		PreparedStatement ps = null;
//		try {
//			co = DatabaseFactory.get().getConnection();
//			ps = co.prepareStatement("DELETE FROM `kzy_GiveBack` WHERE `id`=?");
//			ps.setInt(1, objid);
//			ps.execute();
//
//		} catch (final SQLException e) {
//			_log.error(e.getLocalizedMessage(), e);
//
//		} finally {
//			SQLUtil.close(ps);
//			SQLUtil.close(co);
//		}
//	}
//
//	/**
//	 * 建立資料
//	 *
//	 * @param char_name
//	 * @param item
//	 */
//	private static void addItem(final String char_name,
//			final L1ItemInstance item) {
//		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(char_name);
//		if (list == null) {
//			list = new CopyOnWriteArrayList<L1ItemInstance>();
//			if (!list.contains(item)) {
//				list.add(item);
//			}
//
//		} else {
//			if (!list.contains(item)) {
//				list.add(item);
//			}
//		}
//		// 將物品加入世界
//		if (World.get().findObject(item.getId()) == null) {
//			World.get().storeObject(item);
//		}
//		_itemList.put(char_name, list);
//	}
//
//	/**
//	 * 刪除遺失資料
//	 *
//	 * @param objid
//	 */
//	private static void deleteItem(final String char_name) {
//		final CopyOnWriteArrayList<L1ItemInstance> list = _itemList
//				.remove(char_name);
//		if (list != null) {
//			// 移出世界
//			for (L1ItemInstance item : list) {
//				World.get().removeObject(item);
//			}
//		}
//
//		Connection cn = null;
//		PreparedStatement ps = null;
//		try {
//			cn = DatabaseFactory.get().getConnection();
//			ps = cn.prepareStatement("DELETE FROM `kzy_GiveBack` WHERE `char_name`=?");
//			ps.setString(1, char_name);
//			ps.execute();
//
//		} catch (final SQLException e) {
//			_log.error(e.getLocalizedMessage(), e);
//
//		} finally {
//			SQLUtil.close(ps);
//			SQLUtil.close(cn);
//		}
//	}
//
//	/**
//	 * 傳回全部角色物品贖回數據
//	 *
//	 * @return
//	 */
//	@Override
//	public Map<String, CopyOnWriteArrayList<L1ItemInstance>> allItems() {
//		return _itemList;
//	}
//
//	/**
//	 * 傳回角色物品贖回數據
//	 *
//	 * @return
//	 */
//	@Override
//	public CopyOnWriteArrayList<L1ItemInstance> loadItems(final String char_name) {
//		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(char_name);
//		if (list != null) {
//			return list;
//		}
//		return null;
//	}
//
//	/**
//	 * 刪除角色物品贖回資料(完整)
//	 *
//	 * @param char_name
//	 */
//	@Override
//	public void delUserItems(final String char_name) {
//		deleteItem(char_name);
//	}
//
//	/**
//	 * 角色物品贖回是否有指定數據
//	 *
//	 * @param char_name
//	 * @param objid
//	 * @param count
//	 * @return
//	 */
//	@Override
//	public boolean getUserItems(final String char_name, final int objid,
//			final int count) {
//		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(char_name);
//		if (list != null) {
//			if (list.size() <= 0) {
//				return false;
//			}
//			for (L1ItemInstance item : list) {
//				if (item.getId() == objid) {
//					if (item.getCount() >= count) {
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * 加入角色物品贖回數據
//	 */
//	@Override
//	public void insertItem(final String char_name, final L1ItemInstance item) {
//		_log.warn("玩家:" + char_name + " 加入角色物品贖回數據:" + item.getItem().getName()
//				+ " OBJID:" + item.getId());
//		addItem(char_name, item);
//
//		Connection co = null;
//		PreparedStatement ps = null;
//		try {
//			co = DatabaseFactory.get().getConnection();
//			ps = co.prepareStatement("INSERT INTO `kzy_GiveBack` SET `id`=?,"
//					+ "`char_name`=?,`item_id`= ?,`item_name`=?,`count`=?,"
//					+ "`is_equipped`=0,`enchantlvl`=?,`is_id`=?,`durability`=?,"
//					+ "`charge_count`=?,`remaining_time`=?,`last_used`=?,`bless`=?,`attr_enchant_kind`=?,`attr_enchant_level`=?,`ItemAttack`=?,`ItemBowAttack`=?,`ItemReductionDmg`=?,`ItemSp`=?,`Itemprobability`=?,`ItemStr`=?,`ItemDex`=?,`ItemInt`=?,`ItemHp`=?,`ItemMp`=?,`ItemCon`=?,`ItemCha`=?,`ItemWis`=?,`racegamno`=?,`gamNo`=?,`gamNpcId` = ?,`starNpcId`=?"
//					);
//			int i = 0;
//			ps.setInt(++i, item.getId());
//			ps.setString(++i, char_name);
//			ps.setInt(++i, item.getItemId());
//			ps.setString(++i, item.getItem().getName());
//			ps.setLong(++i, item.getCount());
//			ps.setInt(++i, item.getEnchantLevel());
//			ps.setInt(++i, item.isIdentified() ? 1 : 0);
//			ps.setInt(++i, item.get_durability());
//			ps.setInt(++i, item.getChargeCount());
//			ps.setInt(++i, item.getRemainingTime());
//			ps.setTimestamp(++i, item.getLastUsed());
//			ps.setInt(++i, item.getBless());
//			ps.setInt(++i, item.getAttrEnchantKind());
//			ps.setInt(++i, item.getAttrEnchantLevel());
//		      ps.setInt(++i, item.getItemAttack());
//		      ps.setInt(++i, item.getItemBowAttack());
//		      ps.setInt(++i, item.getItemReductionDmg());
//		      ps.setInt(++i, item.getItemSp());
//		      ps.setInt(++i, item.getItemprobability());
//		      ps.setInt(++i, item.getItemStr());
//		      ps.setInt(++i, item.getItemDex());
//		      ps.setInt(++i, item.getItemInt());
//		      ps.setInt(++i, item.getItemHp());
//			  ps.setInt(++i, item.getItemMp());
//		      ps.setInt(++i, item.getItemCon());
//		      ps.setInt(++i, item.getItemCha());
//		      ps.setInt(++i, item.getItemWis());
//		      ps.setString(++i, item.getraceGamNo());
//			/** [原碼] 怪物對戰系統 */
//			ps.setInt(++i, item.getGamNo());
//			ps.setInt(++i, item.getGamNpcId());
//			/** [原碼] 大樂透系統 */
//			ps.setString(++i, item.getStarNpcId());
//			ps.execute();
//
//		} catch (final SQLException e) {
//			_log.error(e.getLocalizedMessage(), e);
//
//		} finally {
//			SQLUtil.close(ps);
//			SQLUtil.close(co);
//		}
//	}
//
//	/**
//	 * 角色物品贖回資料更新(物品數量)
//	 *
//	 * @param item
//	 */
//	@Override
//	public void updateItem(final L1ItemInstance item) {
//		_log.warn("更新角色物品贖回數據:" + item.getItem().getName() + " OBJID:"
//				+ item.getId() + " Count:" + item.getCount());
//		Connection con = null;
//		PreparedStatement ps = null;
//		try {
//			con = DatabaseFactory.get().getConnection();
//			ps = con.prepareStatement("UPDATE `kzy_GiveBack` SET `count`=? WHERE `id`=?");
//			ps.setLong(1, item.getCount());
//			ps.setInt(2, item.getId());
//			ps.execute();
//
//		} catch (final SQLException e) {
//			_log.error(e.getLocalizedMessage(), e);
//
//		} finally {
//			SQLUtil.close(ps);
//			SQLUtil.close(con);
//		}
//	}
//
//	/**
//	 * 角色物品贖回物品資料刪除
//	 *
//	 * @param char_name
//	 * @param item
//	 */
//	@Override
//	public void deleteItem(final String char_name, final L1ItemInstance item) {
//		// System.out.println("角色物品贖回物品資料刪除 : SQL");
//		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(char_name);
//		if (list != null) {
//			_log.warn("玩家:" + char_name + " 角色物品贖回 :"
//					+ item.getItem().getName() + " OBJID:" + item.getId());
//			list.remove(item);
//
//			Connection co = null;
//			PreparedStatement pstm = null;
//			try {
//				co = DatabaseFactory.get().getConnection();
//				pstm = co
//						.prepareStatement("DELETE FROM `kzy_GiveBack` WHERE `id`=?");
//				pstm.setInt(1, item.getId());
//				pstm.execute();
//
//			} catch (final SQLException e) {
//				_log.error(e.getLocalizedMessage(), e);
//
//			} finally {
//				SQLUtil.close(pstm);
//				SQLUtil.close(co);
//			}
//		}
//	}
//}
