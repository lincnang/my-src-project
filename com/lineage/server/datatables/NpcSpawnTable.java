package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Spawn;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class NpcSpawnTable {
    private static final Log _log = LogFactory.getLog(NpcSpawnTable.class);
    private static final Map<Integer, L1Spawn> _spawntable = new HashMap<Integer, L1Spawn>();
    private static NpcSpawnTable _instance;

    public static NpcSpawnTable get() {
        if (_instance == null) {
            _instance = new NpcSpawnTable();
        }
        return _instance;
    }

    public static void delete(int npc_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `spawnlist_npc` WHERE `npc_templateid`=?");
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
            pstm = con.prepareStatement("SELECT * FROM `spawnlist_npc`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int npcTemplateid = rs.getInt("npc_templateid");
                L1Npc l1npc = NpcTable.get().getTemplate(npcTemplateid);
                if (l1npc == null) {
                    _log.error("召喚NPC編號: " + npcTemplateid + " 不存在資料庫中!(spawnlist_npc)");
                    delete(npcTemplateid);
                } else {
                    int count = rs.getInt("count");
                    if (count != 0) {
                        L1Spawn l1spawn = new L1Spawn(l1npc);
                        l1spawn.setId(rs.getInt("id"));
                        l1spawn.setAmount(count);
                        l1spawn.setLocX(rs.getInt("locx"));
                        l1spawn.setLocY(rs.getInt("locy"));
                        l1spawn.setRandomx(rs.getInt("randomx"));
                        l1spawn.setRandomy(rs.getInt("randomy"));
                        l1spawn.setLocX1(0);
                        l1spawn.setLocY1(0);
                        l1spawn.setLocX2(0);
                        l1spawn.setLocY2(0);
                        l1spawn.setHeading(rs.getInt("heading"));
                        l1spawn.setMinRespawnDelay(rs.getInt("respawn_delay"));
                        l1spawn.setMapId(rs.getShort("mapid"));
                        l1spawn.setMovementDistance(rs.getInt("movement_distance"));
                        l1spawn.setDeleteTime(rs.getTimestamp("deleteTime") == null ? 0L : rs.getTimestamp("deleteTime").getTime());
                        l1spawn.setName(l1npc.get_name());
                        l1spawn.init();
                        spawnCount += l1spawn.getAmount();
                        _spawntable.put(new Integer(l1spawn.getId()), l1spawn);
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
        _log.info("讀取->召喚NPC資料數量: " + _spawntable.size() + "/" + spawnCount + "(" + timer.get() + "ms)");
    }

    public void storeSpawn(L1PcInstance pc, L1Npc npc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            int count = 1;
            String note = npc.get_name();
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `spawnlist_npc` SET `location`=?,`count`=?,`npc_templateid`=?,`locx`=?,`locy`=?,`heading`=?,`mapid`=?");
            pstm.setString(1, note);
            pstm.setInt(2, count);
            pstm.setInt(3, npc.get_npcId());
            pstm.setInt(4, pc.getX());
            pstm.setInt(5, pc.getY());
            pstm.setInt(6, pc.getHeading());
            pstm.setInt(7, pc.getMapId());
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public final void storeSpawn(int npcId, int locx, int locy, int mapId, int time) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO `spawnlist_npc` SET `location`=?,`count`=1,`npc_templateid`=?,`locx`=?,`locy`=?,`heading`=0,`mapid`=?,`deleteTime`=?");
            pstm.setString(1, "限時NPC物件");
            pstm.setInt(2, npcId);
            pstm.setInt(3, locx);
            pstm.setInt(4, locy);
            pstm.setInt(5, mapId);
            pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis() + time));
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public final void checkMaps() {
        long timeMillis = System.currentTimeMillis();
        for (L1Spawn spawn : _spawntable.values()) {
            if (spawn.getDeleteTime() > 0L) {
                L1NpcInstance npc = spawn.getNpcTemp();
                if (npc != null) {
                    long checkTime = spawn.getDeleteTime() - timeMillis;
                    if ((checkTime > 0L) && (checkTime <= 600000L)) {
                        if (npc.getNpcId() == 70936) {
                            for (L1PcInstance pc : World.get().getAllPlayers()) {
                                if (pc.getMapId() == 1002) {
                                    pc.sendPackets(new S_ServerMessage(1584));
                                }
                            }
                        }
                    } else if (checkTime <= 0L) {
                        if (npc.getNpcId() == 70936) {
                            npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 8));
                        }
                        npc.setCurrentHpDirect(0);
                        npc.setDead(true);
                        npc.setStatus(8);
                        npc.setreSpawn(false);
                        npc.deleteMe();
                        deleteId(spawn.getId());
                        break;
                    }
                }
            }
        }
    }

    private final void deleteId(int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `spawnlist_npc` WHERE `id`=?");
            ps.setInt(1, id);
            ps.execute();
            _spawntable.remove(Integer.valueOf(id));
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public L1Spawn getTemplate(int i) {
        return (L1Spawn) _spawntable.get(Integer.valueOf(i));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.NpcSpawnTable JD-Core Version: 0.6.2
 */