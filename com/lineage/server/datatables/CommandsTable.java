package com.lineage.server.datatables;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.templates.L1Command;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandsTable {
    private static final Log _log = LogFactory.getLog(CommandsTable.class);
    private static final Map<String, L1Command> _commandList = new HashMap<>();
    private static CommandsTable _instance;

    public static CommandsTable get() {
        if (_instance == null) {
            _instance = new CommandsTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `commands`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int access_level = rs.getInt("access_level");
                String class_name = rs.getString("class_name");
                String note = rs.getString("note");
                boolean system = rs.getBoolean("system");
                L1Command command = new L1Command(name, system, access_level, class_name, note);
                _commandList.put(name.toLowerCase(), command);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->管理者命令數量: " + _commandList.size() + "(" + timer.get() + "ms)");
    }

    public L1Command get(String name) {
        return (L1Command) _commandList.get(name);
    }

    public Collection<L1Command> getList() {
        return _commandList.values();
    }
}
