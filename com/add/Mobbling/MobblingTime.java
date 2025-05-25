package com.add.Mobbling;

import com.add.L1Config;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NpcChatPacket;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.Random;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MobblingTime extends TimerTask {
    private static final Log _log = LogFactory.getLog(MobblingTime.class);
    // 對戰判斷
    private static boolean _Mobbling = false;
    private Timer _timeHandler = new Timer(true);
    private L1NpcInstance _victoryNpc = null;
    private boolean _isOver = false;
    private int _startTime1 = 0;
    private MobblingTimeList _Mob = MobblingTimeList.Mob();
    private double _npcRateA;
    private int _npcChipA;

    public static boolean getMobbling() {
        return _Mobbling;
    }

    public void setMobbling(boolean Mobbling) {
        _Mobbling = Mobbling;
    }

    public int get_npcChipA() {
        return this._npcChipA;
    }

    public double get_npcRateA() {
        return this._npcRateA;
    }

    public void startMobbling() {
        this._timeHandler.schedule(this, 500L, 500L);
        GeneralThreadPool.get().execute(this);
        nowStart();
    }

    public void run() {
        if (this._isOver) {
            try {
                TimeUnit.MILLISECONDS.sleep(10000L);
                clear();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this._startTime1 += 1;
        switch (this._startTime1) {
            case 2:
                // World.get().broadcastPacketToAll(new
                // S_SystemMessage("距離怪物對戰開打還有5分鐘。"));
                toAllTimeM("5");
                break;
            case 120:
                toAllTimeM("4");
                break;
            case 240:
                toAllTimeM("3");
                break;
            case 360:
                toAllTimeM("2");
                break;
            case 480:
                toAllTimeM("1");
                break;
            case 590:
                toAllTimeS("5");
                break;
            case 592:
                toAllTimeS("4");
                break;
            case 594:
                toAllTimeS("3");
                break;
            case 596:
                toAllTimeS("2");
                break;
            case 598:
                toAllTimeS("1");
                break;
            case 600:
                // World.get().broadcastPacketToAll(new S_SystemMessage("怪物對戰開始。"));
                checkMobbling();
                start();
                break;
            case 612:
                toRate(1);
                break;
            case 614:
                toRate(2);
                break;
            case 616:
                toRate(3);
                break;
            case 618:
                toRate(4);
                break;
            case 620:
                toRate(5);
                break;
            case 622:
                toRate(6);
                break;
            case 624:
                toRate(7);
                break;
            case 626:
                toRate(8);
                break;
            case 628:
                toRate(9);
                break;
            case 630:
                toRate(10);
        }
    }

    private void toRate(int type) {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() == L1Config._2154) {
                double npcRateA = 0.0D;
                String npcName = null;
                switch (type) {
                    case 1:
                        npcRateA = this._Mob.get_npcRate1A();
                        npcName = this._Mob.get_npcMob1().getNameId();
                        break;
                    case 2:
                        npcRateA = this._Mob.get_npcRate2A();
                        npcName = this._Mob.get_npcMob2().getNameId();
                        break;
                    case 3:
                        npcRateA = this._Mob.get_npcRate3A();
                        npcName = this._Mob.get_npcMob3().getNameId();
                        break;
                    case 4:
                        npcRateA = this._Mob.get_npcRate4A();
                        npcName = this._Mob.get_npcMob4().getNameId();
                        break;
                    case 5:
                        npcRateA = this._Mob.get_npcRate5A();
                        npcName = this._Mob.get_npcMob5().getNameId();
                        break;
                    case 6:
                        npcRateA = this._Mob.get_npcRate6A();
                        npcName = this._Mob.get_npcMob6().getNameId();
                        break;
                    case 7:
                        npcRateA = this._Mob.get_npcRate7A();
                        npcName = this._Mob.get_npcMob7().getNameId();
                        break;
                    case 8:
                        npcRateA = this._Mob.get_npcRate8A();
                        npcName = this._Mob.get_npcMob8().getNameId();
                        break;
                    case 9:
                        npcRateA = this._Mob.get_npcRate9A();
                        npcName = this._Mob.get_npcMob9().getNameId();
                        break;
                    case 10:
                        npcRateA = this._Mob.get_npcRate10A();
                        npcName = this._Mob.get_npcMob10().getNameId();
                }
                String si = String.valueOf(npcRateA);
                int re = si.indexOf(".");
                if (re != -1) {
                    si = si.substring(0, re + 2);
                }
                String toUser = npcName + " $402 " + si + "$367";
                if (object != null) {
                    object.broadcastPacketAll(new S_NpcChatPacket(object, toUser, 2));
                }
            }
        }
    }

    private void toAllTimeM(String info) {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() != L1Config._2154) {
                continue;
            }
            String toUser = "$376 " + info + " $377";
            object.broadcastPacketAll(new S_NpcChatPacket(object, toUser, 2));
        }
    }

    private void toAllTimeS(String info) {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() != L1Config._2154) {
                continue;
            }
            object.broadcastPacketAll(new S_NpcChatPacket(object, info, 2));
        }
    }

    private void start() {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() != L1Config._2154) {
                continue;
            }
            this._Mob.computationRate();
            String toUser = "$364";
            object.broadcastPacketAll(new S_NpcChatPacket(object, toUser, 2));
            this._Mob.set_isStart(true);
        }
    }

    public void clear() {
        // World.get().broadcastPacketToAll(new S_SystemMessage("正在重置怪物對戰。"));
        if (cancel()) {
            this._timeHandler.purge();
        }
        this._victoryNpc = null;
        this._startTime1 = 0;
        this._isOver = false;
        this._Mob.clear();
        System.gc();
    }

    private void checkVictory() {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() != L1Config._2154) {
                continue;
            }
            int MobId = this._Mob.get_MobId();
            if (_victoryNpc != null) {
                isOver();
                this._Mob.set_isStart(false);
                for (L1PcInstance listner : World.get().getAllPlayers()) {
                    if (listner.isShowWorldChat()) {
                        listner.sendPackets(new S_ServerMessage(166, "怪物對戰: $375 (" + MobId + ") $366 " + this._victoryNpc.getNameId() + "$367" + "賠率:" + get_npcRateA() + "下注金額:" + get_npcChipA()));
                    }
                }
                _log.info("怪物對戰: 第 (" + MobId + ") 場比賽的優勝者是" + this._victoryNpc.getNameId() + "。" + "賠率:" + get_npcRateA() + "下注金額:" + get_npcChipA());
            }
        }
    }

    private void isOver() {
        L1NpcInstance npcMob1 = this._Mob.get_npcMob1();
        L1NpcInstance npcMob2 = this._Mob.get_npcMob2();
        L1NpcInstance npcMob3 = this._Mob.get_npcMob3();
        L1NpcInstance npcMob4 = this._Mob.get_npcMob4();
        L1NpcInstance npcMob5 = this._Mob.get_npcMob5();
        L1NpcInstance npcMob6 = this._Mob.get_npcMob6();
        L1NpcInstance npcMob7 = this._Mob.get_npcMob7();
        L1NpcInstance npcMob8 = this._Mob.get_npcMob8();
        L1NpcInstance npcMob9 = this._Mob.get_npcMob9();
        L1NpcInstance npcMob10 = this._Mob.get_npcMob10();
        int npcId = this._victoryNpc.getNpcId();
        if (this._victoryNpc == npcMob1) {
            _npcRateA = this._Mob.get_npcRate1A();
            _npcChipA = this._Mob.get_npcChip1A();
        } else if (this._victoryNpc == npcMob2) {
            _npcRateA = this._Mob.get_npcRate2A();
            _npcChipA = this._Mob.get_npcChip2A();
        } else if (this._victoryNpc == npcMob3) {
            _npcRateA = this._Mob.get_npcRate3A();
            _npcChipA = this._Mob.get_npcChip3A();
        } else if (this._victoryNpc == npcMob4) {
            _npcRateA = this._Mob.get_npcRate4A();
            _npcChipA = this._Mob.get_npcChip4A();
        } else if (this._victoryNpc == npcMob5) {
            _npcRateA = this._Mob.get_npcRate5A();
            _npcChipA = this._Mob.get_npcChip5A();
        } else if (this._victoryNpc == npcMob6) {
            _npcRateA = this._Mob.get_npcRate6A();
            _npcChipA = this._Mob.get_npcChip6A();
        } else if (this._victoryNpc == npcMob7) {
            _npcRateA = this._Mob.get_npcRate7A();
            _npcChipA = this._Mob.get_npcChip7A();
        } else if (this._victoryNpc == npcMob8) {
            _npcRateA = this._Mob.get_npcRate8A();
            _npcChipA = this._Mob.get_npcChip8A();
        } else if (this._victoryNpc == npcMob9) {
            _npcRateA = this._Mob.get_npcRate9A();
            _npcChipA = this._Mob.get_npcChip9A();
        } else if (this._victoryNpc == npcMob10) {
            _npcRateA = this._Mob.get_npcRate10A();
            _npcChipA = this._Mob.get_npcChip10A();
        }
        int a1 = this._Mob.get_npcChip1A();
        int a2 = this._Mob.get_npcChip2A();
        int a3 = this._Mob.get_npcChip3A();
        int a4 = this._Mob.get_npcChip4A();
        int a5 = this._Mob.get_npcChip5A();
        int a6 = this._Mob.get_npcChip6A();
        int a7 = this._Mob.get_npcChip7A();
        int a8 = this._Mob.get_npcChip8A();
        int a9 = this._Mob.get_npcChip9A();
        int a10 = this._Mob.get_npcChip10A();
        int totalPrice = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + a10;
        MobblingLock.create().create(this._Mob.get_MobId(), npcId, _npcRateA, totalPrice);
        this._isOver = true;
        // World.get().broadcastPacketToAll(new S_SystemMessage("怪物對戰結束。"));
    }

    private void checkMobbling() {
        L1NpcInstance npcMob1 = this._Mob.get_npcMob1();
        L1NpcInstance npcMob2 = this._Mob.get_npcMob2();
        L1NpcInstance npcMob3 = this._Mob.get_npcMob3();
        L1NpcInstance npcMob4 = this._Mob.get_npcMob4();
        L1NpcInstance npcMob5 = this._Mob.get_npcMob5();
        L1NpcInstance npcMob6 = this._Mob.get_npcMob6();
        L1NpcInstance npcMob7 = this._Mob.get_npcMob7();
        L1NpcInstance npcMob8 = this._Mob.get_npcMob8();
        L1NpcInstance npcMob9 = this._Mob.get_npcMob9();
        L1NpcInstance npcMob10 = this._Mob.get_npcMob10();
        if (npcMob1 == null || npcMob2 == null || npcMob3 == null || npcMob4 == null || npcMob5 == null || npcMob6 == null || npcMob7 == null || npcMob8 == null || npcMob9 == null || npcMob10 == null) {
            return;
        }
        setMobbling(true);
        StartGame startGame = new StartGame();
        startGame.begin(5 * 1000);
    }

    private void nowStart() {
        int MobId = IdFactory.get().nextMobId();
        this._Mob.set_MobId(MobId);
        int[] npcid = {990000, 990001, 990002, 990003, 990004, 990005, 990006, 990007, 990008, 990009, 990010, 990011, 990012, 990013, 990014, 990015, 990016, 990017, 990018, 990019, 990020, 990021, 990022, 990023, 990024, 990025, 990026, 990027, 990028};
        int npcid1 = npcid[Random.getInt(npcid.length)];
        spawnMobblingNpc(1, npcid1);
        int npcid2 = npcid[Random.getInt(npcid.length)];
        while (npcid2 == npcid1) {
            npcid2 = npcid[Random.getInt(npcid.length)];
        }
        spawnMobblingNpc(2, npcid2);
        int npcid3 = npcid[Random.getInt(npcid.length)];
        while (npcid3 == npcid1 || npcid3 == npcid2) {
            npcid3 = npcid[Random.getInt(npcid.length)];
        }
        spawnMobblingNpc(3, npcid3);
        int npcid4 = npcid[Random.getInt(npcid.length)];
        while (npcid4 == npcid1 || npcid4 == npcid2 || npcid4 == npcid3) {
            npcid4 = npcid[Random.getInt(npcid.length)];
        }
        spawnMobblingNpc(4, npcid4);
        int npcid5 = npcid[Random.getInt(npcid.length)];
        while (npcid5 == npcid1 || npcid5 == npcid2 || npcid5 == npcid3 || npcid5 == npcid4) {
            npcid5 = npcid[Random.getInt(npcid.length)];
        }
        spawnMobblingNpc(5, npcid5);
        int npcid6 = npcid[Random.getInt(npcid.length)];
        while (npcid6 == npcid1 || npcid6 == npcid2 || npcid6 == npcid3 || npcid6 == npcid4 || npcid6 == npcid5) {
            npcid6 = npcid[Random.getInt(npcid.length)];
        }
        spawnMobblingNpc(6, npcid6);
        int npcid7 = npcid[Random.getInt(npcid.length)];
        while (npcid7 == npcid1 || npcid7 == npcid2 || npcid7 == npcid3 || npcid7 == npcid4 || npcid7 == npcid5 || npcid7 == npcid6) {
            npcid7 = npcid[Random.getInt(npcid.length)];
        }
        spawnMobblingNpc(7, npcid7);
        int npcid8 = npcid[Random.getInt(npcid.length)];
        while (npcid8 == npcid1 || npcid8 == npcid2 || npcid8 == npcid3 || npcid8 == npcid4 || npcid8 == npcid5 || npcid8 == npcid6 || npcid8 == npcid7) {
            npcid8 = npcid[Random.getInt(npcid.length)];
        }
        spawnMobblingNpc(8, npcid8);
        int npcid9 = npcid[Random.getInt(npcid.length)];
        while (npcid9 == npcid1 || npcid9 == npcid2 || npcid9 == npcid3 || npcid9 == npcid4 || npcid9 == npcid5 || npcid9 == npcid6 || npcid9 == npcid7 || npcid9 == npcid8) {
            npcid9 = npcid[Random.getInt(npcid.length)];
        }
        spawnMobblingNpc(9, npcid9);
        int npcid10 = npcid[Random.getInt(npcid.length)];
        while (npcid10 == npcid1 || npcid10 == npcid2 || npcid10 == npcid3 || npcid10 == npcid4 || npcid10 == npcid5 || npcid10 == npcid6 || npcid10 == npcid7 || npcid10 == npcid8 || npcid10 == npcid9) {
            npcid10 = npcid[Random.getInt(npcid.length)];
        }
        spawnMobblingNpc(10, npcid10);
        this._Mob.set_isWaiting(true);
        this._Mob.set_isBuy(true);
    }

    private void spawnMobblingNpc(int type, int npcid) {
        int x = 32699;
        int y = 32896;
        switch (type) {
            case 1:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
                break;
            case 2:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
                break;
            case 3:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
                break;
            case 4:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
                break;
            case 5:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
                break;
            case 6:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
                break;
            case 7:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
                break;
            case 8:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
                break;
            case 9:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
                break;
            case 10:
                x = 32699 + Random.getInt(11) - 5;
                y = 32896 + Random.getInt(11) - 5;
        }
        try {
            L1Npc l1npc = NpcTable.get().getTemplate(npcid);
            if (l1npc == null) {
                return;
            }
            try {
                String s = l1npc.getImpl();
                Constructor<?> constructor = java.lang.Class.forName("com.lineage.server.model.Instance." + s + "Instance").getConstructors()[0];
                Object[] aobj = {l1npc};
                L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(aobj);
                npc.setId(IdFactory.get().nextId());
                npc.setMap((short) 93);
                npc.setX(x);
                npc.setY(y);
                npc.setHomeX(npc.getX());
                npc.setHomeY(npc.getY());
                npc.setHeading(6);
                World.get().storeObject(npc);
                World.get().addVisibleObject(npc);
                npc.onNpcAI();
                npc.turnOnOffLight();
                npc.setMaxHp(10000);
                npc.setCurrentHp(10000);
                npc.stopHpRegeneration();
                npc.setMaxMp(5000);
                npc.setCurrentMp(5000);
                npc.stopMpRegeneration();
                switch (type) {
                    case 1:
                        this._Mob.set_npcMob1(npc);
                        break;
                    case 2:
                        this._Mob.set_npcMob2(npc);
                        break;
                    case 3:
                        this._Mob.set_npcMob3(npc);
                        break;
                    case 4:
                        this._Mob.set_npcMob4(npc);
                        break;
                    case 5:
                        this._Mob.set_npcMob5(npc);
                        break;
                    case 6:
                        this._Mob.set_npcMob6(npc);
                        break;
                    case 7:
                        this._Mob.set_npcMob7(npc);
                        break;
                    case 8:
                        this._Mob.set_npcMob8(npc);
                        break;
                    case 9:
                        this._Mob.set_npcMob9(npc);
                        break;
                    case 10:
                        this._Mob.set_npcMob10(npc);
                }
            } catch (Exception e) {
                e.getLocalizedMessage();
            }
        } catch (Exception localException1) {
        }
    }

    // 計算剩餘怪物數量
    private int MobCount(short mapId) {
        int MobCount = 0;
        for (Object obj : World.get().getVisibleObjects(mapId).values()) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mob = (L1MonsterInstance) obj;
                if (mob != null && !mob.isDead() && mob.getCurrentHp() > 0) {
                    MobCount++;
                }
            }
        }
        // World.get().broadcastPacketToAll(new S_SystemMessage("怪物對戰剩餘怪物:" +
        // MobCount));
        return MobCount;
    }

    // 結束判斷
    private class StartGame extends TimerTask {
        public StartGame() {
        }

        @Override
        public void run() {
            L1NpcInstance _npcMob1 = MobblingTime.this._Mob.get_npcMob1();
            L1NpcInstance _npcMob2 = MobblingTime.this._Mob.get_npcMob2();
            L1NpcInstance _npcMob3 = MobblingTime.this._Mob.get_npcMob3();
            L1NpcInstance _npcMob4 = MobblingTime.this._Mob.get_npcMob4();
            L1NpcInstance _npcMob5 = MobblingTime.this._Mob.get_npcMob5();
            L1NpcInstance _npcMob6 = MobblingTime.this._Mob.get_npcMob6();
            L1NpcInstance _npcMob7 = MobblingTime.this._Mob.get_npcMob7();
            L1NpcInstance _npcMob8 = MobblingTime.this._Mob.get_npcMob8();
            L1NpcInstance _npcMob9 = MobblingTime.this._Mob.get_npcMob9();
            L1NpcInstance _npcMob10 = MobblingTime.this._Mob.get_npcMob10();
            try {
                if (!_npcMob1.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob1;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                } else if (!_npcMob2.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob2;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                } else if (!_npcMob3.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob3;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                } else if (!_npcMob4.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob4;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                } else if (!_npcMob5.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob5;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                } else if (!_npcMob6.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob6;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                } else if (!_npcMob7.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob7;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                } else if (!_npcMob8.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob8;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                } else if (!_npcMob9.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob9;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                } else if (!_npcMob10.isDead()) {
                    if (MobCount((short) 93) == 1) {
                        if (MobblingTime.this._victoryNpc == null) {
                            MobblingTime.this._victoryNpc = _npcMob10;
                            setMobbling(false);
                            checkVictory();
                        }
                    }
                }
            } catch (Exception e) {
                e.getLocalizedMessage();
            }
            if (getMobbling() == true) {
                StartGame startGame = new StartGame();
                startGame.begin(10 * 1000);
            } else {
                return;
            }
        }

        public void begin(int time) {
            Timer timer = new Timer();
            timer.schedule(this, time);
        }
    }
}