package com.lineage.data.event.ice;

import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.templates.MapData;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 冰之女王副本
 *
 * @author sudawei
 */
public class IceQueenSystem {
    private static IceQueenSystem _instance = null;
    //private static int NumberOfIceQueenInstance = 100;
    boolean[] mapStat = new boolean[50];

    public static IceQueenSystem getInstance() {
        if (_instance == null) {
            _instance = new IceQueenSystem();
        }
        return _instance;
    }

    public void load() {
        // for (int n = 0; n < NumberOfIceQueenInstance; n++) {
        // _subinstance[n] = new IceQueenInstance(2101 + n);
        // }
        for (int i = 1; i < 50; i++) {
            // L1Map clone=map.newNull();
            L1WorldMap.get().cloneMap(2101, 2101 + i);
            World.get().addMap(2101 + i);
            MapData mapdata = MapsTable.get().getMapData(2101);
            MapsTable.get().setMaps(2101 + i, mapdata);
        }
    }
    //private static IceQueenInstance[] _subinstance = new IceQueenInstance[NumberOfIceQueenInstance];
    //	public List<IceQueenInstance> getAllIceInstance() {
    //		List<IceQueenInstance> list = new ArrayList();
    //		for (int n = 0; n < NumberOfIceQueenInstance; n++) {
    //			list.add(_subinstance[n]);
    //		}
    //		return list;
    //	}
    //
    //	public IceQueenInstance getIdleInstance() {
    //		for (int n = 0; n < NumberOfIceQueenInstance; n++) {
    //			if (_subinstance[n].isIdle() == true) {
    //				return _subinstance[n];
    //			}
    //		}
    //		return null;
    //	}

    /**
     * 人物進入副本
     */
    public void AttachPc(L1PcInstance pc, int choose) {
        //		if (pc != null) {
        //			IceQueenInstance iceQueenInstance = getIdleInstance();
        //			if (iceQueenInstance != null) {
        //				if (pc.getIceQueenInstance() != null) {
        //					pc.getIceQueenInstance().Detach();
        //					pc.setIceQueenInstance(null);
        //				}
        //				return iceQueenInstance.Attach(pc, choose);
        //			}
        //			pc.sendPackets(new S_SystemMessage("暫時沒有可用的副本"));
        //		}
        //		return false;
        for (int i = 0; i < mapStat.length; i++) {
            if (!mapStat[i]) {
                mapStat[i] = true;
                IceQueenThread thread = new IceQueenThread(2101 + i, pc, choose);
                //pc.setTempThread(thread);
                GeneralThreadPool.get().execute(thread);
                break;
            }
        }
    }
}
