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

public class C1_Name_Table {
    private static final Log _log = LogFactory.getLog(C1_Name_Table.class);
    private static final Map<Integer, String> _names = new HashMap<>();
    private static C1_Name_Table _instance;

    public static C1_Name_Table get() {
        if (_instance == null) {
            _instance = new C1_Name_Table();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `系統_陣營_名稱設定`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int c1_id = rs.getInt("c1_id");
                String c1_name = rs.getString("c1_name");
                _names.put(c1_id, c1_name);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->陣營名稱記錄數量: " + _names.size() + "(" + timer.get() + "ms)");
    }

    public String get(int key) {
        return (String) _names.get(key);
    }

    public Integer getv(String v) {
        for (Integer key : _names.keySet()) {
            String value = (String) _names.get(key);
            if (value.equals(v)) {
                return key;
            }
        }
        return -1;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.C1_Name_Table JD-Core Version: 0.6.2
 */