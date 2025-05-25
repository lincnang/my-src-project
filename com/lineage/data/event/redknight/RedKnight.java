package com.lineage.data.event.redknight;

import com.lineage.server.IdFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Broadcaster;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NpcChatPacket;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import javolution.util.FastTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 紅騎士訓練副本
 *
 * @author darling
 */
public class RedKnight implements Runnable {
    private static final Log _log = LogFactory.getLog(RedKnight.class);
    private static final int SPAWN = 0;
    private static final int READY = 1;
    private static final int MEMBER_CHECK = 2;
    private static final int ROUND_1 = 3;
    private static final int ROUND_2 = 4;
    private static final int ROUND_3 = 5;
    private static final int END = 6;
    private static final int TIME_OVER = 7;
    private static Random rnd = new Random(System.currentTimeMillis());
    short mapId;
    L1NpcInstance npc;
    L1PcInstance pc;
    ArrayList<L1NpcInstance> list = new ArrayList<>();
    //private int mapId = 0;
    private int step = 0;
    private int READY_TIME = 4;
    private int ROUND_1_STEP = 8;
    private int ROUND_2_STEP = 2;
    private int ROUND_3_STEP = 2;
    private int END_TIME = 13;
    private FastTable<L1NpcInstance> Bari_1 = null;
    private FastTable<L1NpcInstance> Bari_2 = null;
    private FastTable<L1NpcInstance> Bari_3 = null;
    private FastTable<L1NpcInstance> Mischievous = null;
    private L1NpcInstance boss = null;
    private L1NpcInstance Defrost = null;
    private L1NpcInstance Red_Templar_1 = null;
    private L1NpcInstance Red_Templar_2 = null;
    private boolean on = true;

    public RedKnight(int mapId, L1PcInstance pc) {
        this.mapId = 2301;
        this.pc = pc;
        GeneralThreadPool.get().schedule(new Runnable() {
            private int TIMER = 90;

            @Override
            public void run() {
                try {
                    if (!on) {
                        return;
                    }
                    if (TIMER == 5) {
                        GREEN_MSG("傳令: 請盡快! 5分鐘後敵人援軍將會來到!.");
                    } else if (TIMER == 4) {
                        GREEN_MSG("傳令: 請盡快! 4分鐘後敵人援軍將會來到!.");
                    } else if (TIMER == 3) {
                        GREEN_MSG("傳令: 請盡快! 3分鐘後敵人援軍將會來到!.");
                    } else if (TIMER == 2) {
                        GREEN_MSG("傳令: 請盡快! 2分鐘後敵人援軍將會來到!.");
                    } else if (TIMER == 1) {
                        GREEN_MSG("傳令: 請盡快! 1分鐘後敵人援軍將會來到!.");
                    } else if (TIMER == 0) {
                        GREEN_MSG("傳令: 敵人援軍快到了!不能再拖延了!先回村莊吧!.");
                        step = TIME_OVER;
                        return;
                    }
                    TIMER--;
                } catch (Exception e) {
                }
                GeneralThreadPool.get().schedule(this, 60000);
            }
        }, 60000);
    }

    // 紅騎士 訓練副本 by darling
    public static L1NpcInstance spawn2(int x, int y, short map, int npcId, int randomRange, int timeMillisToDelete, int movemap) {
        int heading = 5;
        if (npcId == 100646)
        //heading = 6;
        //else if (npcId == 100430 || npcId == 100709 || npcId == 100710)
        {
            heading = 4;
        }
        return spawn4(x, y, map, heading, npcId, randomRange, timeMillisToDelete, movemap, false);
    }

    // 紅騎士 訓練副本 by darling
    public static L1NpcInstance spawn2(int x, int y, short map, int npcId, int randomRange, int timeMillisToDelete, int movemap, boolean level) {
        int heading = 5;
        if (npcId == 100646)
        //	heading = 6;
        //else if (npcId == 100430 || npcId == 100709 || npcId == 100710)
        {
            heading = 4;
        }
        return spawn4(x, y, map, heading, npcId, randomRange, timeMillisToDelete, movemap, level);
    }

    // 紅騎士 訓練副本 by darling
    public static L1NpcInstance spawn4(int x, int y, short map, int heading, int npcId, int randomRange, int timeMillisToDelete, int movemap) {
        return spawn4(x, y, map, heading, npcId, randomRange, timeMillisToDelete, movemap, false);
    }

