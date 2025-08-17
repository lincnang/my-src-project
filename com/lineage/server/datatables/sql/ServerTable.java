package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.config.Config;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.storage.ServerStorage;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerTable implements ServerStorage {
    private static final Log _log = LogFactory.getLog(ServerTable.class);
    private static int _srcminId = 10000;
    private int _maxId;
    private int _minId;

    private static void createServer() {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "INSERT INTO `server_info` SET `id`=?,`minid`=?,`maxid`=?,`start`=?";
            ps = cn.prepareStatement(sqlstr);
            int i = 0;
            ps.setInt(++i, Config.SERVERNO);
            ps.setInt(++i, _srcminId);
            ps.setInt(++i, 0);
            ps.setBoolean(++i, true);
            ps.execute();
            _log.info("新服務器資料表建立 - 編號:" + Config.SERVERNO);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "SELECT * FROM `server_info`";
            ps = co.prepareStatement(sqlstr);
            rs = ps.executeQuery();
            boolean isInfo = false;
            while (rs.next()) {
                int id = rs.getInt("id");
                if (Config.SERVERNO == id) {
                    isInfo = true;
                    int minid = rs.getInt("minid");
                    int maxid = rs.getInt("maxid");
                    _minId = minid;
                    if (_minId < _srcminId) {
                        _minId = _srcminId;
                    }
                    _maxId = maxid;
                    set_start();
                }
            }
            if (!isInfo) {
                createServer();
                _minId = _srcminId;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
            SQLUtil.close(rs);
        }
        _log.info("讀取->服務器存檔資料完成  (" + timer.get() + "ms)");
    }

    private void set_start() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "UPDATE `server_info` SET `start`=? WHERE `id`=?";
            pstm = con.prepareStatement(sqlstr);
            int i = 0;
            pstm.setBoolean(++i, true);
            pstm.setInt(++i, Config.SERVERNO);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void isStop() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "UPDATE `server_info` SET `maxid`=?,`start`=? WHERE `id`=?";
            pstm = con.prepareStatement(sqlstr);
            int i = 0;
            pstm.setInt(++i, IdFactory.get().maxId());
            pstm.setBoolean(++i, false);
            pstm.setInt(++i, Config.SERVERNO);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int minId() {
        return _minId;
    }

    public int maxId() {
        return _maxId;
    }
}