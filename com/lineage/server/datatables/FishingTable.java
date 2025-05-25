package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Fishing;
import com.lineage.server.templates.L1Item;
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
import java.util.Random;

/**
 * 漁獲資料暫存
 *
 * @author dexc
 */
public class FishingTable {
    public static final Log _log = LogFactory.getLog(FishingTable.class);
    private static final Map<Integer, ArrayList<L1Fishing>> _fishingMap = new HashMap<>();
    private static Random _random = new Random();
    private static FishingTable _instance;

    public static FishingTable get() {
        if (_instance == null) {
            _instance = new FishingTable();
        }
        return _instance;
    }

    private static void delete(final int item_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `server_fishing` WHERE `give_itemid`=?");
            ps.setInt(1, item_id);
            ps.execute();
        } catch (final SQLException e) {
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
            ps = cn.prepareStatement("SELECT * FROM `server_fishing`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int fishitemid = rs.getInt("fish_itemid"); // 魚竿編號
                final int fishtime = rs.getInt("fish_time"); // 魚竿釣魚時間
                final int giveitemid = rs.getInt("give_itemid"); // 釣魚成功給予的物品編號
                final int givecount = rs.getInt("give_count"); // 釣魚成功給予的物品數量
                final int random = rs.getInt("random"); // 釣魚機率(百萬)
                final int failitemid = rs.getInt("fail_itemid"); // 釣魚失敗給予的物品編號
                final int failcount = rs.getInt("fail_count"); // 釣魚成功給予的物品數量
                final int fishgfx = rs.getInt("fish_gfx"); // 成功特效
                final int showworld = rs.getInt("show_world"); // 是否公告
                if (ItemTable.get().getTemplate(giveitemid) == null) { // 釣魚成功給予的物品編號
                    _log.error("漁獲資料錯誤: 沒有這個編號的道具:" + giveitemid);
                    delete(giveitemid);
                    continue;
                }
                if (givecount > 0) {
                    final L1Fishing value = new L1Fishing();
                    value.setFishItemId(fishitemid); // 魚竿編號
                    value.setFishTime(fishtime); // 釣魚時間
                    value.setGiveItemId(giveitemid); // 釣魚成功給予的物品編號
                    value.setGiveCount(givecount); // 釣魚成功給予的物品數量
                    value.setRandom(random); // 釣魚機率(百萬)
                    value.setFailItemId(failitemid); // 釣魚失敗給予的物品編號
                    value.setFailCount(failcount); // 釣魚失敗給予的物品數量
                    value.setFishGfx(fishgfx); // 成功特效
                    value.setShowWorld(showworld); // 是否公告
                    ArrayList<L1Fishing> itemlist = _fishingMap.get(fishitemid);
                    if (itemlist == null) {
                        itemlist = new ArrayList<>();
                        itemlist.add(value);
                        _fishingMap.put(fishitemid, itemlist);
                    } else {
                        _fishingMap.get(fishitemid).add(value);
                    }
                }
                String note = rs.getString("note");// 更新釣魚訊息
                // 更新釣魚訊息
                if (!note.contains("=>")) {
                    // 找回物品資訊
                    final L1Item tempget = ItemTable.get().getTemplate(failitemid);
                    if (tempget == null) {
                        updata_name(fishitemid, giveitemid);
                    } else {
                        updata_name(fishitemid, giveitemid, failitemid);
                    }
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->漁獲資料數量: " + _fishingMap.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 更新釣魚訊息
     *
     */
    private void updata_name(int fishitemid, int giveitemid) {
        Connection cn = null;
        PreparedStatement ps = null;
        final L1Item fish_item = ItemTable.get().getTemplate(fishitemid);
        final L1Item key_item = ItemTable.get().getTemplate(giveitemid);
        String itemname1 = fish_item.getName();
        String itemname2 = key_item.getName();
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `server_fishing` SET `note`=? WHERE `fish_itemid`=? AND `give_itemid`=?");
            int i = 0;
            ps.setString(++i, itemname1 + "=>釣【" + itemname2 + "】失敗給【無】");
            ps.setInt(++i, fishitemid);
            ps.setInt(++i, giveitemid);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 更新釣魚訊息
     *
     */
    private void updata_name(int fishitemid, int giveitemid, int failitemid) {
        Connection cn = null;
        PreparedStatement ps = null;
        final L1Item fish_item = ItemTable.get().getTemplate(fishitemid);
        final L1Item key_item = ItemTable.get().getTemplate(giveitemid);
        final L1Item fail_itemid = ItemTable.get().getTemplate(failitemid);
        String itemname1 = fish_item.getName();
        String itemname2 = key_item.getName();
        String itemname3 = fail_itemid.getName();
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `server_fishing` SET `note`=? WHERE `fish_itemid`=? AND `give_itemid`=? AND `fail_itemid`=?");
            int i = 0;
            ps.setString(++i, itemname1 + "=>釣【" + itemname2 + "】失敗給【" + itemname3 + "】");
            ps.setInt(++i, fishitemid);
            ps.setInt(++i, giveitemid);
            ps.setInt(++i, failitemid);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public L1Fishing get_item(final int itemid) {
        try {
            Object[] objs;
            if (_fishingMap.get(itemid) == null) {
                objs = _fishingMap.get(0).toArray();
                _log.error("魚竿：" + itemid + "無可釣起的道具!");
            } else {
                objs = _fishingMap.get(itemid).toArray();
            }
            if (objs.length <= 0) {
                objs = _fishingMap.get(0).toArray();
            }
            final Object obj = objs[_random.nextInt(objs.length)];
            final L1Fishing fishing = (L1Fishing) obj;
            return fishing;
        } catch (final Exception e) {
        }
        return null;
    }

    public boolean isFishing(final int itemid) {
        if (_fishingMap == null || _fishingMap.size() <= 0 || itemid <= 0) {
            return false;
        }
        for (final ArrayList<L1Fishing> itemlist : _fishingMap.values()) {
            if (itemlist != null) {
                if (itemlist.size() <= 0) {
                    continue;
                }
                for (final L1Fishing temp : itemlist) {
                    if (itemid == temp.getGiveItemId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
