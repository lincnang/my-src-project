package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.MapData;
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

public final class MapsTable {
    private static final Log _log = LogFactory.getLog(MapsTable.class);
    private static final Map<Integer, MapData> _maps = new HashMap<>();
    private static final Map<Object, Object> _mapName = new HashMap<>();
    private static MapsTable _instance;

    public static MapsTable get() {
        if (_instance == null) {
            _instance = new MapsTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `mapids`");
            for (rs = pstm.executeQuery(); rs.next(); ) {//src015
                MapData data = new MapData();
                int mapId = rs.getInt("mapid");
                data.mapId = mapId;
                data.locationname = rs.getString("locationname");
                data.startX = rs.getInt("startX");
                data.endX = rs.getInt("endX");
                data.startY = rs.getInt("startY");
                data.endY = rs.getInt("endY");
                data.monster_amount = rs.getDouble("monster_amount");
                data.dropRate = rs.getDouble("drop_rate");
                data.isUnderwater = rs.getBoolean("underwater");
                data.markable = rs.getBoolean("markable");
                data.teleportable = rs.getBoolean("teleportable");
                data.escapable = rs.getBoolean("escapable");
                data.isUseResurrection = rs.getBoolean("resurrection");
                data.isUsePainwand = rs.getBoolean("painwand");
                data.isEnabledDeathPenalty = rs.getBoolean("penalty");
                data.isTakePets = rs.getBoolean("take_pets");
                data.isRecallPets = rs.getBoolean("recall_pets");
                data.isUsableItem = rs.getBoolean("usable_item");
                data.isUsableSkill = rs.getBoolean("usable_skill");
                data.maptime = rs.getInt("maptime");
                // 該地圖是否可擺設商店 by terry0412
                data.isUsableShop = rs.getInt("usable_shop");
                data.isAutoBot = rs.getBoolean("是否掛機");// 此地圖是否允許掛機
                data.CopyMapId = rs.getInt("CopyMapId");
                data.chuanyunjian = rs.getBoolean("是否穿雲");
                _maps.put(mapId, data);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->地圖設置資料數量: " + _maps.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 取回計時地圖最大可用時間(分)
     *
     */
    public int getMapTime(final int mapId) {
        final MapData map = _maps.get(mapId);
        if (map == null) {
            return 0;
        }
        return _maps.get(mapId).maptime;
    }

    /**
     * 是否為計時地圖
     *
     */
    public boolean isTimingMap(final int mapId) {
        final MapData map = _maps.get(mapId);
        if (map == null) {
            return false;
        }
        if (map.maptime > 0) {
            return true;
        }
        return false;
    }

    public Map<Integer, MapData> getMaps() {
        return _maps;
    }

    public MapData getMapData(final int mapId) {
        final MapData map = _maps.get(mapId);
        return map;
    }

    public void setMaps(int mapId, MapData mapdata) {
        _maps.put(mapId, mapdata);
    }

    public int getStartX(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return 0;
        }
        return ((MapData) _maps.get(mapId)).startX;
    }

    public int getEndX(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return 0;
        }
        return ((MapData) _maps.get(mapId)).endX;
    }

    public int getStartY(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return 0;
        }
        return ((MapData) _maps.get(mapId)).startY;
    }

    public int getEndY(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return 0;
        }
        return ((MapData) _maps.get(mapId)).endY;
    }

    public double getMonsterAmount(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return 0.0D;
        }
        return map.monster_amount;
    }

    public double getDropRate(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return 0.0D;
        }
        return map.dropRate;
    }

    public boolean isUnderwater(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).isUnderwater;
    }

    public boolean isMarkable(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).markable;
    }

    public boolean isTeleportable(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).teleportable;
    }

    public boolean isEscapable(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).escapable;
    }

    public boolean isUseResurrection(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).isUseResurrection;
    }

    public boolean isUsePainwand(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).isUsePainwand;
    }

    public boolean isEnabledDeathPenalty(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).isEnabledDeathPenalty;
    }

    public boolean isTakePets(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).isTakePets;
    }

    public boolean isRecallPets(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).isRecallPets;
    }

    public boolean isUsableItem(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return ((MapData) _maps.get(mapId)).isUsableItem;
    }

    public boolean isUsableSkill(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return _maps.get(mapId).isUsableSkill;
    }

    /**
     * 該地圖是否可擺設商店 by terry0412
     *
     */
    public int isUsableShop(final int mapId) {
        final MapData map = _maps.get(mapId);
        if (map == null) {
            return 0;
        }
        return _maps.get(mapId).isUsableShop;
    }

    /**
     * 該地圖是否可挂机
     *
     */
    public boolean isAutoBot(int mapId) {
        MapData map = (MapData) _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return _maps.get(mapId).isAutoBot;
    }

    /**
     * 虛擬地圖 把複製的地圖這個欄位輸入官方原有的地圖編號即可 其他官方原本地圖預設-1
     *
     */
    public int getCopyMapId(int mapId) {
        final MapData map = _maps.get(mapId);
        if (map == null) {
            return -1;
        }
        return _maps.get(mapId).CopyMapId;
    }

    public String locationname(int mapId) {
        MapData map = _maps.get(mapId);
        if (map == null) {
            return null;
        }
        return _maps.get(mapId).locationname;
    }

    /**
     * 挖礦使用
     *
     * @param mapId 調べるマップのマップID
     * @return スキルを使用できるならばtrue
     */
    public String getMapName(int mapId) {
        if (_mapName.containsKey(mapId)) {
            return (String) _mapName.get(mapId);
        } else {
            return null;
        }
    }

    public boolean chuanyunjian(final int mapId) {
        final MapData map = _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return _maps.get(mapId).chuanyunjian;
    }
}
