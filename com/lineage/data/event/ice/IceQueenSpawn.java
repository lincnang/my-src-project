package com.lineage.data.event.ice;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 冰之副本召喚類
 *
 * @author sudawei
 */
public class IceQueenSpawn {
    private static final Log _log = LogFactory.getLog(IceQueenSpawn.class);
    private static IceQueenSpawn _instance;

    public static IceQueenSpawn getInstance() {
        if (_instance == null) {
            _instance = new IceQueenSpawn();
        }
        return _instance;
    }

    /**
     * 召喚地圖怪物
     */
    public ArrayList<L1NpcInstance> fillSpawnTable(int mapid, int type) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        // L1Npc l1npc = null;
        // L1NpcInstance field = null;
        ArrayList<L1NpcInstance> list = new ArrayList<>();
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 任務_冰之女副本設置");
            rs = pstm.executeQuery();
            while (rs.next()) {
                if (type == rs.getInt("type")) {
                    int count = rs.getInt("count");
                    int npcid = rs.getInt("npc_id");
                    int locx = rs.getInt("locx");
                    int locy = rs.getInt("locy");
                    int heading = rs.getInt("heading");
                    if (type == 11 || type == 14) {
                        list.add(spawnNpc(locx, locy, mapid, npcid, 1, heading));
                    } else {
                        for (int i = 0; i < count; i++) {
                            list.add(spawnNpc(locx, locy, mapid, npcid, 10, heading));
                        }
                    }
                    // l1npc = NpcTable.get().getTemplate(rs.getInt("npc_id"));
                    // if (l1npc != null) {
                    // try {
                    // int npcId = rs.getInt("npc_id");
                    // field = NpcTable.get().newNpcInstance(npcId);
                    // field.setId(IdFactory.get().nextId());
                    // field.setX(rs.getInt("locx"));
                    // field.setY(rs.getInt("locy"));
                    // field.setMap((short) mapid);
                    // field.setHomeX(field.getX());
                    // field.setHomeY(field.getY());
                    // field.setHeading(rs.getInt("heading"));
                    // field.setLightSize(l1npc.getLightSize());
                    // field.setLightSize(0);
                    // World.get().storeObject(field);
                    // World.get().addVisibleObject(field);
                    // } catch (Exception e) {
                    // _log.error(e.getLocalizedMessage(), e);
                    // }
                    // } else {
                    // l1npc = null;
                    // field = null;
                    // }
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (SecurityException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (IllegalArgumentException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return list;
    }

    /**
     * 召喚NPC
     */
    private L1NpcInstance spawnNpc(int locx, int locy, int mapId, int npcid, int randomRange, int heading) {
        L1Npc l1npc = NpcTable.get().getTemplate(npcid);
        L1NpcInstance field = null;
        if (l1npc == null) {
            _log.error("召喚的NPCID:" + npcid + "不存在");
            return null;
        }
        field = NpcTable.get().newNpcInstance(npcid);
        field.setId(IdFactory.get().nextId());
        field.setMap((short) mapId);
        int tryCount = 0;
        do {
            tryCount++;
            field.setX(locx + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
            field.setY(locy + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
            if (field.getMap().isInMap(field.getLocation()) && field.getMap().isPassable(field.getLocation(), field)) {
                // System.out.println("X坐標："+field.getX()+"，Y坐標："+field.getY()+",坐標狀態："+field.getMap().isPassable(field.getLocation(), field));
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(2);
            } catch (InterruptedException e) {
                // TODO 自動生成的 catch 塊
                // e.printStackTrace();
            }
        } while (tryCount < 50);
        if (tryCount >= 50) {
            // field.getLocation().set(loc);
            field.getLocation().set(locx, locy, mapId);
            // npc.getLocation().forward(_pc.getHeading());
        }
        field.setHomeX(field.getX());
        field.setHomeY(field.getY());
        field.setHeading(heading);
        field.setLightSize(l1npc.getLightSize());
        // field.setLightSize(0);
        // field.Check_Light(null, 0);
        //        L1WorldMap.get().getMap((short) mapId).setPassable(field.getLocation(), false);// 20240815
        World.get().storeObject(field);
        World.get().addVisibleObject(field);
        // if(field instanceof L1MonsterInstance){
        // field.onNpcAI();
        // }
        return field;
    }
}
