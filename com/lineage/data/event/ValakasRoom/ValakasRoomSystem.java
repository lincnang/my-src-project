package com.lineage.data.event.ValakasRoom;

import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.MapData;
import com.lineage.server.world.World;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 火龍副本地圖管理
 */
public class ValakasRoomSystem {
    private static ValakasRoomSystem _instance;
    /**
     * 準備副本線程列表
     */
    private final Map<Integer, ValakasReady> _readylist = new ConcurrentHashMap<Integer, ValakasReady>();
    private final Map<Integer, ValakasStart> _startlist = new ConcurrentHashMap<Integer, ValakasStart>();

    public static ValakasRoomSystem getInstance() {
        if (_instance == null) {
            _instance = new ValakasRoomSystem();
        }
        return _instance;
    }
	/*public void load() {
		// for (int n = 0; n < NumberOfIceQueenInstance; n++) {
		// _subinstance[n] = new IceQueenInstance(2101 + n);
		// }
		for (int i = 2600; i < 2698; i++) {
			// L1Map clone=map.newNull();
			L1WorldMap.get().cloneMap(2600, 2600 + i);
			World.get().addMap(2600 + i);
		}
		for (int i = 2699; i < 2797; i++) {
			// L1Map clone=map.newNull();
			L1WorldMap.get().cloneMap(2699, 2699 + i);
			World.get().addMap(2699 + i);
		}
	}*/

    /**
     * 刪除任務道具
     */
    public static void deleteIceItem(L1PcInstance _pc) {
        if (_pc != null && (_pc.getMapId() < 2600 || _pc.getMapId() > 2698)) {
            L1ItemInstance[] item = _pc.getInventory().findItemsId(5010);
            if (item != null && item.length > 0) {
                for (int i = 0; i < item.length; i++) {
                    _pc.getInventory().removeItem(item[i]);
                }
            }
        }
    }

    /**
     * 準備開始火龍副本
     */
    public void startReady(L1PcInstance pc) {
        if (countReadyRaid() >= 99) {
            pc.sendPackets(new S_SystemMessage("火龍副本地圖人數已滿，請稍後再試."));
            return;
        }
        int mapid = blankMapId_ready();
        //宣告線程設置NPC
        ValakasReady ar = new ValakasReady(mapid, pc);
        _readylist.put(mapid, ar);
        if (mapid != 2699) {
            //複製地圖
            L1WorldMap.get().cloneMap(2699, mapid);
            //世界加入地圖
            World.get().addMap(mapid);
            MapData mapdata = MapsTable.get().getMapData(2699);
            MapsTable.get().setMaps(mapid, mapdata);
        }
        //是否在接任務地圖中
        L1Teleport.teleport(pc, 32624, 33059, (short) mapid, 5, false);
        pc.ValakasStatus = 0;
        System.out.println(pc.getName() + " [傳送進入火窟副本接任務地圖] mapId:" + mapid);
        ar.BasicNpcList = ValakasRoomSpawn.getInstance().fillSpawnTable(mapid, 0, true);
        //List加入map及線程
        _readylist.put(mapid, ar);
        //啟動線程
        ar.Start();
    }

    /**
     * 正式開始火龍副本
     */
    public void startRaid(L1PcInstance pc) {
        if (countStartRaid() >= 99) {
            pc.sendPackets(new S_SystemMessage("火龍副本地圖人數已滿，請稍後再試."));
            return;
        }
        //裝上死劍並變身
        pc.getInventory().takeoffWeaponFor2600();
        int mapid = blankMapId_start();
        //宣告線程設置NPC
        ValakasStart ar = new ValakasStart(mapid, pc);
        _startlist.put(mapid, ar);
        if (mapid != 2600) {
            //複製地圖
            L1WorldMap.get().cloneMap(2600, mapid);
            //世界加入地圖
            World.get().addMap(mapid);
            MapData mapdata = MapsTable.get().getMapData(2600);
            MapsTable.get().setMaps(mapid, mapdata);
        }
        //是否在主要任務地圖中
        L1Teleport.teleport(pc, 32624, 33059, (short) mapid, 5, false);
        pc.ValakasStatus = 1;
        System.out.println(pc.getName() + " [傳送進入火窟副本主要任務地圖] mapId:" + mapid);
        int rnd = new Random().nextInt(3);
        //召換橋
        ar.BasicNpcList = ValakasRoomSpawn.getInstance().fillSpawnTable(mapid, 2, true);
        //隨機產生Boss
        if (rnd == 0) {
            //巴拉卡斯
            ar.BossList = ValakasRoomSpawn.getInstance().fillSpawnTable(mapid, 1000, true);
            System.out.println(pc.getName() + " [火窟副本Boss]:巴拉卡斯");
        } else if (rnd == 1) {
            //不死鳥
            ar.BossList = ValakasRoomSpawn.getInstance().fillSpawnTable(mapid, 1001, true);
            System.out.println(pc.getName() + " [火窟副本Boss]:不死鳥");
        } else {
            //伊弗利特
            ar.BossList = ValakasRoomSpawn.getInstance().fillSpawnTable(mapid, 1002, true);
            System.out.println(pc.getName() + " [火窟副本Boss]:伊弗利特");
        }
        //List加入map及線程
        _startlist.put(mapid, ar);
        //啟動線程
        ar.Start();
    }

    /**
     * 返回火龍窟副本準備地圖ID
     *
     * @return 2699~2797 共99張地圖
     */
    public int blankMapId_ready() {
        if (_readylist.size() == 0) {
            return 2699;
        }
        for (int i = 2699; i < 2797; i++) {
            ValakasReady h = _readylist.get(i);
            if (h == null) {
                System.out.println("i:" + i);
                return i;
            }
        }
        return 2797;
    }

    /**
     * 返回火龍窟副本地圖ID
     *
     * @return 2600~2698 共99張地圖;
     */
    public int blankMapId_start() {
        if (_startlist.size() == 0) {
            return 2600;
        }
        for (int i = 2600; i < 2698; i++) {
            ValakasStart h = _startlist.get(i);
            if (h == null) {
                System.out.println("i:" + i);
                return i;
            }
        }
        return 2698;
    }

    /**
     * 刪除準備線程
     */
    public void removeReady(int id) {
        System.out.println("火龍副本接任務地圖刪除:" + id);
        _readylist.remove(id);
    }

    /**
     * 刪除火龍窟副本線程
     */
    public void removeStart(int id) {
        System.out.println("火龍副本主要任務地圖刪除:" + id);
        _startlist.remove(id);
    }

    /**
     * 取得接任務地圖人數
     */
    public int countReadyRaid() {
        return _readylist.size();
    }

    /**
     * 取得主要任務地圖人數
     */
    public int countStartRaid() {
        return _startlist.size();
    }
}
