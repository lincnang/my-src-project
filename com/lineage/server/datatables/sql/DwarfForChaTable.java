package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.storage.DwarfForChaStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DwarfForChaTable implements DwarfForChaStorage {
    private static final Log _log = LogFactory.getLog(DwarfForChaTable.class);
    private static final Map<String, CopyOnWriteArrayList<L1ItemInstance>> _itemList = new ConcurrentHashMap<>();

    private static void errorItem(int objid) {
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("DELETE FROM `character_warehouse_for_cha` WHERE `id`=?");
            ps.setInt(1, objid);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    /**
     * 建立資料
     *
     */
    private static void addItem(final String owner_name, final L1ItemInstance item) {
        // System.out.println("建立資料-角色專屬倉庫");
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(owner_name);
        if (list == null) {
            list = new CopyOnWriteArrayList<>();
            if (!list.contains(item)) {
                list.add(item);
            }
        } else {
            if (!list.contains(item)) {
                list.add(item);
            }
        }
        // 將物品加入世界
        if (World.get().findObject(item.getId()) == null) {
            World.get().storeObject(item);
        }
        _itemList.put(owner_name, list);
    }

    /**
     * 刪除遺失資料
     *
     */
    private static void deleteItem(final String owner_name) {
        System.out.println("刪除遺失資料-角色名稱不存在");
        final CopyOnWriteArrayList<L1ItemInstance> list = _itemList.remove(owner_name);
        if (list != null) {
            // 移出世界
            for (L1ItemInstance item : list) {
                World.get().removeObject(item);
            }
        }
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `character_warehouse_for_cha` WHERE `owner_name`=?");
            ps.setString(1, owner_name);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        int i = 0;
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM character_warehouse_for_cha order by item_id");
            rs = ps.executeQuery();
            while (rs.next()) {
                int objid = rs.getInt("id");
                String owner_name = rs.getString("owner_name");
                boolean owner = CharacterTable.doesCharNameExist(owner_name);
                if (owner) {
                    int item_id = rs.getInt("item_id");
                    long count = rs.getLong("count");
                    int enchantlvl = rs.getInt("enchantlvl");
                    int is_id = rs.getInt("is_id");
                    int durability = rs.getInt("durability");
                    int charge_count = rs.getInt("charge_count");
                    int remaining_time = rs.getInt("remaining_time");
                    Timestamp last_used = null;
                    try {
                        last_used = rs.getTimestamp("last_used");
                    } catch (Exception e) {
                        last_used = null;
                    }
                    int bless = rs.getInt("bless");
                    int attr_enchant_kind = rs.getInt("attr_enchant_kind");
                    int attr_enchant_level = rs.getInt("attr_enchant_level");
                    final int itemAttack = rs.getInt("itemAttack");
                    final int itemBowAttack = rs.getInt("itemBowAttack");
                    final int itemReductionDmg = rs.getInt("itemReductionDmg");
                    final int itemSp = rs.getInt("itemSp");
                    final int itemprobability = rs.getInt("itemprobability");
                    final int itemStr = rs.getInt("itemStr");
                    final int itemDex = rs.getInt("itemDex");
                    final int itemInt = rs.getInt("itemInt");
                    final int itemHp = rs.getInt("ItemHp");
                    final int itemMp = rs.getInt("ItemMp");
                    final int itemCon = rs.getInt("itemCon");
                    final int itemCha = rs.getInt("itemCha");
                    final int itemWis = rs.getInt("itemWis");
                    final String racegamno = rs.getString("racegamno");
                    L1ItemInstance item = new L1ItemInstance();
                    item.setId(objid);
                    L1Item itemTemplate = ItemTable.get().getTemplate(item_id);
                    if (itemTemplate == null) {
                        errorItem(objid);
                    } else {
                        item.setItem(itemTemplate);
                        item.setCount(count);
                        item.setEquipped(false);
                        item.setEnchantLevel(enchantlvl);
                        item.setIdentified(is_id != 0);
                        item.set_durability(durability);
                        item.setChargeCount(charge_count);
                        item.setRemainingTime(remaining_time);
                        item.setLastUsed(last_used);
                        item.setBless(bless);
                        item.setAttrEnchantKind(attr_enchant_kind);
                        item.setAttrEnchantLevel(attr_enchant_level);
                        item.setItemAttack(itemAttack);
                        item.setItemBowAttack(itemBowAttack);
                        item.setItemReductionDmg(itemReductionDmg);
                        item.setItemSp(itemSp);
                        item.setItemprobability(itemprobability);
                        item.setItemStr(itemStr);
                        item.setItemDex(itemDex);
                        item.setItemInt(itemInt);
                        item.setItemHp(itemHp);
                        item.setItemMp(itemMp);
                        item.setItemCon(itemCon);
                        item.setItemCha(itemCha);
                        item.setItemWis(itemWis);
                        item.setraceGamNo(racegamno);
                        /** [原碼] 怪物對戰系統 */
                        item.setGamNo(rs.getInt("gamNo"));
                        item.setGamNpcId(rs.getInt("gamNpcId"));
                        /** [原碼] 大樂透系統 */
                        item.setStarNpcId(rs.getString("starNpcId"));
                        // 登入鑰匙紀錄
                        if (item.getItem().getItemId() == 40312) {// 旅館鑰匙
                            InnKeyTable.checkey(item);
                        }
                        if (item.getItem().getItemId() == 82503) {// 訓練所鑰匙
                            InnKeyTable.checkey(item);
                        }
                        if (item.getItem().getItemId() == 82504) {// 龍門憑證
                            InnKeyTable.checkey(item);
                        }
                        addItem(owner_name, item);
                        i++;
                    }
                } else {
                    deleteItem(owner_name);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
        _log.info("讀取->角色專屬倉庫物件清單資料數量: " + _itemList.size() + "/" + i + "(" + timer.get() + "ms)");
    }

    /**
     * 傳回全部倉庫數據
     *
     */
    @Override
    public Map<String, CopyOnWriteArrayList<L1ItemInstance>> allItems() {
        return _itemList;
    }

    /**
     * 傳回倉庫數據
     *
     */
    @Override
    public CopyOnWriteArrayList<L1ItemInstance> loadItems(final String owner_name) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(owner_name);
        // System.out.println("list ==" + list);
        if (list != null) {
            return list;
        }
        return null;
    }

    /**
     * 刪除倉庫資料(完整)
     *
     */
    @Override
    public void delUserItems(final String owner_name) {
        deleteItem(owner_name);
    }

    /**
     * 該倉庫是否有指定數據
     *
     */
    @Override
    public boolean getUserItems(final String owner_name, final int objid, final int count) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(owner_name);
        if (list != null) {
            if (list.size() <= 0) {
                return false;
            }
            for (L1ItemInstance item : list) {
                if (item.getId() == objid) {
                    if (item.getCount() >= count) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void insertItem(String owner_name, L1ItemInstance item) {
        _log.warn("角色:" + owner_name + " 加入角色專屬倉庫數據:" + item.getNumberedName(item.getCount(), false) + " OBJID:" + item.getId());
        addItem(owner_name, item);
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("INSERT INTO `character_warehouse_for_cha` SET `id`=?,`owner_name`=?,`item_id`= ?,`item_name`=?,`count`=?,`is_equipped`=0,`enchantlvl`=?,`is_id`=?,`durability`=?,`charge_count`=?,`remaining_time`=?,`last_used`=?,`bless`=?,`attr_enchant_kind`=?,`attr_enchant_level`=?,`ItemAttack`=?,`ItemBowAttack`=?,`ItemReductionDmg`=?,`ItemSp`=?,`Itemprobability`=?,`ItemStr`=?,`ItemDex`=?,`ItemInt`=?,`ItemHp`=?,`ItemMp`=?,`ItemCon`=?,`ItemCha`=?,`ItemWis`=?,`racegamno`=?,`gamNo`=?,`gamNpcId` = ?,`starNpcId`=?");
            int i = 0;
            ps.setInt(++i, item.getId());
            ps.setString(++i, owner_name);
            ps.setInt(++i, item.getItemId());
            ps.setString(++i, item.getItem().getName());
            ps.setLong(++i, item.getCount());
            ps.setInt(++i, item.getEnchantLevel());
            ps.setInt(++i, item.isIdentified() ? 1 : 0);
            ps.setInt(++i, item.get_durability());
            ps.setInt(++i, item.getChargeCount());
            ps.setInt(++i, item.getRemainingTime());
            if (item.getLastUsed() != null) {
                System.out.println(item.getLastUsed().getTime());
            }
            ps.setTimestamp(++i, item.getLastUsed());
            ps.setInt(++i, item.getBless());
            ps.setInt(++i, item.getAttrEnchantKind());
            ps.setInt(++i, item.getAttrEnchantLevel());
            ps.setInt(++i, item.getItemAttack());
            ps.setInt(++i, item.getItemBowAttack());
            ps.setInt(++i, item.getItemReductionDmg());
            ps.setInt(++i, item.getItemSp());
            ps.setInt(++i, item.getItemprobability());
            ps.setInt(++i, item.getItemStr());
            ps.setInt(++i, item.getItemDex());
            ps.setInt(++i, item.getItemInt());
            ps.setInt(++i, item.getItemHp());
            ps.setInt(++i, item.getItemMp());
            ps.setInt(++i, item.getItemCon());
            ps.setInt(++i, item.getItemCha());
            ps.setInt(++i, item.getItemWis());
            ps.setString(++i, item.getraceGamNo());
            /** [原碼] 怪物對戰系統 */
            ps.setInt(++i, item.getGamNo());
            ps.setInt(++i, item.getGamNpcId());
            /** [原碼] 大樂透系統 */
            ps.setString(++i, item.getStarNpcId());
            // 賭狗票
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    /**
     * 角色專屬倉庫資料更新(物品數量)
     *
     */
    @Override
    public void updateItem(final L1ItemInstance item) {
        _log.warn("更新角色專屬倉庫數據:" + item.getNumberedName(item.getCount(), false) + " OBJID:" + item.getId());
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseFactory.get().getConnection();
            ps = con.prepareStatement("UPDATE `character_warehouse_for_cha` SET `count`=? WHERE `id`=?");
            ps.setLong(1, item.getCount());
            ps.setInt(2, item.getId());
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(con);
        }
    }

    /**
     * 角色專屬倉庫物品資料刪除
     *
     */
    @Override
    public void deleteItem(final String owner_name, final L1ItemInstance item) {
        // System.out.println("倉庫物品資料刪除 : SQL");
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(owner_name);
        if (list != null) {
            _log.warn("角色:" + owner_name + " 角色專屬倉庫物品移出 :" + item.getNumberedName(item.getCount(), false) + " OBJID:" + item.getId());
            list.remove(item);
            Connection co = null;
            PreparedStatement pstm = null;
            try {
                co = DatabaseFactory.get().getConnection();
                pstm = co.prepareStatement("DELETE FROM `character_warehouse_for_cha` WHERE `id`=?");
                pstm.setInt(1, item.getId());
                pstm.execute();
            } catch (final SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(pstm);
                SQLUtil.close(co);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.DwarfTable JD-Core Version: 0.6.2
 */