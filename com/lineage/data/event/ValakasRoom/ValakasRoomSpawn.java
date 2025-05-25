package com.lineage.data.event.ValakasRoom;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 火龍副本召喚讀取
 */
public class ValakasRoomSpawn {  //src022
    private static final Log _log = LogFactory.getLog(ValakasRoomSpawn.class);
    private static ValakasRoomSpawn _instance;

    private ValakasRoomSpawn() {
    }

    public static ValakasRoomSpawn getInstance() {
        if (_instance == null) {
            _instance = new ValakasRoomSpawn();
        }
        return _instance;
    }

    public void fillSpawnTable(int mapid, int type) {
        fillSpawnTable(mapid, type, false);
    }

    /**
     * 召喚怪物
     */
    public CopyOnWriteArrayList<L1NpcInstance> fillSpawnTable(int mapid, int type, boolean RT) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        L1Npc l1npc = null;
        L1NpcInstance field = null;
        CopyOnWriteArrayList<L1NpcInstance> list = null;
        if (RT) {
            list = new CopyOnWriteArrayList<>();
        }
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 任務_火龍窟副本設置");
            rs = pstm.executeQuery();
            while (rs.next()) {
                if (type != rs.getInt("type")) {
                    continue;
                }
                int npcid = rs.getInt("npc_id");
                if (npcid == 4070019) {
                    L1DoorInstance door1 = DoorSpawnTable.get().spawnDoor(npcid, 8307, 32654, 33002, (short) mapid, 0, 1, false, 32994, 33005, 1);
                    //System.out.println("召喚火龍副本的橋1:"+door1.getDoorId());
                    door1.close();
                    if (RT) {
                        list.add(door1);
                    }
                } else if (npcid == 4070020) {
                    L1DoorInstance door1 = DoorSpawnTable.get().spawnDoor(npcid, 8307, 32694, 33054, (short) mapid, 0, 1, false, 33046, 33058, 1);
                    //System.out.println("召喚火龍副本的橋2:"+door1.getDoorId());
                    door1.close();
                    if (RT) {
                        list.add(door1);
                    }
                } else if (npcid == 4070021) {
                    L1DoorInstance door1 = DoorSpawnTable.get().spawnDoor(npcid, 8305, 32736, 33008, (short) mapid, 0, 1, false, 32731, 32743, 0);
                    //System.out.println("召喚火龍副本的橋3:"+door1.getDoorId());
                    door1.close();
                    if (RT) {
                        list.add(door1);
                    }
                } else {
                    l1npc = NpcTable.get().getTemplate(npcid);
                    if (l1npc != null) {
                        try {
                            field = NpcTable.get().newNpcInstance(npcid);
                            field.setId(IdFactory.get().nextId());
                            int locx = rs.getInt("locx");
                            int locy = rs.getInt("locy");
                            field.setMap((short) mapid);
                            if (npcid == 4070009) {// 火龍的紅雷歐
                                field.setX(locx);
                                field.setY(locy);
                                field.setHomeX(32654);
                                field.setHomeY(33001);
                            } else if (npcid == 4070008) {// 火龍的雷歐
                                field.setX(locx);
                                field.setY(locy);
                                field.setHomeX(32662);
                                field.setHomeY(33038);
                            } else if (npcid == 4070010) {// 火龍的火燒雷歐
                                field.setX(locx);
                                field.setY(locy);
                                field.setHomeX(32738);
                                field.setHomeY(33008);
                            } else if (npcid >= 4070015 && npcid <= 4070018) {
                                field.setX(locx);
                                field.setY(locy);
                                if (field instanceof L1DoorInstance) {
                                    L1DoorInstance door = (L1DoorInstance) field;
                                    door.setDoorId(2600);
                                }
                            } else if (npcid == 4070081 || npcid == 4070080 || npcid == 45516) {
                                field.setX(locx);
                                field.setY(locy);
                                field.setHomeX(field.getX());
                                field.setHomeY(field.getY());
                                if (field instanceof L1MonsterInstance) {
                                    L1MonsterInstance mob = (L1MonsterInstance) field;
                                    mob.onNpcAI();
                                }
                            } else if ((npcid >= 46164 && npcid <= 46165) || npcid == 4070014) {
                                field.setX(locx);
                                field.setY(locy);
                                field.setHomeX(field.getX());
                                field.setHomeY(field.getY());
                            } else {
                                field.setX(locx + (int) (Math.random() * 10) - (int) (Math.random() * 10));
                                field.setY(locy + (int) (Math.random() * 10) - (int) (Math.random() * 10));
                                field.setHomeX(field.getX());
                                field.setHomeY(field.getY());
                            }
                            field.setHeading(rs.getInt("heading"));
                            field.setLightSize(l1npc.getLightSize());
                            field.turnOnOffLight();
                            World.get().storeObject(field);
                            World.get().addVisibleObject(field);
                            if (RT) {
                                list.add(field);
                            }
                        } catch (Exception e) {
                            _log.error("火龍窟副本" + type + "召喚怪物出錯!NPCID:" + npcid);
                        }
                    } else {
                        _log.error("火龍窟副本召喚的怪物不存在!NPCID:" + npcid);
                    }
                    l1npc = null;
                }
            }
        } catch (SQLException | IllegalArgumentException | SecurityException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            _log.error(e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return list;
    }
}
