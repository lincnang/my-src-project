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
package com.lineage.server.model;

import com.lineage.config.Config;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.UBSpawnTable;
import com.lineage.server.datatables.UBTable;
import com.lineage.server.datatables.UbSupplies;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.IntRange;
import com.lineage.server.utils.Random;
import com.lineage.server.utils.collections.Lists;
import com.lineage.server.world.World;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package com.lineage.server.model:
// L1UltimateBattle
public class L1UltimateBattle {
    private static final Logger _log = Logger.getLogger(L1UltimateBattle.class.getName());
    private static int BEFORE_MINUTE = 5; // 5分前入場開始
    private final List<L1PcInstance> _members = Lists.newList();
    private int _locX;
    private int _locY;
    private L1Location _location; // 中心點
    private short _mapId;
    private int _locX1;
    private int _locY1;
    private int _locX2;
    private int _locY2;
    private int _ubId;
    private int _pattern;
    private boolean _isNowUb;
    private boolean _active; // UB入場可能～競技終了true
    private int _minLevel;
    private int _maxLevel;
    private int _maxPlayer;
    private boolean _enterRoyal;
    private boolean _enterKnight;
    private boolean _enterMage;
    private boolean _enterElf;
    private boolean _enterDarkelf;
    private boolean _enterDragonKnight;
    private boolean _enterIllusionist;
    private boolean _enterWarrior;
    private boolean _enterMale;
    private boolean _enterFemale;
    private boolean _usePot;
    private int _hpr;
    private int _mpr;
    private String _ubName; // TODO [非仿原碼] 無限大賽廣播
    private Set<Integer> _managers = new HashSet<>();
    private SortedSet<Integer> _ubTimes = new TreeSet<>();
    private String[] _ubInfo;
    /**
     * 等補給出現。
     *
     * @param curRound
     *            現在
     */
    /**
     * 。
     */
    public L1UltimateBattle() {
    }

    private static String intToTimeFormat(int n) {
        return n / 100 + ":" + n % 100 / 10 + "" + n % 10;
    }

    private static Calendar getRealTime() {
        TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
        Calendar cal = Calendar.getInstance(_tz);
        return cal;
    }

    /**
     * 開始時送信。
     *
     * @param curRound 開始
     */
    private void sendRoundMessage(int curRound) {
        // XXX - ID間違
        final int MSGID_ROUND_TABLE[] = {893, 894, 895, 896};
        sendMessage(MSGID_ROUND_TABLE[curRound - 1], "");
    }

    /**
     * [原碼] 無限大賽掉落物品
     */
    private void spawnSupplies(int curRound) {
        List<L1UbSupplie> sup = UbSupplies.getInstance().getUbSupplies(curRound);
        if (sup.size() != 0) {
            for (L1UbSupplie t : sup) {
                spawnGroundItem(t.getUbItemId(), t.getUbItemStackCont(), t.getUbItemCont());
            }
        }
    }

    /**
     * 出削除。
     */
    private void removeRetiredMembers() {
        L1PcInstance[] temp = getMembersArray();
        for (L1PcInstance element : temp) {
            if (element.getMapId() != _mapId) {
                removeMember(element);
            }
        }
    }

    /**
     * UB參加(S_ServerMessage)送信。
     *
     * @param type 
     * @param msg  送信
     */
    private void sendMessage(int type, String msg) {
        for (L1PcInstance pc : getMembersArray()) {
            pc.sendPackets(new S_ServerMessage(type, msg));
        }
    }

    /**
     * 上出現。
     *
     * @param itemId     出現ID
     * @param stackCount 數
     * @param count      出現數
     */
    private void spawnGroundItem(int itemId, int stackCount, int count) {
        L1Item temp = ItemTable.get().getTemplate(itemId);
        if (temp == null) {
            return;
        }
        for (int i = 0; i < count; i++) {
            L1Location loc = _location.randomLocation((getLocX2() - getLocX1()) / 2, false);
            if (temp.isStackable()) {
                L1ItemInstance item = ItemTable.get().createItem(itemId);
                item.setEnchantLevel(0);
                item.setCount(stackCount);
                L1GroundInventory ground = World.get().getInventory(loc.getX(), loc.getY(), _mapId);
                if (ground.checkAddItem(item, stackCount) == L1Inventory.OK) {
                    ground.storeItem(item);
                }
            } else {
                L1ItemInstance item = null;
                for (int createCount = 0; createCount < stackCount; createCount++) {
                    item = ItemTable.get().createItem(itemId);
                    item.setEnchantLevel(0);
                    L1GroundInventory ground = World.get().getInventory(loc.getX(), loc.getY(), _mapId);
                    if (ground.checkAddItem(item, stackCount) == L1Inventory.OK) {
                        ground.storeItem(item);
                    }
                }
            }
        }
    }

