package com.lineage.server.model;

import com.lineage.config.ConfigAlt;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.MonsterEnhanceTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.gametime.L1GameTime;
import com.lineage.server.model.gametime.L1GameTimeAdapter;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1SpawnTime;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.npc.NpcSpawnBossTimer;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Time;
import java.util.*;

public class L1Spawn extends L1GameTimeAdapter {  //src053
    private static final Log _log = LogFactory.getLog(L1Spawn.class);
    private static final int SPAWN_TYPE_PC_AROUND = 1;
    private final L1Npc _template;
    private final List<L1NpcInstance> _mobs = new ArrayList<>();
    private final Random _random = new Random();
    private int _id;
    private String _location;
    private int _maximumCount;
    private int _npcid;
    private int _groupId;
    private int _locx;
    private int _locy;
    private int _tmplocx;
    private int _tmplocy;
    private short _tmpmapid;
    private int _randomx;
    private int _randomy;
    private int _locx1;
    private int _locy1;
    private int _locx2;
    private int _locy2;
    private int _heading;
    private int _minRespawnDelay;
    private int _maxRespawnDelay;
    private short _mapid;
    private boolean _respaenScreen;
    private int _movementDistance;
    private boolean _rest;
    private int _spawnType;
    private int _killTime;
    private int _delayInterval;
    private L1SpawnTime _time;
    private Calendar _nextSpawnTime = null;
    private long _spawnInterval = 0L;
    private int _existTime = 0;
    private Map<Integer, Point> _homePoint = null;
    private String _name;
    private boolean _initSpawn = false;
    private boolean _spawnHomePoint;
    private L1NpcInstance npcTemp;
    private long deleteTime;
    private boolean _isBroadcast;
    private String _broadcastInfo;

    public L1Spawn(L1Npc mobTemplate) {
        _template = mobTemplate;
    }

    public static void doCrystalCave(int npcId) {
        int[] npcId2 = {46143, 46144, 46145, 46146, 46147, 46148, 46149, 46150, 46151, 46152};
        int[] doorId = {5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008, 5009, 5010};
        for (int i = 0; i < npcId2.length; i++) {
            if (npcId == npcId2[i]) {
                closeDoorInCrystalCave(doorId[i]);
            }
        }
    }

    private static void closeDoorInCrystalCave(int doorId) {
        for (final L1NpcInstance object : WorldNpc.get().all()) {
            if (object instanceof L1DoorInstance) {
                L1DoorInstance door = (L1DoorInstance) object;
                if (door.getDoorId() == doorId) {
                    door.close();
                }
            }
        }
    }

