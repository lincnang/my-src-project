package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.L1NpcTalkData;
import com.lineage.server.model.shop.L1Shop;
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
import java.util.List;
import java.util.Map;

/**
 * 商店販賣資料
 *
 * @author dexc
 */
public class ShopTable {
    private static final Log _log = LogFactory.getLog(ShopTable.class);
    // 銷售清單
    private static final Map<Integer, L1Shop> _allShops = new HashMap<>();
    // 回收物品
    private static final Map<Integer, Integer> _allShopItem = new HashMap<>();
    // 不回收的物品
    private static final Map<Integer, Integer> _noBuyList = new HashMap<>();
    private static ShopTable _instance;

    public static ShopTable get() {
        if (_instance == null) {
            _instance = new ShopTable();
        }
        return _instance;
    }

    private static ArrayList<Integer> enumNpcIds() {
        final ArrayList<Integer> ids = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT DISTINCT `npc_id` FROM `shop`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("npc_id"));
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return ids;
    }

    // 在 ShopTable.java 中
    private static L1Shop loadShop(int npcId, ResultSet rs) throws SQLException {
        // 賣出清單
        final List<L1ShopItem> sellingList = new ArrayList<>();
        // 買入清單
        final List<L1ShopItem> purchasingList = new ArrayList<>();
        // 使用的貨幣設定
        int currencyItemId = 40308;
        L1NpcTalkData action = NPCTalkDataTable.get().getTemplate(npcId);
        if (action != null && action.getCurrencyItemId() != 0) {
            currencyItemId = action.getCurrencyItemId();
        }
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            if (ItemTable.get().getTemplate(itemId) == null) {
                _log.error("商店販賣資料錯誤: 沒有這個編號的道具:" + itemId + " 對應NPC編號:" + npcId);
                delete(npcId, itemId);
            } else {
                int sellingPrice = rs.getInt("selling_price");// 賣出金額
                int purchasingPrice = rs.getInt("purchasing_price");// 回收金額
                int packCount = rs.getInt("pack_count");// 賣出數量
                String note = rs.getString("note");// 物品注記
                /** [原碼] 出售強化物品 */
                int enchantlevel = rs.getInt("enchant_level");

                // --- ▼▼▼ 【修正一】在這裡新增，從資料庫讀取限購數量 ▼▼▼ ---
                int dailyLimit = rs.getInt("daily_limit");
                // --- ▲▲▲ ---

                Connection conI = null;
                PreparedStatement pstmI = null;
                ResultSet rsI = null;
                try {
                    conI = DatabaseFactory.get().getConnection();
                    pstmI = conI.prepareStatement("SELECT * FROM shop WHERE item_id='" + itemId + "' order by order_id ");
                    rsI = pstmI.executeQuery();
                    while (rsI.next()) {
                        if ((rsI.getInt("selling_price") >= 1) && (purchasingPrice > rsI.getInt("selling_price"))) {
                            System.out.println("偵測到買低賣高錯誤!!! NpcId=" + rsI.getInt("npc_id") + ", ItemID=" + itemId + ", 價格錯誤!!!");
                            purchasingPrice = -1;
                        }
                    }
                    rsI.close();
                } catch (SQLException e) {
                    _log.error(e.getLocalizedMessage(), e);
                } finally {
                    SQLUtil.close(rsI, pstmI, conI);
                }
                if (!note.contains("=>")) {
                    updata_name(npcId, itemId);
                }
                addSellList(itemId, sellingPrice, purchasingPrice, packCount);
                packCount = packCount == 0 ? 1 : packCount;
                if (sellingPrice >= 0) {
                    // --- ▼▼▼ 【修正二】將讀取到的 dailyLimit 變數傳入建構子 ▼▼▼ ---
                    L1ShopItem item = new L1ShopItem(itemId, sellingPrice, packCount, enchantlevel, dailyLimit);
                    // --- ▲▲▲ ---
                    sellingList.add(item);
                }
                if (purchasingPrice >= 0) {
                    // 玩家賣給商店的物品，不需要限購，所以維持 -1
                    L1ShopItem item = new L1ShopItem(itemId, purchasingPrice, packCount, enchantlevel, -1);
                    purchasingList.add(item);
                }
            }
        }
        return new L1Shop(npcId, currencyItemId, sellingList, purchasingList);
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
            ps = cn.prepareStatement("UPDATE `shop` SET `note`=? WHERE `npc_id`=? AND `item_id`=?");
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
            ps = cn.prepareStatement("DELETE FROM `shop` WHERE `npc_id`=? AND `item_id`=?");
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

    /**
     * 加入出售物品價格查詢清單
     *
     * @param itemId          物品編號
     * @param sellingPrice    賣出價
     * @param purchasingPrice 回收價
     * @param packCount       數量
     */
    private static void addSellList(final int itemId, final int sellingPrice, final int purchasingPrice, final int packCount) {
        // 已經加入不回收清單 忽略以下判斷
        if (_noBuyList.get(itemId) != null) {
            return;
        }
        // 目前回收價格
        final Integer price = _allShopItem.get(itemId);
        double value3 = 0;// 回收單價
        // 回收價格不為0
        if (purchasingPrice > 0) {
            if (packCount > 0) {
                // 售價 / 數量 / 2
                value3 = (sellingPrice / packCount) / 2.0;
            } else {
                value3 = purchasingPrice;
            }
        } else {
            if (sellingPrice > 0) {
                if (packCount > 0) {
                    // 售價 / 數量 / 2
                    value3 = (sellingPrice / packCount) / 2.0;
                } else {
                    // 售價 / 2
                    value3 = sellingPrice / 2.0;
                }
            }
        }
        // 計算後回收單價小於1
        if (value3 < 1) {
            _noBuyList.put(itemId, (int) value3);
            if (price != null) {// 目前回收價格不為空
                // 移出回收物品列
                _allShopItem.remove(itemId);
            }
            return;
        }
        // 目前回收價格不為空
        if (price != null) {
            // 計算後回收單價 小於 目前回收價格
            if (value3 < price) {
                // 更新回收單價(降低回收單價)
                _allShopItem.put(itemId, (int) value3);
            }
            // 目前價格為空
        } else {
            // 更新回收單價
            _allShopItem.put(itemId, (int) value3);
        }
    }

    public void restshop() {
        _allShops.clear();
        _allShopItem.clear();
        _noBuyList.clear();
        load();
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `shop` WHERE `npc_id`=? ORDER BY `order_id`");
            for (final int npcId : enumNpcIds()) {
                ps.setInt(1, npcId);
                rs = ps.executeQuery();
                final L1Shop shop = loadShop(npcId, rs);
                _allShops.put(npcId, shop);
                rs.close();
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
        _log.info("讀取->商店販賣資料數量: " + _allShops.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 傳回回收單價
     *
     */
    public int getPrice(final int itemid) {
        int tgprice = 0;// 預設價格
        final Integer price = _allShopItem.get(itemid);
        if (price != null) {// 已有回收價格
            tgprice = price;
        }
        // 不回收物
        if (_noBuyList.get(itemid) != null) {
            tgprice = 0;
        }
        return tgprice;
    }

    /**
     * 傳回此NPCID的商店資料
     *
     */
    public L1Shop get(final int npcId) {
        return _allShops.get(npcId);
    }
}
