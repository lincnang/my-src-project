package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoorSpawnTable {
    private static final Log _log = LogFactory.getLog(DoorSpawnTable.class);
    private static final ArrayList<L1DoorInstance> _doorList = new ArrayList<>();
    private static DoorSpawnTable _instance;

    public static DoorSpawnTable get() {
        if (_instance == null) {
            _instance = new DoorSpawnTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        int i = 0;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `spawnlist_door`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                i++;
                L1Npc l1npc = NpcTable.get().getTemplate(81158);
                if (l1npc != null) {
                    int id = rs.getInt("id");
                    if ((id < 808) || (id > 812)) {
                        L1DoorInstance door = (L1DoorInstance) NpcTable.get().newNpcInstance(l1npc);
                        door.setId(IdFactoryNpc.get().nextId());
                        door.setDoorId(id);
                        door.setGfxId(rs.getInt("gfxid"));
                        int x = rs.getInt("locx");
                        int y = rs.getInt("locy");
                        door.setX(x);
                        door.setY(y);
                        door.setMap(rs.getShort("mapid"));
                        door.setHomeX(x);
                        door.setHomeY(y);
                        door.setDirection(rs.getInt("direction"));
                        door.setLeftEdgeLocation(rs.getInt("left_edge_location"));
                        door.setRightEdgeLocation(rs.getInt("right_edge_location"));
                        int hp = rs.getInt("hp");
                        door.setMaxHp(hp);
                        door.setCurrentHp(hp);
                        door.setKeeperId(rs.getInt("keeper"));
                        World.get().storeObject(door);
                        World.get().addVisibleObject(door);
                        _doorList.add(door);
                    }
                }
            }
        } catch (SQLException | IllegalArgumentException | SecurityException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        SQLUtil.close(rs);
        SQLUtil.close(pstm);
        SQLUtil.close(con);
        _log.info("讀取->門資料數量: " + i + "(" + timer.get() + "ms)");
    }

    public L1DoorInstance[] getDoorList() {
        return (L1DoorInstance[]) _doorList.toArray(new L1DoorInstance[0]);
    }

    /**
     * 返回此地圖的所有門
     */
    public List<L1DoorInstance> getDoorByMap(int mapId) {
        List<L1DoorInstance> doors = new ArrayList<>();
        for (L1DoorInstance door : _doorList) {
            if (door.getMapId() == mapId) {
                doors.add(door);
            }
        }
        return doors;
    }

    /**
     * 移除門
     */
    public void removeDoor(L1DoorInstance door) {
        _doorList.remove(door);
    }

    public void addDoor(L1DoorInstance door) {
        _doorList.add(door);
    }

    public L1DoorInstance spawnDoor(int doorId, int gfxId, int locx, int locy, short mapid, int hp, int keeper, boolean isopening, int left_edge_location, int right_edge_location) {
        return spawnDoor(doorId, gfxId, locx, locy, mapid, hp, keeper, isopening, left_edge_location, right_edge_location, 0);
    }

    /**
     * 召喚門
     *
     * @param doorId              召喚門ID
     * @param locx                召喚門X坐標
     * @param locy                召喚門Y坐標
     * @param mapid               召喚門地圖ID
     * @param hp                  召喚門血量
     * @param keeper              召喚門守護者ID
     * @param isopening           召喚門是否開啟
     * @param left_edge_location  召喚門左側邊界
     * @param right_edge_location 召喚門右側邊界
     * @param direction           召喚門方向
     */
    public L1DoorInstance spawnDoor(int doorId, int gfxId, int locx, int locy, short mapid, int hp, int keeper, boolean isopening, int left_edge_location, int right_edge_location, int direction) {
        for (L1DoorInstance temp : _doorList) {
            if (temp.getMapId() == mapid && temp.getHomeX() == locx && temp.getHomeY() == locy) {
                // System.out.println("temp.getMapId():"+temp.getMapId() + "
                // mapid:"+mapid);
                return temp;
            }
        }
        final L1Npc l1npc = NpcTable.get().getTemplate(81158);
        L1DoorInstance door = (L1DoorInstance) NpcTable.get().newNpcInstance(l1npc);
        door.setId(IdFactoryNpc.get().nextId());
        door.setDoorId(doorId);
        door.setGfxId(gfxId);
        // int x = rs.getInt("locx");
        // int y = rs.getInt("locy");
        door.setX(locx);
        door.setY(locy);
        door.setMap(mapid);
        door.setHomeX(locx);
        door.setHomeY(locy);
        door.setDirection(direction);
        door.setLeftEdgeLocation(left_edge_location);
        door.setRightEdgeLocation(right_edge_location);
        // int hp = rs.getInt("hp");
        door.setMaxHp(hp);
        door.setCurrentHp(hp);
        door.setKeeperId(keeper);
        World.get().storeObject(door);
        World.get().addVisibleObject(door);
        _doorList.add(door);
        return door;
    }
}
