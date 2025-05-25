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

public final class ExpMeteUpTable {
    private static final Log _log = LogFactory.getLog(ExpMeteUpTable.class);
    private static final Map<Integer, Double> _expmeteupList = new HashMap<>();
    private static ExpMeteUpTable _instance;

    public static ExpMeteUpTable get() {
        if (_instance == null) {
            _instance = new ExpMeteUpTable();
        }
        return _instance;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `其他_轉身後經驗遞減`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int level = rs.getInt("轉身次數");
                double expPenalty = rs.getDouble("經驗減少倍數");
                _expmeteupList.put(level, expPenalty);
            }
            _log.info("讀取->轉生經驗減少： " + _expmeteupList.size() + " (" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public double getRate(int meteup) {
        double expPenalty = 1.0D;
        if (_expmeteupList.isEmpty()) {
            return expPenalty;
        }
        if (_expmeteupList.containsKey(meteup)) {
            return (Double) _expmeteupList.get(meteup);
        }
        return expPenalty;
    }
}