    /**
     * 上全削除。
     */
    private void clearColosseum() {
        for (Object obj : World.get().getVisibleObjects(_mapId).values()) {
            if (obj instanceof L1MonsterInstance) // 削除
            {
                L1MonsterInstance mob = (L1MonsterInstance) obj;
                if (!mob.isDead()) {
                    mob.setDead(true);
                    mob.setStatus(ActionCodes.ACTION_Die);
                    mob.setCurrentHpDirect(0);
                    mob.deleteMe();
                }
            } else if (obj instanceof L1Inventory) // 削除
            {
                L1Inventory inventory = (L1Inventory) obj;
                inventory.clearItems();
            }
        }
    }

    /**
     * 開始。
     *
     */
    public void start() {
        int patternsMax = UBSpawnTable.getInstance().getMaxPattern(_ubId);
        _pattern = Random.nextInt(patternsMax) + 1; // 出現決
        UbThread ub = new UbThread();
        GeneralThreadPool.get().execute(ub);
    }

    /**
     * 參加追加。
     *
     * @param pc 新參加
     */
    public void addMember(L1PcInstance pc) {
        if (!_members.contains(pc)) {
            _members.add(pc);
        }
    }

    /**
     * 參加削除。
     *
     * @param pc 削除
     */
    public void removeMember(L1PcInstance pc) {
        _members.remove(pc);
    }

    /**
     * 參加。
     */
    public void clearMembers() {
        _members.clear();
    }

    /**
     * 、參加返。
     *
     * @param pc 調
     * @return 參加true、false。
     */
    public boolean isMember(L1PcInstance pc) {
        return _members.contains(pc);
    }

    /**
     * 參加配列作成、返。
     *
     * @return 參加配列
     */
    public L1PcInstance[] getMembersArray() {
        return _members.toArray(new L1PcInstance[0]);
    }

    /**
     * 參加數返。
     *
     * @return 參加數
     */
    public int getMembersCount() {
        return _members.size();
    }

    /**
     * UB中返。
     *
     * @return UB中true、false。
     */
    public boolean isNowUb() {
        return _isNowUb;
    }

    /**
     * UB中設定。
     *
     * @param i true/false
     */
    private void setNowUb(boolean i) {
        _isNowUb = i;
    }

    public int getUbId() {
        return _ubId;
    }

    public void setUbId(int id) {
        _ubId = id;
    }

    public short getMapId() {
        return _mapId;
    }

