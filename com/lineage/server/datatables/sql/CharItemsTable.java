package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.storage.CharItemsStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CharItemsTable implements CharItemsStorage {
    private static final Log _log = LogFactory.getLog(CharItemsTable.class);
    private static final Map<Integer, CopyOnWriteArrayList<L1ItemInstance>> _itemList = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<L1ItemInstance>>();

    /**
     * 刪除錯誤的物品資料
     *
     */
    private static void errorItem(int objid) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM `character_items` WHERE `id`=?");
            pstm.setInt(1, objid);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 建立資料
     *
     */
    private static void addItem(final Integer objid, final L1ItemInstance item) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(objid);
        if (list == null) {// 該人物尚無背包數據
            list = new CopyOnWriteArrayList<L1ItemInstance>();
            if (!list.contains(item)) {// 清單中不包含該物品
                list.add(item);
            }
        } else {
            if (!list.contains(item)) {// 清單中不包含該物品
                list.add(item);
            }
        }
        // 世界物件中不包含該OBJID數據
        if (World.get().findObject(item.getId()) == null) {
            World.get().storeObject(item);
        }
        _itemList.put(objid, list);
    }

    /**
     * 刪除遺失角色的物品資料
     *
     */
    private static void deleteItem(final Integer objid) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.remove(objid);
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
            ps = cn.prepareStatement("DELETE FROM `character_items` WHERE `char_id`=?");
            ps.setInt(1, objid);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private static void executeUpdate(int objId, String sql, Timestamp ts) throws SQLException {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement(sql.toString());
            pstm.setTimestamp(1, ts);
            pstm.setInt(2, objId);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        int i = 0;
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_items`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int objid = rs.getInt("id");
                int item_id = rs.getInt("item_id");
                int char_id = rs.getInt("char_id");
                if (CharObjidTable.get().isChar(char_id) != null) {
                    L1Item itemTemplate = ItemTable.get().getTemplate(item_id);
                    if (itemTemplate == null) {
                        errorItem(objid);
                    } else {
                        long count = rs.getLong("count");
                        int is_equipped = rs.getInt("is_equipped");
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
                        final int itemAttack = rs.getInt("ItemAttack");
                        final int itemBowAttack = rs.getInt("ItemBowAttack");
                        final int itemReductionDmg = rs.getInt("ItemReductionDmg");
                        final int itemSp = rs.getInt("ItemSp");
                        final int itemprobability = rs.getInt("Itemprobability");
                        final int itemStr = rs.getInt("ItemStr");
                        final int itemDex = rs.getInt("ItemDex");
                        final int itemInt = rs.getInt("ItemInt");
                        final int itemHp = rs.getInt("ItemHp");
                        final int itemMp = rs.getInt("ItemMp");
                        final int itemCon = rs.getInt("ItemCon");
                        final int itemCha = rs.getInt("ItemCha");
                        final int itemWis = rs.getInt("ItemWis");
                        final String racegamno = rs.getString("racegamno");
                        L1ItemInstance item = new L1ItemInstance();
                        item.setId(objid);
                        item.setItem(itemTemplate);
                        item.setCount(count);
                        item.setEquipped(is_equipped != 0);
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
                        item.set_char_objid(char_id);
                        item.getLastStatus().updateAll();
                        if (item.getItem().getItemId() == 40312) {// 旅館鑰匙
                            InnKeyTable.checkey(item);
                        }
                        if (item.getItem().getItemId() == 82503) {// 訓練所鑰匙
                            InnKeyTable.checkey(item);
                        }
                        // if (item.getItem().getItemId() == 82504) {// 龍門憑證
                        // InnKeyTable.checkey(item);
                        // }
                        addItem(Integer.valueOf(char_id), item);
                        i++;
                    }
                } else {
                    deleteItem(Integer.valueOf(char_id));
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->人物背包物件清單資料數量: " + _itemList.size() + "/" + i + "(" + timer.get() + "ms)");
    }

    /**
     * 傳回該人物背包資料
     *
     */
    @Override
    public CopyOnWriteArrayList<L1ItemInstance> loadItems(final Integer objid) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(objid);
        if (list != null) {
            return list;
        }
        return null;
    }

    /**
     * 刪除人物背包資料(完整)
     *
     */
    @Override
    public void delUserItems(final Integer objid) {
        deleteItem(objid);
    }

    /**
     * 該人物背包是否有指定數據
     *
     */
    @Override
    public boolean getUserItems(final Integer pcObjid, final int objid, final long count) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(pcObjid);
        if (list != null) {
            for (L1ItemInstance item : list) {
                if (item.getId() == objid && item.getCount() >= count) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否有指定數據
     *
     */
    @Override
    public boolean getUserItem(final int objid) {
        for (CopyOnWriteArrayList<L1ItemInstance> list : _itemList.values()) {
            for (L1ItemInstance item : list) {
                if (item.getId() == objid) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 傳回佣有該物品ID的人物清單<BR>
     * (適用該物品每人只能傭有一個的狀態)
     *
     */
    @Override
    public Map<Integer, L1ItemInstance> getUserItems(final int itemid) {
        // 人物OBJID / 物品
        final Map<Integer, L1ItemInstance> outList = new ConcurrentHashMap<Integer, L1ItemInstance>();
        try {
            for (Integer key : _itemList.keySet()) {
                CopyOnWriteArrayList<L1ItemInstance> value = _itemList.get(key);
                for (L1ItemInstance item : value) {
                    if (item.getItemId() == itemid) {
                        outList.put(key, item);
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return outList;
    }

    /**
     * 刪除指定編號全部數據
     *
     */
    @Override
    public void del_item(final int itemid) {
        try {
            for (Integer key : _itemList.keySet()) {
                // 人物背包
                final CopyOnWriteArrayList<L1ItemInstance> value = _itemList.get(key);
                for (L1ItemInstance item : value) {
                    if (item.getItemId() == itemid) {
                        deleteItem(key, item);
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 增加背包物品
     *
     */
    @Override
    public void storeItem(int objId, L1ItemInstance item) throws Exception {
        addItem(Integer.valueOf(objId), item);
        item.getLastStatus().updateAll();
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `character_items` SET `id`=?,`item_id`=?,`char_id`=?,`item_name`=?,`count`=?,`is_equipped`=?,`enchantlvl`=?,`is_id`=?,`durability`=?,`charge_count`=?,`remaining_time`=?,`last_used`=?,`bless`=?,`attr_enchant_kind`=?,`attr_enchant_level`=?,`ItemAttack`=?,`ItemBowAttack`=?,`ItemReductionDmg`=?,`ItemSp`=?,`Itemprobability`=?,`ItemStr`=?,`ItemDex`=?,`ItemInt`=?,`ItemHp`=?,`ItemMp`=?,`ItemCon`=?,`ItemCha`=?,`ItemWis`=?,`racegamno`=?,`gamNo`=?,`gamNpcId` = ?,`starNpcId`=?");
            int i = 0;
            pstm.setInt(++i, item.getId());
            pstm.setInt(++i, item.getItem().getItemId());
            pstm.setInt(++i, objId);
            pstm.setString(++i, item.getItem().getName());
            pstm.setLong(++i, item.getCount());
            pstm.setInt(++i, item.isEquipped() ? 1 : 0);
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
            /** 賭狗票 **/
            pstm.setString(++i, item.getraceGamNo()); // 修復位置錯誤
            /** [原碼] 怪物對戰系統 */
            pstm.setInt(++i, item.getGamNo());
            pstm.setInt(++i, item.getGamNpcId());
            /** [原碼] 大樂透系統 */
            pstm.setString(++i, item.getStarNpcId());
            pstm.execute();
        } catch (SQLException e) {
            _log.error("背包物品增加時發生異常 人物OBJID:" + objId, e);
            L1Object object = World.get().findObject(item.get_char_objid());
            if ((object != null) && ((object instanceof L1PcInstance))) {
                L1PcInstance tgpc = (L1PcInstance) object;
                tgpc.getInventory().removeItem(item);
            }
            _log.error(" 已刪除異常物品 物品名稱:" + item.getName());
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 刪除背包物品
     *
     * @param objid 人物OBJID
     * @param item  物品
     */
    @Override
    public void deleteItem(final int objid, final L1ItemInstance item) throws Exception {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(objid);
        if (list != null) {
            list.remove(item);
            Connection cn = null;
            PreparedStatement ps = null;
            try {
                cn = DatabaseFactory.get().getConnection();
                ps = cn.prepareStatement("DELETE FROM `character_items` WHERE `id`=?");
                ps.setInt(1, item.getId());
                ps.execute();
            } catch (final SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(ps);
                SQLUtil.close(cn);
            }
        }
    }

    public void updateItemId_Name(L1ItemInstance item) throws Exception {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `character_items` SET `item_id`=?,`item_name`=?,`bless`=? WHERE `id`=?");
            pstm.setInt(1, item.getItemId());
            pstm.setString(2, item.getItem().getName());
            pstm.setInt(3, item.getItem().getBless());
            pstm.setInt(4, item.getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public void updateItemAttack(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemAttack`=? WHERE `id`=?", item.getItemAttack());
        item.getLastStatus().updateItemAttack();
    }

    @Override
    public void updateItemBowAttack(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemBowAttack`=? WHERE `id`=?", item.getItemBowAttack());
        item.getLastStatus().updateItemBowAttack();
    }

    @Override
    public void updateItemReductionDmg(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemReductionDmg`=? WHERE `id`=?", item.getItemReductionDmg());
        item.getLastStatus().updateItemReductionDmg();
    }

    @Override
    public void updateItemSp(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemSp`=? WHERE `id`=?", item.getItemSp());
        item.getLastStatus().updateItemSp();
    }

    @Override
    public void updateItemprobability(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `Itemprobability`=? WHERE `id`=?", item.getItemprobability());
        item.getLastStatus().updateItemprobability();
    }

    @Override
    public void updateItemStr(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemStr`=? WHERE `id`=?", item.getItemStr());
        item.getLastStatus().updateItemStr();
    }

    @Override
    public void updateItemDex(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemDex`=? WHERE `id`=?", item.getItemDex());
        item.getLastStatus().updateItemDex();
    }

    @Override
    public void updateItemInt(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemInt`=? WHERE `id`=?", item.getItemInt());
        item.getLastStatus().updateItemInt();
    }

    public void updateItemId(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `item_id`=? WHERE `id`=?", item.getItemId());
        item.getLastStatus().updateItemId();
    }

    public void updateItemCount(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `count`=? WHERE `id`=?", item.getCount());
        item.getLastStatus().updateCount();
    }

    /**
     * [原碼] 怪物對戰系統
     */
    public void updateGamNo(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET gamNo = ? WHERE id = ?", item.getGamNo());
        item.getLastStatus().updateGamNo();
    }

    public void updateGamNpcId(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET gamNpcId = ? WHERE id = ?", item.getGamNpcId());
        item.getLastStatus().updateGamNpcId();
    }

    /**
     * [原碼] 大樂透系統
     */
    public void updateStarNpcId(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE character_items SET StarNpcId = ? WHERE id = ?", item.getStarNpcId());
        item.getLastStatus().updateStarNpcId();
    }

    /**
     * [原碼] 大樂透系統
     */
    private void executeUpdate(int objId, String sql, String updateNum) throws SQLException {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement(sql.toString());
            pstm.setString(1, updateNum);
            pstm.setInt(2, objId);
            pstm.execute();
        } catch (SQLException e) {
            throw e;
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateItemDurability(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `durability`=? WHERE `id`=?", item.get_durability());
        item.getLastStatus().updateDuraility();
    }

    public void updateItemChargeCount(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `charge_count`=? WHERE `id`=?", item.getChargeCount());
        item.getLastStatus().updateChargeCount();
    }

    public void updateItemRemainingTime(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `remaining_time`=? WHERE `id`=?", item.getRemainingTime());
        item.getLastStatus().updateRemainingTime();
    }

    public void updateItemEnchantLevel(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `enchantlvl`=? WHERE `id`=?", item.getEnchantLevel());
        item.getLastStatus().updateEnchantLevel();
    }

    public void updateItemEquipped(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `is_equipped`=? WHERE `id`=?", item.isEquipped() ? 1 : 0);
        item.getLastStatus().updateEquipped();
    }

    public void updateItemIdentified(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `is_id`=? WHERE `id`=?", item.isIdentified() ? 1 : 0);
        item.getLastStatus().updateIdentified();
    }

    public void updateItemBless(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `bless`=? WHERE `id`=?", item.getBless());
        item.getLastStatus().updateBless();
    }

    public void updateItemAttrEnchantKind(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `attr_enchant_kind`=? WHERE `id`=?", item.getAttrEnchantKind());
        item.getLastStatus().updateAttrEnchantKind();
    }

    public void updateItemAttrEnchantLevel(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `attr_enchant_level`=? WHERE `id`=?", item.getAttrEnchantLevel());
        item.getLastStatus().updateAttrEnchantLevel();
    }

    public void updateItemDelayEffect(L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `last_used`=? WHERE `id`=?", item.getLastUsed());
        item.getLastStatus().updateLastUsed();
    }

    @Override
    public void updateItemHp(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemHp`=? WHERE `id`=?", item.getItemHp());
        item.getLastStatus().updateItemHp();
    }

    @Override
    public void updateItemMp(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemMp`=? WHERE `id`=?", item.getItemMp());
        item.getLastStatus().updateItemMp();
    }

    @Override
    public void updateItemCon(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemCon`=? WHERE `id`=?", item.getItemCon());
        item.getLastStatus().updateItemCon();
    }

    @Override
    public void updateItemWis(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemWis`=? WHERE `id`=?", item.getItemWis());
        item.getLastStatus().updateItemWis();
    }

    @Override
    public void updateItemCha(final L1ItemInstance item) throws Exception {
        executeUpdate(item.getId(), "UPDATE `character_items` SET `ItemCha`=? WHERE `id`=?", item.getItemCha());
        item.getLastStatus().updateItemCha();
    }

    public int getItemCount(int objId) throws Exception {
        int count = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `character_items` WHERE `char_id`=?");
            pstm.setInt(1, objId);
            rs = pstm.executeQuery();
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return count;
    }

    public void getAdenaCount(int objid, long count) throws Exception {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(objid);
        if (list != null) {
            boolean isAdena = false;// 背包有金幣
            for (L1ItemInstance item : list) {
                if (item.getItemId() == L1ItemId.ADENA) {
                    // 更新數量
                    item.setCount(item.getCount() + count);
                    updateItemCount(item);
                    isAdena = true;
                }
            }
            // 背包無金幣
            if (!isAdena) {
                final L1ItemInstance item = ItemTable.get().createItem(L1ItemId.ADENA);
                item.setCount(count);
                this.storeItem(objid, item);
            }
        }
    }

    private void executeUpdate(int objId, String sql, long updateNum) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement(sql.toString());
            pstm.setLong(1, updateNum);
            pstm.setInt(2, objId);
            pstm.   execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int checkItemId(int itemId) {
        int counter = 0;
        for (Iterator<CopyOnWriteArrayList<L1ItemInstance>> iterator = _itemList.values().iterator(); iterator.hasNext(); ) {
            CopyOnWriteArrayList<?> list = (CopyOnWriteArrayList<?>) iterator.next();
            for (Iterator<?> iterator1 = list.iterator(); iterator1.hasNext(); ) {
                L1ItemInstance item = (L1ItemInstance) iterator1.next();
                if (item.getItemId() == itemId) {
                    counter++;
                }
            }
        }
        return counter;
    }

    @Override
    public void updateItemCharTrade(L1ItemInstance item) throws Exception {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE character_items SET tradeCharId = ?,tradeCharName = ?,tradeCharLevel = ?,tradeCharType = ? WHERE id = ?");
            pstm.setInt(1, item.getItemCharaterTrade().get_char_objId());
            pstm.setString(2, item.getItemCharaterTrade().getName());
            pstm.setInt(3, item.getItemCharaterTrade().getLevel());
            pstm.setInt(4, item.getItemCharaterTrade().get_Type());
            pstm.setInt(5, item.getId());
            pstm.execute();
        } catch (SQLException e) {
            throw e;
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.CharItemsTable JD-Core Version: 0.6.2
 */