package com.add.BigHot;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.TimeInform;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MySqlBigHotblingStorage implements BigHotblingStorage {
    private final Map<Integer, L1BigHotbling> _BigHotbling = new ConcurrentHashMap<>();

    public void create(int id, String number, int totalPrice, int money1, int count, int money2, int count1, int money3, int count2, int count3) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            L1BigHotbling BigHot = new L1BigHotbling();
            BigHot.set_id(id);
            BigHot.set_number(number);
            BigHot.set_totalPrice(totalPrice);
            BigHot.set_money1(money1);
            BigHot.set_count(count);
            BigHot.set_money2(money2);
            BigHot.set_count1(count1);
            BigHot.set_money3(money3);
            BigHot.set_count2(count2);
            BigHot.set_count3(count3);
            this._BigHotbling.put(id, BigHot);
            con = DatabaseFactory.get().getConnection();
            String sqlstr = "INSERT INTO `race_bighotbling` SET `id`=?,`number`=?,`totalPrice`=?,`money1`=?,`count`=?,`money2`=?,`count1`=?,`money3`=?,`count2`=?,`count3`=?,`time`=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, BigHot.get_id());
            pstm.setString(2, BigHot.get_number());
            pstm.setInt(3, BigHot.get_totalPrice());
            pstm.setInt(4, BigHot.get_money1());
            pstm.setInt(5, BigHot.get_count());
            pstm.setInt(6, BigHot.get_money2());
            pstm.setInt(7, BigHot.get_count1());
            pstm.setInt(8, BigHot.get_money3());
            pstm.setInt(9, BigHot.get_count2());
            pstm.setInt(10, BigHot.get_count3());
            String time = TimeInform.time().getNowTime_Standard();
            pstm.setString(11, time);
            pstm.execute();
        } catch (Exception e) {
            e.getLocalizedMessage();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void load() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        L1BigHotbling BigHot = null;
        try {
            con = DatabaseFactory.get().getConnection();
            String sqlstr = "SELECT * FROM `race_BigHotbling`";
            pstm = con.prepareStatement(sqlstr);
            rs = pstm.executeQuery();
            while (rs.next()) {
                BigHot = new L1BigHotbling();
                int id = rs.getInt("id");
                BigHot.set_id(id);
                BigHot.set_number(rs.getString("number"));
                BigHot.set_totalPrice(rs.getInt("totalPrice"));
                BigHot.set_money1(rs.getInt("money1"));
                BigHot.set_count(rs.getInt("count"));
                BigHot.set_money2(rs.getInt("money2"));
                BigHot.set_count1(rs.getInt("count1"));
                BigHot.set_money3(rs.getInt("money3"));
                BigHot.set_count2(rs.getInt("count2"));
                BigHot.set_count3(rs.getInt("count3"));
                this._BigHotbling.put(id, BigHot);
            }
        } catch (SQLException e) {
            e.getLocalizedMessage();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public L1BigHotbling[] getBigHotblingList() {
        return (L1BigHotbling[]) this._BigHotbling.values().toArray(new L1BigHotbling[0]);
    }

    public L1BigHotbling getBigHotbling(int id) {
        return (L1BigHotbling) this._BigHotbling.get(id);
    }
}
