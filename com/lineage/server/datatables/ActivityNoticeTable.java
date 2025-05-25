package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.ActivityNotice;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 活動
 *
 * @author dexc
 */
public class ActivityNoticeTable {
    private static final Log _log = LogFactory.getLog(ActivityNoticeTable.class);
    private static ActivityNoticeTable _notice;
    private HashMap<Integer, ActivityNotice> list = new HashMap<>();

    public static ActivityNoticeTable get() {
        if (_notice == null) {
            _notice = new ActivityNoticeTable();
        }
        return _notice;
    }

    public HashMap<Integer, ActivityNotice> getAllActivityNotice() {
        return list;
    }

    public ActivityNotice getActivityNotice(final int id) {
        return list.get(id);
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM activity_notice");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final ActivityNotice data = new ActivityNotice();
                data.setId(rs.getInt("id"));
                data.setTitle(rs.getString("title"));
                data.setisShow(rs.getInt("isShow"));
                if (data.getisShow() == 1) {
                    data.setboss_spawnid(rs.getInt("boss_spawnid"));
                    data.setLocx(rs.getInt("teleport_x"));
                    data.setLocy(rs.getInt("teleport_y"));
                    data.setMapid(rs.getInt("teleport_mapid"));
                    data.setMarterial(rs.getInt("marterial"));
                    data.setMarterial_count(rs.getInt("marterial_count"));
                    data.setweek(rs.getInt("start_week"));
                    data.sethour(rs.getInt("start_hour"));
                    data.setminute(rs.getInt("start_minute"));
                    data.setsecond(rs.getInt("start_second"));
                    data.setendtime(rs.getInt("end_time"));
                    list.put(data.getId(), data);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->活動顯示設置數量: " + list.size() + "(" + timer.get() + "ms)");
    }
}
