package com.lineage.server.model.map;

import com.lineage.MapReader;
import com.lineage.TextMapReader;
import com.lineage.server.utils.PerformanceTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class L1WorldMap {
    private static final Log _log = LogFactory.getLog(TextMapReader.class);
    private static L1WorldMap _instance;
    private Map<Integer, L1Map> _maps;

    public static L1WorldMap get() {
        if (_instance == null) {
            _instance = new L1WorldMap();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        _log.info("MAP進行數據加載...");
        MapReader in = new TextMapReader();
        _maps = in.read();
        if (_maps == null) {
            _log.error("MAP數據載入異常 maps未建立初始化");
            System.exit(0);
            return;
        }
        _log.info("MAP數據加載完成(" + timer.get() + " ms)");
    }
	/*public L1Map getMap(short mapId) {
		L1Map map = (L1Map) _maps.get(Integer.valueOf(mapId));
		if (map == null) {
			map = L1Map.newNull();
		}
		return map;
	}*/

    /**
     * 指定地圖編號 返回地圖資訊
     *
     * @param mapId 地圖編號
     * @return 地圖資訊
     */
    public L1Map getMap(final short mapId) {
        L1Map map = this._maps.get((int) mapId);
        if (map == null) {
            map = L1Map.newNull();
        }
        return map;
    }

    /**
     * 創建地圖副本
     *
     * @param targetId 目標地圖ID
     * @param newId    新地圖ID
     */
    public void cloneMap(int targetId, int newId) {
        L1Map copymap = null;
        //System.out.println("mapid:"+targetId+"test1:"+((L1Map) this._maps.get(Integer.valueOf(targetId))).isUsableItem());
        copymap = ((L1Map) this._maps.get(targetId)).copyMap(newId);
        this._maps.put(newId, copymap);
        //System.out.println("mapid:"+newId+"test2:"+((L1Map) this._maps.get(Integer.valueOf(newId))).isUsableItem());
    }

    /* 新手任務 */
    public L1Map getMap(int mapId) {
        L1Map map = this._maps.get(mapId);
        if (map != null) {
            return map;
        }
        return map;
    }
}
