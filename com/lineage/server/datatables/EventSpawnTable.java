package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.L1Spawn;
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

public class EventSpawnTable {
    private static final Log _log = LogFactory.getLog(EventSpawnTable.class);
    private static final Map<Integer, L1Spawn> _spawntable = new HashMap<>();
    private static EventSpawnTable _instance;

    public static EventSpawnTable get() {
        if (_instance == null) {
            _instance = new EventSpawnTable();
        }
        return _instance;
    }

    public static void delete(int npc_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `server_event_spawn` WHERE `npc_templateid`=?");
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
        int spawnCount = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `server_event_spawn`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int eventid = rs.getInt("eventid");
                if (EventTable.get().getTemplate(eventid) != null) {
                    int npcTemplateId = rs.getInt("npc_templateid");
                    L1Npc template1 = NpcTable.get().getTemplate(npcTemplateId);
                    if (template1 == null) {
                        _log.error("召喚NPC編號: " + npcTemplateId + " 不存在資料庫中!(server_event_spawn)");
                        delete(npcTemplateId);
                    } else {
                        int count = rs.getInt("count");
                        if (count != 0) {
                            int group_id = rs.getInt("group_id");
                            int locx1 = rs.getInt("locx1");
                            int locx2 = rs.getInt("locx2");
                            int locy1 = rs.getInt("locy1");
                            int locy2 = rs.getInt("locy2");
                            int mapid = rs.getInt("mapid");
                            int heading = rs.getInt("heading");
                            int respawn_delay = rs.getInt("respawn_delay");
                            int movement_distance = rs.getInt("movement_distance");
                            int spawntype = rs.getInt("avoid_pc");
                            L1Spawn spawnDat = new L1Spawn(template1);
                            spawnDat.setId(id);
                            spawnDat.setAmount(count);
                            spawnDat.setGroupId(group_id);
                            if ((locx2 == 0) && (locy2 == 0)) {
                                spawnDat.setLocX(locx1);
                                spawnDat.setLocY(locy1);
                                spawnDat.setLocX1(0);
                                spawnDat.setLocY1(0);
                                spawnDat.setLocX2(0);
                                spawnDat.setLocY2(0);
                            } else {
                                spawnDat.setLocX(locx1);
                                spawnDat.setLocY(locy1);
                                spawnDat.setLocX1(locx1);
                                spawnDat.setLocY1(locy1);
                                spawnDat.setLocX2(locx2);
                                spawnDat.setLocY2(locy2);
                            }
                            if ((count > 1) && (spawnDat.getLocX1() == 0)) {
                                int range = Math.min(count * 6, 30);
                                spawnDat.setLocX1(spawnDat.getLocX() - range);
                                spawnDat.setLocY1(spawnDat.getLocY() - range);
                                spawnDat.setLocX2(spawnDat.getLocX() + range);
                                spawnDat.setLocY2(spawnDat.getLocY() + range);
                            }
                            if ((locx2 < locx1) && (locx2 != 0)) {
                                _log.error("server_event_spawn : locx2 < locx1: " + id);
                            } else if ((locy2 < locy1) && (locy2 != 0)) {
                                _log.error("server_event_spawn : locy2 < locy1: " + id);
                            } else {
                                spawnDat.setRandomx(0);
                                spawnDat.setRandomy(0);
                                spawnDat.setMinRespawnDelay(respawn_delay);
                                spawnDat.setMovementDistance(movement_distance);
                                spawnDat.setHeading(heading);
                                spawnDat.setMapId((short) mapid);
                                spawnDat.setSpawnType(spawntype);
                                spawnDat.setTime(SpawnTimeTable.getInstance().get(spawnDat.getId()));
                                spawnDat.setName(template1.get_name());
                                spawnDat.init();
                                spawnCount += spawnDat.getAmount();
                                _spawntable.put(spawnDat.getId(), spawnDat);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->召喚EVENT NPC資料數量: " + spawnCount + "(" + timer.get() + "ms)");
    }

    public L1Spawn getTemplate(int Id) {
        return (L1Spawn) _spawntable.get(Id);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.EventSpawnTable JD-Core Version: 0.6.2
 */