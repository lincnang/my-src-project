package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.storage.DwarfForClanStorage;
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

public class DwarfForClanTable implements DwarfForClanStorage {
    private static final Log _log = LogFactory.getLog(DwarfForClanTable.class);
    private static final Map<String, CopyOnWriteArrayList<L1ItemInstance>> _itemList = new ConcurrentHashMap<>();

    private static void errorItem(int objid) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM `clan_warehouse` WHERE `id`=?");
            pstm.setInt(1, objid);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private static void addItem(String clan_name, L1ItemInstance item) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(clan_name);
        if (list == null) {
            list = new CopyOnWriteArrayList<>();
            if (!list.contains(item)) {
                list.add(item);
            }
        } else if (!list.contains(item)) {
            list.add(item);
        }
        if (World.get().findObject(item.getId()) == null) {
            World.get().storeObject(item);
        }
        _itemList.put(clan_name, list);
    }

    private static void deleteItem(String clan_name) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.remove(clan_name);
        if (list != null) {
            for (L1ItemInstance item : list) {
                World.get().removeObject(item);
            }
        }
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `clan_warehouse` WHERE `clan_name`=?");
            ps.setString(1, clan_name);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        int i = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `clan_warehouse` order by item_id");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int objid = rs.getInt("id");
                String clan_name = rs.getString("clan_name");
                int clan_id = CharObjidTable.get().clanObjid(clan_name);
                if (clan_id != 0) {
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
                        addItem(clan_name, item);
                        i++;
                    }
                } else {
                    deleteItem(clan_name);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->血盟倉庫物件清單資料數量: " + _itemList.size() + "/" + i + "(" + timer.get() + "ms)");
    }

    public CopyOnWriteArrayList<L1ItemInstance> loadItems(String clan_name) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(clan_name);
        if (list != null) {
            return list;
        }
        return null;
    }

    public void delUserItems(String clan_name) {
        deleteItem(clan_name);
    }

    public boolean getUserItems(String clan_name, int objid, int count) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(clan_name);
        if (list != null) {
            if (list.size() <= 0) {
                return false;
            }
            for (L1ItemInstance item : list) {
                if ((item.getId() == objid) && (item.getCount() >= count)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void insertItem(String clan_name, L1ItemInstance item) {
        _log.warn("血盟:" + clan_name + " 加入血盟倉庫數據:" + item.getNumberedName(item.getCount(), false) + " OBJID:" + item.getId());
        addItem(clan_name, item);
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `clan_warehouse` SET `id`=?,`clan_name`=?,`item_id`= ?,`item_name`=?,`count`=?,`is_equipped`=0,`enchantlvl`=?,`is_id`=?,`durability`=?,`charge_count`=?,`remaining_time`=?,`last_used`=?,`bless`=?,`attr_enchant_kind`=?,`attr_enchant_level`=?,`ItemAttack`=?,`ItemBowAttack`=?,`ItemReductionDmg`=?,`ItemSp`=?,`Itemprobability`=?,`ItemStr`=?,`ItemDex`=?,`ItemInt`=?,`ItemHp`=?,`ItemMp`=?,`ItemCon`=?,`ItemCha`=?,`ItemWis`=?,`racegamno`=?,`gamNo`=?,`gamNpcId` = ?,`starNpcId`=?");
            int i = 0;
            pstm.setInt(++i, item.getId());
            pstm.setString(++i, clan_name);
            pstm.setInt(++i, item.getItemId());
            pstm.setString(++i, item.getItem().getName());
            pstm.setLong(++i, item.getCount());
            pstm.setInt(++i, item.getEnchantLevel());
            pstm.setInt(++i, item.isIdentified() ? 1 : 0);
            pstm.setInt(++i, item.get_durability());
            pstm.setInt(++i, item.getChargeCount());
            pstm.setInt(++i, item.getRemainingTime());
            pstm.setTimestamp(++i, item.getLastUsed());
            pstm.setInt(++i, item.getBless());
            pstm.setInt(++i, item.getAttrEnchantKind());
            pstm.setInt(++i, item.getAttrEnchantLevel());
            pstm.setInt(++i, item.getItemAttack());
            pstm.setInt(++i, item.getItemBowAttack());
            pstm.setInt(++i, item.getItemReductionDmg());
            pstm.setInt(++i, item.getItemSp());
            pstm.setInt(++i, item.getItemprobability());
            pstm.setInt(++i, item.getItemStr());
            pstm.setInt(++i, item.getItemDex());
            pstm.setInt(++i, item.getItemInt());
            pstm.setInt(++i, item.getItemHp());
            pstm.setInt(++i, item.getItemMp());
            pstm.setInt(++i, item.getItemCon());
            pstm.setInt(++i, item.getItemCha());
            pstm.setInt(++i, item.getItemWis());
            pstm.setString(++i, item.getraceGamNo());
            /** [原碼] 怪物對戰系統 */
            pstm.setInt(++i, item.getGamNo());
            pstm.setInt(++i, item.getGamNpcId());
            /** [原碼] 大樂透系統 */
            pstm.setString(++i, item.getStarNpcId());
            // 賭狗票
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateItem(L1ItemInstance item) {
        _log.warn(" 更新血盟倉庫數據:" + item.getNumberedName(item.getCount(), false) + " OBJID:" + item.getId());
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `clan_warehouse` SET `count`=? WHERE `id`=?");
            pstm.setLong(1, item.getCount());
            pstm.setInt(2, item.getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void deleteItem(String clan_name, L1ItemInstance item) {
        CopyOnWriteArrayList<?> list = _itemList.get(clan_name);
        if (list != null) {
            _log.warn("血盟:" + clan_name + " 血盟倉庫物品移出:" + item.getNumberedName(item.getCount(), false) + " OBJID:" + item.getId());
            list.remove(item);
            Connection con = null;
            PreparedStatement pstm = null;
            try {
                con = DatabaseFactory.get().getConnection();
                pstm = con.prepareStatement("DELETE FROM `clan_warehouse` WHERE `id`=?");
                pstm.setInt(1, item.getId());
                pstm.execute();
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.DwarfForClanTable JD-Core Version: 0.6.2
 */