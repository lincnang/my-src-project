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

public class MapExpTable {
    private static final Log _log = LogFactory.getLog(MapExpTable.class);
    private static final Map<Integer, Double> _exp = new HashMap<>();
    private static final Map<Integer, int[]> _level = new HashMap<>();
    private static MapExpTable _instance;

    public static MapExpTable get() {
        if (_instance == null) {
            _instance = new MapExpTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `mapids_exp`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int mapid = rs.getInt("mapid");
                double exp = rs.getInt("exp");
                _exp.put(mapid, exp);
                int[] level = new int[2];
                int min = rs.getInt("min");
                int max = rs.getInt("max");
                level[0] = min;
                level[1] = max;
                _level.put(mapid, level);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
        _log.info("讀取->地圖經驗值倍率資料數量: " + _exp.size() + "(" + timer.get() + "ms)");
    }

    public boolean get_level(int mapid, int level) {
        if (_exp.get(mapid) == null) {
            return false;
        }
        int[] levelX = (int[]) _level.get(mapid);
        if ((level >= levelX[0]) && (level <= levelX[1])) {
            return true;
        }
        return false;
    }

    public Double get_exp(int mapid) {
        return (Double) _exp.get(mapid);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.MapExpTable JD-Core Version: 0.6.2
 */