    // 紅騎士 訓練副本 by darling
    public static L1NpcInstance spawn4(int x, int y, short map, int heading, int npcId, int randomRange, int timeMillisToDelete, int movemap, boolean level) {
        L1NpcInstance npc = null;
        try {
            if (level) {
                npc = NpcTable.get().newNpcInstance(npcId + 1000000);
            } else {
                npc = NpcTable.get().newNpcInstance(npcId);
            }
            npc.setId(IdFactory.get().nextId());
            npc.setMap(map);
            if (randomRange == 0) {
                npc.getLocation().set(x, y, map);
                /**
                 * 용땅도포함~~~ 하딘 관련 NPC가 아닐 경우에만 적용 일단 주석 처리 해봄.
                 **/
                /*
                 * if(npcId != 4212013 && !(npcId >= 5000038 && npcId <=
                 * 5000093)) npc.getLocation().forward(5);
                 */
            } else {
                int tryCount = 0;
                do {
                    tryCount++;
                    npc.setX(x + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
                    npc.setY(y + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
                    if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation())) {
                        break;
                    }
                    TimeUnit.MILLISECONDS.sleep(1);
                } while (tryCount < 50);
                if (tryCount >= 50) {
                    npc.getLocation().set(x, y, map);
                    // npc.getLocation().forward(5);
                }
            }
            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());
            npc.setHeading(heading);
            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);
            npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
            if (0 < timeMillisToDelete) {
                // 存在時間(秒)
                npc.set_spawnTime(timeMillisToDelete);
            }
        } catch (Exception e) {
            _log.error("執行npc召喚發生異常", e);
            ;
        }
        return npc;
    }

    @Override
    public void run() {
        int sleep = 1;
        try {
            switch (step) {
                case SPAWN:
                    Bari_1 = RedKnightSpawn.getInstance().fillSpawnTable(mapId, 0);
                    Bari_2 = RedKnightSpawn.getInstance().fillSpawnTable(mapId, 1);
                    Bari_3 = RedKnightSpawn.getInstance().fillSpawnTable(mapId, 2);
                    sleep = 10;
                    step++;
                    break;
                case READY:
                    if (READY_TIME == 4) {
                        GREEN_MSG("傳令: 4分後出發.");
                    } else {
                        GREEN_MSG("傳令: " + READY_TIME + "分後出發 參加人數低於10人時,本場將會取消.");
                    }
                    sleep = 10;
                    READY_TIME--;
                    if (READY_TIME <= 0) {
                        step++;
                    }
                    break;
                case MEMBER_CHECK:
                    int count = 0;
                    for (L1Object ob : World.get().getVisibleObjects(mapId).values()) {
                        if (ob == null) {
                            continue;
                        }
                        if (ob instanceof L1PcInstance) {
                            count++;
                        }
                    }
                    if (count < 1) {
                        GREEN_MSG("傳令: 參與人數不足,本場取消.請參加下一場比賽.");
                        TimeUnit.MILLISECONDS.sleep(3000);
                        HOME_TELEPORT();
                        Object_Delete();
                        return;
                    } else {
                        GREEN_MSG("傳令: 進場之前,特羅斯會來鼓勵大家！.");
                        sleep = 5;
                    }
                    step++;
                    break;
                case ROUND_1:
                    sleep = 5;
                    if (ROUND_1_STEP == 8) {
                        Red_Templar_1 = spawn4(32772, 32814, (short) mapId, 4, 100660, 0, 0, 0);
                        Red_Templar_2 = spawn4(32768, 32814, (short) mapId, 4, 100660, 0, 0, 0);
                        GeneralThreadPool.get().schedule(new NpcMove(Red_Templar_1, 4, 5), 50);
                        GeneralThreadPool.get().schedule(new NpcMove(Red_Templar_2, 4, 5), 50);
                    } else if (ROUND_1_STEP == 7) {
                        SHOUT_MSG(Red_Templar_2, "紅騎士:請集中!! 特羅斯團長來了!.");
                        Defrost = spawn4(32770, 32814, (short) mapId, 4, 100659, 0, 0, 0);
                        GeneralThreadPool.get().schedule(new NpcMove(Defrost, 4, 7), 50);
                    } else if (ROUND_1_STEP == 6) {
                        SHOUT_MSG(Defrost, "特羅斯: 來協助的紅騎士團員們，感謝你們的辛勞!.");
                        sleep = 10;
                    } else if (ROUND_1_STEP == 5) {
                        SHOUT_MSG(Defrost, "特羅斯: 已透過傳令聽說了吧，這任務非常的艱鉅!.");
                        sleep = 10;
                    } else if (ROUND_1_STEP == 4) {
                        SHOUT_MSG(Defrost, "特羅斯: 等等前方的防護牆打破，請殲滅敵人尋找地圖碎片！.");
                        sleep = 10;
                    } else if (ROUND_1_STEP == 3) {
                        SHOUT_MSG(Defrost, "特羅斯: 我相信你們！為了紅騎士團的榮耀！上吧！！！！.");
                        GeneralThreadPool.get().schedule(new NpcMove(Red_Templar_1, 0, 5), 2500);
                        GeneralThreadPool.get().schedule(new NpcMove(Red_Templar_2, 0, 5), 2500);
                        GeneralThreadPool.get().schedule(new NpcMove(Defrost, 0, 7), 50);
                        sleep = 10;
                    } else if (ROUND_1_STEP == 2) {
                        Bari_remove(Bari_1);
                        Mischievous = RedKnightSpawn.getInstance().fillSpawnTable(mapId, 3);
                        GREEN_MSG("傳令: 第一個防護牆破了！ 前進!!!");
                    } else if (ROUND_1_STEP == 1) {
                        if (Mischievous_check()) {
                            boss = spawn2(32770, 32923, (short) mapId, 100653, 3, 0, 0);// 라미아스
                            GREEN_MSG("副隊長拉曼斯: 怎麼會來到這裡!? 黑騎士團絕對不會放過你們的!!");
                        } else {
                            GeneralThreadPool.get().schedule(this, 5 * 1000);
                            return;
                        }
                    } else if (ROUND_1_STEP == 0) {
                        if (boss_check()) {
                            step++;
                        }
                    }
                    if (ROUND_1_STEP != 0) {
                        ROUND_1_STEP--;
                    }
                    break;
                case ROUND_2:
                    sleep = 5;
                    if (ROUND_2_STEP == 2) {
                        Bari_remove(Bari_2);
                        Mischievous = RedKnightSpawn.getInstance().fillSpawnTable(mapId, 5);
                        GREEN_MSG("傳令: 第二個防護牆破了!前進!!!");
                    } else if (ROUND_2_STEP == 1) {
                        if (Mischievous_check()) {
                            boss = spawn2(32771, 33009, (short) mapId, 100654, 3, 0, 0);// 바로드
                            GREEN_MSG("副隊長巴陸德:前線的部隊被攻破了嗎!?看來不能小看你們!讓我來!!");
                        } else {
                            GeneralThreadPool.get().schedule(this, 5 * 1000);
                            return;
                        }
                    } else if (ROUND_2_STEP == 0) {
                        if (boss_check()) {
                            step++;
                        }
                    }
                    if (ROUND_2_STEP != 0) {
                        ROUND_2_STEP--;
                    }
                    break;
                case ROUND_3:
                    sleep = 5;
                    if (ROUND_3_STEP == 2) {
                        Bari_remove(Bari_3);
                        Mischievous = RedKnightSpawn.getInstance().fillSpawnTable(mapId, 7);
                        GREEN_MSG("傳令: 最後一個防護牆破了!勝利就在眼前!!!!");
                    } else if (ROUND_3_STEP == 1) {
                        if (Mischievous_check()) {
                            boss = spawn2(32769, 33093, (short) mapId, 100655, 3, 0, 0);// 그림리퍼
                            GREEN_MSG("副隊長鐮刀死神的使者: 我不會放過你們的!!!");
                        } else {
                            GeneralThreadPool.get().schedule(this, 5 * 1000);
                            return;
                        }
                    } else if (ROUND_3_STEP == 0) {
                        if (boss_check()) {
                            step++;
                        }
                    }
                    if (ROUND_3_STEP != 0) {
                        ROUND_3_STEP--;
                    }
                    break;
                case END:
                    if (END_TIME <= 0) {
                        HOME_TELEPORT();
                        Object_Delete();
                        return;
                    } else if (END_TIME == 13) {
                        GREEN_MSG("傳令: 任務成功!特羅斯團長一定很開心的!.");
                        sleep = 3;
                    } else if (END_TIME == 12) {
                        GREEN_MSG("傳令: 獲得的3種地圖碎片請交給紅騎士團參謀。");
                        sleep = 3;
                    } else if (END_TIME == 11) {
                        GREEN_MSG("系統命令: 1分鐘後回到村莊.");
                        sleep = 50;
                    } else {
                        GREEN_MSG("系統命令: " + END_TIME + "秒");
                    }
                    END_TIME--;
                    break;
                case TIME_OVER:
                    HOME_TELEPORT();
                    Object_Delete();
                    return;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        GeneralThreadPool.get().schedule(this, sleep * 1000);
    }

    private boolean boss_check() {
        if (boss == null || boss._destroyed || boss.isDead()) {
            return true;
        }
        return false;
    }

    private boolean Mischievous_check() {
        if (Mischievous == null || Mischievous.size() <= 0) {
            return true;
        }
        for (L1NpcInstance npc : Mischievous) {
            if (npc == null || npc._destroyed || npc.isDead()) {
                continue;
            }
            // System.out.println(npc.getX()+" > "+npc.getY());
            return false;
        }
        return true;
    }

    private void Bari_remove(FastTable<L1NpcInstance> list) {
        // TODO 自動生成方法存根
        if (list == null || list.size() <= 0) {
            return;
        }
        for (L1NpcInstance npc : list) {
            if (npc == null || npc._destroyed) {
                return;
            }
            npc.getMap().setPassable(npc.getLocation(), true);
            npc.deleteMe();
        }
    }

    private void GREEN_MSG(String msg) {
        for (L1Object ob : World.get().getVisibleObjects(mapId).values()) {
            if (ob == null) {
                continue;
            }
            if (ob instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) ob;
                pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, msg));
            }
        }
    }

    private void SHOUT_MSG(L1NpcInstance npc, String msg) {
        Broadcaster.broadcastPacket(npc, new S_NpcChatPacket(npc, msg, 2), true);
        /*
         * for(L1Object ob :
         * L1World.getInstance().getVisibleObjects(mapId).values()){ if(ob ==
         * null) continue; if(ob instanceof L1PcInstance){ L1PcInstance pc =
         * (L1PcInstance) ob; pc.sendPackets(new S_SystemMessage(msg, true)); }
         * }
         */
    }

    private void HOME_TELEPORT() {
        for (L1Object ob : World.get().getVisibleObjects(mapId).values()) {
            if (ob == null) {
                continue;
            }
            if (ob instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) ob;
                L1Teleport.teleport(pc, 33436 + rnd.nextInt(12), 32795 + rnd.nextInt(14), (short) 4, 5, true);
            }
        }
    }

    private void Object_Delete() {
        on = false;
        for (L1Object ob : World.get().getVisibleObjects(mapId).values()) {
            if (ob == null || ob instanceof L1DollInstance || ob instanceof L1DollInstance2
                    || ob instanceof L1SummonInstance || ob instanceof L1PetInstance) {
                continue;
            }
            if (ob instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) ob;
                npc.deleteMe();
            }
        }
        for (L1ItemInstance obj : World.get().getAllItem()) {
            if (obj.getMapId() != mapId) {
                continue;
            }
            L1Inventory groundInventory = World.get().getInventory(obj.getX(), obj.getY(), obj.getMapId());
            groundInventory.removeItem(obj);
        }
    }

    static class NpcMove implements Runnable {
        private L1NpcInstance npc = null;
        private int count = 0;
        private int direct = 0;

        public NpcMove(L1NpcInstance _npc, int _direct, int _count) {
            npc = _npc;
            count = _count;
            direct = _direct;
        }

        @Override
        public void run() {
            // TODO 自動生成方法存根
            try {
                if (count <= 0) {
                    if (direct == 0) {
                        npc.deleteMe();
                    }
                    return;
                }
                count--;
                npc.setDirectionMoveSrc(direct);
                GeneralThreadPool.get().schedule(this, 640);
            } catch (Exception e) {
            }
        }
    }
}