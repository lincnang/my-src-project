/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.eric;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RandomMobTable {
    private static Logger _log = Logger.getLogger(RandomMobTable.class.getName());
    private static RandomMobTable _instance;
    private final Map<Integer, Data> _mobs = Maps.newHashMap();

    private RandomMobTable() {
        loadRandomMobFromDatabase();
    }

    public static RandomMobTable getInstance() {
        if (_instance == null) {
            _instance = new RandomMobTable();
        }
        return _instance;
    }

    private void loadRandomMobFromDatabase() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM eric_random_mob");
            rs = pstm.executeQuery();
            while (rs.next()) {
                Data data = new Data();
                int id = rs.getInt("id");
                data.id = id;
                data.note = rs.getString("note");
                String temp[] = rs.getString("mapId").split(",");
                short i[] = new short[temp.length];
                int loop = 0;
                for (String s : temp) {
                    i[loop] = (short) Integer.parseInt(s);
                    loop++;
                }
                data.mapId = i;
                data.mobId = rs.getInt("mobId");
                data.cont = rs.getInt("cont");
                data.timeSecondToDelete = rs.getInt("timeSecondToKill");
                data.isActive = rs.getBoolean("isActive");
                _mobs.put(new Integer(id), data);
            }
            _log.config("RandomMob " + _mobs.size());
        } catch (SQLException e) {
            e.printStackTrace();
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void startRandomMob() {
        // 招喚
        for (Data data : _mobs.values()) {
            if (data.isActive) {// 假如有啟動
                L1SpawnUtil.spawn(data.id);
            }
        }
    }

    public short getRandomMapId(int RandomMobId) {
        Data data = _mobs.get(RandomMobId);
        if (data == null) {
            return 0;
        }
        int length = _mobs.get(RandomMobId).mapId.length;
        int rand = new Random().nextInt(length);
        return _mobs.get(RandomMobId).mapId[rand];
    }

    public int getRandomMapX(int mapId) {
        int startX = MapsTable.get().getStartX(mapId);
        int endX = MapsTable.get().getEndX(mapId);
        int rand = new Random().nextInt(endX - startX);
        return startX + rand;
    }

    public int getRandomMapY(int mapId) {
        int startY = MapsTable.get().getStartY(mapId);
        int endY = MapsTable.get().getEndY(mapId);
        int rand = new Random().nextInt(endY - startY);
        return startY + rand;
    }

    public String getName(int RandomMobId) {
        Data data = _mobs.get(RandomMobId);
        if (data == null) {
            return "";
        }
        return _mobs.get(RandomMobId).note;
    }

    public int getMobId(int RandomMobId) {
        Data data = _mobs.get(RandomMobId);
        if (data == null) {
            return 0;
        }
        return _mobs.get(RandomMobId).mobId;
    }

    public int getCont(int RandomMobId) {
        Data data = _mobs.get(RandomMobId);
        if (data == null) {
            return 0;
        }
        return _mobs.get(RandomMobId).cont;
    }

    public int getTimeSecondToDelete(int RandomMobId) {
        Data data = _mobs.get(RandomMobId);
        if (data == null) {
            return 0;
        }
        return _mobs.get(RandomMobId).timeSecondToDelete;
    }

    private class Data {
        public int id = 0;
        public String note = "";
        public int mobId = 0;
        public int cont = 0;
        public short mapId[] = {};
        public int timeSecondToDelete = -1;
        public boolean isActive = false;
    }
}
