package com.add.Crack;

import com.add.L1Config;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;

import java.lang.reflect.Constructor;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 控制項時間軸
 */
public class L1CrackTime extends TimerTask {
    private static final int[][] _crack = {{32639, 32876, 780}, // 底比斯
            {32794, 32751, 783} // 提卡爾
    };
    private static final int[][] _crackLoc = {{32728, 32709, 4}, {32848, 32639, 4}, {32852, 32705, 4}, // 邪惡神殿
            {32913, 33168, 4}, {32957, 33247, 4}, {32913, 33425, 4}, // 沙漠綠洲
            {34255, 33203, 4}, {34232, 33312, 4}, {34276, 33359, 4} // 黃昏山脈
    };
    // 突破祭壇判斷
    public static boolean _BossKill = false;
    private static Logger _log = Logger.getLogger(L1CrackTime.class.getName());
    private static L1CrackTime _instance;
    private Timer _timeHandler = new Timer(true);
    private boolean _isOver = false;
    private int _Crack = 0;
    // 時空裂痕處理時間軸已開始時間(秒)
    private int _startTime = 0;
    // 超過2小時30分 判斷
    private boolean _GateOpen = false;

    private L1CrackTime() {// 啟動時空裂痕處理時間軸
        // 開始執行此時間軸 間隔一秒
        _timeHandler.schedule(this, 1000, 1000);
        // 交由線程工廠 處理
        GeneralThreadPool.get().execute(this);
    }

    public static L1CrackTime getStart() {
        if (_instance == null) {
            _instance = new L1CrackTime();
        }
        return _instance;
    }

    private static boolean getBossKill() {
        return _BossKill;
    }

    public static void setBossKill(boolean BossKill) {
        _BossKill = BossKill;
    }

    @Override
    public void run() {
        if (_isOver) {// 時空裂痕是否結束
            try {
                clear();// 清空時空裂痕資訊(時空裂痕結束)
                TimeUnit.MILLISECONDS.sleep(L1Config._2216 * 3600 * 1000L);// 時空裂痕關閉後下次開啟間隔
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _startTime++;
        switch (_startTime) {
            case 2 * 60:// 時空裂痕處理時間軸啟動兩分鐘後
                spawnCrack();// 時空裂痕開啟
                break;
        }
        // 開啟2小時30分鐘 可進入祭壇
        if (_startTime >= 150 * 60 && getBossKill() == false) {
            setGateOpen(true);
        }
        // 超過(開放時間)前一分鐘提示
        if (_startTime == (L1Config._2217 - 1) * 60 && getBossKill() == false) {
            World.get().broadcastServerMessage("時空裂痕即將關閉...");
        }
        // 超過(開放時間)
        if (_startTime >= L1Config._2217 * 60 && getBossKill() == false) {
            // 副本成功或失敗判斷
            if ((L1Tikal.getGameEnd() == 0 && _Crack == 2) || (L1Thebes.getGameEnd() == 0 && _Crack == 1)) { // GameEnd
                // =0
                // 副本未開啟
                _isOver = true;
            } else if ((L1Tikal.getGameEnd() == 1 && _Crack == 2) || (L1Thebes.getGameEnd() == 1 && _Crack == 1)) { // GameEnd=1
                // 副本成功
                for (L1PcInstance pc : World.get().getAllPlayers()) {
                    if (pc.getMapId() >= 780 && pc.getMapId() <= 784) {
                        if (pc.isDead()) {// 玩家已死亡
                            restartPlayer(pc, 32616, 32782, (short) 4);
                        }
                    }
                }
                setBossKill(true); // 突破 祭壇成功
                World.get().broadcastServerMessage("時空裂痕的力量減弱了...將維持一天的異界空間...");
            } else if ((L1Tikal.getGameEnd() == 2 && _Crack == 2) || (L1Thebes.getGameEnd() == 2 && _Crack == 1)) { // GameEnd
                // =2
                // 副本失敗
                _isOver = true;
            }
        }
        // 超過(開放時間+維持時間)
        if (_startTime >= (L1Config._2217 + L1Config._2218) * 60) {
            _isOver = true;
        }
    }

    /**
     * 清空時空裂痕資訊(時空裂痕結束)
     */
    private void clear() {
        _startTime = 0;
        _isOver = false;
        _Crack = 0; // 底比斯或提卡爾 都未開啟
        setGateOpen(false); // 未開啟2小時30分鐘
        setBossKill(false); // 設定祭壇尚未突破
        L1Tikal.setGameStatus(0); // 設定副本狀態為等待加入
        L1Thebes.setGameStatus(0); // 設定副本狀態為等待加入
        World.get().broadcastServerMessage("時空裂痕關閉了。");
        for (L1Object obj : WorldNpc.get().all()) {
            if (obj instanceof L1NpcInstance) {
                L1NpcInstance Cracknpc = (L1NpcInstance) obj;
                if (Cracknpc.getNpcId() == 71254) {
                    Cracknpc.deleteMe();
                }
            }
        }
        for (L1Object obj : WorldNpc.get().all()) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mob = (L1MonsterInstance) obj;
                if ((mob.getNpcId() >= 46107 && mob.getNpcId() <= 46122 || mob.getNpcId() >= 46123 && mob.getNpcId() <= 46124) || (mob.getNpcId() >= 92002 && mob.getNpcId() <= 92017 || mob.getNpcId() >= 92000 && mob.getNpcId() <= 92001)) {
                    mob.deleteMe();
                }
            }
        }
        for (L1PcInstance pc : World.get().getAllPlayers()) {
            if (pc.getMapId() >= 780 && pc.getMapId() <= 784) {
                if (pc.isDead()) {
                    restartPlayer(pc, 32616, 32782, (short) 4);
                } else {
                    L1Teleport.teleport(pc, 33442, 32797, (short) 4, 5, true);// 時空裂痕結束，傳送的地圖
                    // 預設奇巖
                }
            }
        }
    }

