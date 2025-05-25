package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.storage.DwarfShopStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopS;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DwarfShopTable implements DwarfShopStorage {
    private static final Log _log = LogFactory.getLog(DwarfShopTable.class);
    private static final Map<Integer, L1ItemInstance> _itemList = new ConcurrentHashMap<>();
    private static final Map<Integer, L1ShopS> _shopSList = new ConcurrentHashMap<>();
    private static int _id = 0;

    private static void addItem(int key, L1ItemInstance value) {
        _itemList.putIfAbsent(key, value);
        if (World.get().findObject(key) == null) {
            World.get().storeObject(value);
        }
    }

    private static void loadShopS() {
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `character_shop`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int item_obj_id = rs.getInt("item_obj_id");
                int user_obj_id = rs.getInt("user_obj_id");
                int adena = rs.getInt("adena");
                Timestamp overtime = rs.getTimestamp("overtime");
                int end = rs.getInt("end");
                String none = rs.getString("none");
                if (_id < id) {
                    _id = id;
                }
                L1ShopS shopS = new L1ShopS();
                shopS.set_id(id);
                shopS.set_item_obj_id(item_obj_id);
                shopS.set_user_obj_id(user_obj_id);
                shopS.set_adena(adena);
                shopS.set_overtime(overtime);
                shopS.set_end(end);
                shopS.set_none(none);
                switch (end) {
                    case 0:
                    case 1:
                    case 3:
                        L1ItemInstance item = (L1ItemInstance) _itemList.get(item_obj_id);
                        shopS.set_item(item);
                        break;
                    case 2:
                    case 4:
                        shopS.set_item(null);
                }
                userMap(id, shopS);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    private static void userMap(int key, L1ShopS value) {
        _shopSList.putIfAbsent(key, value);
    }

    private static void errorItem(int objid) {
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("DELETE FROM `character_shopinfo` WHERE `id`=?");
            ps.setInt(1, objid);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `character_shopinfo`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int objid = rs.getInt("id");
                int item_id = rs.getInt("item_id");
                L1Item itemTemplate = ItemTable.get().getTemplate(item_id);
                if (itemTemplate == null) {
                    errorItem(objid);
                } else {
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
                    if (item.getItem().getItemId() == 40312) {
                        InnKeyTable.checkey(item);
                    }
                    if (item.getItem().getItemId() == 82503) {// 訓練所鑰匙
                        InnKeyTable.checkey(item);
                    }
                    if (item.getItem().getItemId() == 82504) {// 龍門憑證
                        InnKeyTable.checkey(item);
                    }
                    addItem(objid, item);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
            loadShopS();
        }
        _log.info("讀取->託售道具資料數量: " + _itemList.size() + "/" + _shopSList.size() + "(" + timer.get() + "ms)");
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int id) {
        _id = id;
    }

    public HashMap<Integer, L1ShopS> allShopS() {
        HashMap<Integer, L1ShopS> shopSList = new HashMap<>();
        for (L1ShopS value : _shopSList.values()) {
            if (value.get_end() == 0) {
                shopSList.put(value.get_id(), value);
            }
        }
        return shopSList;
    }

    public Map<Integer, L1ItemInstance> allItems() {
        return _itemList;
    }

    public L1ShopS getShopS(int objid) {
        L1ShopS out = null;
        int i = 0;
        for (L1ShopS value : _shopSList.values()) {
            if (value.get_end() == 0) {
                if (value.get_item_obj_id() == objid) {
                    out = value;
                    i++;
                }
            }
        }
        if (i > 1) {
            _log.error("取回託售物品資料異常-未售出物品OBJID重複:" + objid);
        }
        return out;
    }

    public HashMap<Integer, L1ShopS> getShopSMap(int pcobjid) {
        HashMap<Integer, L1ShopS> shopSMap = new HashMap<>();
        int index = 0;
        for (int i = _shopSList.size() + 1; i > 0; i--) {
            L1ShopS value = (L1ShopS) _shopSList.get(i);
            if ((value != null) && (value.get_user_obj_id() == pcobjid)) {
                shopSMap.put(index, value);
                index++;
            }
        }
        if (shopSMap.size() > 0) {
            return shopSMap;
        }
        return null;
    }

    public void insertItem(int key, L1ItemInstance item, L1ShopS shopS) {
        addItem(key, item);
        set_userMap(shopS.get_id(), shopS);
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("INSERT INTO `character_shopinfo` SET `id`=?,`item_id`= ?,`item_name`=?,`count`=?,`enchantlvl`=?,`is_id`=?,`durability`=?,`charge_count`=?,`remaining_time`=?,`last_used`=?,`bless`=?,`attr_enchant_kind`=?,`attr_enchant_level`=?,`ItemAttack`=?,`ItemBowAttack`=?,`ItemReductionDmg`=?,`ItemSp`=?,`Itemprobability`=?,`ItemStr`=?,`ItemDex`=?,`ItemInt`=?,`ItemHp`=?,`ItemMp`=?,`ItemCon`=?,`ItemCha`=?,`ItemWis`=?,`racegamno`=?,`gamNo`=?,`gamNpcId` = ?,`starNpcId`=?");
            int i = 0;
            ps.setInt(++i, item.getId());
            ps.setInt(++i, item.getItemId());
            ps.setString(++i, item.getItem().getName());
            ps.setLong(++i, item.getCount());
            ps.setInt(++i, item.getEnchantLevel());
            ps.setInt(++i, item.isIdentified() ? 1 : 0);
            ps.setInt(++i, item.get_durability());
            ps.setInt(++i, item.getChargeCount());
            ps.setInt(++i, item.getRemainingTime());
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

    private void set_userMap(int getId, L1ShopS shopS) {
        userMap(shopS.get_id(), shopS);
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("INSERT INTO `character_shop` SET `id`=?,`item_obj_id`= ?,`user_obj_id`=?,`adena`=?,`overtime`=?,`end`=?,`none`=?");
            int i = 0;
            ps.setInt(++i, shopS.get_id());
            ps.setInt(++i, shopS.get_item_obj_id());
            ps.setInt(++i, shopS.get_user_obj_id());
            ps.setInt(++i, shopS.get_adena());
            ps.setTimestamp(++i, shopS.get_overtime());
            ps.setInt(++i, shopS.get_end());
            ps.setString(++i, shopS.get_none());
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    public void updateShopS(L1ShopS shopS) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `character_shop` SET `end`=?,`buy_obj_id`=?,`buy_name`=? WHERE `id`=?");
            pstm.setLong(1, shopS.get_end());
            pstm.setInt(2, shopS.get_buy_objid());
            pstm.setString(3, shopS.get_buy_name());
            pstm.setInt(4, shopS.get_id());
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void deleteItem(int key) {
        L1ItemInstance item = (L1ItemInstance) _itemList.get(key);
        if (item != null) {
            _itemList.remove(key);
            errorItem(key);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.DwarfShopTable JD-Core Version: 0.6.2
 */