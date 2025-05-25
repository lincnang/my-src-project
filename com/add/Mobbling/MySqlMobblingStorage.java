package com.add.Mobbling;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.TimeInform;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MySqlMobblingStorage implements MobblingStorage {
    private final Map<Integer, L1Mobbling> _Mobbling = new ConcurrentHashMap<>();

    public void create(int id, int npcid, double rate, int totalPrice) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            L1Mobbling Mob = new L1Mobbling();
            Mob.set_id(id);
            Mob.set_npcid(npcid);
            Mob.set_rate(rate);
            this._Mobbling.put(id, Mob);
            con = DatabaseFactory.get().getConnection();
            String sqlstr = "INSERT INTO `race_mobbling` SET `id`=?,`npcid`=?,`rate`=?,`time`=?,`totalPrice`=?";
            pstm = con.prepareStatement(sqlstr);
            pstm.setInt(1, Mob.get_id());
            pstm.setInt(2, Mob.get_npcid());
            pstm.setDouble(3, Mob.get_rate());
            String time = TimeInform.time().getNowTime_Standard();
            pstm.setString(4, time);
            pstm.setInt(5, totalPrice);
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
        L1Mobbling Mob = null;
        try {
            con = DatabaseFactory.get().getConnection();
            String sqlstr = "SELECT * FROM `race_Mobbling`";
            pstm = con.prepareStatement(sqlstr);
            rs = pstm.executeQuery();
            while (rs.next()) {
                Mob = new L1Mobbling();
                int id = rs.getInt("id");
                Mob.set_id(id);
                Mob.set_npcid(rs.getInt("npcid"));
                Mob.set_rate(rs.getDouble("rate"));
                this._Mobbling.put(id, Mob);
            }
        } catch (SQLException e) {
            e.getLocalizedMessage();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public L1Mobbling[] getMobblingList() {
        return ((L1Mobbling[]) this._Mobbling.values().toArray(new L1Mobbling[0]));
    }

    public L1Mobbling getMobbling(int id) {
        return ((L1Mobbling) this._Mobbling.get(id));
    }
}