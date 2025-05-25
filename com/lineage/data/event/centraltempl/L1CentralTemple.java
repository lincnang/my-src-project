package com.lineage.data.event.centraltempl;

import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1V1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.templates.MapData;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 中央寺廟副本
 *
 * @author sudawei
 */
public class L1CentralTemple {
    private static L1CentralTemple _centralTemple;
    boolean[] mapStat = new boolean[50];

    private L1CentralTemple() {
        //L1V1Map map=(L1V1Map)L1WorldMap.get().getMap((short)1936);
        int[] mapid = {1936, 1937, 1938, 1939, 1940, 1941, 1942, 1943, 1944, 1945};
        for (int i = 0; i < mapid.length; i++) {
            L1V1Map map = (L1V1Map) L1WorldMap.get().getMap((short) mapid[i]);
            //一排3個怪
            for (int x = 32799; x <= 32800; x++) {
                for (int y = 32843; y <= 32851; y++) {
                    map.setTile(x, y, 15);
                }
            }
            for (int x = 32811; x <= 32817; x++) {
                for (int y = 32861; y <= 32862; y++) {
                    map.setTile(x, y, 15);
                }
            }
            for (int x = 32800; x <= 32801; x++) {
                for (int y = 32874; y <= 32879; y++) {
                    map.setTile(x, y, 15);
                }
            }
            for (int x = 32784; x <= 32788; x++) {
                for (int y = 32860; y <= 32861; y++) {
                    map.setTile(x, y, 15);
                }
            }
        }
    }

    public static L1CentralTemple get() {
        if (_centralTemple == null) {
            _centralTemple = new L1CentralTemple();
        }
        return _centralTemple;
    }

    public void load() {
        for (int i = 1; i < 50; i++) {
            //L1Map clone=map.newNull();
            L1WorldMap.get().cloneMap(1936, 1936 + i);
            World.get().addMap(1936 + i);
            MapData mapdata = MapsTable.get().getMapData(1936);
            MapsTable.get().setMaps(1936 + i, mapdata);
        }
    }

    /**
     * 啟動中央寺院線程-玩家加入線程
     */
    public void centralTemplStart(L1PcInstance pc) {
        for (int i = 0; i < mapStat.length; i++) {
            if (!mapStat[i]) {
                mapStat[i] = true;
                CentralTemplThread thread = new CentralTemplThread(1936 + i, pc);
                GeneralThreadPool.get().execute(thread);
                return;
            }
        }
    }
}
