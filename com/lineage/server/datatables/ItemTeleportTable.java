package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 傳送卷軸傳送點定義
 *
 * @author dexc
 */
public class ItemTeleportTable {
    private static final Log _log = LogFactory.getLog(ItemTeleportTable.class);
    // private static final Map<Integer, int[]> _teleportList = new
    // HashMap<Integer, int[]>();
    private static final Map<Integer, TeleportList> _teleportList = new HashMap<>();
    public static boolean START = false;
    private static ItemTeleportTable _instance;

    public static ItemTeleportTable get() {
        if (_instance == null) {
            _instance = new ItemTeleportTable();
        }
        return _instance;
    }

    /**
     * 初始化載入
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `etcitem_teleport`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int item_id = rs.getInt("item_id");
                final int locx = rs.getInt("locx");
                final int locy = rs.getInt("locy");
                final short mapid = rs.getShort("mapid");
                final int time = rs.getInt("time");
                final int week = rs.getInt("Week"); // 限制星期使用
                final int starttime = rs.getInt("Starttime"); // 限制開始時間
                final int unitytime = rs.getInt("Unitytime"); // 限制結束時間
                final TeleportList list = new TeleportList();
                list._locx = locx;
                list._locy = locy;
                list._mapid = mapid;
                list._time = time;
                list._week = week;
                list._starttime = starttime;
                list._unitytime = unitytime;
                // _teleportList.put(new Integer(item_id), new int[] { locx,
                // locy, mapid, time });
                _teleportList.put(item_id, list);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->傳送卷軸傳送點定義數量: " + _teleportList.size() + "(" + timer.get() + "ms)");
        if (_teleportList.size() <= 0) {
            _teleportList.clear();
        } else {
            START = true;
        }
    }

    /**
     * 傳回指定傳送卷軸傳送點
     */
    public TeleportList getLoc(final int item_id) {
        TeleportList teleport = null;
        if (_teleportList.containsKey(item_id)) {
            teleport = _teleportList.get(item_id);
        }
        return teleport;
    }

    /**
     * 傳回指定傳送卷軸傳送點
     */
    // public int[] getLoc(final int item_id) {
    // return _teleportList.get(item_id);
    // }
    public static class TeleportList {
        //
        private int _locx;
        //
        private int _locy;
        //
        private short _mapid;
        //
        private int _time;
        //
        private int _week; // 限制星期使用
        //
        private int _starttime; // 限制開始時間
        //
        private int _unitytime; // 限制結束時間

        public int getLocX() {
            return _locx;
        }

        public int getLocY() {
            return _locy;
        }

        public short getMapId() {
            return _mapid;
        }

        public int getTime() {
            return _time;
        }

        public int getWeek() {
            return _week;
        }

        public int getStarttime() {
            return _starttime;
        }

        public int getUnitytime() {
            return _unitytime;
        }
    }
}
