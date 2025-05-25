package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.Config;
import com.lineage.config.ConfigIpCheck;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.datatables.storage.IpStorage;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class IpTable implements IpStorage {
    private static final Log _log = LogFactory.getLog(IpTable.class);
    private static IpTable _instance;
    // 帳號解鎖時間
    private Timestamp _UnbanTime;

    public static IpTable get() {
        if (_instance == null) {
            _instance = new IpTable();
        }
        return _instance;
    }

    public Timestamp getUnbanTime() {
        return _UnbanTime;
    }

    public void setUnbanTime(Timestamp time) {
        _UnbanTime = time;
    }

    private void ufwDeny(String key) {
        try {
            if (ConfigIpCheck.UFW) {
                String command = "sudo ufw deny from " + key;
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                for (String line = null; (line = input.readLine()) != null; ) {
                    _log.info("Linux 系統命令執行: 防火牆" + line);
                }
            }
        } catch (IOException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void ufwDeleteDeny(String key) {
        try {
            String command = "sudo ufw delete deny from " + key;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            for (String line = null; (line = input.readLine()) != null; ) {
                _log.info("Linux 系統命令執行: 防火牆" + line);
            }
        } catch (IOException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactoryLogin.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `ban_ip`");
            rs = ps.executeQuery();
            while (rs.next()) {
                String key = rs.getString("ip");
                if (key.lastIndexOf(".") != -1) {// 是IP位置
                    if (!LanSecurityManager.BANIPMAP.containsKey(key)) {
                        LanSecurityManager.BANIPMAP.put(key, 100);
                        IpReading.get().checktime(key);// 檢查解封鎖時間
                    }
                } else if (!LanSecurityManager.BANNAMEMAP.containsKey(key)) {// 是帳號名稱
                    LanSecurityManager.BANNAMEMAP.put(key, 100);
                    IpReading.get().checktime(key);// 檢查解封鎖時間
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->禁止登入IP資料數量: " + LanSecurityManager.BANIPMAP.size() + "(" + timer.get() + "ms)");
        _log.info("讀取->禁止登入NAME資料數量: " + LanSecurityManager.BANNAMEMAP.size() + "(" + timer.get() + "ms)");
    }

    public void add(String key, String info) {
        boolean isBan = false;
        if (key.lastIndexOf(".") != -1) {
            if (!LanSecurityManager.BANIPMAP.containsKey(key)) {
                LanSecurityManager.BANIPMAP.put(key, 100);
                isBan = true;
                if (Config.ISUBUNTU) {
                    ufwDeny(key);
                }
            }
        } else if (!LanSecurityManager.BANNAMEMAP.containsKey(key)) {
            LanSecurityManager.BANNAMEMAP.put(key, 100);
            isBan = true;
        }
        if (check(key)) {
            isBan = false;
        }
        if (isBan) {
            Connection con = null;
            PreparedStatement pstm = null;
            try {
                con = DatabaseFactoryLogin.get().getConnection();
                pstm = con.prepareStatement("INSERT INTO `ban_ip` SET `ip`=?,`info`=?,`datetime`=SYSDATE(), UnbanTime=?");
                int i = 0;
                pstm.setString(++i, key);
                pstm.setString(++i, info);
                pstm.setTimestamp(++i, getUnbanTime());
                pstm.execute();
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        }
    }

    public void remove(String key) {
        boolean isBan = false;
        if (key.lastIndexOf(".") != -1) {
            if (LanSecurityManager.BANIPMAP.containsKey(key)) {
                LanSecurityManager.BANIPMAP.remove(key);
                isBan = true;
                if (Config.ISUBUNTU) {
                    _log.info("******Linux 系統命令執行**************************");
                    ufwDeleteDeny(key);
                    _log.info("******Linux 系統命令完成**************************");
                }
            }
        } else if (LanSecurityManager.BANNAMEMAP.containsKey(key)) {
            LanSecurityManager.BANNAMEMAP.remove(key);
            isBan = true;
        }
        if (!check(key)) {
            isBan = false;
        }
        if (isBan) {
            Connection con = null;
            PreparedStatement pstm = null;
            try {
                con = DatabaseFactoryLogin.get().getConnection();
                pstm = con.prepareStatement("DELETE FROM `ban_ip` WHERE `ip`=?");
                pstm.setString(1, key);
                pstm.execute();
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        }
    }

    private boolean check(String key) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "SELECT * FROM `ban_ip` WHERE `ip` ='" + key + "'";
            ps = cn.prepareStatement(sqlstr);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        SQLUtil.close(rs);
        SQLUtil.close(ps);
        SQLUtil.close(cn);
        return false;
    }

    public void checktime(String key) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "SELECT * FROM `ban_ip` WHERE `ip` ='" + key + "'";
            ps = cn.prepareStatement(sqlstr);
            rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("UnbanTime");
                if ((ts != null) && (ts.getTime() < System.currentTimeMillis())) {
                    IpReading.get().remove(key);
                    System.out.println("解除封鎖：" + key);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        SQLUtil.close(rs);
        SQLUtil.close(ps);
        SQLUtil.close(cn);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.IpTable JD-Core Version: 0.6.2
 */