package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.CastleStorage;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CastleTable implements CastleStorage {
    private static final Log _log = LogFactory.getLog(CastleTable.class);
    private static final Map<Integer, L1Castle> _castles = new HashMap<Integer, L1Castle>();

    private Calendar timestampToCalendar(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        return cal;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `server_castle`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int castle_id = rs.getInt("castle_id");
                String name = rs.getString("name");
                Calendar war_time = timestampToCalendar(rs.getTimestamp("war_time"));
                int tax_rate = rs.getInt("tax_rate");
                long public_money = rs.getLong("public_money");
                L1Castle castle = new L1Castle(castle_id, name);
                castle.setWarTime(war_time);
                castle.setTaxRate(tax_rate);
                castle.setPublicMoney(public_money);
                _castles.put(Integer.valueOf(castle_id), castle);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->城堡資料數量: " + _castles.size() + "(" + timer.get() + "ms)");
    }

    public Map<Integer, L1Castle> getCastleMap() {
        return _castles;
    }

    public L1Castle[] getCastleTableList() {
        return (L1Castle[]) _castles.values().toArray(new L1Castle[_castles.size()]);
    }

    public L1Castle getCastleTable(int id) {
        return (L1Castle) _castles.get(Integer.valueOf(id));
    }

    public void updateCastle(L1Castle castle) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `server_castle` SET `name`=?,`war_time`=?,`tax_rate`=?,`public_money`=? WHERE `castle_id`=?");
            int i = 0;
            pstm.setString(++i, castle.getName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String fm = sdf.format(castle.getWarTime().getTime());
            pstm.setString(++i, fm);
            pstm.setInt(++i, castle.getTaxRate());
            pstm.setLong(++i, castle.getPublicMoney());
            pstm.setInt(++i, castle.getId());
            pstm.execute();
            _castles.put(Integer.valueOf(castle.getId()), castle);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.CastleTable JD-Core Version: 0.6.2
 */