    private static boolean isSetPassable(L1NpcInstance npc) {
        boolean setPassable = !(npc instanceof L1DollInstance);
        if (npc instanceof L1DollInstance2) {
            setPassable = false;
        }
        if (npc instanceof L1EffectInstance) {
            setPassable = false;
        }
        if (npc instanceof L1FieldObjectInstance) {
            setPassable = false;
        }
        if (npc instanceof L1FurnitureInstance) {
            setPassable = false;
        }
        if (npc instanceof L1DoorInstance) {
            setPassable = false;
        }
        if (npc instanceof L1BowInstance) {
            setPassable = false;
        }
        return setPassable;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public short getMapId() {
        return _mapid;
    }

    public void setMapId(short _mapid) {
        this._mapid = _mapid;
    }

    /**
     * 是否出生於隊長周圍
     *
     */
    public boolean isRespawnScreen() {
        return _respaenScreen;
    }

    /**
     * 設定是否出生於隊長周圍
     *
     */
    public void setRespawnScreen(boolean flag) {
        _respaenScreen = flag;
    }

    public int getMovementDistance() {
        return _movementDistance;
    }

    public void setMovementDistance(int i) {
        _movementDistance = i;
    }

    public int getAmount() {
        return _maximumCount;
    }

    public void setAmount(int amount) {
        _maximumCount = amount;
    }

    public int getGroupId() {
        return _groupId;
    }

    public void setGroupId(int i) {
        _groupId = i;
    }

    /**
     * 傳回召喚編號
     *
     */
    public int getId() {
        return _id;
    }

    /**
     * 設定召喚編號
     *
     */
    public void setId(int id) {
        _id = id;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String location) {
        _location = location;
    }

    public int getLocX() {
        return _locx;
    }

    public void setLocX(int locx) {
        _locx = locx;
    }

    public int getLocY() {
        return _locy;
    }

    public void setLocY(int locy) {
        _locy = locy;
    }

    public int getNpcId() {
        return _npcid;
    }

    public int getHeading() {
        return _heading;
    }

    public void setHeading(int heading) {
        _heading = heading;
    }

    public int getRandomx() {
        return _randomx;
    }

    public void setRandomx(int randomx) {
        _randomx = randomx;
    }

    public int getRandomy() {
        return _randomy;
    }

    public void setRandomy(int randomy) {
        _randomy = randomy;
    }

    public int getLocX1() {
        return _locx1;
    }

    public void setLocX1(int locx1) {
        _locx1 = locx1;
    }

    public int getLocY1() {
        return _locy1;
    }

    public void setLocY1(int locy1) {
        _locy1 = locy1;
    }

    public int getLocX2() {
        return _locx2;
    }

    public void setLocX2(int locx2) {
        _locx2 = locx2;
    }

    public int getLocY2() {
        return _locy2;
    }

    public void setLocY2(int locy2) {
        _locy2 = locy2;
    }

    public int getMinRespawnDelay() {
        return _minRespawnDelay;
    }

    /**
     * 召喚延遲(秒)
     *
     */
    public void setMinRespawnDelay(int i) {
        _minRespawnDelay = i;
    }

    public int getMaxRespawnDelay() {
        return _maxRespawnDelay;
    }

    public void setMaxRespawnDelay(int i) {
        _maxRespawnDelay = i;
    }

    public void setNpcid(int npcid) {
        _npcid = npcid;
    }

    public int getTmpLocX() {
        return _tmplocx;
    }

    public int getTmpLocY() {
        return _tmplocy;
    }

    public short getTmpMapid() {
        return _tmpmapid;
    }

    /**
     * 是否到達出生時間
     *
     */
    private boolean isSpawnTime(L1NpcInstance npcTemp) {
        if (_nextSpawnTime != null) {
            Calendar cals = Calendar.getInstance();
            long nowTime = System.currentTimeMillis();
            cals.setTimeInMillis(nowTime);
            if (cals.after(_nextSpawnTime)) {// 已到達時間
                return true;
            }
            if (NpcSpawnBossTimer.MAP.get(npcTemp) == null) {// 尚未加入出生時間清單
                long spawnTime = _nextSpawnTime.getTimeInMillis();
                long spa = (spawnTime - nowTime) / 1000L + 5L;
                NpcSpawnBossTimer.MAP.put(npcTemp, spa);// 加入出生時間清單
            }
            return false;
        }
        return true;
    }

    public Calendar get_nextSpawnTime() {
        return _nextSpawnTime;
    }

    public void set_nextSpawnTime(Calendar next_spawn_time) {
        _nextSpawnTime = next_spawn_time;
    }

    public long get_spawnInterval() {
        return _spawnInterval;
    }

    public void set_spawnInterval(long spawn_interval) {
        _spawnInterval = spawn_interval;
    }

    /**
     * 傳回BOSS存在時間限制(分)
     *
     */
    public int get_existTime() {
        return _existTime;
    }

    /**
     * 設定BOSS存在時間限制(分)
     *
     */
    public void set_existTime(int exist_time) {
        _existTime = exist_time;
    }

    private final int calcRespawnDelay() {
        int respawnDelay = _minRespawnDelay * 1000;
        if (_delayInterval > 0) {
            respawnDelay += _random.nextInt(_delayInterval) * 1000;
        }
        if (_time != null) {
            if (_time.getWeekDays() != null && !_time.getWeekDays().isEmpty()) {
                final Calendar cal = Calendar.getInstance();
                final int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
                cal.set(Calendar.YEAR, 1970);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DATE, 1);
                respawnDelay = (int) (_time.getTimeStart().getTime() - new Time(cal.getTimeInMillis()).getTime());
                if (respawnDelay < 0) {
                    respawnDelay += 24 * 3600L * 1000L;
                }
                // 如果星期不相同
                if (!_time.getWeekDays().contains(String.valueOf(day_of_week))) {
                    long diff = 0L;
                    // 可用 , 同時設定
                    final String[] weekDays = _time.getWeekDays().split(",");
                    // 1 - 7
                    for (final String str : weekDays) {
                        final int value = Integer.parseInt(str) - day_of_week;
                        if (value > 0) {
                            diff = (value - 1) * 24 * 3600L * 1000L;
                            break;
                        }
                    }
                    // 如果以上都不是
                    if (diff == 0L) {
                        int week = Integer.parseInt(weekDays[0]);
                        diff = getWeekdayArea(day_of_week, week) * 24 * 3600L * 1000L;
                    }
                    respawnDelay += diff;
                }
            } else {
                // 指定時間外指定時間時間足
                final L1GameTime currentTime = L1GameTimeClock.getInstance().currentTime();
                if (!_time.getTimePeriod().includes(currentTime)) {
                    long diff = _time.getTimeStart().getTime() - currentTime.toTime().getTime();
                    if (diff < 0) {
                        diff += 24 * 3600L * 1000L;
                    }
                    diff /= 6; // real time to game time
                    respawnDelay = (int) diff;
                }
            }
        }
        return respawnDelay;
    }

