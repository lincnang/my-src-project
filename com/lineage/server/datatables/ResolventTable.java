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

public final class ResolventTable {
    private static final Log _log = LogFactory.getLog(ResolventTable.class);
    private static final Map<Integer, Integer> _resolvent = new HashMap<Integer, Integer>();
    private static ResolventTable _instance;

    public static ResolventTable get() {
        if (_instance == null) {
            _instance = new ResolventTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM resolvent");
            for (rs = ps.executeQuery(); rs.next(); ) {
                int itemId = rs.getInt("道具編號");
                int crystalCount = rs.getInt("溶解數量");
                _resolvent.put(new Integer(itemId), Integer.valueOf(crystalCount));
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->溶解物品設置資料數量: " + _resolvent.size() + "(" + timer.get() + "ms)");
    }

    public int getCrystalCount(int itemId) {
        int crystalCount = 0;
        if (_resolvent.containsKey(Integer.valueOf(itemId))) {
            crystalCount = ((Integer) _resolvent.get(Integer.valueOf(itemId))).intValue();
        }
        return crystalCount;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.ResolventTable JD-Core Version: 0.6.2
 */