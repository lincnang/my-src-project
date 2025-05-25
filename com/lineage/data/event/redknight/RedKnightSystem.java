package com.lineage.data.event.redknight;

import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.templates.MapData;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 紅騎士訓練副本
 *
 * @author darling
 */
public class RedKnightSystem {
    private static RedKnightSystem _instance = null;
    boolean[] mapStat = new boolean[50];

    public static RedKnightSystem getInstance() {
        if (_instance == null) {
            _instance = new RedKnightSystem();
        }
        return _instance;
    }

    public void load() {
        for (int i = 1; i < 50; i++) {
            L1WorldMap.get().cloneMap(2301, 2301 + i);
            World.get().addMap(2301 + i);
            MapData mapdata = MapsTable.get().getMapData(2301);
            MapsTable.get().setMaps(2301 + i, mapdata);
        }
    }

    /**
     * 人物進入副本
     */
    public void KnightAttachPc(L1PcInstance pc, int choose) {
        for (int i = 0; i < mapStat.length; i++) {
            if (!mapStat[i]) {
                mapStat[i] = true;
                RedKnight thread = new RedKnight(2301 + i, pc);
                GeneralThreadPool.get().execute(thread);
                break;
            }
        }
    }
}