    private int getWeekdayArea(int NowWeek, int oldWeek) {
        // 判斷取得的數值等於星期幾
        switch (NowWeek) {
            case Calendar.MONDAY:
                switch (oldWeek) {
                    case Calendar.MONDAY:
                        return 0;
                    case Calendar.TUESDAY:
                        return 1;
                    case Calendar.WEDNESDAY:
                        return 2;
                    case Calendar.THURSDAY:
                        return 3;
                    case Calendar.FRIDAY:
                        return 4;
                    case Calendar.SATURDAY:
                        return 5;
                    case Calendar.SUNDAY:
                        return 6;
                }
            case Calendar.TUESDAY:
                switch (oldWeek) {
                    case Calendar.MONDAY:
                        return 6;
                    case Calendar.TUESDAY:
                        return 0;
                    case Calendar.WEDNESDAY:
                        return 1;
                    case Calendar.THURSDAY:
                        return 2;
                    case Calendar.FRIDAY:
                        return 3;
                    case Calendar.SATURDAY:
                        return 4;
                    case Calendar.SUNDAY:
                        return 5;
                }
            case Calendar.WEDNESDAY:
                switch (oldWeek) {
                    case Calendar.MONDAY:
                        return 5;
                    case Calendar.TUESDAY:
                        return 6;
                    case Calendar.WEDNESDAY:
                        return 0;
                    case Calendar.THURSDAY:
                        return 1;
                    case Calendar.FRIDAY:
                        return 2;
                    case Calendar.SATURDAY:
                        return 3;
                    case Calendar.SUNDAY:
                        return 4;
                }
            case Calendar.THURSDAY:
                switch (oldWeek) {
                    case Calendar.MONDAY:
                        return 4;
                    case Calendar.TUESDAY:
                        return 5;
                    case Calendar.WEDNESDAY:
                        return 6;
                    case Calendar.THURSDAY:
                        return 0;
                    case Calendar.FRIDAY:
                        return 1;
                    case Calendar.SATURDAY:
                        return 2;
                    case Calendar.SUNDAY:
                        return 3;
                }
            case Calendar.FRIDAY:
                switch (oldWeek) {
                    case Calendar.MONDAY:
                        return 3;
                    case Calendar.TUESDAY:
                        return 4;
                    case Calendar.WEDNESDAY:
                        return 5;
                    case Calendar.THURSDAY:
                        return 6;
                    case Calendar.FRIDAY:
                        return 0;
                    case Calendar.SATURDAY:
                        return 1;
                    case Calendar.SUNDAY:
                        return 2;
                }
            case Calendar.SATURDAY:
                switch (oldWeek) {
                    case Calendar.MONDAY:
                        return 2;
                    case Calendar.TUESDAY:
                        return 3;
                    case Calendar.WEDNESDAY:
                        return 4;
                    case Calendar.THURSDAY:
                        return 5;
                    case Calendar.FRIDAY:
                        return 6;
                    case Calendar.SATURDAY:
                        return 0;
                    case Calendar.SUNDAY:
                        return 1;
                }
            case Calendar.SUNDAY:
                switch (oldWeek) {
                    case Calendar.MONDAY:
                        return 1;
                    case Calendar.TUESDAY:
                        return 2;
                    case Calendar.WEDNESDAY:
                        return 3;
                    case Calendar.THURSDAY:
                        return 4;
                    case Calendar.FRIDAY:
                        return 5;
                    case Calendar.SATURDAY:
                        return 6;
                    case Calendar.SUNDAY:
                        return 0;
                }
        }
        return 0;
    }

