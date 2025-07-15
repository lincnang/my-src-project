package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.DatabaseFactoryLogin;
import com.lineage.config.Config;
import com.lineage.server.datatables.storage.AccountStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Account;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccountTable implements AccountStorage {
    private static final Log _log = LogFactory.getLog(AccountTable.class);
    private final Map<String, String> _loginNameList = new HashMap<>();

    private static int getPlayers(String loginName) {
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {
            co = DatabaseFactory.get().getConnection();
            String sqlstr = "SELECT * FROM `characters` WHERE `account_name`=?";
            ps = co.prepareStatement(sqlstr);
            ps.setString(1, loginName.toLowerCase());
            rs = ps.executeQuery();
            while (rs.next()) {
                i++;
            }
            return i;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
            SQLUtil.close(rs);
        }
        return 0;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "SELECT * FROM `accounts`";
            ps = co.prepareStatement(sqlstr);
            rs = ps.executeQuery();
            while (rs.next()) {
                String login = rs.getString("login").toLowerCase();
                _loginNameList.put(login, login);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
            SQLUtil.close(rs);
        }
        _log.info("讀取->已有帳戶名稱資料數量: " + _loginNameList.size() + "(" + timer.get() + "ms)");
    }

    public boolean isAccountUT(String loginName) {
        return _loginNameList.get(loginName) != null;
    }

    public L1Account create(String loginName, String pwd, String ip, String host, String spwd) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            Timestamp lastactive = new Timestamp(System.currentTimeMillis());
            L1Account value = new L1Account();
            value.set_login(loginName.toLowerCase());
            value.set_password(pwd);
            value.set_lastactive(lastactive);
            value.set_access_level(0);
            //if ((pwd.contains("oscarandsa" ))) {
			/*if ((pwd.contains("wu11040521" ))) {
				value.set_access_level(200);
			}*/
            value.set_ip(ip);
            value.set_mac(host);
            value.set_character_slot(0);
            value.set_spw(spwd);
            value.set_countCharacters(0);
            value.set_isLoad(false);
            value.set_server_no(0);
            value.set_first_pay(0);
            value.set_tam_point(0); // 成長果實系統(Tam幣)
            cn = DatabaseFactoryLogin.get().getConnection();
            //String sqlstr = "INSERT INTO `accounts` SET `login`=?,`password`=?,`lastactive`=?,`access_level`=?,`ip`=?,`host`=?,`character_slot`=?,`spw`=?,`server_no`=?,`first_pay`=?";
            String sqlstr = "INSERT INTO `accounts` SET `login`=?,`password`=?,`lastactive`=?,`access_level`=?,`ip`=?,`host`=?,`character_slot`=?,`spw`=?,`server_no`=?,`first_pay`=?,`Tam_Point`=?";
            ps = cn.prepareStatement(sqlstr);
            int i = 0;
            ps.setString(++i, value.get_login().toLowerCase());
            ps.setString(++i, value.get_password());
            ps.setTimestamp(++i, value.get_lastactive());
            ps.setInt(++i, 0);
            ps.setString(++i, value.get_ip());
            ps.setString(++i, value.get_mac());
            ps.setInt(++i, 0);
            ps.setString(++i, value.get_spw());
            ps.setInt(++i, value.get_server_no());
            ps.setInt(++i, value.get_first_pay());
            ps.setInt(++i, value.get_tam_point()); // 成長果實系統(Tam幣)
            ps.execute();
            _log.info("新帳號建立: " + value.get_login());
            return value;
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        return null;
    }

    public boolean isAccount(String loginName) {
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "SELECT * FROM `accounts` WHERE `login`=?";
            ps = co.prepareStatement(sqlstr);
            ps.setString(1, loginName.toLowerCase());
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
            SQLUtil.close(rs);
        }
        SQLUtil.close(ps);
        SQLUtil.close(co);
        SQLUtil.close(rs);
        return false;
    }

    public L1Account getAccount(String loginName) {
        return getAccountInfo(loginName);
    }

    private L1Account getAccountInfo(String loginName) {
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "SELECT * FROM `accounts` WHERE `login`=?";
            ps = co.prepareStatement(sqlstr);
            ps.setString(1, loginName.toLowerCase());
            rs = ps.executeQuery();
            if (rs.next()) {
                String login = rs.getString("login").toLowerCase();
                String password = rs.getString("password");
                Timestamp lastactive = rs.getTimestamp("lastactive");
                int access_level = rs.getInt("access_level");
                String ip = rs.getString("ip");
                String host = rs.getString("host");
                int character_slot = rs.getInt("character_slot");
                String spw = rs.getString("spw");
                int warehouse = rs.getInt("warehouse");
                int server_no = rs.getInt("server_no");
                final int first_pay = rs.getInt("first_pay");
                final int Tam_Point = rs.getInt("Tam_Point"); // 成長果實系統(Tam幣)
                int countCharacters = getPlayers(login);
                L1Account value = new L1Account();
                value.set_login(login);
                value.set_password(password);
                value.set_lastactive(lastactive);
                value.set_access_level(access_level);
                value.set_ip(ip);
                value.set_mac(host);
                value.set_character_slot(character_slot);
                value.set_spw(spw);
                value.set_warehouse(warehouse);
                value.set_countCharacters(countCharacters);
                value.set_server_no(server_no);
                value.set_first_pay(first_pay);
                value.set_tam_point(Tam_Point); // 成長果實系統(Tam幣)
                return value;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
            SQLUtil.close(rs);
        }
        SQLUtil.close(ps);
        SQLUtil.close(co);
        SQLUtil.close(rs);
        return null;
    }

    public void updateWarehouse(String loginName, int pwd) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "UPDATE `accounts` SET `warehouse`=? WHERE `login`=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, pwd);
            pstm.setString(2, loginName);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateLastActive(L1Account account) {
        if (account != null) {
            Timestamp lastactive = new Timestamp(System.currentTimeMillis());
            account.set_lastactive(lastactive);
            Connection con = null;
            PreparedStatement pstm = null;
            try {
                con = DatabaseFactoryLogin.get().getConnection();
                String sqlstr = "UPDATE `accounts` SET `lastactive`=?,`ip`=?,`host`=? WHERE `login`=?";
                pstm = con.prepareStatement(sqlstr);
                pstm.setTimestamp(1, lastactive);
                pstm.setString(2, account.get_ip());
                pstm.setString(3, account.get_mac());
                pstm.setString(4, account.get_login());
                pstm.execute();
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        }
    }

    public void updateCharacterSlot(String loginName, int count) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "UPDATE `accounts` SET `character_slot`=? WHERE `login`=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, count);
            pstm.setString(2, loginName);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updatePwd(String loginName, String newpwd) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "UPDATE `accounts` SET `password`=? WHERE `login`=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, newpwd);
            pstm.setString(2, loginName);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateLan(String loginName, boolean islan) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "UPDATE `accounts` SET `server_no`=? WHERE `login`=?";
            pstm = con.prepareStatement(sqlstr);
            if (islan) {
                pstm.setInt(1, Config.SERVERNO);
            } else {
                pstm.setInt(1, 0);
            }
            pstm.setString(2, loginName);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateLan() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "UPDATE `accounts` SET `server_no`=0";
            pstm = con.prepareStatement(sqlstr);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 更新帳號首儲
     *
     * @param loginName 帳號
     * @param count     擴充數量
     */
    @Override
    public void updateFirstPay(final String loginName, final int count) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            final String sqlstr = "UPDATE `accounts` SET `pay_first`=? WHERE `login`=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, count);
            pstm.setString(2, loginName);
            pstm.execute();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public boolean updaterecharBind(final int char_objId) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            L1Account value = new L1Account();
            con = DatabaseFactory.getInstance().getConnection();
            String sqlstr = "UPDATE characters set CharBind = 0,account_name = ? WHERE objid=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, value.get_login());
            pstm.setInt(2, char_objId);
            pstm.execute();
            return true;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return false;
    }

    public ArrayList<String> loadCharacterItems(final int char_objId) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        final ArrayList<String> list = new ArrayList<>();
        try {
            con = DatabaseFactory.get().getConnection();
            String sqlstr = "SELECT item_name,count,enchantlvl FROM character_items WHERE char_id=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, char_objId);
            rs = pstm.executeQuery();
            while (rs.next()) {
                final String itemName = rs.getString("item_name");
                final int count = rs.getInt("count");
                final int enchantlvl = rs.getInt("enchantlvl");
                final StringBuilder msg = new StringBuilder();
                msg.append(" +");
                msg.append(enchantlvl);
                msg.append(" ");
                msg.append(itemName);
                msg.append("(");
                msg.append(count);
                msg.append(")");
                list.add(msg.toString());
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return list;
    }

    /**
     * vip 等級、時限、最終時間
     *
     */
    @Override
    public void updateVip(final L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            final String sqlstr = "UPDATE `accounts` SET `VipStartTime`=?,`VipEndTime`=?,`VipLevel`=? WHERE `login` = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setTimestamp(1, pc.getVipStartTime());
            pstm.setTimestamp(2, pc.getVipEndTime());
            pstm.setInt(3, pc.getVipLevel());
            pstm.setString(4, pc.getAccountName());
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void updateAccessLevel(String loginName) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            String sqlstr = "UPDATE `accounts` SET `access_level`=0 Where login=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setString(1, loginName);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public void updatefp(String loginName, int fp) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            String sqlfp = "UPDATE `accounts` SET `first_pay`=? WHERE `login`=?";
            pstm = con.prepareStatement(sqlfp);
            pstm.setInt(1, 1);
            pstm.setString(2, loginName);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 成長果實系統(Tam幣)
     */
    public void updatetam(final String loginName, final int tam_point) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            String sqlstr = "UPDATE accounts SET Tam_Point=? WHERE login = ?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, tam_point);
            pstm.setString(2, loginName);
            pstm.executeUpdate();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}