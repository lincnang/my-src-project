package com.add;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public class L1SystemMessageTable {
    private static final Log _log = LogFactory.getLog(L1SystemMessageTable.class);
    private static L1SystemMessageTable _instance;
    private final Map<Integer, L1SystemMessage> _ConfigIndex = Maps.newHashMap();

    public static L1SystemMessageTable get() {
        if (_instance == null) {
            _instance = new L1SystemMessageTable();
        }
        return _instance;
    }

    public void loadSystemMessage() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 系統_DB化開關設定");
            rs = pstm.executeQuery();
            fillSystemMessage(rs);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->DB化系統設定檔資料數量: " + _ConfigIndex.size() + "(" + timer.get() + "ms)");
    }

    private void fillSystemMessage(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int Id = rs.getInt("id");
            String Message = rs.getString("message");
            Timestamp time = rs.getTimestamp("resetMaptime");
            Calendar resetmaptime = null;
            if (time != null) {
                resetmaptime = timestampToCalendar(rs.getTimestamp("resetMaptime"));
            }
            L1SystemMessage System_Message = new L1SystemMessage(Id, Message, resetmaptime);
            _ConfigIndex.put(Integer.valueOf(Id), System_Message);
        }
    }

    public L1SystemMessage getTemplate(int Id) {
        return (L1SystemMessage) _ConfigIndex.get(Integer.valueOf(Id));
    }

    private Calendar timestampToCalendar(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        return cal;
    }
}