    public void executeSpawnTask(int spawnNumber, int objectId) {
        if (_nextSpawnTime != null) {
            doSpawn(spawnNumber, objectId);
        } else {
            SpawnTask task = new SpawnTask(spawnNumber, objectId, calcRespawnDelay());
            task.getStart();
        }
    }

    /**
     * 執行初始化召喚
     */
    public void init() {
        if (_time != null && _time.isDeleteAtEndTime()) {
            // 時間外削除指定、時間經過通知受。
            L1GameTimeClock.getInstance().addListener(this);
        }
        _delayInterval = _maxRespawnDelay - _minRespawnDelay;
        _initSpawn = true;
        if (ConfigAlt.SPAWN_HOME_POINT && ConfigAlt.SPAWN_HOME_POINT_COUNT <= getAmount() && ConfigAlt.SPAWN_HOME_POINT_DELAY >= getMinRespawnDelay() && isAreaSpawn()) {
            _spawnHomePoint = true;
            _homePoint = new HashMap<>();
        }
        int spawnNum = 0;
        while (spawnNum < _maximumCount) {
            doSpawn(++spawnNum);
        }
        _initSpawn = false;
    }

    /**
     * 執行召喚
     *
     */
    protected void doSpawn(final int spawnNumber) { // 初期配置
        // 指定時間外、次spawn予約終。
        if (_time != null) {
            if (_time.getWeekDays() != null && !_time.getWeekDays().isEmpty()) {
                executeSpawnTask(spawnNumber, 0);
                return;
            }
            // 指定時間外指定時間時間足
            final L1GameTime currentTime = L1GameTimeClock.getInstance().currentTime();
            if (!_time.getTimePeriod().includes(currentTime)) {
                executeSpawnTask(spawnNumber, 0);
                return;
            }
        }
        this.doSpawn(spawnNumber, 0);
    }

