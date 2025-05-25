package com.lineage.data.event.ValakasRoom;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NpcChatPacket;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 火龍窟 接任務地圖 線程管理
 */
public class ValakasReady implements Runnable {
    private static final int READY_START = 1;
    private static final int WAIT_RAID = 2;
    private static final int END = 3;
    /**
     * 地圖所有怪物
     */
    public CopyOnWriteArrayList<L1NpcInstance> BasicNpcList;
    private short _map;
    private int stage = 1;
    private L1NpcInstance death;
    private L1PcInstance pc;
    private boolean Running = true;

    public ValakasReady(int id, L1PcInstance pc) {
        _map = (short) id;
        this.pc = pc;
    }

    @Override
    public void run() {
        setting();
        int i = 0;
        while (Running) {
            try {
                Sleep(300);
                checkPc();
                //火龍窟副本狀態 若不等於0 則直接關閉副本
                if (pc.ValakasStatus != 0) {
                    stage = END;
                }
                switch (stage) {
                    case READY_START:
                        TimeUnit.MILLISECONDS.sleep(2000);
                        pc.sendPackets(new S_NpcChatPacket(death, "$18861"));
                        TimeUnit.MILLISECONDS.sleep(3000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_NpcChatPacket(pc, "$18862"));
                        }
                        TimeUnit.MILLISECONDS.sleep(3000);
                        pc.sendPackets(new S_NpcChatPacket(death, "$18863"));
                        TimeUnit.MILLISECONDS.sleep(3000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_NpcChatPacket(pc, "$18864"));
                        }
                        TimeUnit.MILLISECONDS.sleep(3000);
                        pc.sendPackets(new S_NpcChatPacket(death, "$18865"));
                        TimeUnit.MILLISECONDS.sleep(3000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_NpcChatPacket(pc, "$18866"));
                        }
                        TimeUnit.MILLISECONDS.sleep(3000);
                        pc.sendPackets(new S_NpcChatPacket(death, "$18867"));
                        TimeUnit.MILLISECONDS.sleep(3000);
                        pc.sendPackets(new S_NpcChatPacket(death, "$18868"));
                        stage = WAIT_RAID;
                        break;
                    case WAIT_RAID:
                        if (i >= 60) {
                            if (_map == pc.getMapId()) {
                                pc.sendPackets(new S_ServerMessage("閒置時間過長,副本強制關閉"));
                                System.out.println(pc.getName() + " 閒置時間過長,副本強制關閉 mapId:" + _map);
                                stage = END;
                            } else if (pc.ValakasStatus == 1) {//已進入主要任務地圖
                                endRaid();
                            }
                        } else {
                            i++;
                        }
                        break;
                    case END:
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1478));
                        }
                        TimeUnit.MILLISECONDS.sleep(5000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1480));
                        }
                        TimeUnit.MILLISECONDS.sleep(1000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1481));
                        }
                        TimeUnit.MILLISECONDS.sleep(1000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1482));
                        }
                        TimeUnit.MILLISECONDS.sleep(1000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1483));
                        }
                        TimeUnit.MILLISECONDS.sleep(1000);
                        if (pc.getMapId() == _map) {
                            pc.sendPackets(new S_ServerMessage(1484));
                        }
                        TimeUnit.MILLISECONDS.sleep(1000);
                        if (pc.getMapId() == _map) {
                            L1Teleport.teleport(pc, 33705, 32504, (short) 4, 5, true);
                        }
                        endRaid();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
            } finally {
                try {
                    TimeUnit.MILLISECONDS.sleep(1500);
                } catch (Exception e) {
                }
            }
        }
        endRaid();
    }

    public void Start() {
        GeneralThreadPool.get().execute(this);
    }

    private void Sleep(int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (Exception e) {
        }
    }

    /**
     * 設置奄奄一息的死亡騎士
     */
    private void setting() {
        for (L1NpcInstance npc : BasicNpcList) {
            if (npc != null) {
                if (npc.getNpcId() == 46164) {//奄奄一息的死亡騎士
                    death = npc;
                }
            }
        }
    }

    /**
     * 檢測玩家是否在地圖內
     */
    private void checkPc() {
        if (pc == null || pc.getOnlineStatus() == 0) {
            endRaid();
            return;
        }
        if (pc.ValakasStatus != 0 && _map != pc.getMapId()) {
            endRaid();
            return;
        }
    }

    /**
     * 副本結束
     */
    private void endRaid() {
        if (Running) {
            Collection<L1Object> cklist = World.get().getVisibleObjects(_map).values();
            for (L1Object ob : cklist) {
                if (ob == null) {
                    continue;
                }
                if (ob instanceof L1ItemInstance) {
                    L1ItemInstance obj = (L1ItemInstance) ob;
                    L1Inventory groundInventory = World.get().getInventory(obj.getX(), obj.getY(), obj.getMapId());
                    groundInventory.removeItem(obj);
                } else if (ob instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) ob;
                    npc.deleteMe();
                }
            }
            if (pc != null && pc.getOnlineStatus() == 1 && pc.ValakasStatus == 0) {
                pc.getInventory().consumeItem(5010);
                L1PolyMorph.undoPoly(pc); //SRC0712 結束副本取消變身
            }
            ValakasRoomSystem.getInstance().removeReady(_map);
            System.out.println(pc.getName() + " [火窟副本接任務地圖完成,釋放地圖完成]" + _map);
            Running = false;
            pc = null;
            death = null;
            BasicNpcList = null;
        }
    }
}
