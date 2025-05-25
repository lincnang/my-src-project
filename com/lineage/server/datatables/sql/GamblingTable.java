package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.GamblingStorage;
import com.lineage.server.templates.L1Gambling;
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

public class GamblingTable implements GamblingStorage {
    private static final Log _log = LogFactory.getLog(GamblingTable.class);
    private static final Map<String, L1Gambling> _gamblingList = new HashMap<String, L1Gambling>();

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_gambling` ORDER BY `id`");
            rs = ps.executeQuery();
            while (rs.next()) {
                L1Gambling gambling = new L1Gambling();
                int id = rs.getInt("id");
                long adena = rs.getLong("adena");
                double rate = rs.getDouble("rate");
                String gamblingno = rs.getString("gamblingno");
                int outcount = rs.getInt("outcount");
                gambling.set_id(id);
                gambling.set_adena(adena);
                gambling.set_rate(rate);
                gambling.set_gamblingno(gamblingno);
                gambling.set_outcount(outcount);
                _gamblingList.put(gamblingno, gambling);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->奇巖賭場紀錄資料數量: " + _gamblingList.size() + "(" + timer.get() + "ms)");
    }

    public L1Gambling getGambling(String key) {
        return (L1Gambling) _gamblingList.get(key);
    }

    public L1Gambling getGambling(int key) {
        for (L1Gambling gambling : _gamblingList.values()) {
            if (gambling.get_id() == key) {
                return gambling;
            }
        }
        return null;
    }

    public void add(L1Gambling gambling) {
        Connection co = null;
        PreparedStatement ps = null;
        int id = gambling.get_id();
        String gamblingno = gambling.get_gamblingno();
        _gamblingList.put(gamblingno, gambling);
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("INSERT INTO `character_gambling` SET `id`=?,`adena`=?,`rate`=?,`gamblingno`=?,`outcount`=?,`r`=?");
            int i = 0;
            ps.setInt(++i, id);
            ps.setLong(++i, gambling.get_adena());
            ps.setDouble(++i, gambling.get_rate());
            ps.setString(++i, gamblingno);
            ps.setInt(++i, gambling.get_outcount());
            ps.setInt(++i, gambling.get_outcount());
            ps.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    public void updateGambling(int id, int outcount) {
        System.out.println("更新賭場紀錄" + id + "-" + outcount);
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("UPDATE `character_gambling` SET `outcount`=? WHERE `id`=?");
            int i = 0;
            ps.setInt(++i, outcount);
            ps.setInt(++i, id);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    public int[] winCount(int npcid) {
        int size = _gamblingList.size();
        int winCount = 0;
        for (L1Gambling gambling : _gamblingList.values()) {
            String no = gambling.get_gamblingno();
            int index = no.indexOf("-");
            int npcidx = Integer.parseInt(no.substring(index + 1));
            if (npcid == npcidx) {
                winCount++;
            }
        }
        return new int[]{size, winCount};
    }

    public int maxId() {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_gambling` ORDER BY `id`");
            rs = ps.executeQuery();
            int id = 0;
            while (rs.next()) {
                id = rs.getInt("id") + 1;
            }
            if (id < 100) {
                id = 100;
            }
            return id;
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        return 100;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.GamblingTable JD-Core Version: 0.6.2
 */