package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigGuardTower;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.*;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerRestartTimer;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 守護塔
 *
 * @author hero<br>
 */
public class GuardTower {
    private static final Log _log = LogFactory.getLog(GuardTower.class);
    private static final Map<Integer, GuardTower> _guardtowerlist = new HashMap<Integer, GuardTower>();
    private static final int STATUS_NONE = 0;
    private static final int STATUS_READY = 1;
    private static final int STATUS_PLAYING = 2;
    private static final int TOWERID = ConfigGuardTower.TOWERID;
    private static final int TOWERX = ConfigGuardTower.TOWERX;
    private static final int TOWERY = ConfigGuardTower.TOWERY;
    private static final int MAXPC = ConfigGuardTower.MAXPC;
    private static final int MAPX = ConfigGuardTower.MAPX;
    private static final int MAPY = ConfigGuardTower.MAPY;
    private static final int MAPID = ConfigGuardTower.MAPID;
    private static final int NEEDITEM = ConfigGuardTower.NEEDITEM;
    private static final int NEEDITEMCOUNT = ConfigGuardTower.NEEDITEMCOUNT;
    private static final int GIFT = ConfigGuardTower.GIFT;
    private static final int GIFT_Count = ConfigGuardTower.GIFT_Count;
    private static final int COMFORTGIFT = ConfigGuardTower.COMFORTGIFT;
    private static final int COMFORTGIFT_Count = ConfigGuardTower.ComfortGift_COUNT;
    static L1MonsterInstance Tower = null;
    private static GuardTower instance;
    private static boolean SpawnStart = false;
    private static boolean MobAllDeath = false;
    private Timer _timeHandler = new Timer(true);
    private ScheduledFuture<?> _timer;
    private ArrayList<Integer> _guardtime = new ArrayList<Integer>();
    private int _id;
    private int _npcid;
    private int _count;
    private int _spawn_delay;
    private String _mags;
    private int _RoomStatus = STATUS_NONE;

    public static GuardTower get() {
        if (instance == null) {
            instance = new GuardTower();
        }
        return instance;
    }

