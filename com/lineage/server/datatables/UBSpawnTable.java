package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.L1UbPattern;
import com.lineage.server.model.L1UbSpawn;
import com.lineage.server.templates.L1Npc;
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

public class UBSpawnTable {
    private static final Log _log = LogFactory.getLog(UBSpawnTable.class);
    private static final Map<Integer, L1UbSpawn> _spawnTable = new HashMap<>();
    private static UBSpawnTable _instance;

    public static UBSpawnTable getInstance() {
        if (_instance == null) {
            _instance = new UBSpawnTable();
        }
        return _instance;
    }

    public static void delete(int npc_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `spawnlist_ub` WHERE `npc_templateid`=?");
            ps.setInt(1, npc_id);
            ps.execute();
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
            pstm = con.prepareStatement("SELECT * FROM `spawnlist_ub`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int npcid = rs.getInt(6);
                L1Npc npcTemp = NpcTable.get().getTemplate(npcid);
                if (npcTemp == null) {
                    _log.error("召喚NPC編號: " + npcid + " 不存在資料庫中!(spawnlist_ub)");
                    delete(npcid);
                } else {
                    L1UbSpawn spawnDat = new L1UbSpawn();
                    spawnDat.setId(rs.getInt(1));
                    spawnDat.setUbId(rs.getInt(2));
                    spawnDat.setPattern(rs.getInt(3));
                    spawnDat.setGroup(rs.getInt(4));
                    spawnDat.setName(npcTemp.get_name());
                    spawnDat.setNpcTemplateId(npcid);
                    spawnDat.setAmount(rs.getInt(7));
                    spawnDat.setSpawnDelay(rs.getInt(8));
                    spawnDat.setSealCount(rs.getInt(9));
                    _spawnTable.put(spawnDat.getId(), spawnDat);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->無線大賽召喚資料數量: " + _spawnTable.size() + "(" + timer.get() + "ms)");
    }

    public L1UbSpawn getSpawn(int spawnId) {
        return (L1UbSpawn) _spawnTable.get(spawnId);
    }

    public int getMaxPattern(int ubId) {
        int n = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT MAX(pattern) FROM `spawnlist_ub` WHERE `ub_id`=?");
            pstm.setInt(1, ubId);
            rs = pstm.executeQuery();
            if (rs.next()) {
                n = rs.getInt(1);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return n;
    }

    public L1UbPattern getPattern(int ubId, int patternNumer) {
        L1UbPattern pattern = new L1UbPattern();
        for (L1UbSpawn spawn : _spawnTable.values()) {
            if ((spawn.getUbId() == ubId) && (spawn.getPattern() == patternNumer)) {
                pattern.addSpawn(spawn.getGroup(), spawn);
            }
        }
        pattern.freeze();
        return pattern;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.UBSpawnTable JD-Core Version: 0.6.2
 */