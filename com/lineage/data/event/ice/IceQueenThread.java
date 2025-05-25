package com.lineage.data.event.ice;

import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 冰之女王副本 刷怪線程
 *
 * @author sudawei
 */
public class IceQueenThread extends Thread {
    // L1DoorInstance door0;
    private static final Log _log = LogFactory.getLog(IceQueenThread.class);
    short mapId;
    L1PcInstance pc;
    int type = 0;
    ArrayList<L1DoorInstance> doorList = new ArrayList<>();
    ArrayList<L1NpcInstance> bossList = new ArrayList<>();
    ArrayList<L1NpcInstance> npcList = new ArrayList<>();

    public IceQueenThread(int mapId, L1PcInstance pc, int type) {
        this.mapId = (short) mapId;
        this.pc = pc;
        this.type = type;
    }

    public static void deleteIceItem(L1PcInstance _pc) {
        if (_pc != null && (_pc.getMapId() < 2101 || _pc.getMapId() > 2151)) {
            L1ItemInstance[] item = _pc.getInventory().findItemsId(41781);// 火焰之魔杖
            if (item != null) {
                for (L1ItemInstance instance : item) {
                    _pc.getInventory().removeItem(instance);
                }
            }
            item = _pc.getInventory().findItemsId(41782);//神秘的恢復藥水
            if (item != null) {
                for (L1ItemInstance instance : item) {
                    _pc.getInventory().removeItem(instance);
                }
            }
        }
    }

    @Override
    public void run() {
        //System.out.println(this.pc.getName()+"開啟冰女副本");
        // 關卡1的門
        L1DoorInstance door1 = DoorSpawnTable.get().spawnDoor(2101, 6640, 32784, 32818, mapId, 0, 0, false, 32818 - 1, 32818 + 3, 1);
        door1.close();
        // doorList.add(door1);
        // 關卡2的門
        L1DoorInstance door2 = DoorSpawnTable.get().spawnDoor(2101, 6640, 32852, 32806, mapId, 0, 0, false, 32806 - 1, 32806 + 3, 1);
        door2.close();
        // doorList.add(door1);
        // 關卡3的門
        L1DoorInstance door3 = DoorSpawnTable.get().spawnDoor(2101, 6640, 32822, 32855, mapId, 0, 0, false, 32855 - 1, 32855 + 3, 1);
        door3.close();
        // doorList.add(door1);
        // 關卡4的門
        L1DoorInstance door4 = DoorSpawnTable.get().spawnDoor(2101, 6642, 32762, 32916, mapId, 0, 0, false, 32762 - 2, 32762 + 2);
        door4.close();
        // doorList.add(door1);
        // 關卡5的門
        L1DoorInstance door5 = DoorSpawnTable.get().spawnDoor(2101, 6640, 32851, 32921, mapId, 0, 0, false, 32921 - 1, 32921 + 3, 1);
        door5.close();
        // doorList.add(door1);
        // 召喚-象牙塔的秘密間諜
        IceQueenSpawn.getInstance().fillSpawnTable(mapId, 14);
        //System.out.println("冰女副本:召喚史斌");
        // 召喚-史斌
        IceQueenSpawn.getInstance().fillSpawnTable(mapId, 11);
        pc.setIceTime(0);
        L1Teleport.teleport(pc, 32728, 32819, mapId, 5, true);
        pc.startTimeMap();
        sendMsg("冰女副本:召喚關卡1的怪");
        // 召喚關卡1的怪
        npcList.addAll(IceQueenSpawn.getInstance().fillSpawnTable(mapId, 1));
        isKillNpc(npcList);
        door1.open();
        sendMsg("冰女副本:召喚關卡2的怪");
        // 召喚關卡2的怪
        npcList = new ArrayList<>();
        npcList.addAll(IceQueenSpawn.getInstance().fillSpawnTable(mapId, 2));
        isKillNpc(npcList);
        door2.open();
        sendMsg("冰女副本:召喚關卡3的怪");
        // 召喚關卡3的怪
        npcList = new ArrayList<>();
        npcList.addAll(IceQueenSpawn.getInstance().fillSpawnTable(mapId, 3));
        isKillNpc(npcList);
        door3.open();
        sendMsg("冰女副本:召喚關卡4的怪");
        // 召喚關卡4的怪
        npcList = new ArrayList<>();
        npcList.addAll(IceQueenSpawn.getInstance().fillSpawnTable(mapId, 4));
        isKillNpc(npcList);
        door4.open();
        sendMsg("冰女副本:召喚關卡5的怪");
        // 召喚關卡5的怪
        npcList = new ArrayList<>();
        npcList.addAll(IceQueenSpawn.getInstance().fillSpawnTable(mapId, 5));
        if (type == 0) {
            npcList.addAll(IceQueenSpawn.getInstance().fillSpawnTable(mapId, 13));
        } else {
            npcList.addAll(IceQueenSpawn.getInstance().fillSpawnTable(mapId, 12));
        }
        isKillNpc(npcList);
        door5.open();
        //System.out.println(this.pc.getName()+"冰女副本任務結束");
    }

    /**
     * 傳送訊息
     */
    private void sendMsg(String msg) {
        if (pc == null) {
            return;
        }
        if (pc.getMapId() >= 2101 && pc.getMapId() <= 2151) {
            pc.sendPackets(new S_PacketBoxGree(2, msg));
        }
    }

    /**
     * 是否在規定時間內清除怪物
     */
    private void isKillNpc(ArrayList<L1NpcInstance> list) {
        // int count = -1;
        boolean isAllKill = true;
        while (isAllKill) {
            if (!isIceQueen()) {
                isAllKill = false;
                quitIceQueen();
                // return -1;
                return;
            }
            boolean isAllDeath = false;
            for (L1NpcInstance npc : list) {
                if (!npc.isDead()) {
                    isAllDeath = npc.isDead();
                    break;
                }
                isAllDeath = npc.isDead();
            }
            if (isAllDeath) {
                // return count;
                isAllKill = false;
                // return 0;
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1000L);
            } catch (InterruptedException e) {
                // TODO 自動生成的 catch 塊
                // e.printStackTrace();
            }
        }
        quitIceQueen();
        return;
        // return -1;
    }

    /**
     * 判斷玩家是否在噬魂塔副本
     *
     * @return 不在副本中返回false
     * @return 在副本中返回true
     */
    private boolean isIceQueen() {
        if (pc == null) {
            return false;
        }
        if (pc.getOnlineStatus() == 0) {
            pc = null;
            return false;
        }
        if (pc.getMapId() != mapId) {
            return false;
        }
        return true;
    }

    /**
     * 退出冰女副本
     */
    private void quitIceQueen() {
        deleteIceItem(pc);
        if (pc != null && pc.getMapId() == mapId) {
            pc.stopTimeMap();
        }
        World.get().closeMap((int) mapId);
        IceQueenSystem.getInstance().mapStat[mapId - 2101] = false;
        _log.info("冰之女王mapid:" + mapId + "線程結束.");
    }
}
