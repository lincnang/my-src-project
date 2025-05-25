/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.add.Crack;

import com.add.L1Config;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class L1Thebes {
    // 副本狀態
    public static final int STATUS_NONE = 0; // 尚未開始、等待加入
    public static final int STATUS_READY = 1; // 準備開始
    public static final int STATUS_PLAYING = 2; // 開始
    public static final int STATUS_AGAIN = 3; // 重置
    public static final int STATUS_END = 4; // 結束
    private static final int END_STATUS_WINNER = 1;
    private static final int END_STATUS_NOWINNER = 2;
    // 副本結束狀態
    public static int _status = STATUS_NONE;
    public static int _type = 0;
    private static Logger _log = Logger.getLogger(L1Thebes.class.getName());
    private static ArrayList<L1PcInstance> playerList = new ArrayList<>();

    public static void enterGame(L1PcInstance pc, int npcid) {
        L1Object obj = World.get().findObject(npcid);
        L1NpcInstance npc = (L1NpcInstance) obj;
        // 時空裂痕未開啟2小時30分鐘
        if (L1CrackTime.getStart().getGateOpen() == false) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "thebegate2"));
            return;
        }
        // 沒有底比斯歐西裡斯祭壇鑰匙
        if (!pc.getInventory().checkItem(49242, 1)) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "thebegate3"));
            return;
        }
        // 副本狀態為開始
        if (getGameStatus() == STATUS_PLAYING) {
            if (playerList.contains(pc)) {
                L1Teleport.teleport(pc, 32734, 32832, (short) 782, 2, true);
            } else {
                pc.sendPackets(new S_SystemMessage("副本已開始，無法進入。"));
            }
            return;
        }
        // 副本狀態為重置
        if (getGameStatus() == STATUS_AGAIN) {
            pc.sendPackets(new S_SystemMessage("副本正在重置中。"));
            return;
        }
        // 副本狀態為結束
        if (getGameStatus() == STATUS_END) {
            pc.sendPackets(new S_SystemMessage("BOSS已死亡，無法進入。"));
            return;
        }
        // 人數超過 20無法入場
        if (playerList.size() >= L1Config._2213) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "thebegate4"));
            return;
        }
        // 等待入場
        if ((getGameStatus() == STATUS_NONE)) {
            L1Teleport.teleport(pc, 32734, 32832, (short) 782, 2, true);
            pc.getInventory().consumeItem(49242, 1);
            addPlayerList(pc); // 增加入場人數
        }
        // 副本狀態為準備開始且未達最大入場人數
        if ((getGameStatus() == STATUS_READY) && (playerList.size() < L1Config._2213)) {
            L1Teleport.teleport(pc, 32734, 32832, (short) 782, 2, true);
            pc.getInventory().consumeItem(49242, 1);
            addPlayerList(pc); // 增加入場人數
        }
    }

    // 增加入場人數
    private static void addPlayerList(L1PcInstance pc) {
        if (!playerList.contains(pc)) {// 不包含玩家
            playerList.add(pc);// 加入入場人數
        }
        if (playerList.size() == L1Config._2211) { // 入場人數等於最低限制人數
            if (getGameStatus() == STATUS_NONE) {// 副本尚未開始
                setGameStatus(STATUS_READY); // 設定副本狀態為準備開始
                for (Object obj : World.get().getVisibleObjects(782).values()) {
                    if (obj instanceof L1PcInstance) {
                        L1PcInstance tgpc = (L1PcInstance) obj;
                        tgpc.sendPackets(new S_SystemMessage("時空裂痕副本將在60秒後開始。"));
                    }
                }
                OverTime(80);// 開場倒數
            }
        } else if (playerList.size() < L1Config._2211) {// 入場人數不足最低限制人數
            pc.sendPackets(new S_SystemMessage("需要有至少 " + L1Config._2211 + " 人以上參加者才可能開始副本。"));
        }
    }

    public static void removePlayerList(L1PcInstance pc) {
        if (playerList.contains(pc)) {
            playerList.remove(pc);
            L1Teleport.teleport(pc, 33442, 32797, (short) 4, 5, true);
        }
    }

    /**
     * 傳回副本狀態
     *
     */
    private static int getGameStatus() {
        return _status;
    }

    /**
     * 設定副本狀態
     *
     */
    public static void setGameStatus(int i) {
        _status = i;
    }

    /**
     * 副本結束狀態(攻略BOSS是否成功)
     *
     */
    public static int getGameEnd() {
        return _type;
    }

    /**
     * 設定副本結束狀態(攻略BOSS是否成功)
     *
     */
    private static void setGameEnd(int type) {
        switch (type) {
            case END_STATUS_WINNER: // 副本成功
                sendEndMessage(type); // 顯示訊息
                break;
            case END_STATUS_NOWINNER: // 副本失敗
                sendEndMessage(type); // 顯示訊息
                break;
        }
        _type = type;
    }

    // 攻略失敗後 重置時間開始
    private static void startAgain() {
        new Again().begin();
    }

    // 攻略成功後 重置時間開始
    private static void startAgain2() {
        new Again2().begin();
    }

    // 顯示訊息
    private static void sendEndMessage(int type) {
        if (type == END_STATUS_WINNER) { // 副本成功
            for (L1PcInstance pc : playerList) {
                // 底比斯 歐西裡斯：怎麼可能...！！！ 我們竟然輸了...
                pc.sendPackets(new S_ServerMessage(1474));
            }
        } else if (type == END_STATUS_NOWINNER) { // 副本失敗
            for (L1PcInstance pc : playerList) {
                // 底比斯 歐西裡斯：你們都失敗了！！！哈哈哈～無知的傢伙！！！
                pc.sendPackets(new S_ServerMessage(1479));
            }
        }
        Overtime2(type);// 開始離場倒數
    }

    /**
     * 離場倒數
     *
     */
    private static void Overtime2(int type) {
        int time = 40; // 副本倒數時間
        try {
            while (time >= 0) {
                TimeUnit.MILLISECONDS.sleep(1000L); // 每1 秒time--;
                for (L1PcInstance pc : playerList) {
                    if (time == 35 && type == END_STATUS_WINNER) {// 副本成功
                        // 底比斯 歐西裡斯：現在開始將開放一天的時空裂痕之底比斯...
                        pc.sendPackets(new S_ServerMessage(1475));
                    } else if (time == 30) {
                        pc.sendPackets(new S_ServerMessage(1476));
                    } else if (time == 20) {
                        pc.sendPackets(new S_ServerMessage(1477));
                    } else if (time == 10) {
                        pc.sendPackets(new S_ServerMessage(1478));
                    } else if (time == 5) {
                        pc.sendPackets(new S_ServerMessage(1480));
                    } else if (time == 4) {
                        pc.sendPackets(new S_ServerMessage(1481));
                    } else if (time == 3) {
                        pc.sendPackets(new S_ServerMessage(1482));
                    } else if (time == 2) {
                        pc.sendPackets(new S_ServerMessage(1483));
                    } else if (time == 1) {
                        pc.sendPackets(new S_ServerMessage(1484));
                    } else if (time == 0) {
                        if (type == END_STATUS_WINNER) {// 副本成功
                            L1Teleport.teleport(pc, 33442, 32797, (short) 4, 5, true);
                            setGameStatus(STATUS_END); // 設定副本狀態為結束
                            playerList.clear(); // 清除加入玩家
                            startAgain2(); // 攻略成功後 重置時間開始
                        } else if (type == END_STATUS_NOWINNER) {// 副本失敗
                            setGameStatus(STATUS_AGAIN); // 設定副本狀態為重置
                            playerList.clear(); // 清除加入玩家
                            startAgain(); // 攻略失敗後 重置時間開始
                        }
                    }
                }
                time--;
            }
        } catch (Exception e) {
            _log.warning(e.getMessage());
        }
    }

    /**
     * 開場倒數並召喚BOSS開始副本
     *
     */
    public static void OverTime(int time) {
        try {
            while (time >= 0) {
                TimeUnit.MILLISECONDS.sleep(1000L); // 每1 秒time--;
                time--;
                for (L1PcInstance pc : playerList) {
                    if (time == 20) {
                        setGameStatus(STATUS_PLAYING); // 設定副本狀態為開始
                        // 底比斯 歐西裡斯：無知的傢伙...你以為這是哪裡！！阿努比斯！賀洛斯！將那些不長眼的都除掉！！
                        pc.sendPackets(new S_ServerMessage(1471));
                    } else if (time == 15) {
                        // 阿努比斯：你們都死定了....
                        pc.sendPackets(new S_ServerMessage(1472));
                    } else if (time == 10) {
                        // 賀洛斯：沒有慈悲....
                        pc.sendPackets(new S_ServerMessage(1473));
                    }
                }
                if (time == 5) {
                    L1SpawnUtil.spawn(46124, 32781, 32826, (short) 782, 0); // 底比斯
                    // 阿努比斯
                    L1SpawnUtil.spawn(46123, 32781, 32837, (short) 782, 0); // 底比斯
                    // 賀洛斯
                    startSeekBossSecond(); // 每5秒判斷BOSS或玩家死亡
                }
            }
        } catch (Exception e) {
            _log.warning(e.getMessage());
        }
    }

    // 每5秒判斷BOSS或玩家死亡
    private static void startSeekBossSecond() {
        new SeekBossSecond().begin();
    }

    // 計算地圖內怪物數量
    private static int MobCount(short mapId) {
        int MobCount = 0;
        for (Object obj : World.get().getVisibleObjects(mapId).values()) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mob = (L1MonsterInstance) obj;
                if (mob != null && !mob.isDead()) {
                    MobCount++;
                }
            }
        }
        return MobCount;
    }

    // 計算地圖內PC數量
    private static int PcCount(short mapId) {
        int PcCount = 0;
        for (Object obj : World.get().getVisibleObjects(mapId).values()) {
            if (obj instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) obj;
                if (pc != null && !pc.isDead()) {
                    PcCount++;
                }
            }
        }
        return PcCount;
    }

    private static class Again extends TimerTask {
        @Override
        public void run() {
            setGameStatus(STATUS_NONE); // 設定副本狀態為等待加入
            for (Object obj : World.get().getVisibleObjects(782).values()) {// 刪除怪物
                if (obj instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    npc.deleteMe();
                }
            }
            this.cancel();
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, (long) L1Config._2212 * 60 * 1000); // 重置時間(分)
        }
    }

    private static class Again2 extends TimerTask {
        @Override
        public void run() {
            setGameStatus(STATUS_NONE); // 設定副本狀態為等待加入
            for (Object obj : World.get().getVisibleObjects(782).values()) {// 刪除怪物
                if (obj instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    npc.deleteMe();
                }
            }
            this.cancel();
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, (long) L1Config._2218 * 60 * 1000); // 時空裂痕減弱維持時間(分)
        }
    }

    private static class SeekBossSecond extends TimerTask {
        @Override
        public void run() {
            if (MobCount((short) 782) == 0) {
                setGameEnd(END_STATUS_WINNER); // 副本情況為成功
            } else if (PcCount((short) 782) == 0) {
                setGameEnd(END_STATUS_NOWINNER); // 副本情況為失敗
            } else {
                startSeekBossSecond(); // 每5秒判斷BOSS或玩家死亡
            }
            this.cancel();
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 5000); // 5s
        }
    }
}