    /**
     * 開始怪物出生處理<br>
     * 該方法可能會產生怪物越來越少的問題<br>
     * 可能需要重構<br>
     * 代码檢查 by 聖子默默 2024/5/16 07:02:30<br>
     *
     * @param spawnNumber number
     * @param objectId    obj
     */
    protected void doSpawn(int spawnNumber, int objectId) {
        L1NpcInstance npc;
        _tmplocx = 0;
        _tmplocy = 0;
        _tmpmapid = 0;
        try {
            int newlocx = getLocX();
            int newlocy = getLocY();
            int tryCount = 0;
            npc = NpcTable.get().newNpcInstance(_template);
            if (npc == null) {
                _log.warn("npcId:" + _template + "可能设定错误.");
                return;
            }
            /* --
            if (_time != null && (_time.isDeleteAtEndTime() || this._time.getWeekDays() != null && !this._time.getWeekDays().isEmpty())) {
                // 因刷怪機制的差異可能性 此處個性可能待觀察 by 聖子默默
                synchronized (_mobs) {
                    _mobs.add(npc);
                }
                // 但是SQL表相關資料是空的 so 這條新增判斷不會有毛病 by 聖子默默
            }*/
            int id;
            if (objectId == 0) {
                id = IdFactoryNpc.get().nextId();
            } else {
                L1Object object = World.get().findObject(objectId);
                id = object != null ? IdFactoryNpc.get().nextId() : objectId;
            }
            npc.setId(id);
            if (getHeading() >= 0 && getHeading() <= 7) {
                npc.setHeading(getHeading());
            } else {
                npc.setHeading(5);
            }
            if (getKillTime() > 0) { // 回收
                L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, getKillTime() * 1000);
                timer.begin();
            }
            // boss隨機出生樓層
            int npcId = npc.getNpcTemplate().get_npcId();
            if (npcId == 45488 && getMapId() == 9) {
                npc.setMap((short) (getMapId() + _random.nextInt(2)));
            } else if (npcId == 45601 && getMapId() == 11) {
                npc.setMap((short) (getMapId() + _random.nextInt(3)));
            } else if (npcId == 45649 && getMapId() == 80) {
                npc.setMap((short) (getMapId() + _random.nextInt(3)));
            } else if (npcId == 105040 && getMapId() == 78) {
                npc.setMap((short) (getMapId() + _random.nextInt(5)));
            } else if (npcId == 105070 && getMapId() == 80) {
                npc.setMap((short) (getMapId() + _random.nextInt(3)));
            } else {
                npc.setMap(getMapId());
            }
            npc.setMovementDistance(getMovementDistance());
            npc.setRest(isRest());
            while (tryCount <= 50) {
                if (isAreaSpawn()) {// 區域召喚
                    Point pt;
                    if (_spawnHomePoint && (pt = _homePoint.get(spawnNumber)) != null) {
                        L1Location loc = new L1Location(pt, getMapId()).randomLocation(ConfigAlt.SPAWN_HOME_POINT_RANGE, false);
                        newlocx = loc.getX();
                        newlocy = loc.getY();
                    } else {
                        int rangeX = getLocX2() - getLocX1();
                        int rangeY = getLocY2() - getLocY1();
                        newlocx = _random.nextInt(rangeX) + getLocX1();
                        newlocy = _random.nextInt(rangeY) + getLocY1();
                    }
                    if (tryCount > 49) { // 已經召喚失敗次數
                        if (_nextSpawnTime == null) {
                            newlocx = this.getLocX();
                            newlocy = this.getLocY();
                        } else {
                            // 延後5秒後重試
                            final SpawnTask task = new SpawnTask(spawnNumber, npc.getId(), 5000L);
                            task.getStart();
                            return;
                        }
                    }
                } else if (isRandomSpawn()) {// 範圍召喚
                    newlocx = getLocX() + (int) (Math.random() * getRandomx()) - (int) (Math.random() * getRandomx());
                    newlocy = getLocY() + (int) (Math.random() * getRandomy()) - (int) (Math.random() * getRandomy());
                } else {// 定點召喚
                    newlocx = getLocX();
                    newlocy = getLocY();
                }
                if (getSpawnType() == SPAWN_TYPE_PC_AROUND) {// 閃避PC
                    L1Location loc = new L1Location(newlocx, newlocy, getMapId());
                    ArrayList<?> pcs = World.get().getVisiblePc(loc);
                    if (!pcs.isEmpty()) {
                        L1Location newloc = loc.randomLocation(20, false);
                        newlocx = newloc.getX();
                        newlocy = newloc.getY();
                    }
                }
                npc.setX(newlocx);
                npc.setHomeX(newlocx);
                npc.setY(newlocy);
                npc.setHomeY(newlocy);
                if (_nextSpawnTime == null) {
                    if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation(), npc)) {
                        if (npc instanceof L1MonsterInstance) {
                            if (this.isRespawnScreen()) {
                                break;
                            }
                            // 19格內PC物件
                            final ArrayList<L1PcInstance> pcs = World.get().getVisiblePc(npc.getLocation());
                            if (pcs.isEmpty()) {
                                break;
                            }
                            // 畫面內具有PC物件 延後5秒
                            final SpawnTask task = new SpawnTask(spawnNumber, npc.getId(), 5000L);
                            task.getStart();
                            return;
                        }
                    }
                } else {
                    // 座標可通行決定召喚位置
                    if (npc.getMap().isPassable(npc.getLocation(), npc)) {
                        break;
                    }
                }
                tryCount++;// 失敗次數+1
            }
            if (npc instanceof L1MonsterInstance) {
                ((L1MonsterInstance) npc).initHide();
            }
            npc.setSpawn(this);// 暫存出生資訊
            npc.setreSpawn(true);
            npc.setSpawnNumber(spawnNumber);
            if (_initSpawn && _spawnHomePoint) {
                Point pt = new Point(npc.getX(), npc.getY());
                _homePoint.put(spawnNumber, pt);
            }
            npcTemp = npc;
            if (_nextSpawnTime != null && !isSpawnTime(npcTemp)) {// 尚未到達出生時間
                return;
            }
            if (npcTemp instanceof L1MonsterInstance) {
                L1MonsterInstance mob = (L1MonsterInstance) npcTemp;
                if (mob.getMapId() == 666) {
                    mob.set_storeDroped(true);
                }
            }
            if (npcId == 45573 && npcTemp.getMapId() == 2) {
                for (L1PcInstance pc : World.get().getAllPlayers()) {
                    if (pc.getMapId() == 2) {
                        L1Teleport.teleport(pc, 32664, 32797, (short) 2, 0, true);
                    }
                }
            }
            if (npcId == 46142 && npcTemp.getMapId() == 73 || npcId == 46141 && npcTemp.getMapId() == 74) {
                for (L1PcInstance pc : World.get().getAllPlayers()) {
                    if (pc.getMapId() >= 72 && pc.getMapId() <= 74) {
                        L1Teleport.teleport(pc, 32840, 32833, (short) 72, pc.getHeading(), true);
                    }
                }
            }
            // 怪物強化系統 by erics4179
            if (MonsterEnhanceTable.getInstance().getTemplate(npcId) != null) {
                final L1MonsterInstance mob;
                if (npcTemp instanceof L1MonsterInstance) {
                    mob = (L1MonsterInstance) npcTemp;
                    L1MonsterEnhanceInstance mei = MonsterEnhanceTable.getInstance().getTemplate(mob.getNpcId());
                    int divisor = mei.getCurrentDc() / mei.getDcEnhance();
                    mob.setLevel(mob.getLevel() + mei.getLevel() * divisor);
                    mob.setMaxHp(mob.getMaxHp() + mei.getHp() * divisor);
                    mob.setMaxMp(mob.getMaxMp() + mei.getMp() * divisor); // 修正為 getMp()
                    mob.setCurrentHp(mob.getMaxHp());
                    mob.setCurrentMp(mob.getMaxMp());
                    mob.setAc(mob.getAc() + mei.getAc() * divisor);
                    mob.setStr(mob.getStr() + mei.getStr() * divisor);
                    mob.setDex(mob.getDex() + mei.getDex() * divisor);
                    mob.setCon(mob.getCon() + mei.getCon() * divisor);
                    mob.setWis(mob.getWis() + mei.getWis() * divisor);
                    mob.setInt(mob.getInt() + mei.getIntelligence() * divisor); // 使用 getIntelligence()
                    mob.setMr(mob.getMr() + mei.getMr() * divisor);
                }
            }
            if (npcTemp instanceof L1MonsterInstance) {
                final L1MonsterInstance mobtemp = (L1MonsterInstance) npcTemp;
                if (!this._initSpawn && mobtemp.getHiddenStatus() == 0) {
                    mobtemp.onNpcAI(); // AI啟用
                }
                if (_existTime > 0) {
                    // 存在時間(秒)
                    mobtemp.set_spawnTime(_existTime * 60);
                }
            }
            doCrystalCave(npcId);
            World.get().storeObject(npcTemp);
            World.get().addVisibleObject(npcTemp);
            if (npcTemp instanceof L1MonsterInstance) {
                L1MonsterInstance mobtemp = (L1MonsterInstance) npcTemp;
                if (!_initSpawn && mobtemp.getHiddenStatus() == 0) {
                    mobtemp.onNpcAI();
                }
                /*
                 * if (_existTime > 0) { mobtemp.set_spawnTime(_existTime * 60);
                 * }
                 */
            }
            if (getGroupId() != 0) {// 具有隊伍資訊
                L1MobGroupSpawn.getInstance().doSpawn(npcTemp, getGroupId(), isRespawnScreen(), _initSpawn);
            }
            npcTemp.turnOnOffLight();
            npcTemp.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);   //src053
            if (isBroadcast() && getBroadcastInfo() != null && !getBroadcastInfo().isEmpty()) {
                World.get().broadcastPacketToAll(new S_SystemMessage(String.format(getBroadcastInfo(), npc.getName())));
            }
            if (this._time != null) {
                final String msg = this._time.getSpawnMsg();
                if (msg != null && !msg.isEmpty()) {
                    final ServerBasePacket packet;
                    if (msg.startsWith("$")) {
                        packet = new S_ServerMessage(Integer.parseInt(msg.substring(1)), "    " + npc.getNameId());
                    } else {
                        packet = new S_ServerMessage("\\F3" + msg);
                    }
                    World.get().broadcastPacketToAll(packet);
                }
            }
            _tmplocx = newlocx;
            _tmplocy = newlocy;
            _tmpmapid = npcTemp.getMapId();
            /* 取消设定 对游戏没有太大影响
            boolean setPassable = isSetPassable(npc);
            if (setPassable) {
                L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), false, 2);
            }*/
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean isRest() {
        return _rest;
    }

    public void setRest(boolean flag) {
        _rest = flag;
    }

    private int getSpawnType() {
        return _spawnType;
    }

    public void setSpawnType(int type) {
        _spawnType = type;
    }

    private int getKillTime() {
        return _killTime;
    }

    public void setKillTime(int i) {
        _killTime = i;
    }

    private boolean isAreaSpawn() {
        return getLocX1() != 0 && getLocY1() != 0 && getLocX2() != 0 && getLocY2() != 0;
    }

    private boolean isRandomSpawn() {
        return getRandomx() != 0 || getRandomy() != 0;
    }

    public L1SpawnTime getTime() {
        return _time;
    }

    public void setTime(L1SpawnTime time) {
        _time = time;
    }

    @Override
    public void onMinuteChanged(final L1GameTime time) {
        if (this._time.getWeekDays() != null && !this._time.getWeekDays().isEmpty()) {
            if (this._time.getTimePeriod().includes(time.toRealTime(System.currentTimeMillis()))) {
                return;
            }
        } else {
            if (this._time.getTimePeriod().includes(time)) {
                return;
            }
        }
        synchronized (this._mobs) {
            if (this._mobs.isEmpty()) {
                return;
            }
            // 指定時間外削除
            for (final L1NpcInstance mob : this._mobs) {
                mob.setCurrentHpDirect(0);
                mob.setDead(true);
                mob.setStatus(ActionCodes.ACTION_Die);
                mob.deleteMe();
            }
            this._mobs.clear();
        }
    }

    /**
     * 傳回NpcInstance資料
     *
     */
    public final L1NpcInstance getNpcTemp() {
        return npcTemp;
    }

    public final long getDeleteTime() {
        return deleteTime;
    }

    public final void setDeleteTime(long deleteTime) {
        this.deleteTime = deleteTime;
    }

    public final boolean isBroadcast() {
        return this._isBroadcast;
    }

    public final void setBroadcast(final boolean isBroadcast) {
        this._isBroadcast = isBroadcast;
    }

    public final String getBroadcastInfo() {
        return this._broadcastInfo;
    }

    public final void setBroadcastInfo(final String broadcastInfo) {
        this._broadcastInfo = broadcastInfo;
    }

    private class SpawnTask implements Runnable {
        private final int _spawnNumber;
        private final int _objectId;
        private final long _delay;

        private SpawnTask(int spawnNumber, int objectId, long delay) {
            _spawnNumber = spawnNumber;
            _objectId = objectId;
            _delay = delay;
        }

        public void getStart() {
            GeneralThreadPool.get().schedule(this, _delay);
        }

        public void run() {
            doSpawn(_spawnNumber, _objectId);
        }
    }
}
