package com.lineage.server.model.Instance;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.DeShopChatTable;
import com.lineage.server.datatables.NpcChatTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.SpawnBossReading;
import com.lineage.server.model.*;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1NpcChat;
import com.lineage.server.timecontroller.npc.NpcWorkTimer;
import com.lineage.server.timecontroller.quest.KIRTAS_Timer;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.lineage.server.model.skill.L1SkillId.*;

public class L1NpcInstance extends L1Character {
    public static final int MOVE_SPEED = 0;
    public static final int ATTACK_SPEED = 1;
    public static final int MAGIC_SPEED = 2;
    public static final int HIDDEN_STATUS_NONE = 0;
    public static final int HIDDEN_STATUS_SINK = 1;
    public static final int HIDDEN_STATUS_FLY = 2;
    public static final int HIDDEN_STATUS_ICE = 3;
    public static final int HIDDEN_STATUS_KIRTAS = 4;
    public static final int REST_MILLISEC = 10;
    public static final int DISTANCE = 20;
    public static final int CHAT_TIMING_APPEARANCE = 0;
    public static final int CHAT_TIMING_DEAD = 1;
    public static final int CHAT_TIMING_HIDE = 2;
    public static final int CHAT_TIMING_GAME_TIME = 3;
    public static final int USEITEM_HEAL = 0;
    public static final int USEITEM_HASTE = 1;
    private static final Log _log = LogFactory.getLog(L1NpcInstance.class);
    private static final long serialVersionUID = 1L;
    private static final Random _random = new Random();
    // 使用的藥水陣列
    public static int[] healPotions = {40012, 40011, 40010};
    // 使用的加速藥水陣列
    public static int[] haestPotions = {140018, 40018, 140013, 40013};
    protected final L1HateList _hateList = new L1HateList();
    protected final L1HateList _dropHateList = new L1HateList();
    protected final List<L1ItemInstance> _targetItemList = new ArrayList<L1ItemInstance>();
    private final HashMap<L1ItemInstance, DelItemTime> _del_map = new HashMap<L1ItemInstance, DelItemTime>();
    public L1Inventory _inventory = new L1Inventory();
    public L1MobSkillUse _mobSkill;
    public NpcExecutor TALK = null;
    public NpcExecutor ACTION = null;
    public NpcExecutor ATTACK = null;
    public NpcExecutor DEATH = null;
    public NpcExecutor WORK = null;
    public NpcExecutor SPAWN = null;
    public boolean _destroyed = false;
    public int _barrierTime;
    protected boolean ISASCAPE = false;
    protected L1Character _target = null;
    protected L1ItemInstance _targetItem = null;
    protected L1Character _master = null;
    protected boolean _hprRunning = false;
    protected NpcMoveExecutor _npcMove = null;
    /**
     * 騎士團
     */
    int clanid = 0;
    private L1Npc _npcTemplate;
    private L1Spawn _spawn;
    private int _spawnNumber;
    private int _petcost;
    private boolean firstFound = true;
    private int _randomMoveDistance = 0;
    private int _randomMoveDirection = 0;
    private boolean _aiRunning = false;
    private boolean _actived = false;
    private boolean _firstAttack = false;
    private int _sleep_time;
    private boolean _deathProcessing = false;
    private boolean _mprRunning = false;
    private int _passispeed;
    private int _atkspeed;
    private boolean _pickupItem;
    private String _nameId;
    private boolean _Agro;
    private boolean _Agrocoi;
    private boolean _Agrososc;
    private int _homeX;
    private int _homeY;
    private boolean _reSpawn;
    private int _lightSize;
    private boolean _weaponBreaked;
    private int _hiddenStatus;
    private int _movementDistance = 0;
    private int _tempLawful = 0;
    private int _drainedMana = 0;
    private boolean _rest = false;
    private boolean _isResurrect = false;
    private L1MobGroupInfo _mobGroupInfo = null;
    private int _mobGroupId = 0;
    private int _bowActId = -1;
    private int _ranged = -1;
    private int _skyTime;
    private int _quest_id;
    private boolean _isShop = false;
    private int _spawnTime = 0;
    private boolean _isspawnTime = false;
    private boolean _isremovearmor = false;
    private int _deadTimerTemp = -1;
    private int _stop_time = -1;
    private int _work_time = -1;
    // ■■■■■■■■■■■■■ ＡＩ關連 ■■■■■■■■■■■
    private KIRTAS_Timer _kirtastimer;
    /**
     * 紅騎士 訓練副本 by darling
     **/
    private String Spawn_Location;
    // 投石器攻級間隔時間
    private long _shells_damag_time = 0;
    private long _shells_silence_time = 0;
    // 暫存製造命令
    private String _craftkey = null;
    private boolean _gripped;

    public L1NpcInstance(L1Npc template) {
        this._drainedMana = 0;
        setStatus(0);
        setMoveSpeed(0);
        setDead(false);
        setStatus(0);
        setreSpawn(false);
        _npcMove = new NpcMove(this);
        if (template != null) {
            setting_template(template);
        }
    }

    public static void shuffle(L1Object[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int t = (int) (Math.random() * i);
            L1Object tmp = arr[i];
            arr[i] = arr[t];
            arr[t] = tmp;
        }
    }

    /**
     * 啟用NPC AI
     */
    protected void startAI() {
        if (this instanceof L1PetInstance) {
            // System.out.println(this.getName());
        } else {
            this.setAiRunning(true);// 修正怪物會爆走
        }
        if (isDead()) {
            return;
        }
        if (destroyed()) {
            return;
        }
        if (getCurrentHp() <= 0) {
            return;
        }
        final NpcAI npcai = new NpcAI(this);
        npcai.startAI();
        startHpRegeneration();
        startMpRegeneration();
    }

    /**
     * npc物品搜尋設置
     */
    public void onItemUse() {
    }

    /**
     * npc目標搜尋設置
     */
    public void searchTarget() {
    }

    /**
     * 增加物件主人(寵物)
     *
     * @param master
     */
    public void addMaster(L1PcInstance master) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            if (master != null) {
                if (master.get_other().get_color() != 0) {
                    stringBuilder.append(master.get_other().color());
                }
                if (master.isProtector()) {
                    stringBuilder.append("**守護者**");
                } else {
                    stringBuilder.append(master.getName());
                }
            } else {
                stringBuilder.append("");
            }
            // 增加物件組人
            master.sendPackets(new S_NewMaster(stringBuilder.toString(), this));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 有效目標檢查
     */
    public void checkTarget() {
        try {
            if (_target == null) {// 目標為空
                clearTagert();
                return;
            }
            if (_target.getMapId() != getMapId()) {// 目標地圖不相等
                clearTagert();
                return;
            }
            if (_target.getCurrentHp() <= 0) {// 目標HP小於等於0
                clearTagert();
                return;
            }
            if (_target.isDead()) {// 目標死亡
                clearTagert();
                return;
            }
            if (get_showId() != _target.get_showId()) {// 副本ID不相等
                clearTagert();
                return;
            }
            if (_target.isInvisble() && // 目標已經隱身
                    !getNpcTemplate().is_agrocoi() && // 不具備探查隱身能力
                    !_hateList.containsKey(_target)) {// 目標不在已有攻擊清單中
                clearTagert();
                return;
            }
            final int distance = getLocation().getTileDistance(_target.getLocation());
            if (distance > 30) {
                clearTagert();
                return;
            }
        } catch (final Exception e) {
            return;
        }
    }

    /**
     * 清除無效目標<BR>
     * 搜尋新目標(仇恨最高之目標)
     */
    private void clearTagert() {
        if (_target != null) {
            // 清除目標
            tagertClear();
        }
        if (!_hateList.isEmpty()) {// 仇恨清單不為空
            _target = _hateList.getMaxHateCharacter();
            checkTarget();
        }
    }

    /**
     * 該物件是否為目標
     *
     * @param cha
     * @return
     */
    public boolean isHate(final L1Character cha) {
        return _hateList.isHate(cha);
    }

    /**
     * 攻擊目標設置
     *
     * @param cha
     * @param hate
     */
    public void setHate(final L1Character cha, int hate) {
        try {
            if ((cha != null) && (cha.getId() != getId())) {
                if (!isFirstAttack() && (hate != 0)) {
                    hate += getMaxHp() / 10; // ＦＡヘイト
                    setFirstAttack(true);
                }
                if (_npcMove != null) {
                    _npcMove.clear();// XXX
                }
                _hateList.add(cha, hate);
                _dropHateList.add(cha, hate);
                _target = _hateList.getMaxHateCharacter();
                checkTarget();
            }
        } catch (final Exception e) {
            return;
        }
    }

    /**
     * 攻擊目標設置
     *
     * @param cha
     */
    public void setLink(final L1Character cha) {
    }

    /**
     * 互相幫助的判斷
     *
     * @param targetPlayer
     * @param family
     */
    public void serchLink(final L1PcInstance targetPlayer, final int family) {
        final List<L1Object> targetKnownObjects = targetPlayer.getKnownObjects();
        for (final Object knownObject : targetKnownObjects) {
            if (knownObject instanceof L1NpcInstance) {
                final L1NpcInstance npc = (L1NpcInstance) knownObject;
                // 副本ID不相等 不列入判斷
                if (this.get_showId() != npc.get_showId()) {
                    continue;
                }
                if (npc.getNpcTemplate().get_agrofamily() > 0) {
                    // 同族間互相幫助的設置
                    if (npc.getNpcTemplate().get_agrofamily() == 1) {
                        // 種族相同
                        if (npc.getNpcTemplate().get_family() == family) {
                            npc.setLink(targetPlayer);
                        }
                    } else {
                        // 大於1全部NPC支援
                        npc.setLink(targetPlayer);
                    }
                }
                // 隊伍間互相幫助的設置
                final L1MobGroupInfo mobGroupInfo = this.getMobGroupInfo();
                if (mobGroupInfo != null) {
                    // 具有隊伍
                    if (this.getMobGroupId() != 0) {
                        // 相同隊伍
                        if (this.getMobGroupId() == npc.getMobGroupId()) {
                            if (npc.getNpcId() == 99007) {// 牛鬼除外
                                return;
                            }
                            npc.setLink(targetPlayer);
                        }
                    }
                }
            }
        }
    }

