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
        PerformanceTimer timer = new PerformanceTimer(); // 建立計時器，用來記錄讀取時間
        Connection con = null; // 資料庫連線物件
        PreparedStatement pstm = null; // 預處理 SQL 的物件
        ResultSet rs = null; // 查詢結果集物件
        try {
            con = DatabaseFactory.get().getConnection(); // 取得資料庫連線
            pstm = con.prepareStatement("SELECT * FROM `mapids`"); // 準備 SQL 查詢，讀取 mapids 資料表
            for (rs = pstm.executeQuery(); rs.next(); ) { // 執行查詢並逐筆讀取結果
                MapData data = new MapData(); // 建立一個新的 MapData 物件，用來存放每一筆資料

                // 從資料表中讀取各欄位並存入 data 物件
                int mapId = rs.getInt("mapid");        // 地圖 ID
                data.mapId = mapId;
                data.locationname = rs.getString("locationname"); // 地圖名稱
                data.startX = rs.getInt("startX");     // X 起始座標
                data.endX = rs.getInt("endX");         // X 結束座標
                data.startY = rs.getInt("startY");     // Y 起始座標
                data.endY = rs.getInt("endY");         // Y 結束座標
                data.monster_amount = rs.getDouble("monster_amount"); // 怪物數量倍率
                data.dropRate = rs.getDouble("drop_rate");           // 掉寶率倍率
                data.isUnderwater = rs.getBoolean("underwater");     // 是否為水下地圖
                data.markable = rs.getBoolean("markable");           // 是否允許記憶傳送點
                data.teleportable = rs.getBoolean("teleportable");   // 是否允許使用傳送
                data.escapable = rs.getBoolean("escapable");         // 是否允許使用回卷 (ESC)
                data.isUseResurrection = rs.getBoolean("resurrection"); // 是否允許復活
                data.isUsePainwand = rs.getBoolean("painwand");         // 是否允許使用痛苦魔杖
                data.isEnabledDeathPenalty = rs.getBoolean("penalty");  // 是否啟用死亡懲罰
                data.isTakePets = rs.getBoolean("take_pets");           // 是否允許帶寵物
                data.isRecallPets = rs.getBoolean("recall_pets");       // 是否允許召回寵物
                data.isUsableItem = rs.getBoolean("usable_item");       // 是否允許使用道具
                data.isUsableSkill = rs.getBoolean("usable_skill");     // 是否允許使用技能
                data.maptime = rs.getInt("maptime");                    // 地圖限制時間 (秒數)

                // 自訂欄位：可否開商店
                data.isUsableShop = rs.getInt("usable_shop"); // 是否允許開設商店 (int，可能代表不同模式)

                // 自訂欄位：是否允許掛機
                data.isAutoBot = rs.getBoolean("是否掛機"); // TRUE=允許掛機

                // 自訂欄位：副本地圖 ID
                data.CopyMapId = rs.getInt("CopyMapId");

                // 自訂欄位：是否允許「穿雲劍」技能
                data.chuanyunjian = rs.getBoolean("是否穿雲");

                // 最後把讀取到的這筆地圖資料存入 _maps 集合
                _maps.put(mapId, data);
            }
        } catch (SQLException e) {
            // 發生 SQL 錯誤就記錄錯誤訊息
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            // 關閉資源，避免記憶體/連線洩漏
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        // 印出載入完成訊息：地圖數量 + 耗時
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
