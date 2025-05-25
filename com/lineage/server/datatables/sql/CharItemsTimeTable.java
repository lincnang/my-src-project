package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.CharItemsTimeStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;

public class CharItemsTimeTable implements CharItemsTimeStorage {
    private static final Log _log = LogFactory.getLog(CharItemsTimeTable.class);

    private static void addValue(int itemr_obj_id, Timestamp usertime) {
        L1Object obj = World.get().findObject(itemr_obj_id);
        boolean isError = true;
        if ((obj != null) && ((obj instanceof L1ItemInstance))) {
            L1ItemInstance item = (L1ItemInstance) obj;
            item.set_time(usertime);
            if ((item.getItem().getType2() == 0) && (item.getItem().getclassname().startsWith("shop.VIP_Card_"))) {
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                if (usertime.before(ts)) {
                    item.set_card_use(2);
                } else {
                    item.set_card_use(1);
                }
            }
            isError = false;
        }
        if (isError) {
            delete(itemr_obj_id);
        }
    }

    private static void delete(int objid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `character_items_time` WHERE `itemr_obj_id`=?");
            ps.setInt(1, objid);
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
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int size = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_items_time`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int itemr_obj_id = rs.getInt("itemr_obj_id");
                Timestamp usertime = rs.getTimestamp("usertime");
                addValue(itemr_obj_id, usertime);
                size++;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->物品使用期限資料數量: " + size + "(" + timer.get() + "ms)");
    }

    public void addTime(int itemr_obj_id, Timestamp usertime) {
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement(
                    "INSERT INTO `character_items_time` (`itemr_obj_id`, `usertime`) " +
                            "VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE `usertime` = VALUES(`usertime`)"
            );
            ps.setInt(1, itemr_obj_id);
            ps.setTimestamp(2, usertime);
            ps.execute();
        } catch (SQLException e) {
            _log.error("限時道具寫入失敗: " + e.getMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    public void updateTime(int itemr_obj_id, Timestamp usertime) {
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("UPDATE `character_items_time` SET `usertime`=? WHERE `itemr_obj_id`=?");
            int i = 0;
            ps.setTimestamp(++i, usertime);
            ps.setInt(++i, itemr_obj_id);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    /**
     * 是否存在時間資料
     *
     */
    @SuppressWarnings("resource")
    public boolean isExistTimeData(int itemr_obj_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_items_time` WHERE `itemr_obj_id`=?");
            ps.setInt(1, itemr_obj_id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int objid = rs.getInt("itemr_obj_id");
                if (objid == itemr_obj_id) {
                    //System.out.println("具有存在時間");
                    return true;
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        //System.out.println("沒有存在時間");
        return false;
    }
}
