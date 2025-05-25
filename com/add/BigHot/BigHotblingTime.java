package com.add.BigHot;

import com.add.L1Config;
import com.lineage.server.IdFactory;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.serverpackets.S_NpcChatPacket;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BigHotblingTime extends TimerTask {
    public String BigHotAN;
    private Timer _timeHandler = new Timer(true);
    private boolean _isOver = false;
    private int _BigHottTime = 0;
    private BigHotblingTimeList _BigHot = BigHotblingTimeList.BigHot();
    private int BigHotAN1 = 0;
    private int BigHotAN2 = 0;
    private int BigHotAN3 = 0;
    private int BigHotAN4 = 0;
    private int BigHotAN5 = 0;
    private int BigHotAN6 = 0;

    public void startBigHotbling() {
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
        this._BigHottTime += 1;
        switch (this._BigHottTime) {
            case 2:
                toAllTimeM("60");
                donumber1();
                World.get().broadcastPacketToAll(new S_SystemMessage("距離大樂透開獎時間還有1個小時。"));
                break;
            case 6000:
                toAllTimeM("10");
                World.get().broadcastPacketToAll(new S_SystemMessage("距離大樂透開獎時間還有10分鐘。"));
                break;
            case 6600:
                toAllTimeM("5");
                World.get().broadcastPacketToAll(new S_SystemMessage("距離大樂透開獎時間還有5分鐘。"));
                break;
            case 6720:
                toAllTimeM("4");
                break;
            case 6840:
                toAllTimeM("3");
                break;
            case 6960:
                toAllTimeM("2");
                break;
            case 7080:
                toAllTimeM("1");
                World.get().broadcastPacketToAll(new S_SystemMessage("距離大樂透開獎時間還有1分鐘。"));
                break;
            case 7190:
                toAllTimeS("5");
                break;
            case 7192:
                toAllTimeS("4");
                break;
            case 7194:
                toAllTimeS("3");
                break;
            case 7196:
                toAllTimeS("2");
                break;
            case 7118:
                toAllTimeS("1");
                break;
            case 7200:
                Start();
                break;
            case 7206:
                toRate1(1);
                break;
            case 7212:
                toRate1(2);
                break;
            case 7218:
                toRate1(3);
                break;
            case 7224:
                toRate(1, this.BigHotAN1);
                break;
            case 7234:
                toRate(2, this.BigHotAN2);
                break;
            case 7244:
                toRate(3, this.BigHotAN3);
                break;
            case 7254:
                toRate(4, this.BigHotAN4);
                break;
            case 7264:
                toRate(5, this.BigHotAN5);
                break;
            case 7274:
                toRate(6, this.BigHotAN6);
                break;
            case 7284:
                checkVictory();
                int BigHotId = this._BigHot.get_BigHotId();
                World.get().broadcastPacketToAll(new S_SystemMessage("大樂透 第 " + String.valueOf(BigHotId) + " 期開出的號碼是 " + this.BigHotAN + "。"));
        }
    }

    private void toRate(int type, int info) {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() == L1Config._2162) {
                String Name = null;
                switch (type) {
                    case 1:
                        Name = "一";
                        break;
                    case 2:
                        Name = "二";
                        break;
                    case 3:
                        Name = "三";
                        break;
                    case 4:
                        Name = "四";
                        break;
                    case 5:
                        Name = "五";
                        break;
                    case 6:
                        Name = "六";
                }
                String toUser = "開出的第" + Name + "個號碼是(" + info + ")";
                object.broadcastPacketAll(new S_NpcChatPacket(object, toUser, 2));
            }
        }
    }

    private void toRate1(int type) {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() == L1Config._2162) {
                String Name1 = null;
                int money = 0;
                switch (type) {
                    case 1:
                        Name1 = "頭獎";
                        money = this._BigHot.get_bigmoney1();
                        break;
                    case 2:
                        Name1 = "一獎";
                        money = this._BigHot.get_bigmoney2();
                        break;
                    case 3:
                        Name1 = "二獎";
                        money = this._BigHot.get_bigmoney3();
                }
                String toUser = "本期" + Name1 + "的獎金是(" + money + ")";
                object.broadcastPacketAll(new S_NpcChatPacket(object, toUser, 2));
            }
        }
    }

    private void toAllTimeM(String info) {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() == L1Config._2162) {
                String toUser = "距離開獎$376 " + info + " $377";
                object.broadcastPacketAll(new S_NpcChatPacket(object, toUser, 2));
            }
        }
    }

    private void toAllTimeS(String info) {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() == L1Config._2162) {
                object.broadcastPacketAll(new S_NpcChatPacket(object, info, 2));
            }
        }
    }

    private void Start() {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object.getNpcTemplate().get_npcId() == L1Config._2162) {
                String toUser = "大樂透即將開獎囉！！";
                object.broadcastPacketAll(new S_NpcChatPacket(object, toUser, 2));
                this._BigHot.set_isStart(true);
            }
        }
        if (this._BigHot.get_yuanbao() == 0) {
            this._BigHot.add_yuanbao(L1Config._2164);
        }
        L1BigHotbling BigHotInfo = BigHotblingLock.create().getBigHotbling(this._BigHot.get_BigHotId() - 1);
        if (BigHotInfo != null) {
            if (BigHotInfo.get_count() == 0) {
                if (BigHotInfo.get_money1() < L1Config._2166) {
                    this._BigHot.add_yuanbao(BigHotInfo.get_money1());
                } else {
                    this._BigHot.add_yuanbao(L1Config._2165);
                }
            } else {
                this._BigHot.add_yuanbao(L1Config._2165);
            }
            if (BigHotInfo.get_count1() == 0) {
                this._BigHot.add_yuanbao(BigHotInfo.get_money2());
            }
            if (BigHotInfo.get_count2() == 0) {
                this._BigHot.add_yuanbao(BigHotInfo.get_money3());
            }
        } else {
            this._BigHot.add_yuanbao(L1Config._2165);
        }
        this._BigHot.computationBigHot();
    }

    private void clear() {
        if (cancel()) {
            this._timeHandler.purge();
        }
        this.BigHotAN = null;
        this._BigHottTime = 0;
        this._isOver = false;
        this._BigHot.clear();
        System.gc();
    }

    private void checkVictory() {
        for (L1NpcInstance object : WorldNpc.get().all()) {
            if (object == null) {
                continue;
            }
            if (object.getNpcTemplate().get_npcId() == L1Config._2162 && this.BigHotAN != null) {
                int BigHotId = this._BigHot.get_BigHotId();
                String toUser = "大樂透 $375 " + String.valueOf(BigHotId) + " 期開出的號碼是 " + this.BigHotAN + "。";
                object.broadcastPacketAll(new S_NpcChatPacket(object, toUser, 2));
                isOver();
                this._BigHot.set_isStart(false);
            }
        }
    }

    private void donumber1() {
        this.BigHotAN = "";
        while (this.BigHotAN.split(",").length < 6) {
            int sk = 1 + (int) (Math.random() * 46.0D);
            if (this.BigHotAN.indexOf(sk + ",") < 0) {
                this.BigHotAN = this.BigHotAN + String.valueOf(sk) + ",";
            }
            if (this.BigHotAN.split(",").length == 1) {
                this.BigHotAN1 = sk;
            }
            if (this.BigHotAN.split(",").length == 2) {
                this.BigHotAN2 = sk;
            }
            if (this.BigHotAN.split(",").length == 3) {
                this.BigHotAN3 = sk;
            }
            if (this.BigHotAN.split(",").length == 4) {
                this.BigHotAN4 = sk;
            }
            if (this.BigHotAN.split(",").length == 5) {
                this.BigHotAN5 = sk;
            }
            if (this.BigHotAN.split(",").length == 6) {
                this.BigHotAN6 = sk;
            }
        }
        this._BigHot.set_BigHotId1(this.BigHotAN);
    }

    private void isOver() {
        int yuanbao = this._BigHot.get_yuanbao();
        int yuanbao1 = this._BigHot.get_bigmoney1();
        int yuanbao2 = this._BigHot.get_bigmoney2();
        int yuanbao3 = this._BigHot.get_bigmoney3();
        int count1 = this._BigHot.get_count1();
        int count2 = this._BigHot.get_count2();
        int count3 = this._BigHot.get_count3();
        int count4 = this._BigHot.get_count4();
        BigHotblingLock.create().create(this._BigHot.get_BigHotId(), this.BigHotAN, yuanbao, yuanbao1, count1, yuanbao2, count2, yuanbao3, count3, count4);
        this._isOver = true;
    }

    private void nowStart() {
        int BigHotId = IdFactory.get().nextBigHotId();
        this._BigHot.set_BigHotId(BigHotId);
        this._BigHot.set_isWaiting(true);
        this._BigHot.set_isBuy(true);
    }
}
