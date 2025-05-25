package com.lineage.data.event.redknight;

import com.lineage.DatabaseFactory;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import javolution.util.FastTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

/**
 * 紅騎士訓練副本
 *
 * @author darling
 */
public class RedKnightSpawn {
    private static RedKnightSpawn _instance;
    private static Random _rnd = new Random(System.currentTimeMillis());

    private RedKnightSpawn() {
    }

    public static RedKnightSpawn getInstance() {
        if (_instance == null) {
            _instance = new RedKnightSpawn();
        }
        return _instance;
    }

    public FastTable<L1NpcInstance> fillSpawnTable(int mapid, int type) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        L1Npc l1npc = null;
        L1NpcInstance field = null;
        FastTable<L1NpcInstance> list = new FastTable<L1NpcInstance>();
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM spawnlist_redknight");
            rs = pstm.executeQuery();
            while (rs.next()) {
                if (type != rs.getInt("type")) {
                    continue;
                }
                int npcid = rs.getInt("npc_id");
                int count = 1;
                if (type == 3 || type == 5 || type == 7) {
                    count = 40;
                    if (npcid == 100651 || npcid == 100652) {
                        count = 20;
                    }
                }
                for (int i = 0; i < count; i++) {
                    l1npc = NpcTable.get().getTemplate(npcid);
                    if (l1npc != null) {
                        try {
                            field = NpcTable.get().newNpcInstance(npcid);
                            field.setId(IdFactory.get().nextId());
                            field.setX(rs.getInt("locx"));
                            field.setY(rs.getInt("locy"));
                            field.setMap((short) mapid);
                            int rndX = 1;
                            int rndY = 1;
                            int minx = 1;
                            int miny = 1;
                            if (type == 3) {
                                field.setX(32734);
                                field.setY(32864);
                                minx = 32734;
                                miny = 32864;
                                rndX = 32801 - field.getX();
                                rndY = 32931 - field.getY();
                            } else if (type == 5) {
                                field.setX(32737);
                                field.setY(32958);
                                minx = 32737;
                                miny = 32958;
                                rndX = 32800 - field.getX();
                                rndY = 33020 - field.getY();
                            } else if (type == 7) {
                                field.setX(32738);
                                field.setY(33052);
                                minx = 32738;
                                miny = 33052;
                                rndX = 32795 - field.getX();
                                rndY = 33109 - field.getY();
                            }
                            int tryCount = 0;
                            if (type == 3 || type == 5 || type == 7) {
                                do {
                                    tryCount++;
                                    field.setX(minx + _rnd.nextInt(rndX));
                                    field.setY(miny + _rnd.nextInt(rndY));
                                    if (field.getMap().isInMap(field.getLocation()) && field.getMap().isPassable(field.getLocation())) {
                                        break;
                                    }
                                } while (tryCount < 50);
                            }
                            if (tryCount >= 50) {
                                if (type == 3) {
                                    field.setX(32734 + (rndX / 2));
                                    field.setY(32864 + (rndY / 2));
                                } else if (type == 5) {
                                    field.setX(32766);
                                    field.setY(32995);
                                } else if (type == 7) {
                                    field.setX(32738 + (rndX / 2));
                                    field.setY(33052 + (rndY / 2));
                                } else {
                                    field.setX(rs.getInt("locx"));
                                    field.setY(rs.getInt("locy"));
                                }
                            }
                            if (npcid == 100647) {
                                // field.getMap().setPassable(field.getLocation(),
                                // false);
                                L1DoorInstance f = (L1DoorInstance) field;
                                f.setDirection(4);
                                f.setOpenStatus(ActionCodes.ACTION_Close);
                                f.isPassibleDoor(false);
                                f.setPassable(1);
                            }
                            field.setHomeX(field.getX());
                            field.setHomeY(field.getY());
                            field.setHeading(rs.getInt("heading"));
                            field.setLightSize(l1npc.getLightSize());
                            field.setSpawnLocation(rs.getString("location"));
                            World.get().storeObject(field);
                            World.get().addVisibleObject(field);
                            list.add(field);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return list;
    }
}
