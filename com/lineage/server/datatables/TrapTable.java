package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Trap;
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

public class TrapTable {
    private static final Log _log = LogFactory.getLog(TrapTable.class);
    private static final Map<Integer, L1Trap> _traps = new HashMap<Integer, L1Trap>();
    private static TrapTable _instance;

    public static TrapTable get() {
        if (_instance == null) {
            _instance = new TrapTable();
        }
        return _instance;
    }

    public static void reload() {
        _instance = new TrapTable();
        _traps.clear();
        _instance.load();
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `trap`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                L1Trap trap = new L1Trap(rs);
                _traps.put(Integer.valueOf(trap.getId()), trap);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->陷阱資料數量: " + _traps.size() + "(" + timer.get() + "ms)");
    }

    public L1Trap getTemplate(int id) {
        return (L1Trap) _traps.get(Integer.valueOf(id));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.TrapTable JD-Core Version: 0.6.2
 */