    private static int PcCount() {
        int PcCount = 0;
        for (Object obj : World.get().getVisibleObjects(MAPID).values()) {
            if (obj instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) obj;
                if (pc != null && !pc.isDead() && !pc.isGhost()) {
                    if (Tower != null) {
                        pc.sendPackets(new S_HPMeter(Tower));
                    }
                    PcCount++;
                }
            }
        }
        return PcCount;
    }

    private static int MobCount() {
        int MobCount = 0;
        for (Object obj : World.get().getVisibleObjects(MAPID).values()) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mob = (L1MonsterInstance) obj;
                if (mob.getNpcId() == TOWERID) {
                    continue;
                }
                MobCount++;
            }
        }
        return MobCount;
    }

    private static boolean TowerIsAlive() {
        for (Object obj : World.get().getVisibleObjects(MAPID).values()) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance npc = (L1MonsterInstance) obj;
                if (npc.getNpcId() == TOWERID && !npc.isDead()) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getSpawnDeldy() {
        return _spawn_delay;
    }

    private void setSpawnDeldy(final int i) {
        _spawn_delay = i;
    }

    private int getCount() {
        return _count;
    }

    private void setCount(final int i) {
        _count = i;
    }

    private int getId() {
        return _id;
    }

    private void setId(final int i) {
        _id = i;
    }

    private int getNpcId() {
        return _npcid;
    }

    private void setNpcId(final int i) {
        _npcid = i;
    }

    private String getMsg() {
        return _mags;
    }

    private void setMsg(final String s) {
        _mags = s;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `系統_守塔活動怪物設定`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final GuardTower Gt = new GuardTower();
                Gt.setId(rs.getInt("波次"));
                Gt.setNpcId(rs.getInt("Npc編號"));
                Gt.setCount(rs.getInt("數量"));
                Gt.setSpawnDeldy(rs.getInt("延遲幾秒"));
                Gt.setMsg(rs.getString("文字公告顯示"));
                _guardtowerlist.put(Gt.getId(), Gt);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->守塔活動召喚資料數量: " + _guardtowerlist.size() + "(" + timer.get() + "ms)");
    }

    public void LoadTime() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `系統_守塔活動時間設定`");
            rs = ps.executeQuery();
            while (rs.next()) {
                _guardtime.add(rs.getInt("整點(24H)"));
                i++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->守塔活動時間軸: " + i + "(" + timer.get() + "ms)");
    }

    private ArrayList<Integer> GuardTimes() {
        return _guardtime;
    }

    private int getRoomStatus() {
        return _RoomStatus;
    }

    private void setRoomStatus(final int i) {
        _RoomStatus = i;
    }

    private void AllMessage(String msg) {
        for (Object obj : World.get().getVisibleObjects(MAPID).values()) {
            if (obj instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) obj;
                if (pc != null && !pc.isGhost()) {
                    pc.sendPackets(new S_ServerMessage("" + msg));
                }
            }
        }
    }

    private void ClearRoom() {
        for (Object obj : World.get().getVisibleObjects(MAPID).values()) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mob = (L1MonsterInstance) obj;
                if (!mob.isDead()) {
                    mob.setDead(true);
                    mob.setStatus(ActionCodes.ACTION_Die);
                    mob.setCurrentHpDirect(0);
                    mob.deleteMe();
                }
            } else if (obj instanceof L1Inventory) {
                L1Inventory inventory = (L1Inventory) obj;
                inventory.clearItems();
            } else if (obj instanceof L1NpcInstance) {
                L1NpcInstance mob = (L1NpcInstance) obj;
                if (!mob.isDead()) {
                    mob.setDead(true);
                    mob.setStatus(ActionCodes.ACTION_Die);
                    mob.setCurrentHpDirect(0);
                    mob.deleteMe();
                }
            }
        }
    }

    private void StartTimer() {
        new BuyTicket().begin();
    }

    private void spawn(int npcid) {
        int X = 0;
        int Y = 0;
        final L1MonsterInstance mob = new L1MonsterInstance(NpcTable.get().getTemplate(npcid));
        if (npcid == TOWERID) {
            X = TOWERX;
            Y = TOWERY;
            Tower = mob;
        } else {
            int index = ThreadLocalRandom.current().nextInt(ConfigGuardTower.X.size());
            X = ConfigGuardTower.X.get(index);
            Y = ConfigGuardTower.Y.get(index);
        }
        mob.setId(IdFactoryNpc.get().nextId());
        mob.setHeading(5);
        if (npcid == TOWERID) {
            mob.setTempCharGfx(9484);
            mob.setGfxId(9484);
        }
        mob.setX(X);
        mob.setY(Y);
        mob.setHomeX(mob.getX());
        mob.setHomeY(mob.getY());
        mob.setMap((short) MAPID);
        World.get().storeObject(mob);
        World.get().addVisibleObject(mob);
        final S_NPCPack s_npcPack = new S_NPCPack(mob);
        for (final L1PcInstance pc : World.get().getRecognizePlayer(mob)) {
            pc.addKnownObject(mob);
            mob.addKnownObject(pc);
            pc.sendPackets(s_npcPack);
        }
        mob.onNpcAI();
        mob.turnOnOffLight();
    }

    private void endRoom() {
        if (TowerIsAlive()) {
            for (Object obj : World.get().getVisibleObjects(MAPID).values()) {
                if (obj instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) obj;
                    if (pc != null && !pc.isDead() && !pc.isGhost()) {
                        L1Teleport.teleport(pc, 33448, 32793, (short) 4, pc.getHeading(), true);
                        if (GIFT != 0) {
                            pc.getInventory().storeItem(GIFT, GIFT_Count);
                            L1Item item2 = ItemTable.get().getTemplate(GIFT);
                            pc.sendPackets(new S_ServerMessage("恭喜你獲得活動獎勵(" + item2.getName() + ")"));
                        }
                    }
                }
            }
        } else {
            for (Object obj : World.get().getVisibleObjects(MAPID).values()) {
                if (obj instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) obj;
                    if (pc != null && !pc.isDead() && !pc.isGhost()) {
                        L1Teleport.teleport(pc, 33448, 32793, (short) 4, pc.getHeading(), true);
                        if (GIFT != 0) {
                            pc.getInventory().storeItem(COMFORTGIFT, COMFORTGIFT_Count);
                            L1Item item2 = ItemTable.get().getTemplate(COMFORTGIFT);
                            pc.sendPackets(new S_ServerMessage("恭喜你獲得活動獎勵(" + item2.getName() + ")"));
                        }
                    }
                }
            }
        }
        World.get().broadcastPacketToAll(new S_SystemMessage("「守塔活動」已結束!歡迎各位下次再來挑戰..."));
        World.get().broadcastPacketToAll(new S_GreenMessage("\\f2「守塔活動」已結束!歡迎各位下次再來挑戰..."));
        ClearRoom();
    }

    public String enterRoom(final L1PcInstance pc) {
        if (getRoomStatus() == STATUS_NONE) {
            pc.sendPackets(new S_SystemMessage("\\fT目前守塔活動尚未開放。"));
            return "";
        }
        if (getRoomStatus() == STATUS_PLAYING) {
            pc.sendPackets(new S_SystemMessage("\\fT守塔活動已經開始。"));
            return "";
        }
        if (PcCount() >= MAXPC) {
            pc.sendPackets(new S_SystemMessage("\\fT守塔活動參與人數已滿無法進入。"));
            return "";
        }
        if (!pc.getInventory().checkItem(NEEDITEM, NEEDITEMCOUNT)) {
            pc.sendPackets(new S_SystemMessage("\\fT您的參賽所需道具數量不足"));
            return "";
        }
        if (pc.isInParty()) {
            pc.getParty().leaveMember(pc);
        }
        if (pc.getTradeID() != 0) {
            final L1Trade trade = new L1Trade();
            trade.tradeCancel(pc);
        }
        final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan != null) {
            if (clan.getWarehouseUsingChar() == pc.getId()) {
                clan.setWarehouseUsingChar(0);
            }
        }
        pc.getInventory().consumeItem(NEEDITEM, NEEDITEMCOUNT);
        L1Teleport.teleport(pc, MAPX, MAPY, (short) MAPID, pc.getHeading(), true);
        return "";
    }

    public class GuardTowerTime extends TimerTask {
        public void start() {
            final int timeMillis = 60 * 1000;
            _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
        }

        @Override
        public void run() {
            try {
                final String mTime = TimeInform.time().getNow_YMDHMS(3);
                final String hTime = TimeInform.time().getNow_YMDHMS(4);
                int mm = Integer.parseInt(mTime);
                int hh = Integer.parseInt(hTime);
                for (int Nowtime : GuardTimes()) {
                    if (Nowtime == hh && mm == 0) {
                        if (ServerRestartTimer.isRtartTime()) {
                            World.get().broadcastPacketToAll(new S_SystemMessage("\\fR由於伺服器即將維護重開，本次守塔活動暫不開放"));
                            World.get().broadcastPacketToAll(new S_GreenMessage("\\f2由於伺服器即將維護重開，本次守塔活動暫不開放"));
                        } else {
                            World.get().broadcastPacketToAll(new S_SystemMessage("\\fU「守塔活動」已開放報名參加，剩餘時間5分鐘!!!"));
                            World.get().broadcastPacketToAll(new S_GreenMessage("\\f2「守塔活動」已開放報名參加剩下時間5分鐘!!!"));
                            StartTimer();
                        }
                        break;
                    }
                }
            } catch (final Exception e) {
                _log.error("守塔活動時間軸異常,活動取消", e);
                GeneralThreadPool.get().cancel(_timer, false);
            }
        }
    }

    private class BuyTicket extends TimerTask {
        @Override
        public void run() {
            setRoomStatus(STATUS_PLAYING);
            World.get().broadcastPacketToAll(new S_SystemMessage("\\fT「守塔活動」報名已結束!活動正式開始..."));
            World.get().broadcastPacketToAll(new S_GreenMessage("\\f2「守塔活動」報名已結束!活動正式開始..."));
            new ReciprocalTime().L1CrackTime();
        }

        private void begin() {
            setRoomStatus(STATUS_READY);
            Timer timer = new Timer();
            timer.schedule(this, 5 * 60 * 1000);
        }
    }

    private class ReciprocalTime extends TimerTask {
        private int _startTime = 0;

        private void L1CrackTime() {
            _timeHandler.schedule(this, 1000, 1000);
            GeneralThreadPool.get().execute(this);
        }

        @Override
        public void run() {
            _startTime++;
            String msg = null;
            switch (_startTime) {
                case 1:
                    msg = "\\f3距離活動開始時間剩下10秒";
                    break;
                case 6:
                    msg = "5";
                    break;
                case 7:
                    msg = "4";
                    break;
                case 8:
                    msg = "3";
                    break;
                case 9:
                    msg = "2";
                    break;
                case 10:
                    if (PcCount() > 0) {
                        spawn(TOWERID);
                        for (Object obj : World.get().getVisibleObjects(MAPID).values()) {
                            if (obj instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) obj;
                                pc.sendPackets(new S_GreenMessage("活動開始"));
                                if (pc.getTradeID() != 0) {
                                    final L1Trade trade = new L1Trade();
                                    trade.tradeCancel(pc);
                                }
                            }
                        }
                        new PlayGame().GameTime();
                    } else {
                        World.get().broadcastPacketToAll(new S_SystemMessage("守塔活動參加人數不足，本場取消。"));
                        setRoomStatus(STATUS_NONE);
                    }
                    cancel();
                    break;
            }
            if (msg != null) {
                AllMessage(msg);
            }
        }
    }

    private class SpawnMob extends TimerTask {
        private int _startTime = 0;
        private int _rand = 0;

        private void SpawnTime() {
            _timeHandler.schedule(this, 1000, 1000);
            GeneralThreadPool.get().execute(this);
        }

        @Override
        public void run() {
            if (getRoomStatus() == STATUS_PLAYING) {
                int r = _guardtowerlist.size() + 1;
                if (_rand > r) {
                    if (MobCount() == 0) {
                        MobAllDeath = true;
                    }
                } else {
                    final GuardTower Gt = _guardtowerlist.get(_rand);
                    if (Gt != null) {
                        if (Gt.getSpawnDeldy() != 0) {
                            _startTime++;
                            if (_startTime == Gt.getSpawnDeldy()) {
                                if (Gt.getMsg() != null) {
                                    AllMessage("" + Gt.getMsg());
                                }
                                for (int i = 0; i < Gt.getCount(); i++) {
                                    spawn(Gt.getNpcId());
                                }
                                _startTime = 0;
                                _rand++;
                            }
                        } else {
                            if (Gt.getMsg() != null) {
                                AllMessage("" + Gt.getMsg());
                            }
                            for (int i = 0; i < Gt.getCount(); i++) {
                                spawn(Gt.getNpcId());
                            }
                            _rand++;
                        }
                    } else {
                        _rand++;
                    }
                }
            } else {
                cancel();
            }
        }
    }

    private class PlayGame extends TimerTask {
        private int _startTime = 0;

        private void GameTime() {
            _timeHandler.schedule(this, 1000, 1000);
            GeneralThreadPool.get().execute(this);
        }

        @Override
        public void run() {
            _startTime++;
            String msg = null;
            if (_startTime == 2340) {
                msg = "\\fH距離活動結束時間剩下一分鐘 ...";
            }
            if (_startTime == 2400) {
                msg = "\\fAH時間到 ...";
                new EndGame().GameTime();
                cancel();
            } else {
                if (TowerIsAlive()) {
                    if (PcCount() > 0) {
                        if (SpawnStart == false) {
                            SpawnStart = true;
                            new SpawnMob().SpawnTime();
                        }
                    } else {
                        msg = "\\fU全體參加者陣亡 ...";
                        new EndGame().GameTime();
                        cancel();
                    }
                } else {
                    msg = "\\fU守護者之塔死亡 ...";
                    new EndGame().GameTime();
                    cancel();
                }
                if (MobAllDeath) {
                    msg = "\\fU所有入侵者都已經被擊殺 ...";
                    new EndGame().GameTime();
                    cancel();
                }
                if (msg != null) {
                    AllMessage(msg);
                }
            }
        }
    }

    private class EndGame extends TimerTask {
        private int _startTime = 0;

        private void GameTime() {
            _timeHandler.schedule(this, 1000, 1000);
            GeneralThreadPool.get().execute(this);
        }

        @Override
        public void run() {
            setRoomStatus(STATUS_NONE);
            SpawnStart = false;
            _startTime++;
            String msg = null;
            switch (_startTime) {
                case 2:
                    msg = "\\fU守塔活動結束 請稍等 ...";
                    break;
                case 4:
                    msg = "\\fU系統正在分配是否有獎勵...。";
                    break;
                case 6:
                    MobAllDeath = false;
                    endRoom();
                    cancel();
                    break;
            }
            if (msg != null) {
                AllMessage(msg);
            }
        }
    }
}