    public void setMapId(short mapId) {
        _mapId = mapId;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public void setMinLevel(int level) {
        _minLevel = level;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    public void setMaxLevel(int level) {
        _maxLevel = level;
    }

    public int getMaxPlayer() {
        return _maxPlayer;
    }

    public void setMaxPlayer(int count) {
        _maxPlayer = count;
    }

    public void setEnterRoyal(boolean enterRoyal) {
        _enterRoyal = enterRoyal;
    }

    public void setEnterKnight(boolean enterKnight) {
        _enterKnight = enterKnight;
    }

    public void setEnterMage(boolean enterMage) {
        _enterMage = enterMage;
    }

    public void setEnterElf(boolean enterElf) {
        _enterElf = enterElf;
    }

    public void setEnterDarkelf(boolean enterDarkelf) {
        _enterDarkelf = enterDarkelf;
    }

    public void setEnterDragonKnight(boolean enterDragonKnight) {
        _enterDragonKnight = enterDragonKnight;
    }

    public void setEnterIllusionist(boolean enterIllusionist) {
        _enterIllusionist = enterIllusionist;
    }

    public void setEnterWarrior(boolean enterWarrior) {
        _enterWarrior = enterWarrior;
    }

    public void setEnterMale(boolean enterMale) {
        _enterMale = enterMale;
    }

    public void setEnterFemale(boolean enterFemale) {
        _enterFemale = enterFemale;
    }

    public boolean canUsePot() {
        return _usePot;
    }

    public void setUsePot(boolean usePot) {
        _usePot = usePot;
    }

    public int getHpr() {
        return _hpr;
    }

    public void setHpr(int hpr) {
        _hpr = hpr;
    }

    public int getMpr() {
        return _mpr;
    }

    public void setMpr(int mpr) {
        _mpr = mpr;
    }

    public int getLocX1() {
        return _locX1;
    }

    public void setLocX1(int locX1) {
        _locX1 = locX1;
    }

    public int getLocY1() {
        return _locY1;
    }

    public void setLocY1(int locY1) {
        _locY1 = locY1;
    }

    public int getLocX2() {
        return _locX2;
    }

    public void setLocX2(int locX2) {
        _locX2 = locX2;
    }

    public int getLocY2() {
        return _locY2;
    }

    public void setLocY2(int locY2) {
        _locY2 = locY2;
    }

    public String getName() {
        return this._ubName;
    }

    /**
     * [非仿原碼] 無限大賽廣播
     */
    public void setName(String name) {
        this._ubName = name;
    }

    // setlocx1～locy2中心點求。
    public void resetLoc() {
        _locX = (_locX2 + _locX1) / 2;
        _locY = (_locY2 + _locY1) / 2;
        _location = new L1Location(_locX, _locY, _mapId);
    }

    public L1Location getLocation() {
        return _location;
    }

    public void addManager(int npcId) {
        _managers.add(npcId);
    }

    public boolean containsManager(int npcId) {
        return _managers.contains(npcId);
    }

    public void addUbTime(int time) {
        _ubTimes.add(time);
    }

    public String getNextUbTime() {
        return intToTimeFormat(nextUbTime());
    }

    private int nextUbTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        int nowTime = Integer.parseInt(sdf.format(getRealTime().getTime()));
        SortedSet<Integer> tailSet = _ubTimes.tailSet(nowTime);
        if (tailSet.isEmpty()) {
            tailSet = _ubTimes;
        }
        return tailSet.first();
    }

    public boolean checkUbTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        Calendar realTime = getRealTime();
        realTime.add(Calendar.MINUTE, BEFORE_MINUTE);
        int nowTime = Integer.parseInt(sdf.format(realTime.getTime()));
        return _ubTimes.contains(nowTime);
    }

    /**
     * @return UB入場可能～競技終了true,以外false返。
     */
    public boolean isActive() {
        return _active;
    }

    private void setActive(boolean f) {
        _active = f;
    }

    /**
     * UB參加可能、、。
     *
     * @param pc UB參加PC
     * @return 參加出來場合true,出來場合false
     */
    public boolean canPcEnter(L1PcInstance pc) {
        _log.log(Level.FINE, "pcname=" + pc.getName() + " ubid=" + _ubId + " minlvl=" + _minLevel + " maxlvl=" + _maxLevel);
        // 參加可能
        if (!IntRange.includes(pc.getLevel(), _minLevel, _maxLevel)) {
            return false;
        }
        // 參加可能
        if (!((pc.isCrown() && _enterRoyal) || (pc.isKnight() && _enterKnight) || (pc.isWizard() && _enterMage) || (pc.isElf() && _enterElf) || (pc.isDarkelf() && _enterDarkelf) || (pc.isDragonKnight() && _enterDragonKnight) || (pc.isIllusionist() && _enterIllusionist) || (pc.isWarrior() && _enterWarrior))) {
            return false;
        }
        return true;
    }