    /**
     * 具有目標的處理 (攻擊的判斷)
     */
    public void onTarget() {
        try {
            switch (this.getNpcId()) {
                case 107037:// 傑羅斯召喚-鬼火
                case 99030:// 巨型骷髏召喚-傳送旋風
                case 99083:// 巨蟻女皇召喚-傳送旋風
                    Astarothfire();
                    break;
            }
            setActived(true);
            // 具有目標 清除想要撿取的物品
            if (_targetItemList.size() > 0) {
                _targetItemList.clear();
            }
            if (_targetItem != null) {
                _targetItem = null;
            }
            // ここから先は_targetが變わると影響出るので別領域に參照確保
            final L1Character target = _target;
            if (target == null) {// 失去目標
                tagertClear();
                ;
                return;
            }
            if (getAtkspeed() == 0) { // 攻擊速度為0
                if (getPassispeed() > 0 && getLevel() <= 90) { // 具有移動速度
                    // 並且等級小於90
                    ISASCAPE = true;
                }
            }
            if (ISASCAPE) {// 逃跑
                ascape(target.getLocation());
            } else {
                attack(target);// 執行攻擊目標
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 傑羅斯詛咒火焰 (主動尋敵)
     */
    private void Astarothfire() {
        int radius = 0;
        int damage = 0;
        switch (this.getNpcId()) {
            case 107037:// 傑羅斯召喚-鬼火
                radius = 3;
                damage = 200;
                break;
            case 99030: // 巨大骷髏召喚 傳送旋風
                radius = 0;
                break;
            case 99083: // 巨蟻女皇召喚 傳送旋風
                radius = 0;
                break;
        }
        for (L1PcInstance player : World.get().getVisiblePlayer(this, radius)) {
            if (player != null) {
                if (this.getNpcId() == 99030) {// 傳送旋風
                    if (_target != null && _target.getId() == player.getId()) {
                        L1Teleport.teleport(player, 32990, 32990, (short) 8001, 5, true);
                        setSleepTime(5000);// 原地發呆5秒
                        return;
                    }
                } else if (this.getNpcId() == 99083) {// 沙塵暴
                    L1Location loc = player.getLocation();
                    L1Location newloc = loc.randomLocation(100, true);
                    L1Teleport.teleport(player, newloc, player.getHeading(), true);
                    return;
                }
                player.receiveDamage(this, damage, false, true);
                player.sendPacketsAll(new S_DoActionGFX(player.getId(), 2));
                this.broadcastPacketAll(new S_DoActionGFX(this.getId(), 11));
                deleteMe();
            }
        }
    }

    /**
     * 吉爾塔斯地面火焰範圍燒傷 (被動偷襲)
     */
    private void kirtasfire() {
        setSleepTime(1000);// 間隔時間
        int radius = 0;
        int damage = 0;
        switch (this.getNpcId()) {
            case 86121:// 新版吉爾塔斯-地面大火
                radius = 3;
                damage = 200;
                break;
            case 86122:// 新版吉爾塔斯-地面小火
                radius = 2;
                damage = 100;
                break;
            case 97221:// 奇怪的烏雲
                radius = 1;
                damage = 100;
                this.broadcastPacketAll(new S_DoActionGFX(this.getId(), 19));
                break;
            case 99084:// 沙漠地區 沙塵暴
                radius = 0;
                break;
        }
        for (L1PcInstance player : World.get().getVisiblePlayer(this, radius)) {
            if (player != null) {
                if (this.getNpcId() == 99084) {// 傳送旋風
                    if (_random.nextInt(100) < 70) {
                        L1Location loc = player.getLocation();
                        L1Location newloc = loc.randomLocation(100, true);
                        L1Teleport.teleport(player, newloc, player.getHeading(), true);
                    } else {
                        L1Teleport.teleport(player, 32812, 33184, (short) 4, player.getHeading(), true);
                    }
                    return;
                }
                player.receiveDamage(this, damage, true, true);
                player.sendPacketsAll(new S_DoActionGFX(player.getId(), 2));
            }
        }
    }

    /**
     * 攻擊目標
     *
     * @param target
     */
    private void attack(L1Character target) {
        // 物理攻擊可能位置
        if (isAttackPosition(target.getX(), target.getY(), get_ranged())) {// 已經到達可以物理攻擊的距離
            if (_mobSkill.isSkillTrigger(target)) { // 符合DB技能數量裡任何一種技能的使用條件
                if (_mobSkill.skillUse(target)) { // 使用技能的判定
                    setSleepTime(calcSleepTime(_mobSkill.getSleepTime(), MAGIC_SPEED));
                } else { // 技能使用失敗則使用物理攻擊
                    setHeading(targetDirection(target.getX(), target.getY()));
                    attackTarget(target);
                }
            } else {// 怪物無技能設定
                setHeading(targetDirection(target.getX(), target.getY()));
                attackTarget(target);
            }
            if (_npcMove != null) {
                _npcMove.clear();
            }
        } else { // 物理攻擊不可能位置
            // 對像直線上無障礙
            if (glanceCheck(target.getX(), target.getY())) {
                if (_mobSkill.skillUse(target)) {
                    setSleepTime(calcSleepTime(_mobSkill.getSleepTime(), MAGIC_SPEED));
                    return;
                }
            }
            if (getPassispeed() > 0) {// 具有移動速度
                final int distance = getLocation().getTileDistance(target.getLocation());
                if (firstFound == true) {
                    if (getNpcTemplate().is_teleport() && (distance > 3) && (distance < DISTANCE)) {
                        if (nearTeleport(target.getX(), target.getY()) == true) {
                            firstFound = false;
                            return;
                        }
                    }
                }
                if (getNpcTemplate().is_teleport()) {// NPC 會使用傳送
                    if ((20 > _random.nextInt(100)) && (getCurrentMp() >= 10) && (distance > 6) && (distance < DISTANCE)) { // テレポート移動
                        if (nearTeleport(target.getX(), target.getY()) == true) {
                            return;
                        }
                    }
                }
                if (isGripped()) { // src1004
                    return;
                }
                if (_npcMove != null) {
                    final int dir = _npcMove.moveDirection(target.getX(), target.getY());
                    if (dir == -1) {// 沒有移動方向
                        // 假如怪物走不過去 就找附近下一個玩家攻擊
                        tagertClear();
                        searchTarget();
                    } else {
                        _npcMove.setDirectionMove(dir);
                        setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                        setNpcDollSpeed();
                    }
                }
            } else {// 移動速度為0
                switch (this.getGfxId()) {// XXX
                    case 816:// 警衛(妖堡箭塔)
                        attackTarget(target);
                        break;
                    default:
                        tagertClear();
                        break;
                }
            }
        }
    }

    /**
     * 設定娃娃跟隨速度
     */
    private void setNpcDollSpeed() {
        if ((this instanceof L1DeInstance)) {
            L1DeInstance de = (L1DeInstance) this;
            de.setNpcSpeed();
        }
    }

    /**
     * 脫逃
     *
     * @param location
     */
    private void ascape(L1Location location) {
        int escapeDistance = 17;
        if (hasSkillEffect(DARKNESS) == true) {
            escapeDistance = 1;
        }
        if (getLocation().getTileLineDistance(location) > escapeDistance) { // 逃亡距離到達設置值
            tagertClear();
        } else { // 目標反方向移動
            if (_npcMove != null) {
                int dir = _npcMove.targetReverseDirection(location.getX(), location.getY());
                dir = _npcMove.checkObject(dir);
                dir = _npcMove.openDoor(dir);
                _npcMove.setDirectionMove(dir);
                setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
            }
        }
    }

    /**
     * 是否可以成為目標
     *
     * @param cha
     * @return 人物 寵物 召喚獸 進入安全區 傳回無法攻擊(false)
     */
    private boolean isTraget(final L1Character cha) {
        boolean isTraget = false;
        if (cha instanceof L1PcInstance) {
            L1PcInstance tg = (L1PcInstance) cha;
            if (!tg.isSafetyZone()) {
                isTraget = true;
            }
        } else if (cha instanceof L1PetInstance) {
            L1PetInstance tg = (L1PetInstance) cha;
            if (!tg.isSafetyZone()) {
                isTraget = true;
            }
        } else if (cha instanceof L1SummonInstance) {
            L1SummonInstance tg = (L1SummonInstance) cha;
            if (!tg.isSafetyZone()) {
                isTraget = true;
            }
        } else if (cha instanceof L1MonsterInstance) {
            isTraget = true;
        }
        // System.out.println("是否可以成為目標:" + isTraget);
        return isTraget;
    }

    /**
     * 對目標進行攻擊
     *
     * @param target
     */
    public void attackTarget(final L1Character target) {
        // 攻擊者是分身
        if (this instanceof L1IllusoryInstance) {
            if (!isTraget(target)) {
                // 目標無法攻擊 暫停攻擊
                return;
            }
        }
        if (target instanceof L1PcInstance) {
            final L1PcInstance player = (L1PcInstance) target;
            if (player.isTeleport()) { // テレポート處理中
                return;
            }
        } else if (target instanceof L1PetInstance) {
            final L1PetInstance pet = (L1PetInstance) target;
            final L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // テレポート處理中
                    return;
                }
            }
        } else if (target instanceof L1SummonInstance) {
            final L1SummonInstance summon = (L1SummonInstance) target;
            final L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // テレポート處理中
                    return;
                }
            }
        }
        if (this instanceof L1PetInstance) {
            final L1PetInstance pet = (L1PetInstance) this;
            final L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // テレポート處理中
                    return;
                }
            }
        } else if (this instanceof L1SummonInstance) {
            final L1SummonInstance summon = (L1SummonInstance) this;
            final L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // テレポート處理中
                    return;
                }
            }
        }
        if (target instanceof L1NpcInstance) {
            final L1NpcInstance npc = (L1NpcInstance) target;
            if (npc.getHiddenStatus() != HIDDEN_STATUS_NONE) { // 地中に潛っているか、飛んでいる
                this.allTargetClear();
                return;
            }
        }
        boolean isCounterBarrier = false;
        final L1AttackMode attack = new L1AttackNpc(this, target);
        if (attack.calcHit()) {// 攻擊命中
            if (target.hasSkillEffect(COUNTER_BARRIER)) {
                final L1Magic magic = new L1Magic(target, this);
                final boolean isProbability = magic.calcProbabilityMagic(COUNTER_BARRIER);
                final boolean isShortDistance = attack.isShortDistance();
                if (isProbability && isShortDistance) {
                    isCounterBarrier = true;
                }
            }
            if (!isCounterBarrier) {
                attack.calcDamage();
            }
        }
        if (isCounterBarrier) {
            // attack.actionCounterBarrier();
            attack.commitCounterBarrier();
        } else {
            attack.action();
            attack.commit();
        }
        setSleepTime(calcSleepTime(getAtkspeed(), ATTACK_SPEED));
    }

    /**
     * 撿取物品探索
     */
    public void searchTargetItem() {
        final ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();
        for (final L1Object obj : World.get().getVisibleObjects(this)) {
            if ((obj != null) && (obj instanceof L1GroundInventory)) {
                gInventorys.add((L1GroundInventory) obj);
            }
        }
        if (gInventorys.size() == 0) {
            return;
        }
        // 拾うアイテム(のインベントリ)をランダムで選定
        final int pickupIndex = (int) (Math.random() * gInventorys.size());
        final L1GroundInventory inventory = gInventorys.get(pickupIndex);
        for (final L1ItemInstance item : inventory.getItems()) {
            if (this.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) { // 持てるならターゲットアイテムに加える
                this._targetItem = item;
                this._targetItemList.add(this._targetItem);
            }
        }
    }

    /**
     * 飛在空中狀態 搜尋拾取藥水及食物
     */
    public void searchItemFromAir() {
        final ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();
        for (final L1Object obj : World.get().getVisibleObjects(this)) {
            if ((obj != null) && (obj instanceof L1GroundInventory)) {
                gInventorys.add((L1GroundInventory) obj);
            }
        }
        if (gInventorys.size() == 0) {
            return;
        }
        // 拾うアイテム(のインベントリ)をランダムで選定
        final int pickupIndex = (int) (Math.random() * gInventorys.size());
        final L1GroundInventory inventory = gInventorys.get(pickupIndex);
        for (final L1ItemInstance item : inventory.getItems()) {
            if ((item.getItem().getType() == 6) // potion
                    || (item.getItem().getType() == 7)) { // food
                if (this.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                    if (this.getHiddenStatus() == HIDDEN_STATUS_FLY) {
                        this.setHiddenStatus(HIDDEN_STATUS_NONE);
                        this.broadcastPacketAll(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_Movedown));
                        this.setStatus(0);
                        this.broadcastPacketAll(new S_NPCPack(this));
                        this.onNpcAI();
                        this.startChat(CHAT_TIMING_HIDE);
                        this._targetItem = item;
                        this._targetItemList.add(this._targetItem);
                    }
                }
            }
        }
    }

    /**
     * 檢取目標有效判斷
     */
    public void checkTargetItem() {
        if ((_targetItem == null) || (_targetItem.getMapId() != getMapId()) || (getLocation().getTileDistance(_targetItem.getLocation()) > 15)) {
            // 具有目標元素
            if (!_targetItemList.isEmpty()) {
                _targetItem = _targetItemList.get(0);
                _targetItemList.remove(0);
                checkTargetItem();// 重新檢查目標物品
            } else {
                _targetItem = null;
            }
        }
    }

    /**
     * 物品檢取的判斷
     */
    public void onTargetItem() {
        if (getLocation().getTileLineDistance(_targetItem.getLocation()) == 0) { // 抵達可撿取物品位置
            pickupTargetItem(_targetItem);
        } else { // 尚未抵達可撿取物品位置
            if (_npcMove != null) {
                final int dir = _npcMove.moveDirection(_targetItem.getX(), _targetItem.getY());
                if (dir == -1) { // 拾うの諦め
                    _targetItemList.remove(_targetItem);
                    _targetItem = null;
                } else { // ターゲットアイテムへ移動
                    _npcMove.setDirectionMove(dir);
                    setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                }
            }
        }
    }

    /**
     * 檢取物品
     *
     * @param targetItem
     */
    public void pickupTargetItem(final L1ItemInstance targetItem) {
        try {
            final int x = targetItem.getX();
            final int y = targetItem.getY();
            final short m = targetItem.getMapId();
            final L1Inventory groundInv = World.get().getInventory(x, y, m);
            final L1ItemInstance item = groundInv.tradeItem(targetItem, targetItem.getCount(), getInventory());
            // turnOnOffLight();
            onGetItem(item);
            _targetItemList.remove(_targetItem);
            _targetItem = null;
            setSleepTime(1000);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 沒有目標的處理 (傳回本次AI是否執行完成)<BR>
     * 具有主人 跟隨主人移動
     *
     * @return true:本次AI執行完成 <BR>
     * false:本次AI執行未完成
     */
    public boolean noTarget() {
        switch (this.getNpcId()) {
            case 86121:
            case 86122:
            case 97221:// 奇怪的烏雲
            case 99084:// 沙漠地區 沙塵暴
                kirtasfire();
                break;
        }
        // 具有主人 並在同地圖 與主人超過兩格距離
        if ((_master != null) && (_master.getMapId() == getMapId()) && (getLocation().getTileLineDistance(_master.getLocation()) > 2)) {
            if (getPassispeed() > 0) {// 具備移動速度
                if (_npcMove != null) {
                    // 跟隨主人移動
                    final int dir = _npcMove.moveDirection(_master.getX(), _master.getY());
                    if (dir != -1) {
                        _npcMove.setDirectionMove(dir);
                        setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        } else {// 沒有主人
            // 周邊無PC物件 AI處理終止
            if (World.get().getVisiblePlayer(this, DISTANCE).size() <= 0) {
                if (this.getMapId() != 93) {// 不是怪物對戰地圖
                    teleport(getHomeX(), getHomeY(), getHeading());// 傳回出生點
                    return true;
                }
            }
            // 沒有隊長 具有移動速度 並未動作暫停
            if ((_master == null) && (getPassispeed() > 0) && !isRest()) {
                // NPC不具有隊伍狀態 或本身是隊長
                final L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
                if ((mobGroupInfo == null) || ((mobGroupInfo != null) && mobGroupInfo.isLeader(this))) {
                    // 如果移動距離已經為0 重新定義隨機移動
                    if (_randomMoveDistance == 0) {
                        // 產生移動距離
                        _randomMoveDistance = _random.nextInt(6) + 1;
                        // 產生移動方向(隨機數值超出7物件會暫停移動)
                        _randomMoveDirection = _random.nextInt(40);
                        // 返回出生點的判斷
                        if ((getHomeX() != 0) && (getHomeY() != 0)// 具有出生點設置
                                && (_randomMoveDirection < 8)) {// 具有移動方向
                            if (getLocation().getLineDistance(new Point(getHomeX(), getHomeY())) > 8) {// 距離超過出生點8格
                                if (_npcMove != null) {
                                    _randomMoveDirection = _npcMove.moveDirection(getHomeX(), getHomeY());
                                }
                            }
                        }
                    } else {
                        _randomMoveDistance--;// 移動距離-1
                    }
                    if (_npcMove != null) {
                        if (_randomMoveDirection < 8) {// 具有移動方向
                            int dir = _npcMove.checkObject(_randomMoveDirection);
                            dir = _npcMove.openDoor(dir);
                            if (dir != -1) {
                                _npcMove.setDirectionMove(dir);
                                setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                            }
                        }
                    }
                } else { // NPC具有隊伍狀態
                    // 領隊追尋
                    final L1NpcInstance leader = mobGroupInfo.getLeader();
                    if (getPassispeed() > 0) {
                        if (getLocation().getTileLineDistance(leader.getLocation()) > 2) {
                            if (_npcMove != null) {
                                final int dir = _npcMove.moveDirection(leader.getX(), leader.getY());
                                if (dir == -1) {
                                    return true;
                                } else {
                                    _npcMove.setDirectionMove(dir);
                                    setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * NPC對話結果的處理
     *
     * @param pc
     * @param s
     */
    public void onFinalAction(final L1PcInstance pc, final String s) {
    }

    /**
     * 現在目標消除(hateList)
     */
    private void tagertClear() {
        if (this._target == null) {
            return;
        }
        this._hateList.remove(this._target);
        this._target = null;
    }

    /**
     * 指定目標消除(hateList)
     *
     * @param target
     */
    public void targetRemove(final L1Character target) {
        this._hateList.remove(target);
        if ((this._target != null) && this._target.equals(target)) {
            this._target = null;
        }
    }

    /**
     * 全部目標消除<BR>
     * (hateList)<BR>
     * (dropHateList)<BR>
     * (targetItemList)
     */
    public void allTargetClear() {
        // XXX
        if (_npcMove != null) {
            _npcMove.clear();
        }
        _hateList.clear();
        _dropHateList.clear();
        _target = null;
        _targetItemList.clear();
        _targetItem = null;
    }

    /**
     * 傳回主人
     *
     * @return
     */
    public L1Character getMaster() {
        return this._master;
    }

    /**
     * 設置主人
     *
     * @param cha
     */
    public void setMaster(final L1Character cha) {
        this._master = cha;
    }

    // ＡＩトリガ
    public void onNpcAI() {
    }

    /**
     * 道具交換製作
     */
    public void refineItem() {
        int[] materials = (int[]) null;
        int[] counts = (int[]) null;
        int[] createitem = (int[]) null;
        int[] createcount = (int[]) null;
        if (_npcTemplate.get_npcId() == 45032) {
            if ((getExp() != 0L) && (!_inventory.checkItem(20))) {
                materials = new int[]{40508, 40521, 40045};
                counts = new int[]{150, 3, 3};
                createitem = new int[]{20};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            if ((getExp() != 0L) && (!_inventory.checkItem(19))) {
                materials = new int[]{40494, 40521};
                counts = new int[]{150, 3};
                createitem = new int[]{19};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            if ((getExp() != 0L) && (!_inventory.checkItem(3))) {
                materials = new int[]{40494, 40521};
                counts = new int[]{50, 1};
                createitem = new int[]{3};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            if ((getExp() != 0L) && (!_inventory.checkItem(100))) {
                materials = new int[]{88, 40508, 40045};
                counts = new int[]{4, 80, 3};
                createitem = new int[]{100};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            if ((getExp() != 0L) && (!_inventory.checkItem(89))) {
                materials = new int[]{88, 40494};
                counts = new int[]{2, 80};
                createitem = new int[]{89};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        L1ItemInstance item = _inventory.storeItem(createitem[j], createcount[j]);
                        if (getNpcTemplate().get_digestitem() > 0) {
                            setDigestItem(item);
                        }
                    }
                }
            }
        } else if ((_npcTemplate.get_npcId() == 45166) || (_npcTemplate.get_npcId() == 45167)) {
            if ((getExp() != 0L) && (!_inventory.checkItem(40726))) {
                materials = new int[]{40725};
                counts = new int[]{1};
                createitem = new int[]{40726};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
        }
    }

    /**
     * 目標清單
     *
     * @return
     */
    public L1HateList getHateList() {
        return this._hateList;
    }

    /**
     * HP是否具備回覆條件
     *
     * @return
     */
    public final boolean isHpR() {
        if (this.destroyed()) {// 進行刪除
            //System.out.println("進行刪除");
            return false;
        }
        if (this.isDead()) {// 死亡
            //System.out.println("死亡");
            return false;
        }
        if (this.getMaxHp() <= 0) {// 沒有HP設置
            //System.out.println("沒有HP設置");
            return false;
        }
        if (this.getCurrentHp() <= 0) {// 目前HP小於0
            //System.out.println("目前HP小於0");
            return false;
        }
        if (this.getCurrentHp() >= this.getMaxHp()) {// HP已滿
            //System.out.println("HP已滿");
            return false;
        }
        if (this.getMaxHp() > this.getCurrentHp()) {
            //System.out.println("啟動回血");
            startHpRegeneration();
        }
        return true;
    }

    /**
     * HP自然回覆
     */
    public final void startHpRegeneration() {
        if (this.destroyed() && this.isDead()) {
            return;
        }
        if (this.getCurrentHp() <= 0) {
            return;
        }
        if (!this._hprRunning) {
            this._hprRunning = true;
        }
    }

    /**
     * HP是否允許回覆
     *
     * @return
     */
    public final boolean isHpRegenerationX() {
        return this._hprRunning;
    }

    /**
     * 暫停HP回覆
     */
    public final void stopHpRegeneration() {
        if (this._hprRunning) {
            this._hprRunning = false;
        }
    }

    /**
     * MP是否具備回覆條件
     *
     * @return
     */
    public final boolean isMpR() {
        if (this.destroyed()) {// 進行刪除
            return false;
        }
        if (this.isDead()) {// 死亡
            return false;
        }
        if (this.getMaxHp() <= 0) {// 沒有HP設置
            return false;
        }
        if (this.getMaxMp() <= 0) {// 沒有MP設置
            return false;
        }
        if (this.getCurrentHp() <= 0) {// 目前HP小於0
            return false;
        }
        if (this.getCurrentMp() >= this.getMaxMp()) {// MP已滿
            return false;
        }
        if (this.getMaxMp() >= this.getCurrentMp()) {// MP未滿
            startMpRegeneration();
        }
        return true;
    }

    /**
     * MP自然回覆
     */
    public final void startMpRegeneration() {
        if (this.destroyed() && this.isDead()) {
            return;
        }
        if (this.getCurrentHp() <= 0) {
            return;
        }
        if (!this._mprRunning) {
            this._mprRunning = true;
        }
    }

    /**
     * MP是否允許回覆
     *
     * @return
     */
    public final boolean isMpRegenerationX() {
        return this._mprRunning;
    }

    /**
     * 暫停MP回覆
     */
    public final void stopMpRegeneration() {
        if (this._mprRunning) {
            this._mprRunning = false;
        }
    }

    /**
     * TODO NPC召喚後 各項數值初始化
     *
     * @param template
     */
    public void setting_template(final L1Npc template) {
        this._npcTemplate = template;
        int randomlevel = 0;
        double rate = 0;
        double diff = 0;
        this.setName(template.get_name());
        final String name = this.newName(template.get_npcId()) + template.get_nameid();
        this.setNameId(name);
        if (template.get_randomlevel() == 0) { // ランダムLv指定なし
            this.setLevel(template.get_level());
        } else { // ランダムLv指定あり（最小值:get_level(),最大值:get_randomlevel()）
            randomlevel = _random.nextInt(template.get_randomlevel() - template.get_level() + 1);
            diff = template.get_randomlevel() - template.get_level();
            rate = randomlevel / diff;
            randomlevel += template.get_level();
            this.setLevel(randomlevel);
        }
        if (template.get_randomhp() == 0) {
            this.setMaxHp(template.get_hp());
            this.setCurrentHpDirect(template.get_hp());
        } else {
            final double randomhp = rate * (template.get_randomhp() - template.get_hp());
            final int hp = (int) (template.get_hp() + randomhp);
            this.setMaxHp(hp);
            this.setCurrentHpDirect(hp);
        }
        if (template.get_randommp() == 0) {
            this.setMaxMp(template.get_mp());
            this.setCurrentMpDirect(template.get_mp());
        } else {
            final double randommp = rate * (template.get_randommp() - template.get_mp());
            final int mp = (int) (template.get_mp() + randommp);
            this.setMaxMp(mp);
            this.setCurrentMpDirect(mp);
        }
        if (template.get_randomac() == 0) {
            this.setAc(template.get_ac());
        } else {
            final double randomac = rate * (template.get_randomac() - template.get_ac());
            final int ac = (int) (template.get_ac() + randomac);
            this.setAc(ac);
        }
        if (template.get_randomlevel() == 0) {
            this.setStr(template.get_str());
            this.setCon(template.get_con());
            this.setDex(template.get_dex());
            this.setInt(template.get_int());
            this.setWis(template.get_wis());
            this.setMr(template.get_mr());
        } else {
            this.setStr((byte) Math.min(template.get_str() + diff, 127));
            this.setCon((byte) Math.min(template.get_con() + diff, 127));
            this.setDex((byte) Math.min(template.get_dex() + diff, 127));
            this.setInt((byte) Math.min(template.get_int() + diff, 127));
            this.setWis((byte) Math.min(template.get_wis() + diff, 127));
            this.setMr((byte) Math.min(template.get_mr() + diff, 127));
            this.addHitup((int) diff * 2);
            this.addDmgup((int) diff * 2);
        }
        this.setPassispeed(template.get_passispeed());
        this.setAtkspeed(template.get_atkspeed());
        this.setAgro(template.is_agro());
        this.setAgrocoi(template.is_agrocoi());
        this.setAgrososc(template.is_agrososc());
        if (this.getNpcTemplate().get_weakAttr() != 0) {
            switch (this.getNpcTemplate().get_weakAttr()) {
                case 1:// 地
                    this.addEarth(-50);
                    break;
                case 2:// 火
                    this.addFire(-50);
                    break;
                case 4:// 水
                    this.addWater(-50);
                    break;
                case 8:// 風
                    this.addWind(-50);
                    break;
                case -1:// 抗地
                    this.addEarth(50);
                    break;
                case -2:// 抗火
                    this.addFire(50);
                    break;
                case -4:// 抗水
                    this.addWater(50);
                    break;
                case -8:// 抗風
                    this.addWind(50);
                    break;
            }
        }
        // 設置多外型
        final int gfxid = this.newGfx(template.get_gfxid());
        this.setTempCharGfx(gfxid);
        this.setGfxId(gfxid);
        // 特定外型改變初始型態
        this.setGfxidInStatus(gfxid);
        if (template.get_randomexp() == 0) {
            this.setExp(template.get_exp());
        } else {
            final int level = this.getLevel();
            int exp = level * level;
            exp += 1;
            this.setExp(exp);
        }
        if (template.get_randomlawful() == 0) {
            this.setLawful(template.get_lawful());
            this.setTempLawful(template.get_lawful());
        } else {
            final double randomlawful = rate * (template.get_randomlawful() - template.get_lawful());
            final int lawful = (int) (template.get_lawful() + randomlawful);
            this.setLawful(lawful);
            this.setTempLawful(lawful);
        }
        this.setPickupItem(template.is_picupitem());
        if (template.is_bravespeed()) {
            this.setBraveSpeed(1);
        } else {
            this.setBraveSpeed(0);
        }
        this.setKarma(template.getKarma());
        this.setLightSize(template.getLightSize());
        if (template.talk()) {// NPC對話判斷
            TALK = template.getNpcExecutor();
        }
        if (template.action()) {// NPC對話執行
            ACTION = template.getNpcExecutor();
        }
        if (template.attack()) {// NPC受到攻擊
            ATTACK = template.getNpcExecutor();
        }
        if (template.death()) {// NPC死亡
            DEATH = template.getNpcExecutor();
        }
        if (template.work()) {// NPC工作時間
            WORK = template.getNpcExecutor();
            if (WORK.workTime() != 0) {
                // 加入NPC工作列隊
                NpcWorkTimer.put(this, WORK.workTime());
            } else {// 工作時間設置為0
                // 執行一次
                WORK.work(this);
            }
        }
        if (template.spawn()) {// NPC召喚
            SPAWN = template.getNpcExecutor();
            SPAWN.spawn(this);
        }
        this._mobSkill = new L1MobSkillUse(this);
    }

    /**
     * 特定外型改變初始型態
     *
     * @param gfxid
     */
    public void setGfxidInStatus(int gfxid) {
        switch (gfxid) {
            case 51:// 持槍警衛
            case 147:// 持槍警衛
            case 110:// 黑騎士
                setStatus(24);
                break;
            case 57:// 妖魔弓箭手
            case 816:// 妖魔弓箭手(塔)
            case 3137:// 警衛
            case 3140:// 警衛
            case 3145:// 弓
            case 3148:// 弓
            case 3151:// 弓
            case 7621:// 阿利歐克 的 守門人
            case 10649:// 腐蝕的 骷髏弓箭手
            case 11714:// 漆黑的 骷髏弓箭手
                setStatus(20);
                break;
            case 111:// 克特
                setStatus(4);
        }
    }

    /**
     * 名稱顏色改變
     *
     * @param npcid
     * @return
     */
    private String newName(final int npcid) {
        String color = "";
        return color;
    }

    /**
     * 取回多外型
     *
     * @param get_gfxid
     * @return
     */
    private int newGfx(int get_gfxid) {
        int[] r = null;
        int newgfx;
        switch (get_gfxid) {
            // 兔子
            case 998:
            case 999:
            case 1002:
            case 1003:
                r = new int[]{998, 999, 1002, 1003};
                break;
            // 蛇女
            case 1597:
            case 1600:
                r = new int[]{1597, 1600};
                break;
            case 5942:// 象牙塔 助手
                r = new int[]{5942, 5135, 5137, 5139, 5141, 5143, 5145, 5156, 5158, 5160, 5162};
                break;
            case 1318:// 鬼魂(藍)
            case 1321:// 鬼魂(紅)
                r = new int[]{1318, 1321};
                break;
        }
        if (r != null) {
            newgfx = _random.nextInt(r.length);
            get_gfxid = r[newgfx];
        }
        return get_gfxid;
    }

    public int getPassispeed() {
        return _passispeed;
    }

    public void setPassispeed(int i) {
        _passispeed = i;
    }

    public int getAtkspeed() {
        return _atkspeed;
    }

    public void setAtkspeed(int i) {
        _atkspeed = i;
    }

    public boolean isPickupItem() {
        return _pickupItem;
    }

    public void setPickupItem(boolean flag) {
        _pickupItem = flag;
    }

    @Override
    public L1Inventory getInventory() {
        return _inventory;
    }

    public void setInventory(L1Inventory inventory) {
        _inventory = inventory;
    }

    public L1Npc getNpcTemplate() {
        return _npcTemplate;
    }

    public int getNpcId() {
        return _npcTemplate.get_npcId();
    }

    public int getPetcost() {
        return _petcost;
    }

    public void setPetcost(int i) {
        _petcost = i;
    }

    /**
     * 取回出生資訊
     *
     * @return
     */
    public L1Spawn getSpawn() {
        return _spawn;
    }

    /**
     * 暫存出生資訊
     *
     * @param spawn
     */
    public void setSpawn(L1Spawn spawn) {
        _spawn = spawn;
    }

    public int getSpawnNumber() {
        return _spawnNumber;
    }

    public void setSpawnNumber(int number) {
        _spawnNumber = number;
    }

    /**
     * 世界物件編號是否再利用
     *
     * @param isReuseId
     */
    public void onDecay(final boolean isReuseId) {
        int id = 0;
        if (isReuseId) {
            id = this.getId();
        } else {
            id = 0;
        }
        this._spawn.executeSpawnTask(this._spawnNumber, id);
    }

    /**
     * 接觸資訊
     */
    @Override
    public void onPerceive(final L1PcInstance perceivedFrom) {
        try {
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack(this));
            onNpcAI();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * NPC物件刪除的處理
     */
    public void deleteMe() {
        if (_destroyed) {
            return;
        }
        if (getFollowEffect() != null) { // R版Top10暴擊特效處理 by 聖子默默
            getFollowEffect().deleteMe();
        }
        int tgid = 0;
        switch (this.getNpcId()) {
            case 92000:// 傑弗雷庫(雌)
                tgid = 92001;
                break;
            case 92001:// 傑弗雷庫(雄)
                tgid = 92000;
                break;
        }
        // 移出分身數據
        if (this instanceof L1IllusoryInstance) {
            if (this.getMaster() != null && (this.getMaster() instanceof L1PcInstance)) {
                L1PcInstance masterPc = (L1PcInstance) this.getMaster();
                if (masterPc != null) {
                    masterPc.get_otherList().removeIllusoryList(this.getId());
                }
            }
        }
        L1NpcInstance tgMob = null;
        if (tgid != 0) {
            // 可見範圍物件
            final ArrayList<L1Object> list = World.get().getVisibleObjects(this);
            for (final L1Object objects : list) {
                if (objects instanceof L1NpcInstance) {
                    L1NpcInstance tgMobR = (L1NpcInstance) objects;
                    if (tgMobR.getNpcId() != tgid) {
                        continue;
                    }
                    // 物件為指定物件 並且未死亡
                    if (!tgMobR.isDead()) {
                        // 復活
                        this.resurrect(this.getMaxHp());
                        return;
                    } else {
                        if (!tgMobR._destroyed) {// 未進行刪除
                            tgMob = tgMobR;
                            break;
                        }
                    }
                }
            }
        }
        this._destroyed = true;// 進行刪除
        if (this.getInventory() != null) {
            this.getInventory().clearItems();
        }
        this.allTargetClear();
        this._master = null;
        final int showid = this.get_showId();
        // 副本編號 是執行中副本
        if (WorldQuest.get().isQuest(showid)) {
            // 移出副本
            WorldQuest.get().remove(showid, this);
        }
        // 取回娃娃
        if (!getDolls().isEmpty()) {
            for (final Object obj : getDolls().values().toArray()) {
                L1DollInstance doll = (L1DollInstance) obj;
                if (doll != null) {
                    doll.deleteDoll();
                }
            }
        }
        if (!getPetList().isEmpty()) {
            for (final Object obj : getPetList().values().toArray()) {
                L1NpcInstance summon = (L1NpcInstance) obj;
                if ((summon != null) && ((summon instanceof L1SummonInstance))) {
                    L1SummonInstance su = (L1SummonInstance) summon;
                    su.deleteMe();
                }
            }
        }
        // 清空清單
        _hateList.clear();
        _dropHateList.clear();
        _targetItemList.clear();
        _del_map.clear();
        // 移出世界
        World.get().removeVisibleObject(this);
        World.get().removeObject(this);
        // 畫面內可見範圍PC物件取回
        final List<L1PcInstance> players = World.get().getRecognizePlayer(this);
        if (players.size() > 0) {
            for (final L1PcInstance tgpc : players) {
                if (tgpc != null) {
                    if (this.getTempCharGfx() == 10947) {// 沙塵暴外型
                        tgpc.sendPackets(new S_DoActionGFX(this.getId(), 8));// 發送結束動畫
                    }
                    tgpc.removeKnownObject(this);// 移出認識清單
                    tgpc.sendPackets(new S_RemoveObject(this));// 移除顯示
                }
            }
        }
        // 認識消除
        removeAllKnownObjects();
        if (tgMob != null) {
            tgMob.deleteMe();
        }
        try {
            // BOSS召喚列表中物件(NPCID)
            if (getNpcTemplate().is_boss()) {
                if (_spawn.get_nextSpawnTime() != null) {
                    // 取得下次召喚時間差異
                    long newTime = (_spawn.get_spawnInterval() * 60 * 1000);// 下次召喚延遲
                    final Calendar cals = Calendar.getInstance();
                    final long nowTime = Calendar.getInstance().getTimeInMillis();// 現在時間
                    // 上次召喚時間
                    long lasttime = _spawn.get_nextSpawnTime().getTimeInMillis();
                    if ((getNpcTemplate().get_npcId() == 97258) || // 巨蟻女皇
                            (getNpcTemplate().get_npcId() == 97259) || // 沙蟲
                            (getNpcTemplate().get_npcId() == 81163)) {// 吉爾塔斯
                        // 計算下次團隊BOSS出現時間
                        do {
                            lasttime += newTime;
                            // System.out.println("延後一天");
                        } while (lasttime < nowTime);
                        cals.setTimeInMillis(lasttime);
                        while ((cals.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) && (cals.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) && (cals.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)) {// 不是星期五、六、日
                            cals.add(Calendar.DAY_OF_MONTH, 1);// 延後一天
                            // System.out.println("不是星期五六日、再延後一天");
                        }
                    } else if ((getNpcTemplate().get_npcId() == 45648) // 暗殺軍王．史雷佛
                            || (getNpcTemplate().get_npcId() == 45676) // 冥法軍王．海露拜
                            || (getNpcTemplate().get_npcId() == 45844) // 魔獸軍王．巴蘭卡
                            || (getNpcTemplate().get_npcId() == 45863)) // 法令軍王．蕾雅
                    {
                        // 計算下次團隊BOSS出現時間
                        do {
                            lasttime += newTime;
                            // System.out.println("延後一天");
                        } while (lasttime < nowTime);
                        cals.setTimeInMillis(lasttime);
                        while ((cals.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) && (cals.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)) {// 不是星期六、日
                            cals.add(Calendar.DAY_OF_MONTH, 1);// 延後一天
                            // System.out.println("不是星期六日、再延後一天");
                        }
                    } else if (getNpcTemplate().get_npcId() == 99019) {// 巨型骷髏
                        // 計算下次團隊BOSS出現時間
                        do {
                            lasttime += newTime;
                            // System.out.println("延後一天");
                        } while (lasttime < nowTime);
                        cals.setTimeInMillis(lasttime);
                        while (cals.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {// 不是星期三
                            cals.add(Calendar.DAY_OF_MONTH, 1);// 延後一天
                            // System.out.println("不是星期三、再延後一天");
                        }
                    } else {// 其他BOSS
                        // 計算下次出現時間
                        if (_random.nextBoolean()) {// 隨機提早或延後15分鐘
                            newTime += _random.nextInt(15 * 60 * 1000);
                        } else {
                            newTime -= _random.nextInt(15 * 60 * 1000);
                        }
                        cals.setTimeInMillis(System.currentTimeMillis() + newTime);
                    }
                    // 設定下次出現時間
                    _spawn.get_nextSpawnTime().setTimeInMillis(cals.getTimeInMillis());
                    // 更新資料庫 下次召喚時間紀錄
                    SpawnBossReading.get().upDateNextSpawnTime(_spawn.getId(), cals);
                    // 公告狀態
                    switch (_spawn.getHeading()) {
                        case 0:// 面相基礎設置為0公告死亡及下次出現時間
                            Date spawnTime = _spawn.get_nextSpawnTime().getTime();
                            final String nextTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(spawnTime);
                            // 9:%s 已經死亡，下次出現時間：%s。
                            World.get().broadcastPacketToAll(new S_ServerMessage(getNameId() + "已經死亡，下次出現時間" + nextTime));
                            break;
                        case 1:// 面相基礎設置為1公告死亡
                            // 8:%s 已經死亡!
                            World.get().broadcastPacketToAll(new S_ServerMessage(getNameId() + "已經死亡!"));
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (final Exception e) {
            // 手動召喚BOSS 因為不具備召喚時間數據
            // 因此忽略此錯誤訊息
            // _log.error(e.getLocalizedMessage(), e);
        }
        // 是否具有隊伍
        L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
        if (mobGroupInfo != null) {
            if (mobGroupInfo.removeMember(this) == 0) {
                setMobGroupInfo(null);
            }
        }
        if (isReSpawn()) {
            _spawn.executeSpawnTask(_spawnNumber, 0);
        }
    }

    /**
     * 刪除隊員
     *
     * @param mob
     */
    public void outParty(L1NpcInstance mob) {
        // 刪除隊員
        final Collection<L1NpcInstance> list = WorldNpc.get().all();
        for (final L1NpcInstance tgNpc : list) {
            if (tgNpc.isDead()) {
                continue;
            }
            if (tgNpc.getMaster() == null) {
                continue;
            }
            if (tgNpc.getMaster().equals(mob)) {
                // 產生動畫
                tgNpc.broadcastPacketAll(new S_SkillSound(tgNpc.getId(), 2236));
                tgNpc.deleteMe();
            }
        }
    }

    /**
     * 受攻擊mp減少計算
     *
     * @param attacker
     * @param damageMp
     */
    public void ReceiveManaDamage(final L1Character attacker, final int damageMp) {
    }

    /**
     * 受攻擊hp減少計算
     *
     * @param attacker
     * @param damage
     */
    public void receiveDamage(final L1Character attacker, final int damage) {
    }

    /**
     * NPC消化道具時間清單
     *
     * @return
     */
    public HashMap<L1ItemInstance, DelItemTime> getDigestItem() {
        return _del_map;
    }

    /**
     * 加入NPC消化道具時間清單
     *
     * @param item
     */
    public void setDigestItem(final L1ItemInstance item) {
        final DelItemTime delItemTime = new DelItemTime();
        delItemTime._del_item_time = this.getNpcTemplate().get_digestitem();
        _del_map.put(item, delItemTime);
        // NpcDigestItemTimer.put(this, item, new
        // Integer(this.getNpcTemplate().get_digestitem()));
    }

    /**
     * 清空NPC消化道具時間清單
     */
    public void getDigestItemClear() {
        _del_map.clear();
    }

    /**
     * NPC消化道具時間清單內無物件
     *
     * @return true:無物件 false:有物件
     */
    public boolean getDigestItemEmpty() {
        return _del_map.isEmpty();
    }

    /**
     * 獲得道具的處理
     *
     * @param item
     */
    public void onGetItem(L1ItemInstance item) {
        refineItem();
        getInventory().shuffle();
        if (getNpcTemplate().get_digestitem() > 0) {
            setDigestItem(item);
        }
    }

    /**
     * 隱藏狀態下玩家接觸時的處理
     *
     * @param pc
     */
    public void approachPlayer(L1PcInstance pc) {
        if ((pc.hasSkillEffect(INVISIBILITY)) || (pc.hasSkillEffect(BLIND_HIDING) || pc.isGmInvis())) {
            return;
        }
        switch (getHiddenStatus()) {
            case 1:
                if ((getCurrentHp() == getMaxHp()) && (pc.getLocation().getTileLineDistance(getLocation()) <= 2)) {
                    appearOnGround(pc);
                }
                break;
            case 2:
                if (getCurrentHp() == getMaxHp()) {
                    if (pc.getLocation().getTileLineDistance(getLocation()) <= 1) {
                        appearOnGround(pc);
                    }
                } else {
                    searchItemFromAir();
                }
                break;
            case 3:
                if (getCurrentHp() < getMaxHp()) {
                    appearOnGround(pc);
                }
                break;
            case 4:
                if ((getCurrentHp() == getMaxHp()) || (getbarrierTime() > 15)) {
                    appearOnGround(pc);
                }
                break;
        }
    }

    public void startKIRTAS_Timer() {
        _kirtastimer = new KIRTAS_Timer(this);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(_kirtastimer, 1000L, 1000L);
    }

    public void stopKIRTAS_Timer() {
        _kirtastimer.cancel();
        _kirtastimer = null;
    }

    public final int getbarrierTime() {
        return _barrierTime;
    }

    public final void setbarrierTime(int i) {
        _barrierTime = i;
    }

    public void appearOnGround(L1PcInstance pc) {
        switch (getHiddenStatus()) {
            case 1:// 遁地
            case 3:// 冰凍
                setHiddenStatus(0);
                if (this.getTempCharGfx() == 1245) {// 地龍
                    broadcastPacketAll(new S_DoActionGFX(getId(), 11));
                } else {
                    broadcastPacketAll(new S_DoActionGFX(getId(), 4));
                }
                setStatus(0);
                broadcastPacketAll(new S_NPCPack(this));
                if ((!pc.hasSkillEffect(INVISIBILITY)) && (!pc.hasSkillEffect(BLIND_HIDING)) && (!pc.isGm())) {
                    _hateList.add(pc, 0);
                    _target = pc;
                }
                onNpcAI();
                break;
            case 4:// 吉爾塔斯反屏
                if ((hasSkillEffect(11060)) && (!hasSkillEffect(11057))) {
                    killSkillEffectTimer(11060);
                    broadcastPacketAll(new S_DoActionGFX(getId(), 21));
                } else if ((hasSkillEffect(11059)) && (!hasSkillEffect(11057))) {
                    killSkillEffectTimer(11059);
                    broadcastPacketAll(new S_DoActionGFX(getId(), 41));
                } else if (hasSkillEffect(11058)) {
                    killSkillEffectTimer(11058);
                    killSkillEffectTimer(78);
                    broadcastPacketAll(new S_DoActionGFX(getId(), 5));
                } else if (hasSkillEffect(11057)) {
                    killSkillEffectTimer(11057);
                    killSkillEffectTimer(11060);
                    killSkillEffectTimer(11059);
                    broadcastPacketAll(new S_DoActionGFX(getId(), 25));
                }
                setbarrierTime(0);
                stopKIRTAS_Timer();
                setHiddenStatus(0);
                setStatus(0);
                broadcastPacketAll(new S_NPCPack(this));
                if ((!pc.hasSkillEffect(INVISIBILITY)) && (!pc.hasSkillEffect(BLIND_HIDING)) && (!pc.isGm())) {
                    _hateList.add(pc, 0);
                    _target = pc;
                }
                onNpcAI();
                break;
            case 2:// 飛天
                setHiddenStatus(0);
                broadcastPacketAll(new S_DoActionGFX(getId(), 45));
                setStatus(0);
                broadcastPacketAll(new S_NPCPack(this));
                if ((!pc.hasSkillEffect(INVISIBILITY)) && (!pc.hasSkillEffect(BLIND_HIDING)) && (!pc.isGm())) {
                    _hateList.add(pc, 0);
                    _target = pc;
                }
                onNpcAI();
                startChat(2);
                break;
        }
    }

    /**
     * 現在目標
     */
    public L1Character is_now_target() {
        return _target;
    }

    /**
     * 現在目標(物品)
     */
    public L1ItemInstance is_now_targetItem() {
        return _targetItem;
    }

    /**
     * 現在目標(物品)
     */
    public void set_now_targetItem(L1ItemInstance item) {
        _targetItem = item;
    }

    /**
     * 移動數據
     */
    public NpcMoveExecutor getMove() {
        return _npcMove;
    }

    /**
     * 使用治愈藥水
     *
     * @param healHp
     * @param effectId
     */
    private void useHealPotion(int healHp, final int effectId) {
        this.broadcastPacketAll(new S_SkillSound(this.getId(), effectId));
        if (this.hasSkillEffect(POLLUTE_WATER)) { // 污濁之水回覆量1/2倍
            healHp = (healHp >> 1);
        }
        if (this.hasSkillEffect(ADLV80_2_2)) {// 污濁的水流(水龍副本 回覆量1/2倍)
            healHp = (healHp >> 1);
        }
        if (this.hasSkillEffect(ADLV80_2_1)) {// 藥水侵蝕術(治愈變成傷害)
            healHp *= -1;
        }
        if (this instanceof L1PetInstance) {
            ((L1PetInstance) this).setCurrentHp(this.getCurrentHp() + healHp);
        } else if (this instanceof L1SummonInstance) {
            ((L1SummonInstance) this).setCurrentHp(this.getCurrentHp() + healHp);
        } else {
            this.setCurrentHpDirect(this.getCurrentHp() + healHp);
        }
    }

    /**
     * 使用加速藥水
     *
     * @param time
     */
    private void useHastePotion(final int time) {
        this.broadcastPacketAll(new S_SkillHaste(this.getId(), 1, time));
        this.broadcastPacketAll(new S_SkillSound(this.getId(), 191));
        this.setMoveSpeed(1);
        this.setSkillEffect(STATUS_HASTE, time * 1000);
    }

    /**
     * 使用加速、補血道具
     *
     * @param type
     * @param chance
     */
    public void useItem(int type, int chance) {
        if (is_decay_potion()) {// 藥水霜化狀態
            return;
        }
        if (_random.nextInt(100) > chance) {
            return;
        }
        if (getInventory() == null) {
            return;
        }
        switch (type) {
            case USEITEM_HEAL:
                if (getInventory().consumeItem(40012, 1L)) {// 白水
                    useHealPotion(75, 197);
                } else if (getInventory().consumeItem(40011, 1L)) {// 澄水
                    useHealPotion(45, 194);
                } else if (getInventory().consumeItem(40010, 1L)) {// 紅水
                    useHealPotion(15, 189);
                }
                break;
            case USEITEM_HASTE:
                if (hasSkillEffect(1001)) {
                    return;
                }
                if (getInventory().consumeItem(140018, 1L)) {
                    useHastePotion(2100);
                } else if (getInventory().consumeItem(40018, 1L)) {
                    useHastePotion(1800);
                } else if (getInventory().consumeItem(140013, 1L)) {
                    useHastePotion(350);
                } else if (getInventory().consumeItem(40013, 1L)) {
                    useHastePotion(300);
                }
                break;
        }
    }

    /**
     * 往指定面向移動1格
     *
     * @param dir
     */
    public void setDirectionMoveSrc(int dir) {
        if ((dir >= 0) && (dir <= 7)) {
            int locx = getX() + HEADING_TABLE_X[dir];
            int locy = getY() + HEADING_TABLE_Y[dir];
            setHeading(dir);
            setX(locx);
            setY(locy);
        }
    }

    /**
     * 目標鄰近位置指定傳送
     *
     * @param nx
     * @param ny
     * @return
     */
    public boolean nearTeleport(int nx, int ny) {
        int rdir = _random.nextInt(8);
        for (int i = 0; i < 8; i++) {
            int dir = rdir + i;
            if (dir > 7) {
                dir -= 8;
            }
            nx += HEADING_TABLE_X[dir];
            ny += HEADING_TABLE_Y[dir];
            if (getMap().isPassable(nx, ny, this)) {
                dir += 4;
                if (dir > 7) {
                    dir -= 8;
                }
                teleport(nx, ny, dir);
                setCurrentMp(getCurrentMp() - 10);
                return true;
            }
        }
        return false;
    }

    /**
     * 目標位置指定座標傳送
     *
     * @param nx
     * @param ny
     * @param heading
     */
    public void teleport(int nx, int ny, int heading) {
        try {
            for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
                pc.sendPackets(new S_SkillSound(getId(), 169));
                pc.sendPackets(new S_RemoveObject(this));
                pc.removeKnownObject(this);
            }
            // 補齊缺少的座標點穿行屬性變動 by 聖子默默
            L1WorldMap.get().getMap(getMapId()).setPassable(getX(), getY(), true, 2);
            setX(nx);
            setY(ny);
            setHeading(heading);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 所处位置为不可穿越类型Npc
     *
     * @return 非 skin effect doll doll2
     */
    protected boolean setPassableInstance() {
        if (this instanceof L1SkinInstance) {
            return false;
        }
        if (this instanceof L1EffectInstance) {
            return false;
        }
        if (this instanceof L1DollInstance2) {
            return false;
        }
        return !(this instanceof L1DollInstance);
    }

    public String getNameId() {
        return _nameId;
    }

    public void setNameId(String s) {
        _nameId = s;
    }

    public boolean isAgro() {
        return _Agro;
    }

    public void setAgro(boolean flag) {
        _Agro = flag;
    }

    public boolean isAgrocoi() {
        return _Agrocoi;
    }

    public void setAgrocoi(boolean flag) {
        _Agrocoi = flag;
    }

    public boolean isAgrososc() {
        return _Agrososc;
    }

    public void setAgrososc(boolean flag) {
        _Agrososc = flag;
    }

    public int getHomeX() {
        return _homeX;
    }

    public void setHomeX(int i) {
        _homeX = i;
    }

    public int getHomeY() {
        return _homeY;
    }

    public void setHomeY(int i) {
        _homeY = i;
    }

    /**
     * 死亡後是否再召喚
     *
     * @return
     */
    public boolean isReSpawn() {
        return _reSpawn;
    }

    /**
     * 設定死亡後是否再召喚
     *
     * @param flag
     */
    public void setreSpawn(boolean flag) {
        _reSpawn = flag;
    }

    public int getLightSize() {
        return _lightSize;
    }

    public void setLightSize(int i) {
        _lightSize = i;
    }

    public boolean isWeaponBreaked() {
        return _weaponBreaked;
    }

    public void setWeaponBreaked(boolean flag) {
        _weaponBreaked = flag;
    }

    /**
     * 1:遁地2:飛天3:冰凍4:吉爾塔斯反屏
     *
     * @return
     */
    public int getHiddenStatus() {
        return _hiddenStatus;
    }

    /**
     * 1:遁地2:飛天3:冰凍4:吉爾塔斯反屏
     *
     * @param i
     */
    public void setHiddenStatus(int i) {
        _hiddenStatus = i;
    }

    public int getMovementDistance() {
        return _movementDistance;
    }

    public void setMovementDistance(int i) {
        _movementDistance = i;
    }

    public int getTempLawful() {
        return _tempLawful;
    }

    public void setTempLawful(int i) {
        _tempLawful = i;
    }

    /**
     * NPC速計算
     *
     * @param sleepTime 原始數據
     * @param type      0:通常 1:加速 2:減速
     * @return 應該延遲的毫秒
     */
    public int calcSleepTime(int sleepTime, final int type) {
        if (sleepTime <= 0) {
            sleepTime = 960; // 如果睡眠時間小於等於0，設定預設值爲960
        }
        switch (this.getMoveSpeed()) {
            case 0: // 通常速度
                break;
            case 1: // 加速
                sleepTime -= (sleepTime * 0.25); // 如果是加速，減少25%的睡眠時間
                break;
            case 2: // 減速
                sleepTime *= 2; // 如果是減速，增加一倍的睡眠時間
                break;
        }
        if (this.getBraveSpeed() == 1) { // 具有勇水狀態
            sleepTime -= (sleepTime * 0.25); // 如果有勇水狀態，減少25%的睡眠時間
        }

        if (this.hasSkillEffect(WIND_SHACKLE)) { // 風之枷鎖167
            if ((type == ATTACK_SPEED) || (type == MAGIC_SPEED)) {
                sleepTime -= (sleepTime * 0.25); // 如果有勇水狀態，減少25%的睡眠時間
            }
        }
        return sleepTime; // 返回計算後的睡眠時間
    }


    /**
     * NPC AI時間軸 正在運行
     *
     * @return
     */
    protected boolean isAiRunning() {
        return this._aiRunning;
    }

    /**
     * NPC AI時間軸 正在運行
     *
     * @param aiRunning
     */
    protected void setAiRunning(final boolean aiRunning) {
        this._aiRunning = aiRunning;
    }

    /**
     * 進行刪除
     *
     * @return true:進行刪除 false:無
     */
    public boolean destroyed() {
        return this._destroyed;
    }

    protected L1MobSkillUse mobSkill() {
        return _mobSkill;
    }

    public String getSpawnLocation() {
        return Spawn_Location;
    }

    public void setSpawnLocation(String st) {
        Spawn_Location = st;
    }

    /**
     * NPC已經激活
     *
     * @return true:激活 false:無
     */
    protected boolean isActived() {
        return this._actived;
    }

    /**
     * NPC已經激活
     *
     * @param actived true:激活 false:無
     */
    protected void setActived(final boolean actived) {
        this._actived = actived;
    }

    protected boolean isFirstAttack() {
        return _firstAttack;
    }

    protected void setFirstAttack(boolean firstAttack) {
        _firstAttack = firstAttack;
    }
    //815 原有瑪那設定 2024.03.31
    //	public int drainMana(int drain) {
    //		if (_drainedMana >= 40) {
    //			return 0;
    //		}
    //		int result = Math.min(drain, getCurrentMp());
    //		if (_drainedMana + result > 40) {
    //			result = 40 - _drainedMana;
    //		}
    //		_drainedMana += result;
    //		return result;
    //	}

    /**
     * 動作間隔時間
     *
     * @return
     */
    protected int getSleepTime() {
        return _sleep_time;
    }

    /**
     * 動作間隔時間
     *
     * @param sleep_time
     */
    public void setSleepTime(int sleep_time) {
        _sleep_time = sleep_time;
    }

    /**
     * 死亡處理中
     *
     * @return
     */
    protected boolean isDeathProcessing() {
        return this._deathProcessing;
    }

    /**
     * 死亡處理中
     *
     * @param deathProcessing
     */
    protected void setDeathProcessing(final boolean deathProcessing) {
        this._deathProcessing = deathProcessing;
    }

    public int drainMana(int drain) {
        if (this._drainedMana >= ConfigOtherSet2.drainedMana) {
            return 0;
        }
        int result = Math.min(drain, getCurrentMp());
        if (this._drainedMana + result > ConfigOtherSet2.drainedMana) {
            result = ConfigOtherSet2.drainedMana - this._drainedMana;
        }
        this._drainedMana += result;
        return result;
    }

    /**
     * NPC死亡變身的處理
     */
    public void transform(final int transformId) {
        this.stopHpRegeneration();
        this.stopMpRegeneration();
        final int transformGfxId = this.getNpcTemplate().getTransformGfxId();
        if (transformGfxId != 0) {
            this.broadcastPacketAll(new S_SkillSound(this.getId(), transformGfxId));
        }
        final L1Npc npcTemplate = NpcTable.get().getTemplate(transformId);
        this.setting_template(npcTemplate);
        this.broadcastPacketAll(new S_ChangeShape(this, this.getTempCharGfx()));
        for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
            // 副本ID不相等 不相護顯示
            if (pc.get_showId() != this.get_showId()) {
                continue;
            }
            this.onPerceive(pc);
        }
    }

    /**
     * NPC 動作暫停
     *
     * @return
     */
    public boolean isRest() {
        return this._rest;
    }

    /**
     * 設置 NPC 動作暫停
     *
     * @param rest
     */
    public void setRest(final boolean rest) {
        this._rest = rest;
    }

    /**
     * 是復活過的
     *
     * @return
     */
    public boolean isResurrect() {
        return this._isResurrect;
    }

    /**
     * 是復活過的
     *
     * @param flag
     */
    public void setResurrect(final boolean flag) {
        this._isResurrect = flag;
    }

    /**
     * 復活處理
     */
    @Override
    public synchronized void resurrect(final int hp) {
        if (this._destroyed) {
            return;
        }
        super.resurrect(hp);
        this.startHpRegeneration();
        this.startMpRegeneration();
        this.broadcastPacketAll(new S_Resurrection(this, this, 0));
        // キャンセレーションをエフェクトなしでかける
        // 本來は死亡時に行うべきだが、負荷が大きくなるため復活時に行う
        final L1SkillUse skill = new L1SkillUse();
        skill.handleCommands(null, CANCELLATION, this.getId(), this.getX(), this.getY(), 0, L1SkillUse.TYPE_LOGIN, this);
        // 移出死亡清單
        if (_deadTimerTemp != -1) {
            _deadTimerTemp = -1;
        }
    }

    /**
     * 加入NPC死亡清單
     *
     * @param deltime 刪除時間(秒)
     */
    protected synchronized void startDeleteTimer(int deltime) {
        if (_deadTimerTemp != -1) {
            return;
        }
        _deadTimerTemp = deltime;
    }

    /**
     * 是否在隊伍中
     *
     * @return
     */
    public boolean isInMobGroup() {
        return this.getMobGroupInfo() != null;
    }

    /**
     * NPC隊伍資訊
     *
     * @return
     */
    public L1MobGroupInfo getMobGroupInfo() {
        return this._mobGroupInfo;
    }

    /**
     * NPC隊伍資訊
     *
     * @param m
     */
    public void setMobGroupInfo(final L1MobGroupInfo m) {
        this._mobGroupInfo = m;
    }

    /**
     * NPC隊伍 隊長NPCID
     *
     * @return
     */
    public int getMobGroupId() {
        return this._mobGroupId;
    }

    /**
     * NPC隊伍 隊長NPCID
     *
     * @param i
     */
    public void setMobGroupId(final int i) {
        this._mobGroupId = i;
    }

    /**
     * NPC出現的對話
     *
     * @param chatTiming
     */
    public void startChat(final int chatTiming) {
        // 出現時のチャットにも關わらず死亡中、死亡時のチャットにも關わらず生存中
        if ((chatTiming == CHAT_TIMING_APPEARANCE) && this.isDead()) {
            return;
        }
        if ((chatTiming == CHAT_TIMING_DEAD) && !this.isDead()) {
            return;
        }
        if ((chatTiming == CHAT_TIMING_HIDE) && this.isDead()) {
            return;
        }
        if ((chatTiming == CHAT_TIMING_GAME_TIME) && this.isDead()) {
            return;
        }
        final int npcId = this.getNpcTemplate().get_npcId();
        L1NpcChat npcChat = null;
        switch (chatTiming) {
            case CHAT_TIMING_APPEARANCE:// 出現
                npcChat = NpcChatTable.get().getTemplateAppearance(npcId);
                break;
            case CHAT_TIMING_DEAD:// 死亡
                npcChat = NpcChatTable.get().getTemplateDead(npcId);
                break;
            case CHAT_TIMING_HIDE:// 取消隱藏
                npcChat = NpcChatTable.get().getTemplateHide(npcId);
                break;
            case CHAT_TIMING_GAME_TIME:// 指定時間
                npcChat = NpcChatTable.get().getTemplateGameTime(npcId);
                break;
        }
        if (npcChat == null) {
            return;
        }
        final Timer timer = new Timer(true);
        final L1NpcChatTimer npcChatTimer = new L1NpcChatTimer(this, npcChat);
        if (!npcChat.isRepeat()) {
            timer.schedule(npcChatTimer, npcChat.getStartDelayTime());
        } else {
            timer.scheduleAtFixedRate(npcChatTimer, npcChat.getStartDelayTime(), npcChat.getRepeatInterval());
        }
    }

    /**
     * 弓的攻擊動畫
     *
     * @return
     */
    public int getBowActId() {
        if (_bowActId != -1) {
            return _bowActId;
        }
        return this.getNpcTemplate().getBowActId();
    }

    /**
     * 弓的攻擊動畫
     *
     * @param bowActId
     */
    public void setBowActId(int bowActId) {
        _bowActId = bowActId;
    }

    /**
     * 攻擊距離
     *
     * @return
     */
    public int get_ranged() {
        if (_ranged != -1) {
            return _ranged;
        }
        return this.getNpcTemplate().get_ranged();
    }

    /**
     * 攻擊距離
     *
     * @param ranged
     */
    public void set_ranged(int ranged) {
        _ranged = ranged;
    }

    public final int getSkyTime() {
        return _skyTime;
    }

    public final void setSkyTime(int i) {
        _skyTime = i;
    }

    public int get_quest_id() {
        return _quest_id;
    }

    public void set_quest_id(int quest_id) {
        _quest_id = quest_id;
    }

    public boolean isShop() {
        return _isShop;
    }

    public void set_isShop(boolean isShop) {
        if ((this instanceof L1DeInstance)) {
            L1DeInstance de = (L1DeInstance) this;
            de.setShopChat(DeShopChatTable.get().getChat(), DeShopChatTable.get().getChat());
        }
        _isShop = isShop;
    }

    /**
     * 傳回存在時間(秒)
     *
     * @return
     */
    public int get_spawnTime() {
        return _spawnTime;
    }

    /**
     * 具有存在時間
     *
     * @return
     */
    public boolean is_spawnTime() {
        return _isspawnTime;
    }

    /**
     * 設定存在時間(秒)
     *
     * @param spawnTime
     */
    public void set_spawnTime(int spawnTime) {
        _spawnTime = spawnTime;
        _isspawnTime = true;
    }

    /**
     * 施展過移除裝備
     *
     * @return
     */
    public boolean isremovearmor() {
        return _isremovearmor;
    }

    /**
     * 施展過移除裝備
     *
     * @param isremovearmor
     */
    public void set_removearmor(boolean isremovearmor) {
        _isremovearmor = isremovearmor;
    }

    /**
     * 死亡時間(秒)
     *
     * @return
     */
    public int get_deadTimerTemp() {
        return _deadTimerTemp;
    }

    /**
     * 死亡時間(秒)
     *
     * @param time
     */
    public void set_deadTimerTemp(int time) {
        _deadTimerTemp = time;
    }

    /**
     * 動作暫停時間(秒)
     *
     * @return
     */
    public int get_stop_time() {
        return _stop_time;
    }

    /**
     * 動作暫停時間(秒)
     *
     * @param time
     */
    public void set_stop_time(int time) {
        _stop_time = time;
    }

    public int get_work_time() {
        return _work_time;
    }

    public void set_work_time(int time) {
        _work_time = time;
    }

    public long getShellsDamageTime() {
        return _shells_damag_time;
    }

    public void setShellsDamageTime(long i) {
        _shells_damag_time = i;
    }

    public long getShellsSilenceTime() {
        return _shells_silence_time;
    }

    public void setShellsSilenceTime(long i) {
        _shells_silence_time = i;
    }

    public String get_craftkey() {
        return _craftkey;
    }

    public void set_craftkey(String craftkey) {
        _craftkey = craftkey;
    }

    public void set_currentPetStatus() {
        // TODO Auto-generated method stub
    }

    public boolean isGripped() {
        return _gripped;
    }

    public void setGripped(final boolean gripped) {
        _gripped = gripped;
    }

    /**
     * 騎士團
     */
    public int getClanid() {
        return clanid;
    }

    /**
     * 騎士團
     */
    public void setClanid(int clanid) {
        this.clanid = clanid;
    }


    // 刪除物件計時器
    public static class DelItemTime {
        public int _del_item_time = 0;// 單位:秒
    }
}
