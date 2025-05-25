package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ShopItem;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 特殊商店販賣資料
 *
 * @author dexc
 */
public class ShopCnTable {
    private static final Log _log = LogFactory.getLog(ShopCnTable.class);
    private static final Map<Integer, ArrayList<L1ShopItem>> _shopList = new HashMap<Integer, ArrayList<L1ShopItem>>();
    private static final ArrayList<Integer> _CnitemidList = new ArrayList<Integer>();
    // 回收物品價格清單
    private static final Map<Integer, Integer> _allCnShopItem = new HashMap<Integer, Integer>();
    private static ShopCnTable _instance;

    public static ShopCnTable get() {
        if (_instance == null) {
            _instance = new ShopCnTable();
        }
        return _instance;
    }

    /**
     * 加入出售物品價格查詢清單
     *
     * @param itemId       物品編號
     * @param sellingPrice 賣出價
     * @param packCount    數量
     */
    private static void addSellList(final int itemId, final int sellingPrice, final int purchasingPrice, final int packCount) {
        // 目前回收價格
        final Integer currentprice = _allCnShopItem.get(new Integer(itemId));
        double value3 = 0;// 回收單價
        // 回收價格不為0
        if (purchasingPrice > 0) {
            if (packCount > 0) {
                // 售價 / 數量 / 2
                value3 = (sellingPrice / packCount) / 2.5;
            } else {
                value3 = purchasingPrice;
            }
        } else {// 沒有設定回收價格
            if (sellingPrice > 0) {
                if (packCount > 0) {
                    // 售價 / 數量 / 2
                    value3 = (sellingPrice / packCount) / 2.5;
                } else {
                    // 售價 / 2
                    value3 = sellingPrice / 2.5;
                }
            }
        }
        // 計算後回收單價小於1
        if (value3 < 1) {
            if (currentprice != null) {// 目前回收價格不為空
                // 移出回收物品列
                _allCnShopItem.remove(new Integer(itemId));
            }
            return;
        }
        // 目前價格不為空
        if (currentprice != null) {
            // 計算後回收單價 小於 目前價格
            if (value3 < currentprice) {
                // 更新回收單價(降低回收單價)
                _allCnShopItem.put(new Integer(itemId), new Integer((int) value3));
            }
            // 目前價格為空
        } else {
            // 更新回收單價
            _allCnShopItem.put(new Integer(itemId), new Integer((int) value3));
            // System.out.println("itemid : " + itemId + " price : " + value3);
        }
    }

    /**
     * 更新物品注記
     *
     */
    private static void updata_name(int npcId, int itemId) {
        Connection cn = null;
        PreparedStatement ps = null;
        String npcname = NpcTable.get().getNpcName(npcId);
        String itemname = ItemTable.get().getTemplate(itemId).getName();
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `shop_cn` SET `note`=? WHERE `npc_id`=? AND `item_id`=?");
            int i = 0;
            ps.setString(++i, npcname + "=>" + itemname);
            ps.setInt(++i, npcId);
            ps.setInt(++i, itemId);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 刪除錯誤資料
     *
     */
    private static void delete(final int npc_id, final int item_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `shop_cn` WHERE `npc_id`=? AND `item_id`=?");
            ps.setInt(1, npc_id);
            ps.setInt(2, item_id);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void restshopCn() {
        _shopList.clear();
        load();
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `shop_cn` ORDER BY `order_id`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("id");
                final int npcId = rs.getInt("npc_id");
                final int itemId = rs.getInt("item_id");
                final String note = rs.getString("note");
                int enchantlevel = rs.getInt("enchant_level");
                if (ItemTable.get().getTemplate(itemId) == null) {
                    _log.error("商城販賣物品資料錯誤: 沒有這個編號的道具:" + itemId + " 對應NPC編號:" + npcId);
                    delete(npcId, itemId);
                    continue;
                }
                final int sellingPrice = rs.getInt("selling_price");
                final int purchasingPrice = rs.getInt("purchasing_price");
                final int packCount = rs.getInt("pack_count");
                L1ShopItem item = new L1ShopItem(id, itemId, sellingPrice, packCount, enchantlevel);
                this.addShopItem(npcId, item);
                // 加入出售物品價格查詢清單
                addSellList(itemId, sellingPrice, purchasingPrice, packCount);
                _CnitemidList.add(itemId);
                if (!note.contains("=>")) {// 更新物品注記
                    updata_name(npcId, itemId);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
        _log.info("讀取->商城販賣物品資料數量: " + _shopList.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 傳回回收單價
     *
     */
    public int getPrice(final int itemid) {
        int tgprice = 0;// 預設價格
        final Integer price = _allCnShopItem.get(new Integer(itemid));
        if (price != null) {// 已有回收價格
            tgprice = price;
        }
        return tgprice;
    }

    /**
     * 傳回商城物品ID列表
     *
     */
    public ArrayList<Integer> get_cnitemidlist() {
        return _CnitemidList;
    }

    /**
     * 加入販賣物品
     *
     */
    private void addShopItem(final int npcId, final L1ShopItem item) {
        ArrayList<L1ShopItem> list = _shopList.get(new Integer(npcId));
        if (list == null) {
            list = new ArrayList<L1ShopItem>();
            list.add(item);
            _shopList.put(npcId, list);
        } else {
            list.add(item);
        }
    }

    /**
     * 傳回NPC販賣清單
     *
     */
    public ArrayList<L1ShopItem> get(final int npcId) {
        final ArrayList<L1ShopItem> list = _shopList.get(new Integer(npcId));
        if (list != null) {
            return list;
        }
        return null;
    }

    /**
     * 傳回該販賣物品資料
     *
     */
    public L1ShopItem getTemp(final int npcId, final int id) {
        final ArrayList<L1ShopItem> list = _shopList.get(new Integer(npcId));
        if (list != null) {
            for (final L1ShopItem shopItem : list) {
                if (shopItem.getId() == id) {
                    return shopItem;
                }
            }
        }
        return null;
    }

    /**
     * 指定NPC是否有販賣此itemid物品
     *
     */
    public boolean isSelling(final int npcId, final int itemid) {
        final ArrayList<L1ShopItem> list = _shopList.get(new Integer(npcId));
        if (list != null) {
            for (final L1ShopItem shopItem : list) {
                if (shopItem.getItemId() == itemid) {
                    return true;
                }
            }
        }
        return false;
    }
}