    public String[] makeUbInfoStrings() {
        if (_ubInfo != null) {
            return _ubInfo;
        }
        String nextUbTime = getNextUbTime() + " ";
        // 
        StringBuilder classesBuff = new StringBuilder();
        if (_enterDarkelf) {
            classesBuff.append("黑暗妖精 ");
        }
        if (_enterMage) {
            classesBuff.append("法師 ");
        }
        if (_enterElf) {
            classesBuff.append("妖精 ");
        }
        if (_enterKnight) {
            classesBuff.append("騎士 ");
        }
        if (_enterRoyal) {
            classesBuff.append("王族 ");
        }
        if (_enterDragonKnight) {
            classesBuff.append("龍騎士 ");
        }
        if (_enterIllusionist) {
            classesBuff.append("幻術師 ");
        }
        if (_enterWarrior) {
            classesBuff.append("戰士 ");
        }
        String classes = classesBuff.toString().trim();
        // 性別
        StringBuilder sexBuff = new StringBuilder();
        if (_enterMale) {
            sexBuff.append("男 ");
        }
        if (_enterFemale) {
            sexBuff.append("女 ");
        }
        String sex = sexBuff.toString().trim();
        String loLevel = String.valueOf(_minLevel + " ");
        String hiLevel = String.valueOf(_maxLevel + " ");
        String teleport = _location.getMap().isEscapable() ? "可以" : "不可以";
        String res = _location.getMap().isUseResurrection() ? "可以" : "不可以";
        String pot = "可以";
        String hpr = String.valueOf(_hpr);
        String mpr = String.valueOf(_mpr);
        String summon = _location.getMap().isTakePets() ? "可以" : "不可以";
        String summon2 = _location.getMap().isRecallPets() ? "可以" : "不可以";
        _ubInfo = new String[]{nextUbTime, classes, sex, loLevel, hiLevel, teleport, res, pot, hpr, mpr, summon, summon2};
        return _ubInfo;
    }

    class UbThread implements Runnable {
        /**
         * 競技開始。
         *
         */
        private void countDown() throws InterruptedException {
            // XXX - ID間違
            final int MSGID_COUNT = 637;
            final int MSGID_START = 632;
            for (int loop = 0; loop < BEFORE_MINUTE * 60 - 10; loop++) { // 開始10秒前待
                TimeUnit.MILLISECONDS.sleep(1000);
                // removeRetiredMembers();
            }
            removeRetiredMembers();
            sendMessage(MSGID_COUNT, "10"); // 10秒前
            TimeUnit.MILLISECONDS.sleep(5000);
            sendMessage(MSGID_COUNT, "5"); // 5秒前
            TimeUnit.MILLISECONDS.sleep(1000);
            sendMessage(MSGID_COUNT, "4"); // 4秒前
            TimeUnit.MILLISECONDS.sleep(1000);
            sendMessage(MSGID_COUNT, "3"); // 3秒前
            TimeUnit.MILLISECONDS.sleep(1000);
            sendMessage(MSGID_COUNT, "2"); // 2秒前
            TimeUnit.MILLISECONDS.sleep(1000);
            sendMessage(MSGID_COUNT, "1"); // 1秒前
            TimeUnit.MILLISECONDS.sleep(1000);
            sendMessage(MSGID_START, " "); // 
            removeRetiredMembers();
        }

        /**
         * 全出現後、次始時間待機。
         *
         * @param curRound 現在
         */
        private void waitForNextRound(int curRound) throws InterruptedException {
            final int WAIT_TIME_TABLE[] = {6, 6, 2, 18};
            int wait = WAIT_TIME_TABLE[curRound - 1];
            for (int i = 0; i < wait; i++) {
                TimeUnit.MILLISECONDS.sleep(10000);
                // removeRetiredMembers();
            }
            removeRetiredMembers();
        }

        /**
         * 。
         */
        @Override
        public void run() {
            try {
                setActive(true);
                countDown();
                setNowUb(true);
                for (int round = 1; round <= 4; round++) {
                    sendRoundMessage(round);
                    L1UbPattern pattern = UBSpawnTable.getInstance().getPattern(_ubId, _pattern);
                    List<L1UbSpawn> spawnList = pattern.getSpawnList(round);
                    for (L1UbSpawn spawn : spawnList) {
                        if (getMembersCount() > 0) {
                            spawn.spawnAll();
                        }
                        TimeUnit.MILLISECONDS.sleep(spawn.getSpawnDelay() * 1000L);
                        // removeRetiredMembers();
                    }
                    if (getMembersCount() > 0) {
                        spawnSupplies(round);
                    }
                    /** [原碼] 無限大戰計分系統 */
                    for (L1PcInstance pc : getMembersArray()) {
                        UBTable.getInstance().writeUbScore(getUbId(), pc);
                    }
                    /** End */
                    waitForNextRound(round);
                }
                for (L1PcInstance pc : getMembersArray()) // 內居PC外出
                {
                    int rndx = Random.nextInt(4);
                    int rndy = Random.nextInt(4);
                    int locx = 33503 + rndx;
                    int locy = 32764 + rndy;
                    short mapid = 4;
                    L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
                    removeMember(pc);
                }
                clearColosseum();
                setActive(false);
                setNowUb(false);
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
    }
}
