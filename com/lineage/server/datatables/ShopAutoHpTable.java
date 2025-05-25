package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ShopAutoHp;
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
 * 特殊商店<BR>
 * <p>
 * 購買自動喝水補魔道具
 */
public class ShopAutoHpTable {
    private static final Log _log = LogFactory.getLog(ShopAutoHpTable.class);
    private static final Map<Integer, ArrayList<L1ShopAutoHp>> _shopList = new HashMap<Integer, ArrayList<L1ShopAutoHp>>();
    private static ShopAutoHpTable _instance;

    public static ShopAutoHpTable get() {
        if (_instance == null) {
            _instance = new ShopAutoHpTable();
        }
        return _instance;
    }

    /**
     * 刪除錯誤資料
     *
     * @param Type
     * @param item_id
     */
    public static void delete(final int Type, final int item_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `shop_內掛商店` WHERE `屬性`=? AND `道具編號`=?");
            ps.setInt(1, Type);
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
     * 更新道具訊息
     *
     * @param type
     * @param itemId
     */
    private static void updata_name(int type, int itemId) {
        Connection cn = null;
        PreparedStatement ps = null;
        String itemtype = "";
        if (type == 1) {
            itemtype = "一";
        } else if (type == 2) {
            itemtype = "二";
        } else if (type == 3) {
            itemtype = "三";
        } else {
            itemtype = "四";
        }
        String itemname = ItemTable.get().getTemplate(itemId).getName();
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `shop_內掛商店` SET `名稱`=? WHERE `屬性`=? AND `道具編號`=?");
            int i = 0;
            ps.setString(++i, "第" + itemtype + "組點選=>" + itemname);
            ps.setInt(++i, type);
            ps.setInt(++i, itemId);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `shop_內掛商店` ORDER BY `道具編號`");
            rs = ps.executeQuery();
            L1ShopAutoHp item;
            while (rs.next()) {
                final int id = rs.getInt("流水號");
                final int Type = rs.getInt("屬性");
                final int itemId = rs.getInt("道具編號");
                if (ItemTable.get().getTemplate(itemId) == null) {
                    _log.error("喝水商店販賣資料錯誤: 沒有這個編號的道具:" + itemId + " 對應Type編號:" + Type);
                    delete(Type, itemId);
                    continue;
                }
                item = new L1ShopAutoHp(id, itemId);
                this.addShopItem(Type, item);
                final String note = rs.getString("名稱");
                if (!note.contains("=>")) {
                    updata_name(Type, itemId);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
        _log.info("讀取->shop_內掛商店數量: " + _shopList.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 加入販賣物品
     *
     * @param Type
     * @param item
     */
    private void addShopItem(final int Type, final L1ShopAutoHp item) {
        ArrayList<L1ShopAutoHp> list = _shopList.get(new Integer(Type));
        if (list == null) {
            list = new ArrayList<L1ShopAutoHp>();
            list.add(item);
            _shopList.put(Type, list);
        } else {
            list.add(item);
        }
    }

    /**
     * 傳回販賣清單
     *
     * @param Type
     * @return
     */
    public ArrayList<L1ShopAutoHp> get(final int Type) {
        final ArrayList<L1ShopAutoHp> list = _shopList.get(new Integer(Type));
        if (list != null) {
            return list;
        }
        return null;
    }

    /**
     * 傳回該販賣物品資料
     *
     * @param Type
     * @param id
     * @return
     */
    public L1ShopAutoHp getTemp(final int Type, final int id) {
        final ArrayList<L1ShopAutoHp> list = _shopList.get(new Integer(Type));
        if (list != null) {
            for (final L1ShopAutoHp shopItem : list) {
                if (shopItem.get_id() == id) {
                    return shopItem;
                }
            }
        }
        return null;
    }
}
