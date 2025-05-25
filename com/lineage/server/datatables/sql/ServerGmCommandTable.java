package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.ServerGmCommandStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;

public class ServerGmCommandTable implements ServerGmCommandStorage {
    public static final ArrayList<Integer> tradeControl = new ArrayList<Integer>();
    private static final Log _log = LogFactory.getLog(ServerGmCommandTable.class);
    private static ServerGmCommandTable _instance;

    public static ServerGmCommandTable get() {
        if (_instance == null) {
            _instance = new ServerGmCommandTable();
        }
        return _instance;
    }

    public void create(L1PcInstance pc, String cmd) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            Timestamp lastactive = new Timestamp(System.currentTimeMillis());
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("INSERT INTO `日誌_GM指令` SET `gmobjid`=?,`gmname`=?,`cmd`=?,`time`=?");
            int i = 0;
            if (pc == null) {
                ps.setInt(++i, 0);
                ps.setString(++i, "--視窗命令--");
                ps.setString(++i, cmd);
                ps.setTimestamp(++i, lastactive);
            } else {
                ps.setInt(++i, pc.getId());
                ps.setString(++i, pc.getName());
                ps.setString(++i, cmd);
                ps.setTimestamp(++i, lastactive);
                _log.info("建立GM指令使用紀錄: " + pc.getName() + " " + cmd);
            }
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void createTradeControl(int objId, String pcName) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            System.out.println(objId + " " + pcName);
            cn = DatabaseFactory.get().getConnection();
            String sqlstr = "INSERT INTO `server_item_trade_control` SET `objId`=?,`other`=?";
            ps = cn.prepareStatement(sqlstr);
            int i = 0;
            ps.setInt(++i, objId);
            ps.setString(++i, pcName);
            ps.execute();
            tradeControl.add(Integer.valueOf(objId));
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `server_item_trade_control`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int objId = rs.getInt("objId");
                tradeControl.add(Integer.valueOf(objId));
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->GM發送物品限制交易道具: " + tradeControl.size() + "(" + timer.get() + "ms)");
    }
}
