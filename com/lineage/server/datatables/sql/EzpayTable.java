package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.EzpayStorage;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class EzpayTable implements EzpayStorage {
    private static final Log _log = LogFactory.getLog(EzpayTable.class);

    public Map<Integer, int[]> ezpayInfo(String loginName) {
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Integer, int[]> list = new HashMap<>();
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `shop_user` WHERE `account`=? ORDER BY `id`");
            ps.setString(1, loginName.toLowerCase());
            rs = ps.executeQuery();
            while (rs.next()) {
                int[] value = new int[4];
                int state = rs.getInt("isget");
                if (state == 0) {
                    int key = rs.getInt("id");
                    int p_id = rs.getInt("p_id");
                    int count = rs.getInt("count");
                    int trueMoney = rs.getInt("trueMoney");
                    value[0] = key;
                    value[1] = p_id;
                    value[2] = count;
                    value[3] = trueMoney;
                    list.put(key, value);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
            SQLUtil.close(rs);
        }
        return list;
    }

    public int[] ezpayInfo(String loginName, int id) {
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int[] info = new int[4];
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `shop_user` WHERE `account`=? AND `id`=?");
            ps.setString(1, loginName.toLowerCase());
            ps.setInt(2, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int state = rs.getInt("isget");
                if (state == 0) {
                    int p_id = rs.getInt("p_id");
                    int count = rs.getInt("count");
                    int trueMoney = rs.getInt("trueMoney");
                    info[0] = id;
                    info[1] = p_id;
                    info[2] = count;
                    info[3] = trueMoney;
                    int[] arrayOfInt1 = info;
                    return arrayOfInt1;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
            SQLUtil.close(rs);
        }
        return null;
    }

    private boolean is(String loginName, int id) {
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `shop_user` WHERE `account`=? AND `id`=?");
            ps.setString(1, loginName.toLowerCase());
            ps.setInt(2, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int state = rs.getInt("isget");
                if (state != 0) {
                    return false;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
            SQLUtil.close(rs);
        }
        return true;
    }

    public boolean update(String loginName, int id, String pcclan, String pcname, String ip) {
        if (!is(loginName, id)) {
            return false;
        }
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            Timestamp lastactive = new Timestamp(System.currentTimeMillis());
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `shop_user` SET `isget`=1,`p_name`=?,`play`=?,`time`=?,`ip`=? WHERE `id`=? AND `account`=?");
            pstm.setString(1, pcclan);
            pstm.setString(2, pcname);
            pstm.setTimestamp(3, lastactive);
            pstm.setString(4, ip);
            pstm.setInt(5, id);
            pstm.setString(6, loginName);
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
}