    /**
     * 時空裂痕開啟
     */
    private void spawnCrack() {
        L1Location crack = null;
        L1Location crack_loc = null;
        int rnd1 = ThreadLocalRandom.current().nextInt(2);
        int rnd2 = ThreadLocalRandom.current().nextInt(9);
        crack = new L1Location(_crack[rnd1][0], _crack[rnd1][1], _crack[rnd1][2]);
        crack_loc = new L1Location(_crackLoc[rnd2][0], _crackLoc[rnd2][1], _crackLoc[rnd2][2]);
        // 開啟底比斯或提卡爾
        if (rnd1 == 1) {
            _Crack = 1;// 底比斯
        } else {
            _Crack = 2;// 提卡爾
        }
        if (getBossKill() == true) { // 突破 祭壇成功
            World.get().broadcastServerMessage("時空裂痕開啟了，裂痕的力量減弱，因此關閉時間呈現被延後的狀態...");
        } else {
            World.get().broadcastServerMessage("時空裂痕開啟了！！異界侵略即將開始...");
        }
        createCrack(crack.getX(), crack.getY(), (short) crack.getMapId(), crack_loc.getX(), crack_loc.getY(), (short) crack_loc.getMapId());
        createCrack(crack_loc.getX(), crack_loc.getY(), (short) crack_loc.getMapId(), crack.getX(), crack.getY(), (short) crack.getMapId());
    }

    /**
     * 創造時空裂痕
     *
     */
    private void createCrack(int x, int y, short mapId, int to_x, int to_y, short to_mapId) {
        try {
            L1Npc l1npc = NpcTable.get().getTemplate(71254);
            if (l1npc == null) {
                return;
            }
            String s = l1npc.getImpl();
            Constructor<?> constructor = Class.forName("com.lineage.server.model.Instance." + s + "Instance").getConstructors()[0];
            Object aobj[] = {l1npc};
            L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(aobj);
            npc.setId(IdFactory.get().nextId());
            npc.setX(x);
            npc.setY(y);
            npc.setMap(mapId);
            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());
            npc.setHeading(0);
            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);
            Teleport teleport = new Teleport(npc, to_x, to_y, to_mapId);
            GeneralThreadPool.get().execute(teleport);
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    /**
     * 死亡玩家重新開始
     *
     */
    public void restartPlayer(L1PcInstance pc, int locx, int locy, short mapid) {
        pc.removeAllKnownObjects();
        pc.broadcastPacketAll(new S_RemoveObject(pc));
        pc.setCurrentHp(pc.getLevel());
        pc.set_food(40);
        pc.setDead(false);
        pc.setStatus(0);
        World.get().moveVisibleObject(pc, mapid);
        pc.setX(locx);
        pc.setY(locy);
        pc.setMap((short) mapid);
        pc.sendPackets(new S_MapID(pc, pc.getMapId(), pc.getMap().isUnderwater()));
        pc.broadcastPacketAll(new S_OtherCharPacks(pc));
        pc.sendPackets(new S_OwnCharPack(pc));
        pc.sendPackets(new S_CharVisualUpdate(pc));
        pc.startHpRegeneration();
        pc.startMpRegeneration();
        pc.sendPackets(new S_Weather(World.get().getWeather()));
        pc.stopPcDeleteTimer();
        if (pc.getHellTime() > 0) {
            pc.beginHell(false);
        }
    }

    public boolean getGateOpen() {
        return _GateOpen;
    }

    public void setGateOpen(boolean GateOpen) {
        _GateOpen = GateOpen;
    }

    static class Teleport implements Runnable {
        private L1NpcInstance _npc = null;
        private int _to_x = 0;
        private int _to_y = 0;
        private short _to_mapId = 0;

        public Teleport(L1NpcInstance npc, int to_x, int to_y, short to_mapId) {
            _npc = npc;
            _to_x = to_x;
            _to_y = to_y;
            _to_mapId = to_mapId;
        }

        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                for (; ; ) {
                    if (_npc._destroyed) {
                        return;
                    }
                    for (L1Object obj : World.get().getVisiblePoint(_npc.getLocation(), 1, _npc.get_showId())) {
                        if (obj instanceof L1PcInstance) {
                            L1PcInstance target = (L1PcInstance) obj;
                            L1Location tmp_loc = new L1Location(_to_x, _to_y, _to_mapId);
                            L1Location rnd_loc = tmp_loc.randomLocation(1, 5, false);
                            L1Teleport.teleport(target, rnd_loc.getX(), rnd_loc.getY(), (short) rnd_loc.getMapId(), target.getHeading(), true);
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(1000);
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
    }
}