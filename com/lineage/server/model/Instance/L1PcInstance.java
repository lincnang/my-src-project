package com.lineage.server.model.Instance;

import com.add.BigHot.L1BigHotSpList;
import com.add.L1PcUnlock;
import com.add.MJBookQuestSystem.Loader.UserMonsterBookLoader;
import com.add.MJBookQuestSystem.Loader.UserWeekQuestLoader;
import com.add.MJBookQuestSystem.UserMonsterBook;
import com.add.MJBookQuestSystem.UserWeekQuest;
import com.add.Mobbling.L1MobSpList;
import com.add.Tsai.Astrology.Astrology1Table;
import com.add.Tsai.Astrology.AstrologyData;
import com.add.Tsai.Astrology.AttonAstrologyData;
import com.add.Tsai.Astrology.AttonAstrologyTable;
import com.add.Tsai.collect;
import com.lineage.DatabaseFactory;
import com.lineage.config.*;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.CampSet;
import com.lineage.data.event.EffectAISet;
import com.lineage.data.event.OnlineGiftSet;
import com.lineage.data.event.ProtectorSet;
import com.lineage.data.item_weapon.proficiency.L1WeaponProficiency;
import com.lineage.data.quest.Chapter01R;
import com.lineage.echo.ClientExecutor;
import com.lineage.echo.EncryptExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.CheckFightTimeController;
import com.lineage.server.Controller.L1DarknessMonitor;
import com.lineage.server.Controller.L1PcAtkMonitor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.clientpackets.AcceleratorChecker;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.*;
import com.lineage.server.datatables.sql.CharSkillTable;
import com.lineage.server.templates.L1EmblemIcon;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.pcdelay.PcTelDelayAI;
import com.lineage.server.model.*;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.model.monitor.L1PcInvisDelay;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.serverpackets.ability.*;
import com.lineage.server.templates.*;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.other.ins.*;
import com.lineage.server.timecontroller.pc.MapTimerThread;
import com.lineage.server.timecontroller.pc.PcHellTimer;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.*;
import com.lineage.server.utils.collections.Maps;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldQuest;
import com.lineage.server.world.WorldWar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.*;
import java.util.concurrent.*;

import static com.lineage.config.ThreadPoolSetNew.TELEPORT_NEED_COUNT;
import static com.lineage.server.model.Instance.L1NpcInstance.HIDDEN_STATUS_FLY;
import static com.lineage.server.model.Instance.L1NpcInstance.HIDDEN_STATUS_SINK;
import static com.lineage.server.model.skill.L1SkillId.*;

public class L1PcInstance extends L1Character { // src015
    public static final int CLASSID_KNIGHT_MALE = 61;
    public static final int CLASSID_KNIGHT_FEMALE = 48;
    public static final int CLASSID_ELF_MALE = 138;
    public static final int CLASSID_ELF_FEMALE = 37;
    public static final int CLASSID_WIZARD_MALE = 734;
    public static final int CLASSID_WIZARD_FEMALE = 1186;
    public static final int CLASSID_DARK_ELF_MALE = 2786;
    public static final int CLASSID_DARK_ELF_FEMALE = 2796;
    public static final int CLASSID_PRINCE = 0;
    public static final int CLASSID_PRINCESS = 1;
    public static final int CLASSID_DRAGON_KNIGHT_MALE = 6658;
    public static final int CLASSID_DRAGON_KNIGHT_FEMALE = 6661;
    public static final int CLASSID_ILLUSIONIST_MALE = 6671;
    public static final int CLASSID_ILLUSIONIST_FEMALE = 6650;
    public static final int CLASSID_WARRIOR_MALE = 12490;
    public static final int CLASSID_WARRIOR_FEMALE = 12494;
    public static final int REGENSTATE_NONE = 4;
    public static final int REGENSTATE_MOVE = 2;
    public static final int REGENSTATE_ATTACK = 1;
    public static final int INTERVAL_BY_AWAKE = 4;
    /**
     * 最大尋怪週期 根據PcAI實際運作情況進行調整
     */
    public static final int _maxThinkingCycle = 10;
    /**
     * 最大尋路週期 根據PcAI實際運作情況進行調整
     */
    public static final int _maxAutoMoveCycle = 13;
    public static final int _maxMoveDirection = 30;
    /**
     * <font color="00ff00">移动</font>
     */
    public static final int MOVE_TYPE = 0;
    /**
     * <font color="00ff00">攻击</font>
     */
    public static final int ATTACK_TYPE = 1;
    /**
     * <font color="00ff00">有向施法</font>
     */
    public static final int SPELL_DIR_TYPE = 2;
    /**
     * <font color="00ff00">无向施法</font>
     */
    public static final int SPELL_NO_DIR_TYPE = 3;
    /**
     * <font color="00ff00">特殊攻击</font>
     */
    public static final int ALT_ATTACK_TYPE = 4;
    private static final Log _log = LogFactory.getLog(L1PcInstance.class);
    private static final long serialVersionUID = 1L;
    private static final Map<Long, Double> _magicDamagerList = new HashMap<>();
    private static final long DELAY_INVIS = 3000L;
    private static final Random _random = new Random();
    private static final boolean _debug = Config.DEBUG;
    private static final Timer _regenTimer = new Timer(true);
    /**
     * 宙斯·守護星盤能力值加載記錄
     */
    private static final ConcurrentHashMap<Integer, AstrologyData> ASTROLOGY_DATA_MAP = Maps.newConcurrentHashMap();
    private final Map<Integer, AttonAstrologyData> ATTON_ASTROLOGY_DATA_MAP = new ConcurrentHashMap<>();

    private static BufferedWriter out;
    // 日版記憶座標
    public final ArrayList<L1BookMark> _bookmarks;
    public final ArrayList<L1BookMark> _speedbookmarks;
    protected final L1HateList _hateList = new L1HateList();// 目標清單
    private final L1PcInventory _inventory;
    // private final HashMap<Integer, L1QuestNew> L1QuestNew = new HashMap<>();
    private final Map<Integer, L1SkinInstance> _skins = new HashMap<>();
    private final L1PandoraInventory _pandora = new L1PandoraInventory(this);
    private final L1Karma _karma = new L1Karma();
    private final L1DwarfInventory _dwarf;
    private final L1DwarfForChaInventory _dwarfForCha;
    private final L1DwarfForElfInventory _dwarfForElf;
    private final L1DwarfForGameMallInventry _dwarfForMALL;
    private final L1RemoveItemInventory _dwarfForRemoveItem;// 輔助(自動刪物)
    private final Map<Integer, L1ItemPower_text> _allpowers = new ConcurrentHashMap<>();
    /**
     * [原碼] 怪物對戰系統
     */
    private final L1MobSpList _MobSpList;
    /**
     * [原碼] 大樂透系統
     */
    private final L1BigHotSpList _BigHotSpList;
    private final Map<Integer, L1ItemPower_text> _powers = new ConcurrentHashMap<>();
    private final L1Inventory _tradewindow; // 交易視窗
    // -- 加速器檢知機能 --
    private final AcceleratorChecker _acceleratorChecker = new AcceleratorChecker(this);
    private final ArrayList<Object> _tempObjects = new ArrayList<>();
    private final ArrayList<Integer> _skillList = new ArrayList<>();
    private final Map<String, Integer> temporaryEffects = new HashMap<>();
    private final int _callsrc = 0;
    private final ArrayList<L1PrivateShopSellList> _sellList = new ArrayList<>();
    private final ArrayList<L1PrivateShopBuyList> _buyList = new ArrayList<>();
    private final L1PcQuest _quest;
    private final L1ActionPc _action;
    private final L1ActionPet _actionPet;
    private final L1ActionSummon _actionSummon;
    private final L1EquipmentSlot _equipSlot;
    /**
     * 武器熟練度
     */
    private final L1WeaponProficiency _weaponProficiency;
    private final Object _invisTimerMonitor = new Object();
    private final int[] _doll_get = new int[2];
    private final ArrayList<String> _InviteList = new ArrayList<>();
    private final ArrayList<String> _cmalist = new ArrayList<>();
    private final AcceleratorChecker _speed;
    // 奪魂T吸血量
    private final ArrayList<Integer> soulHp = new ArrayList<>();
    // 轉生天賦
    private final int[] reincarnationSkill = new int[3];
    private final ArrayList<String> _ds = new ArrayList<>();
    private final Timer _timeHandler = new Timer(true);
    private final ArrayList<Integer> _targetId = new ArrayList<>();
    private final ArrayList<String> _attackenemy = new ArrayList<>();
    private final ArrayList<Integer> _autoattackNew = new ArrayList<>();
    private final List<Integer> _adenaTradeIndexList = new CopyOnWriteArrayList<>();
    private final List<L1CharacterAdenaTrade> _adenaTradeList = new CopyOnWriteArrayList<>();
    private final int _IgnoreDmgR = 0;
    private final int _DownTgDmgR = 0;
    private final int _IgnoreMagicR = 0;
    private final int _DownTgMagicR = 0;
    // 技能ID -> 等級 (預設0表示尚未升)
    private final Map<Integer, Integer> _skillLevels = new ConcurrentHashMap<>();
    public short _originalHpr = 0;
    public short _originalMpr = 0;
    public long _oldTime = 0L;
    public short _temp;
    public short _baseMaxMpc;
    // l1j-jp釣魚
    public int fishX = 0;
    public int fishY = 0;
    /**
     * 火龍窟副本狀態控制
     */
    public int ValakasStatus = 0;
    /**
     * 新手任務
     */
    public int cL = 0;
    public int _Pbacount = 0;
    // 底比斯大戰遊戲
    public boolean isSiege = false;
    public boolean _TRIPLEARROW = false;
    public int[] _reward_ac = new int[2];
    protected List<L1ItemInstance> _items = new CopyOnWriteArrayList<>();
    protected NpcMoveExecutor _pcMove = null;// XXX
    int _venom_resist = 0;
    private L1PcOther1 _other1;
    private int _polyarrow;  // 箭矢特效的管理類

    private int _vipDiceChance = 0;
    private int _vipDiceDamage = 0;

    public int getVipDiceChance() { return _vipDiceChance; }
    public int getVipDiceDamage() { return _vipDiceDamage; }
    public void setVipDiceChance(int value) { _vipDiceChance = value; }
    public void setVipDiceDamage(int value) { _vipDiceDamage = value; }

    private int _arrowId;
    private int _stingId;
    /**
     * vip 保護經驗
     */
    private boolean _death_exp = false;
    /**
     * vip 保護物品
     */
    private boolean _death_item = false;
    /**
     * vip 保護技能
     */
    private boolean _death_skill = false;
    /**
     * vip 保護積分
     */
    private boolean _death_score = false;
    private boolean _isKill = false;
    private boolean isTripleArrow = false;
    private short _hpr = 0;
    private short _trueHpr = 0;
    private short _mpr = 0;
    private short _trueMpr = 0;
    private boolean _mpRegenActive;
    private boolean _mpReductionActiveByAwake;
    private boolean _hpRegenActive;
    private int _hpRegenType = 0;
    private int _hpRegenState = 4;
    private int _mpRegenType = 0;
    private int _mpRegenState = 4;
    private int _awakeMprTime = 0;
    private int _awakeSkillId = 0;
    private int _old_lawful;
    private int _old_karma;
    private boolean _jl1 = false;
    private boolean _jl2 = false;
    private boolean _jl3 = false;
    private boolean _el1 = false;
    private boolean _el2 = false;
    private boolean _el3 = false;
    private long _old_exp;
    private boolean _isCHAOTIC = false;
    private boolean _isShow_Open_PandoraMsg = true; // 抽抽樂
    private int _bowHit;
    private int _trueBowHit;
    //衝暈等級
    private int _shockStunLevel = 0;
    /**
     * 自動掛機 釋放技能%
     */
    private Integer _autoAttackMP = 15;
    /**
     * 自動掛機 間格秒數
     */
    private Integer _autoAttackSecond = 0;
    private boolean _attackskill = false;
    private int _summonid = 0;
    private boolean _issetsummonfalg = false;
    private int _m = 50;
    private int _h = 50;
    private int _h1 = 0;
    private int _m1 = 0;
    private boolean _autoteleport = true;
    private boolean _callclan = false;
    private boolean _iscall = false;
    private boolean _indexcall = false;
    private int _BossSeachPage = 0;
    private int _na432 = -1;
    private L1ClassFeature _classFeature = null;
    private int _PKcount;
    private int _PkCountForElf;
    private int _clanid;
    private String clanname;
    private int _clanRank;
    private byte _sex;
    private byte[] _shopChat;
    private boolean _isPrivateShop = false;
    private boolean _isTradingInPrivateShop = false;
    private int _partnersPrivateShopItemCount = 0;
    private EncryptExecutor _out;
    private int _originalEr = 0;
    private int _forgetElfAttrCount;
    private ClientExecutor _netConnection = null;
    private int _classId;
    private int _type;
    private long _exp;
    private boolean _gm;
    private boolean _monitor;
    private boolean _gmInvis;
    private short _accessLevel;
    private int _currentWeapon;
    private L1ItemInstance _weapon;
    private L1Party _party;
    private L1ChatParty _chatParty;
    private int _partyID;
    private int _tradeID;
    private boolean _tradeOk;
    private int _tempID;
    private boolean _isTeleport = false;
    private boolean _isDrink = false;
    private boolean _isGres = false;
    private boolean _isPinkName = false;
    private String _accountName;
    private short _baseMaxHp = 0;
    private short _baseMaxMp = 0;
    private int _baseAc = 0;
    private int _originalAc = 0;
    private int _baseStr = 0;
    private int _baseCon = 0;
    private int _baseDex = 0;
    private int _baseCha = 0;
    private int _baseInt = 0;
    private int _baseWis = 0;
    private int _originalStr = 0;
    private int _originalCon = 0;
    private int _originalDex = 0;
    private int _originalCha = 0;
    private int _originalInt = 0;
    private int _originalWis = 0;
    private int _originalDmgup = 0;
    private int _originalBowDmgup = 0;
    private int _originalHitup = 0;
    private int _originalBowHitup = 0;
    private int _originalMr = 0;
    private int _diceDmg = 0;
    private int _dmg = 0;
    private int _originalMagicHit = 0;
    private int _originalMagicCritical = 0;
    private int _originalMagicConsumeReduction = 0;
    private int _originalMagicDamage = 0;
    private int _originalHpup = 0;
    private int _originalMpup = 0;
    private int _baseDmgup = 0;
    private int _baseBowDmgup = 0;
    private int _baseHitup = 0;
    private int _baseBowHitup = 0;
    private int _baseMr = 0;
    private int _advenHp;
    private int _advenMp;
    private int _highLevel;
    private int _bonusStats;
    private int _otherStats;
    private int _addPoint;
    private int _delPoint;
    private int _elixirStats;
    private int _elfAttr;
    private int _expRes;
    private int _partnerId;
    private int _onlineStatus;
    private int _homeTownId;
    private int _contribution;
    private int _hellTime;
    private boolean _banned;
    private int _food;
    private int invisDelayCounter = 0;
    private boolean _ghost = false;
    private int _ghostTime = -1;
    private boolean _ghostCanTalk = true;
    private boolean _isReserveGhost = false;
    private int _ghostSaveLocX = 0;
    private int _ghostSaveLocY = 0;
    private short _ghostSaveMapId = 0;
    private int _ghostSaveHeading = 0;
    private Timestamp _lastPk;
    private Timestamp _lastPkForElf;
    private Timestamp _deleteTime;
    private double _weightUP = 1.0D;
    private int _weightReduction = 0;
    private int _originalStrWeightReduction = 0;
    private int _originalConWeightReduction = 0;
    private int _hasteItemEquipped = 0;
    private int _damageReductionByArmor = 0;
    private int _hitModifierByArmor = 0;
    private int _dmgModifierByArmor = 0;
    private int _bowHitModifierByArmor = 0;
    private int _bowDmgModifierByArmor = 0;
    private boolean _gresValid;
    private int _cookingId = 0;
    private int _dessertId = 0;
    private int _teleportX = 0;
    private int _teleportY = 0;
    private short _teleportMapId = 0;
    private int _teleportHeading = 0;
    private int _tempCharGfxAtDead;
    private boolean _isCanWhisper = true;
    private boolean _isShowTradeChat = true;
    private boolean _isShowWorldChat = true;
    private int _fightId;
    private byte _chatCount = 0;
    private long _oldChatTimeInMillis = 0L;
    private int _callClanId;
    private int _callClanHeading;
    private boolean _isInCharReset = false;
    private int _tempLevel = 1;
    private int _tempMaxLevel = 1;
    private boolean _isSummonMonster = false;
    private boolean _isShapeChange = false;
    private String _text;
    private byte[] _textByte = null;
    private L1PcOther _other;
    private L1PcOtherList _otherList;
    private int _oleLocX;
    private int _oleLocY;
    private L1PcInstance _target = null;
    private L1DeInstance _outChat = null;
    private long _h_time;
    private boolean _mazu = false;
    private int _mazu_time = 0;
    private int _int1;
    private int _int2;
    private int _evasion;
    private double _expadd = 0.0D;
    private int _dd1;
    private int _dd2;
    // private ArrayList<L1TradeItem> _trade_items = new ArrayList<L1TradeItem>();
    private boolean _isFoeSlayer = false;
    private int _weaknss;
    private long _weaknss_t;
    private int _actionId = -1;
    private Chapter01R _hardin;
    private int _unfreezingTime;
    private int _misslocTime;
    private L1User_Power _c_power;
    private int _dice_hp;
    private int _sucking_hp;
    private int _dice_mp;
    private int _sucking_mp;
    private int _double_dmg;
    private int _lift;
    private int _magic_modifier_dmg = 0;
    private int _magic_reduction_dmg = 0;
    private boolean _rname = false;
    private boolean _retitle = false;
    private int _repass = 0;
    private int _mode_id = 0;
    private boolean _check_item = false;
    private boolean _vip_1 = false;
    private boolean _vip_2 = false;
    private boolean _vip_3 = false;
    private boolean _vip_4 = false;
    // private AcceleratorChecker _speed = null;
    private long _global_time = 0L;
    private int _doll_hpr = 0;
    private int _doll_hpr_time = 0;
    private int _doll_hpr_time_src = 0;
    private int _doll_mpr = 0;
    private int _doll_mpr_time = 0;
    private int _doll_mpr_time_src = 0;
    private int _doll_get_time = 0;
    private int _doll_get_time_src = 0;
    private String _board_title;
    private String _board_content;
    private long _spr_move_time = 0L;
    private long _spr_attack_time = 0L;
    private long _spr_skill_time = 0L;
    private int _delete_time = 0;
    private int _up_hp_potion = 0;// 增加藥水回復量%
    private int _uhp_number;// 增加藥水回復指定量
    private int _arena = 0;
    private int _temp_adena = 0;
    private long _ss_time = 0L;
    private int _ss = 0;
    private int killCount;
    private int _meteLevel;
    private L1MeteAbility _meteAbility;
    private boolean _isProtector;
    private boolean _isGetPolyPower;
    private boolean _isMars;
    private L1Apprentice _apprentice;
    private int _tempType;
    private Timestamp _punishTime;
    private int _magicDmgModifier;
    private int _magicDmgReduction;
    // 奪魂T
    private int _soulHp_r = 0;
    private int _soulHp_hpmin = 0;
    private int _soulHp_hpmax = 0;
    // 水龍甲
    private int _elitePlateMail_Fafurion = 0;
    private int _fafurion_hpmin = 0;
    private int _fafurion_hpmax = 0;
    // 風龍甲
    private int _elitePlateMail_Lindvior;
    private int _lindvior_mpmin;
    private int _lindvior_mpmax;
    // 火龍甲
    private int _elitePlateMail_Valakas;
    private int _valakas_dmgmin;
    private int _valakas_dmgmax;
    // 黑帝斯斗篷
    private int _hades_cloak;
    private int _hades_cloak_dmgmin;
    private int _hades_cloak_dmgmax;
    // 死亡騎士脛甲
    private int _death_pant;
    private int _death_pant_dmgmin;
    private int _death_pant_dmgmax;
    // 六芒星魔法符
    private int _Hexagram_Magic_Rune;
    private int _hexagram_hpmin;
    private int _hexagram_hpmax;
    private int _hexagram_gfx;
    // 蒂蜜特的祝福
    private int _dimiter_mpr_rnd;
    private int _dimiter_mpmin;
    private int _dimiter_mpmax;
    private int _dimiter_bless;
    private int _dimiter_time;
    private int _expPoint;
    private int _pay;
    private int _SummonId = 0;
    private L1PolyPower _polyPower;
    private int _lap = 1;
    private int _lapCheck = 0;
    //========================================================================================================
    private boolean _order_list = false;
    // 收藏
    private collect _collectTemp;
    private double _GF;
    private boolean _isFishing = false;
    private boolean _isFishingReady = false;
    private long _fishingTime = 0;
    private L1ItemInstance _fishingitem;
    private int _DamageIncreasePVE = 0; // PVE攻擊力加成
    private int _DamageReductionPVE = 0; // PVE被怪打減傷
    private int tripleArrowCount = 0; // 記錄三重矢施放次數
    private L1Character lastTripleArrowTarget = null; // 新增，用來記錄上次攻擊的目標
    private int _tempMeleeEvasion = 0;
    //========================================================================================================
    //等級排行榜
    private int _groupId;  // 排行榜組別
    private int _ranking;  // 排行榜名次
    /**
     * 武器特效動畫 [自己] 開關
     *
     */
    private boolean _attackme = true;
    /**
     * 武器特效動畫 [對方] 開關
     *
     */
    private boolean _attackhe = false;
    /**
     * 套裝特效動畫 [自己] 開關
     *
     */
    private boolean _armorme = true;
    /**
     * 套裝特效動畫 [對方] 開關
     *
     */
    private boolean _armorhe = false;
    /**
     * 掉寶公告訊息 [自身] 開關
     *
     */
    private boolean _droplist = true;
    /**
     * 殺人公告訊息 [自身] 開關
     *
     */
    private boolean _kill = true;
    private int _weaponMD;
    private int _weaponMDC;
    private int _reducedmg;
    private int _reduceMdmg;
    // 真 妲蒂斯魔石
    private boolean _EffectDADIS;
    // 妲蒂斯魔石
    private boolean _EffectGS;
    private int isSoulHp = 0;
    private int _isStar;
    private int _isBigHot;
    private String _bighot1;
    private String _bighot2;
    private String _bighot3;
    private String _bighot4;
    private String _bighot5;
    private String _bighot6;
    /**
     * 隊伍對決系統
     */
    private boolean _isATeam = false;
    private boolean _isBTeam = false;
    // 血盟再加入時間
    private Timestamp _rejoinClanTime;
    /**
     * 組隊類型
     */
    private int _partyType;
    /**
     * [原碼] 無限大戰計分系統
     */
    private int _ubscore;
    /**
     * [原碼] 定時外掛檢測
     */
    private int _super;
    /**
     * [原碼] 定時外掛檢測
     */
    private int _super2;
    private int _inputerror;// 輸入錯誤
    private int _speederror;// 加速器偵測 封鎖帳號判斷用錯誤次數
    private int _banerror;// 逾時斷線 封鎖帳號判斷用錯誤次數
    private int _inputbanerror;// 輸入錯誤斷線 封鎖帳號判斷用錯誤次數
    private int _attbowdmgr = 0; // Att
    // 7.6
    private Date _birthday;
    // 飾品開啟欄位判斷
    private int _Slot = 0;
    // 是否使用道具變身
    private boolean _itempoly = false;
    // 自訂變身卷軸
    private L1ItemInstance _polyscroll;
    // 墓碑系統
    private L1EffectInstance _tomb;
    // 是否魔法爆擊狀態
    private boolean _isMagicCritical;
    // 是否正在使用幻象的傲慢之塔移動傳送符
    private boolean _isPhantomTeleport;
    /**
     * 奇巖地監/古魯丁地監 已使用時間(秒).
     */
    private int _rocksPrisonTime;
    /**
     * 拉斯塔巴德地監已使用時間(秒).
     */
    private int _lastabardTime;
    /**
     * 象牙塔已使用時間(秒).
     */
    private int _ivorytowerTime;
    /**
     * 龍之谷地監已使用時間(秒).
     */
    private int _dragonvalleyTime;
    /**
     * 噬魂塔副本已使用時間(秒).
     */
    private int _SoulTime;
    /**
     * 冰女副本已使用時間(秒).
     */
    private int _iceTime;
    /**
     * 媽祖已使用狀態.
     */
    private int _MazuTime;
    private int _clanMemberId; // 血盟成員Id
    private String _clanMemberNotes; // 血盟成員備註
    private int _stunlevel = 0; // 昏迷等級
    private int _attackError = 0; // 暫存Boss攻擊失敗次數
    private String _attackBossName = ""; // 暫存Boss攻擊name
    private int _giftIndex;
    private boolean _isWaitEnd;
    private int _range = 0;
    private int[] _gfxids;
    private int _times;
    private L1ItemInstance _weaponWarrior;
    private boolean _change_weapon = false;
    private int _giganticHp;
    // 7.6
    private int _attacktargetid;
    /**
     * 師徒系統
     **/
    private int _masterid = 0;
    private Timestamp _rejoinMasterTime;
    // --------------------------記時地圖-----------------
    private ConcurrentHashMap<Integer, Integer> mapTime;
    /**
     * 是否在記時地圖
     */
    private boolean isTimeMap;
    private int _MonsterLabTime;
    private int _WarzoneTime;
    private int _twotimes = 0;
    private String _vip_title = null;
    private boolean _checkSustainEffect = false;
    // 套裝特效
    private SustainEffect SustainEffect;
    private int _mcdmgModifierByArmor = 0; // 防具增加物理傷害
    private int _Weaponsprobability = 0; // 增加娃娃魔法武器發動機率
    private int _Weapondmg = 0; // 增加娃娃魔法武器傷害
    private int _Propertyprobability = 0; // 增加娃娃屬性卷機率
    private int _gfx = 0;
    private int _time = 0;
    // 城堡額外附加能力 by terry0412
    private ArrayList<Integer> _castleAbility;
    private long _shopAdenaRecord;
    /**
     * 經驗值增加
     */
    private double _expRateToPc = 0.0;
    private int _amount = 0;
    // 武器DIY特效
    private int _gfx1 = 0;
    private int _time1 = 0;
    // 特效編號 (每XX秒出現1次) by terry0412
    private int _effectId;
    private boolean _newTeleport;
    // 自動攻擊
    private L1Character _aiTarget = null;
    private boolean _firstAttack = false;
    private boolean _aiRunning = false; // PC AI時間軸 正在運行
    /**
     * 尋怪週期 by 聖子默默
     */
    private int _searchThink = 0;
    /**
     * 尋路週期 by 聖子默默
     */
    private int _autoMove = 0;
    /**
     * 無目標尋路週期
     */
    private int _moveDirectionError;
    private boolean _activated = false; // 掛機激活
    private boolean _Pathfinding = false; // 尋路中.. hjx1000
    // 隨機移動距離
    // private int _randomMoveDistance = 0;
    // 隨機移動方向
    private int _randomMoveDirection = 0;
    // 地圖時間記錄
    private Map<Integer, Integer> _mapsList;
    // 戒指欄位擴充紀錄 by terry0412 //src013
    private byte _ringsExpansion;
    private byte _earringsExpansion;
    // src1003
    private byte _equipmentindexamulet;
    private byte _redblueReward;
    // 占卜
    private int _probability;
    private boolean _bbdmg = false;
    // 占卜 沖暈
    private boolean _bbdmg1 = false;
    private boolean _bbdmg2 = false;
    private boolean _bbdmg3 = false;
    private int counterattack = 0;
    private int bowcounterattack = 0;
    private int _redbluejoin = 0;
    private int _redblueroom = 0;
    private int _redblueleader = 0;
    private int _redbluepoint = 0;
    // 王者加護
    private boolean _Pbavatar = false;
    private boolean _Pbavataron = false;
    // 王族新技能 恩典庇護
    private int graceLv = 0;
    // 幻術師新技能 衝突強化
    private int impactUp = 0;
    // 狂戰士新技能 泰坦狂暴
    private int risingUp = 0;
    /*
     * public void sendPackets(ServerBasePacket serverbasepacket, boolean clear) {
     * // 升級經驗獎勵狀態 try { if ((getMapId() == 2699 || getMapId() == 2100) &&
     * serverbasepacket.getType().equalsIgnoreCase("[S] S_OtherCharPacks")) { } else
     * sendPackets(serverbasepacket); if (clear) { serverbasepacket.clear();
     * serverbasepacket = null; } } catch (Exception e) { e.printStackTrace(); } }
     */
    // 狂戰士新技能 泰坦狂暴 end
    // 第2把武器(戰士使用)
    private L1ItemInstance _secondweapon = null;
    // 8.1連擊系統
    private int comboCount;
    // 安全區域右下顯示死亡懲罰狀態圖示
    private boolean isSafetyZone;
    private int SiegeTeam = -1;
    private UserMonsterBook _monsterBook;
    private UserWeekQuest _weekQuest;
    private int _turnLifeSkillCount;
    private int si = -1;
    // 增加PVP傷害
    private int _PvpDmg = 0;
    // 减免PVP傷害
    private int _PvpDmg_R = 0;
    // 阿頓星盤：隨機減傷（機率/數值）
    private int _attonProcChance = 0;
    private int _attonProcReduce = 0;
    // 阿頓星盤：攻擊吸血參數
    private int _leechChance = 0;
    private int _leechAmount = 0;
    private int _leechGfx1 = 0;
    private int _leechGfx2 = 0;
    // 正義滿角色名稱變黃
    private boolean _LawfulName = false;
    private L1ArmorKitPower _armorKitPower;
    private ArrayList<Integer> _armorKit;
    // 特效驗證系統
    private int _aistay = 0;
    private int[] _aixyz = null;
    private int _ai_timer = 0;
    private int _ai_error = 0;
    private Timestamp _tamTime;
    private int _markcount;
    private ScheduledFuture<?> _atkMonitorFuture; // l1j-tw連續攻擊
    private ScheduledFuture<?> _moveHandDarkness; // HAND_DARKNESS 黑暗之手
    // 自動鋪組
    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    private SkillSoundHp1 _SkillSoundAutoHp1;// 自動補血 -> 第一組
    private boolean _SkillSoundActiveAutoHp1;// 自動補血 -> 第一組
    private SkillSoundHp2 _SkillSoundAutoHp2;// 自動補血 -> 第二組
    private boolean _SkillSoundActiveAutoHp2;// 自動補血 -> 第二組
    private SkillSoundHp3 _SkillSoundAutoHp3;// 自動補血 -> 第三組
    private boolean _SkillSoundActiveAutoHp3;// 自動補血 -> 第三組
    private SkillSoundMp1 _SkillSoundAutoMp1;// 自動補魔 -> 第一組 道具
    private boolean _SkillSoundActiveAutoMp1;// 自動補魔 -> 第一組 道具
    private SkillSoundMp2 _SkillSoundAutoMp2;// 自動補魔 -> 第二組 魂體
    private boolean _SkillSoundActiveAutoMp2;// 自動補魔 -> 第二組 魂體
    private SkillSoundBackHome _SkillSoundAutoBackHome;// 自動回村
    private boolean _SkillSoundActiveAutoBackHome;// 自動回村
    private java.util.concurrent.ScheduledFuture<?> _autoHp1Future;
    private java.util.concurrent.ScheduledFuture<?> _autoHp2Future;
    private java.util.concurrent.ScheduledFuture<?> _autoHp3Future;
    private java.util.concurrent.ScheduledFuture<?> _autoMp1Future;
    private java.util.concurrent.ScheduledFuture<?> _autoMp2Future;
    private java.util.concurrent.ScheduledFuture<?> _autoBackHomeFuture;
    private java.util.concurrent.ScheduledFuture<?> SustainEffectFuture;
    private boolean _isAutoHpAll = false;// 自動喝藥總開關
    private boolean _isAutoHp1 = false;// 自動喝药第一组
    private boolean _isAutoHp2 = false;// 自動喝药第二组
    private boolean _isAutoHp3 = false;// 自動喝药第三组
    private boolean _isAutoBackHome = false;// 自動回村
    private boolean _isAutoMp1 = false;// 自動補魔 -> 第一組 道具
    private boolean _isAutoMp2 = false;// 自動補魔 -> 第二組 魂體
    // 衝裝贖回
    private int _backpage = 1;// 衝裝贖回
    private int _temporary = 0; // 自動喝水的輸入狀態
    private int _texthp1 = 95;// 第一組 自動喝水的%設定
    private int _texthp2 = 60;// 第二組 自動喝水的%設定
    private int _texthp3 = 80;// 第三組 自動喝水的%設定
    private int _textmp1 = 40;// 自動補魔%設定
    private int _textmp2 = 85;// 自動魂體%設定
    private int _textbh = 30;// 自動回村%設定
    private int _autoItemId1 = 40010; // 第一組 自動喝水的藥水編號
    private int _autoItemId2 = 40024; // 第二組 自動喝水的藥水編號
    private int _autoItemId3 = 700025; // 第三組 自動喝水的藥水編號
    private int _autoItemId4 = 40067; // 第一組 自動補魔的道具編號
    private SkillSoundItemAll _SkillSoundAutoItemAll;// 自動鋪助道具
    private boolean _SkillSoundActiveAutoItemAll;// 自動鋪助道具
    private java.util.concurrent.ScheduledFuture<?> _autoItemAllFuture;
    private boolean _isAutoItemAll = false;// 輔助狀態(道具)總開關
    private boolean _isAutoItem1 = false;// 解毒藥水
    private boolean _isAutoItem2 = false;// 藍色藥水
    private boolean _isAutoItem3 = false;// 慎重藥水
    private boolean _isAutoItem4 = false;// 綠色藥水
    private boolean _isAutoItem5 = false;// 名譽貨幣
    private boolean _isAutoItem6 = false;// 巧克力蛋糕
    private boolean _isAutoItem7 = false;// 自動變形卷軸
    private boolean _isAutoItem8 = false;// 自動聖結界卷軸
    private boolean _isAutoItem9 = false;// 自動魔法娃娃召喚
    private boolean _isAutoItem10 = false;// 自動一段經驗藥水
    private boolean _isAutoItem11 = false;// 自動二段經驗藥水
    private boolean _isAutoItem12 = false;// 飽食度順滿(5萬金幣)
    private boolean _isAutoItem13 = false;// 自動魔法卷軸(魔法屏障)
    private SkillSoundSkillAll _SkillSoundAutoSkillAll;// 輔助狀態(魔法) -> 全職
    private boolean _SkillSoundActiveAutoSkillAll;// 輔助狀態(魔法) -> 全職
    private java.util.concurrent.ScheduledFuture<?> _autoSkillAllFuture;
    private boolean _isAutoSkillAll = false;// 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_1 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_2 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_3 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_4 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_5 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_6 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_7 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_8 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_9 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_10 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_11 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_12 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_13 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_14 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_15 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_16 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_17 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_18 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_19 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_20 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_21 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_22 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_23 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_24 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_25 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_26 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_27 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_28 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_29 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_30 = false; // 輔助狀態(魔法) -> 全職通用
    private boolean _AutoSkill_31 = false; // 輔助狀態(魔法) -> 全職通用
    private SkillSoundRemoveItem _SkillSoundAutoRemoveItem;// 輔助(自動刪物)
    private boolean _SkillSoundActiveAutoRemoveItem;// 輔助(自動刪物)
    private java.util.concurrent.ScheduledFuture<?> _autoRemoveItemFuture;
    private boolean _AutoRemoveItem = false; // 輔助(自動刪物) -> 全職通用
    private boolean check_lv = false;
    // AI偵測
    private boolean ATK_ai = false;
    // terry770106 娃娃減傷 針對
    private int _dolldamageReductionByArmor = 0; // 防具增加傷害減免
    private int _fearlevel = 0;
    //TODO 掛機
    private boolean _autoattack = false;
    //------------------------------------------
    //跟隨系統
    private boolean _isfollowon = false;
    private int _cmd = 0;
    private int _followid = 0;
    private int _befollowid = 0;
    private int _tempfollowid = 0;
    private boolean _isfollow = false;
    private int _attackid = 0;
    private int _followmode = 0;
    private boolean _disconnect = false;
    // TODO 內掛
    private boolean _isauto = false;
    private int _autocount = 0;
    private int _autoattackcount = 0;
    private int _autoattacknpcid = 0;
    private int _autoBuyItem1 = 0;
    private int _autoBuyItemNum1 = 0;
    private int _autoBuyItemAdena1 = 0;
    private int _autoBuyItem2 = 0;
    private int _autoBuyItemNum2 = 0;
    private int _autoBuyItemAdena2 = 0;
    private int _resetautosec = 0;
    /**
     * 自動練功 定點功能
     */
    private int _lslocx = 0;
    private int _lslocy = 0;
    private boolean _lsOpen = false;
    private int _lsRange = 0;
    private int _resetautostartsec = 0;
    private int _drx = 0;
    private int _dry = 0;
    private int _drm = 0;
    private boolean _deathreturn = false;
    private boolean _buyarrow = false;
    private boolean _attackteleport = false;
    private boolean _attackteleporthp = false;
    private boolean _enemyteleport = true;
    private boolean _keyenemy = false;
    private boolean _outenemy = false;
    private int _na61 = 0;
    //血盟盟徽開關（預設開啟）
    private boolean _isClanGfx = true;
    //TODO 強化武器/防具加成系統
    private int _neran;
    private int _neextradmg;
    private int _nehit;
    private int _nemagicdmg;
    private double _critdmg;
    private int _critrate;
    private int _critgfx;
    private int _neexpup;
    private int _neskillid;
    private int _negfx;
    private SkillSound _SkillSound;
    private boolean _SkillSoundActive;
    private java.util.concurrent.ScheduledFuture<?> _skillSoundFuture;
    /////////////////////////////////////娃娃合成///////////////////////////////////////
    private int _dollcount = 0;//增加減少娃娃數量2
    private int _dollcount2 = 0;//增加減少娃娃數量3
    private int _dollcount3 = 0;//增加減少娃娃數量4
    private int _dollcount4 = 0;//增加減少娃娃數量5
    private int _dollrun2 = 0;//娃娃合成幾率2階段
    private int _dollrun3 = 0;//娃娃合成幾率3階段
    private int _dollrun4 = 0;//娃娃合成幾率4階段
    private int _dollrun5 = 0;//娃娃合成幾率5階段
    /////////////////////////////////////魔法合成///////////////////////////////////////
    private int _Magiccount = 0;//增加減少娃娃數量2
    private int _Magiccount2 = 0;//增加減少娃娃數量3
    private int _Magiccount3 = 0;//增加減少娃娃數量4
    private int _Magiccount4 = 0;//增加減少娃娃數量5
    private int _Magicrun2 = 0;//娃娃合成幾率2階段
    private int _Magicrun3 = 0;//娃娃合成幾率3階段
    private int _Magicrun4 = 0;//娃娃合成幾率4階段
    private int _Magicrun5 = 0;//娃娃合成幾率5階段
    private int _netimegfx;
    private int _neskillid2;
    private int _negfx2;
    private int _netimegfx2;
    private boolean _zudui = false;
    private int _Armmoeset;
    private int _魔法格檔 = 0;
    private int _blockWeapon = 0;
    private int _loginpoly = 1;
    /**
     * 血盟等級傷害減免
     */
    private int _Clan_ReductionDmg = 0;
    private int _Clanmagic_reduction_dmg = 0;// 套裝減免魔法傷害
    private double _addExpByArmor = 0; // 裝備增加經驗加倍效果
    private int _dragonexp = 0;
    private int _listpage = 0;
    private long _adenaTradeCount = 0;
    // 金幣交易 記錄輸入的元寶數量
    private long _adenaTradeAmount = 0;
    private int _adenaTradeId = 0;
    /**
     * 祝福道具物理傷害減免
     */
    private int _zhufupvp;
    /**
     * 祝福道具物理傷害減免百分比
     */
    private int _zhufupvpbai;
    /**
     * 祝福化魔法傷害減免
     */
    private int _zhufumopvp;
    /**
     * 祝福化魔法傷害減免百分比
     */
    private int _zhufumopvpbai;
    private int _paceacha;
    private int _paceacon;
    private int _paceadex;
    private int _paceaint;
    private int _paceastr;
    private int _paceawis;
    // VIP能力
    private int _vipLevel;
    private Timestamp _startTime;
    private Timestamp _endTime;
    /**
     * 聲望等級
     **/
    private int _Honor = 0;
    private int _HonorLevel = 0;
    /**
     * 內掛自動召喚
     */
    private String _zhaohuan;
    /**
     * 內掛召喚是否啟動
     */
    private boolean _summid = false;
    /**
     * 內掛召喚補血ID
     */
    private int _summon_skillid;
    /**
     * 內掛召喚補血PC魔量判定
     *
     */
    private int _summon_skillidmp;
    /**
     * 是否開啟寵物補血內掛
     *
     */
    private boolean _checksummidhp = false;
    private boolean _bossfei = true;//內掛遇見BOSS是否飛
    private boolean _heping = true;//內掛是否開啟和平掛機 不搶怪
    private int _CardId = 0;//卡冊編號
    private int _DollId = 0;//娃娃編號
    private int _HolyId = 0;//聖物編號
    private int _FireDisId = 0;//火神編號
    // 魔法傷害增加+%
    private int _magicDmgUp;
    ///////////////////////////////紋樣系統開始///////////////////////////////////////
    private int _wenyangjifen;//紋樣積分
    // 紋樣機率1
    private int _wenyangRate1;
    // 紋樣機率2
    private int _wenyangRate2;
    // 紋樣機率3
    private int _wenyangRate3;
    // 紋樣機率4
    private int _wenyangRate4;
    // 紋樣機率1
    private int _wenyangRate5;
    // 紋樣機率1
    private int _wenyangRate6;
    private int _wytype1;//紋樣類型1
    private int _wytype2;//紋樣類型2
    private int _wytype3;//紋樣類型3
    private int _wytype4;//紋樣類型4
    private int _wytype5;//紋樣類型5
    private int _wytype6;//紋樣類型6
    private int _wylevel1;//紋樣等級1匹配類型1
    private int _wylevel2;//紋樣等級2匹配類型2
    private int _wylevel3;//紋樣等級3匹配類型3
    // l1j-jp釣魚 end
    private int _wylevel4;//紋樣等級4匹配類型4
    private int _wylevel5;//紋樣等級5匹配類型5
    private int _wylevel6;//紋樣等級5匹配類型6
    private int _wyjiajilv;//紋樣升級機率+
    private int _wyjianjilv;//紋樣升級機率-
    /////////////////////////////////////娃娃招喚紀錄//////////////////////////////////////
    private int _lastDollId = 0;
    private int _lastHolyId2 = 0;
    private int _lastPolyCardId = 0;
    /////////////////////////////////////簽到紀錄//////////////////////////////////////
    private int _day_signature = 0;
    private Timestamp _day_signature_time;
    /////////////////////////////////////變身卡合成///////////////////////////////////////
    private int _polycount = 0;//增加減少變身卡數量2
    private int _polycount2 = 0;//增加減少變身卡數量3
    private int _polycount3 = 0;//增加減少變身卡數量4
    private int _polycount4 = 0;//增加減少變身卡數量5
    private int _polyrun2 = 0;//變身卡合成幾率2階段
    private int _polyrun3 = 0;//變身卡合成幾率3階段
    private int _polyrun4 = 0;//變身卡合成幾率4階段
    private int _polyrun5 = 0;//變身卡合成幾率5階段
    /////////////////////////////////////聖物卡合成///////////////////////////////////////
    // 聖物合成進度屬性
    private int _holyCount = 0;    // 二階合成次數
    private int _holyCount2 = 0;   // 三階合成次數
    private int _holyCount3 = 0;   // 四階合成次數
    private int _holyCount4 = 0;   // 五階合成次數

    private int _holyRun2 = 0;     // 二階合成累積機率
    private int _holyRun3 = 0;     // 三階合成累積機率
    private int _holyRun4 = 0;     // 四階合成累積機率
    private int _holyRun5 = 0;     // 五階合成累積機率


    private long _autoSkillDelay = 0;
    /**
     * 星盤系統
     */
    private int _astrologyType = 0;
    /**
     * 星盤系統
     */
    private int _astrologyId = 0;
    // 在 L1PcInstance 類中新增以下屬性和方法
    private int _addhunmi; // 用於儲存昏迷抗性的屬性
    private int _closeCritical = 0; // 近距離爆擊率
    private int _bowCritical = 0; // 遠距離爆擊率
    private int _er;
    private int _sleepTimeAI;
    //排行榜
    private String _viewName = null;
    private L1NpcInstance _lastTalkedNpc;

    

    public L1PcInstance() {
        _accessLevel = 0;
        _currentWeapon = 0;
        _polyarrow = 66;
        _inventory = new L1PcInventory(this);
        _dwarf = new L1DwarfInventory(this);
        _dwarfForCha = new L1DwarfForChaInventory(this);
        _dwarfForElf = new L1DwarfForElfInventory(this);
        _dwarfForMALL = new L1DwarfForGameMallInventry(this);
        _dwarfForRemoveItem = new L1RemoveItemInventory(this);// 輔助(自動刪物)
        _tradewindow = new L1Inventory(); // 交易視窗
        _quest = new L1PcQuest(this);
        _action = new L1ActionPc(this);
        _actionPet = new L1ActionPet(this);
        _actionSummon = new L1ActionSummon(this);
        _equipSlot = new L1EquipmentSlot(this);
        _weaponProficiency = new L1WeaponProficiency(this);
        /** [原碼] 怪物對戰系統 */
        _MobSpList = new L1MobSpList(this);
        /** [原碼] 大樂透系統 */
        _BigHotSpList = new L1BigHotSpList(this);
        _speed = new AcceleratorChecker(this);
        _bookmarks = new ArrayList<>(); // 日版記憶座標
        _speedbookmarks = new ArrayList<>(); // 日版記憶座標

    }

    /**
     * 連續魔法傷害遞減
     */
    public static void load() {
        double newdmg = 100.0D;
        for (long i = 2000L; i > 0L; i -= 1L) {
            if (i % 100L == 0L) {
                newdmg -= 3.33D;
            }
            _magicDamagerList.put(i, newdmg);
        }
    }

    public static L1PcInstance load(String charName) {
        L1PcInstance result = null;
        try {
            result = CharacterTable.get().loadCharacter(charName);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return result;
    }

    public static void writeDeathlog(L1PcInstance player1, L1PcInstance player2, String note) {
        final String logDirPath = "./物品操作記錄/log";
        final String logFileName = "※死亡紀錄※.txt";
        final File logDir = new File(logDirPath);
        final File logFile = new File(logDir, logFileName);

        try {
            // 確保資料夾存在
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // 如果是第一次建立檔案，寫入標題
            if (!logFile.exists()) {
                try (BufferedWriter out = new BufferedWriter(new FileWriter(logFile, false))) {
                    out.write("※死亡紀錄※\r\n");
                }
            }

            // 取得角色資訊
            String deadName = (player1 != null) ? player1.getName() : "未知";
            short mapId = (player1 != null) ? player1.getMapId() : 0;
            String killerName = (player2 != null) ? player2.getName() : "未知";

            // 寫入紀錄
            try (BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true))) {
                out.write("\r\n");
                out.write("死者: " + deadName + " 殺人者: " + killerName +
                        " 地圖: " + mapId +
                        " 時間: " + new Timestamp(System.currentTimeMillis()) +
                        " (" + note + ")\r\n");
            }

        } catch (IOException e) {
            System.err.println("[writeDeathlog] 發生錯誤：");
            e.printStackTrace();
        }
    }

    public static void writeReceivelog(L1Character player1, L1Character player2, String s, double dmg, int pcHp) {
        try {
            File DeleteLog = new File("./物品操作記錄/log/※受攻擊紀錄※.txt");
            if (DeleteLog.createNewFile()) {
                out = new BufferedWriter(new FileWriter("./物品操作記錄/log/※受攻擊紀錄※.txt", false));
                out.write("※受攻擊紀錄※" + "\r\n");
                out.close();
            }
            String p1name = "";
            Short p1map = 0;
            String p2name = "";
            if (player1 != null) {
                p1name = player1.getName();
                p1map = player1.getMapId();
            }
            if (player2 != null) {
                p2name = player2.getName();
            }
            out = new BufferedWriter(new FileWriter("./物品操作記錄/log/※受攻擊紀錄※.txt", true));
            out.write("\r\n");// 每次填寫資料都控一行
            out.write("攻擊者: " + p1name + " 被攻擊者: " + p2name + " 血量: " + pcHp + " 傷害值: " + dmg + " 地圖: " + p1map + " 時間: " + new Timestamp(System.currentTimeMillis()) + "(" + s + ")" + "\r\n");
            out.close();
        } catch (IOException e) {
            System.out.println("以下是錯誤訊息: " + e.getMessage());
        }
    }

    public static void writeDeathlog2(L1PcInstance player1, L1MonsterInstance player2, String note) {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        final String logDirPath = "./物品操作記錄/Log " + today;
        final String logFileName = "死亡紀錄.txt";
        final File logDir = new File(logDirPath);
        final File logFile = new File(logDir, logFileName);

        try {
            if (!logDir.exists()) {
                logDir.mkdirs(); // 自動建立今天的資料夾
            }

            if (!logFile.exists()) {
                try (BufferedWriter out = new BufferedWriter(new FileWriter(logFile, false))) {
                    out.write("※死亡紀錄※\r\n");
                }
            }

            String deadName = (player1 != null) ? player1.getName() : "未知";
            short mapId = (player1 != null) ? player1.getMapId() : 0;
            String mobName = (player2 != null) ? player2.getName() : "未知怪物";

            try (BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true))) {
                out.write("\r\n");
                out.write("死者: " + deadName + " 殺人者: " + mobName +
                        " 地圖: " + mapId +
                        " 時間: " + new Timestamp(System.currentTimeMillis()) +
                        " (" + note + ")\r\n");
            }

        } catch (IOException e) {
            System.err.println("[writeDeathlog2] 發生錯誤：");
            e.printStackTrace();
        }
    }

    public L1PcOther1 get_other1() {
        return this._other1;
    }

    public void set_other1(L1PcOther1 other1) {
        this._other1 = other1;
    }

    // 取得排行榜組別
    public int getGroupId() {
        return _groupId;
    }

    // 設定排行榜組別
    public void setGroupId(int groupId) {
        _groupId = groupId;
    }

    // 取得排行榜名次
    public int getRanking() {
        return _ranking;
    }

    // 設定排行榜名次
    public void setRanking(int ranking) {
        _ranking = ranking;
    }

    public int getAstrologyType() {
        return _astrologyType;
    }

    public void setAstrologyType(int i) {
        _astrologyType = i;
    }

    public int getAstrologyId() {
        return _astrologyId;
    }

    public void setAstrologyId(int i) {
        _astrologyId = i;
    }

    public int getBowHit() {
        return _bowHit;
    }

    public void addBowHit(int i) {
        _trueBowHit += i;
        if (_trueBowHit >= 127) {
            _bowHit = 127;
        } else _bowHit = Math.max(_trueBowHit, -128);
    }

    public void load_src() {
        _old_exp = getExp();
        _old_lawful = getLawful();
        _old_karma = getKarma();
    }

    public boolean is_isKill() {
        return _isKill;
    }

    public void set_isKill(boolean _isKill) {
        this._isKill = _isKill;
    }

    public short getHpr() {
        return _hpr;
    }

    /**
     * 人物訊息拒絕清單
     *
     */
    public void addHpr(int i) {
        _trueHpr = (short) (_trueHpr + i);
        _hpr = (short) Math.max(0, _trueHpr);
    }

    public short getMpr() {
        return _mpr;
    }

    public void addMpr(int i) {
        _trueMpr = (short) (_trueMpr + i);
        _mpr = (short) Math.max(0, _trueMpr);
    }

    public short getOriginalHpr() {
        return _originalHpr;
    }

    public short getOriginalMpr() {
        return _originalMpr;
    }

    public int getHpRegenState() {
        return _hpRegenState;
    }

    public void set_hpRegenType(int hpRegenType) {
        _hpRegenType = hpRegenType;
    }

    public int hpRegenType() {
        return _hpRegenType;
    }


    private int _expComboCount = 0;
    public int getExpComboCount() { return _expComboCount; }
    public void setExpComboCount(int count) { _expComboCount = count; }

    private int regenMax() {
        int[] lvlTable = {30, 25, 20, 16, 14, 12, 11, 10, 9, 3, 2};
        int regenLvl = Math.min(10, getLevel());
        if (30 <= getLevel() && isKnight()) {
            regenLvl = 11;
        }
        return lvlTable[regenLvl - 1] << 2;
    }

    public boolean isRegenHp() {
        if (_temp != 0) {
            _accessLevel = _temp;
        }
        if (!_hpRegenActive) {
            return false;
        }
        if (hasSkillEffect(EXOTIC_VITALIZE) || hasSkillEffect(ADDITIONAL_FIRE) || isMASS_TELEPORT()) {
            return _hpRegenType >= regenMax();
        }
        if (120 <= _inventory.getWeight240()) {
            return false;
        }
        if (_food < 3) {
            return false;
        }
        return _hpRegenType >= regenMax();
    }

    /**
     * 反擊屏障:精通
     **/
    public boolean isCOUNTER_BARRIER_VETERAN() {
        return CharSkillReading.get().spellCheck(this.getId(), COUNTER_BARRIER_VETERAN);
    }

    /**
     * 法師 意志專注 負重可以繼續回魔力
     */
    public boolean isMASS_TELEPORT() {
        return CharSkillReading.get().spellCheck(this.getId(), MASS_TELEPORT);
    }

    /**
     * 治愈能量風暴 改 破壞之鏡 被動型 鏡反射 設定僅對PVP有效
     */
    public boolean isLIFE_STREAM() {
        return CharSkillReading.get().spellCheck(this.getId(), LIFE_STREAM);
    }

    /**
     * 法師 究極光裂術(古代)
     */
    public boolean isHOLY_WALK2() {
        return CharSkillReading.get().spellCheck(this.getId(), HOLY_WALK2);
    }

    /**
     * 法師 神聖迅猛
     */
    public boolean isDISINTEGRATE_2() {
        return CharSkillReading.get().spellCheck(this.getId(), DISINTEGRATE_2);
    }

    public int getMpRegenState() {
        return _mpRegenState;
    }

    public void set_mpRegenType(int hpmpRegenType) {
        _mpRegenType = hpmpRegenType;
    }

    public int mpRegenType() {
        return _mpRegenType;
    }

    public boolean isRegenMp() {
        if (!_mpRegenActive) {
            return false;
        }
        if (hasSkillEffect(EXOTIC_VITALIZE) || hasSkillEffect(ADDITIONAL_FIRE) || isMASS_TELEPORT()) {
            return _mpRegenType >= 64;
        }
        if (120 <= _inventory.getWeight240()) {
            return false;
        }
        if (_food < 3) {
            return false;
        }
        return _mpRegenType >= 64;
    }

    public void setRegenState(int state) {
        _mpRegenState = state;
        _hpRegenState = state;
    }

    public void startHpRegeneration() {
        if (!_hpRegenActive) {
            _hpRegenActive = true;
        }
    }

    public void stopHpRegeneration() {
        if (_hpRegenActive) {
            _hpRegenActive = false;
        }
    }

    public boolean getHpRegeneration() {
        return _hpRegenActive;
    }

    public void startMpRegeneration() {
        if (!_mpRegenActive) {
            _mpRegenActive = true;
        }
    }

    public void stopMpRegeneration() {
        if (_mpRegenActive) {
            _mpRegenActive = false;
        }
    }

    public boolean getMpRegeneration() {
        return _mpRegenActive;
    }

    public int get_awakeMprTime() {
        return _awakeMprTime;
    }

    public void set_awakeMprTime(int awakeMprTime) {
        _awakeMprTime = awakeMprTime;
    }

    public void startMpReductionByAwake() {
        if (!_mpReductionActiveByAwake) {
            set_awakeMprTime(4);
            _mpReductionActiveByAwake = true;
        }
    }

    public void stopMpReductionByAwake() {
        if (_mpReductionActiveByAwake) {
            set_awakeMprTime(0);
            _mpReductionActiveByAwake = false;
        }
    }

    public boolean isMpReductionActiveByAwake() {
        return _mpReductionActiveByAwake;
    }

    public int getAwakeSkillId() {
        return _awakeSkillId;
    }
    public void setAwakeSkillId(int i) {
        _awakeSkillId = i;
    }

    public collect get_collectTemp() {
        return _collectTemp;
    }

    public void set_collectTemp(collect collectTemp) {
        _collectTemp = collectTemp;
    }

    public void startObjectAutoUpdate() {
        removeAllKnownObjects();
    }

    /**
     * 移出各種處理清單
     */
    public void stopEtcMonitor() {
        if (getFollowEffect() != null) { // R版Top10暴擊特效處理 by 聖子默默
            getFollowEffect().deleteMe();
        }
        set_ghostTime(-1);
        setGhost(false);
        setGhostCanTalk(true);
        setReserveGhost(false);
        set_mazu_time(0);
        set_mazu(false);
        stopMpReductionByAwake();
        // if (ConfigAutoAll.Auto_All) {
        stopSkillSound_autoHP1();// 停止自動喝水 -> 第一組
        stopSkillSound_autoHP2();// 停止自動喝水 -> 第二組
        stopSkillSound_autoHP3();// 停止自動喝水 -> 第三組
        stopSkillSound_autoMP1();// 停止自動補魔 -> 第一組 道具
        stopSkillSound_autoMP2();// 停止自動補魔 -> 第二組 魂體
        stopSkillSound_autoBackHome();// 停止自動回村
        stopSkillSound_autoItemAll();// 停止鋪助道具狀態
        stopSkillSound_autoSkillAll();// 停止自動輔助狀態(魔法) -> 全職業
        stopSkillSound_autoRemoveItem();// 停止輔助(自動刪物)
        // }
        if (_atkMonitorFuture != null) { // l1j-tw連續攻擊
            _atkMonitorFuture.cancel(true);
            _atkMonitorFuture = null;
        }
        stopMoveHandDarkness();
        // 移出短時間計時地圖時間軸
        if (ServerUseMapTimer.MAP.get(this) != null) {
            ServerUseMapTimer.MAP.remove(this);
        }
        // 移出計時地圖時間軸
        if (MapTimerThread.TIMINGMAP.get(this) != null) {
            MapTimerThread.TIMINGMAP.remove(this);
        }
        OnlineGiftSet.remove(this);
        ListMapUtil.clear(_skillList);
        ListMapUtil.clear(_sellList);
        ListMapUtil.clear(_buyList);
        // ListMapUtil.clear(_trade_items);
        ListMapUtil.clear(_allpowers);
        ListMapUtil.clear(ASTROLOGY_DATA_MAP);
    }

    public void onChangeLawful() {
        if (_old_lawful != getLawful()) {
            _old_lawful = getLawful();
            sendPacketsAll(new S_Lawful(this));
            lawfulUpdate();
        }
    }

    public int getKarmalo() {
        return _old_karma;
    }

    public void onChangeKarma() {
        if (_old_karma != getKarma()) {
            _old_karma = getKarma();
            sendPackets(new S_Karma(this));
        }
    }

    /**
     * 根據角色的正義值(Lawful)等級，決定各種保護狀態與資料更新
     */
    public void lawfulUpdate() {
        int l = getLawful(); // 取得目前的正義值 Lawful

        // 正義值介於 10000~19999，第一階段光明騎士
        if (l >= 10000 && l <= 19999) {
            if (!_jl1) { // 只會進來一次，避免重複
                overUpdate(); // 進行通用的更新處理
                _jl1 = true;  // 標記這個區間已經處理過
                sendPackets(new S_PacketBoxProtection(0, 1)); // 發送保護狀態封包
                sendPackets(new S_OwnCharAttrDef(this));      // 更新角色屬性防禦
                sendPackets(new S_SPMR(this));                // 更新SP/MR資訊
            }
            // 正義值介於 20000~29999，第二階段光明騎士
        } else if (l >= 20000 && l <= 29999) {
            if (!_jl2) {
                overUpdate();
                _jl2 = true;
                sendPackets(new S_PacketBoxProtection(1, 1));
                sendPackets(new S_OwnCharAttrDef(this));
                sendPackets(new S_SPMR(this));
            }
            // 正義值介於 30000~39999，第三階段光明騎士
        } else if (l >= 30000 && l <= 39999) {
            if (!_jl3) {
                overUpdate();
                _jl3 = true;
                sendPackets(new S_PacketBoxProtection(2, 1));
                sendPackets(new S_OwnCharAttrDef(this));
                sendPackets(new S_SPMR(this));
            }
            // 正義值介於 -10000~-19999，第一階段邪惡騎士
        } else if (l >= -19999 && l <= -10000) {
            if (!_el1) {
                overUpdate();
                _el1 = true;
                sendPackets(new S_PacketBoxProtection(3, 1));
                sendPackets(new S_SPMR(this));
            }
            // 正義值介於 -20000~-29999，第二階段邪惡騎士
        } else if (l >= -29999 && l <= -20000) {
            if (!_el2) {
                overUpdate();
                _el2 = true;
                sendPackets(new S_PacketBoxProtection(4, 1));
                sendPackets(new S_SPMR(this));
            }
            // 正義值介於 -30000~-39999，第三階段邪惡騎士
        } else if (l >= -39999 && l <= -30000) {
            if (!_el3) {
                overUpdate();
                _el3 = true;
                sendPackets(new S_PacketBoxProtection(5, 1));
                sendPackets(new S_SPMR(this));
            }
            // 其他情況，只要有overUpdate就發屬性更新
        } else if (overUpdate()) {
            sendPackets(new S_OwnCharAttrDef(this));
            sendPackets(new S_SPMR(this));
        }
    }
    /**
     * 更新保護狀態
     * 0: 光明騎士第一階段
     * 1: 光明騎士第二階段
     * 2: 光明騎士第三階段
     * 3: 邪惡騎士第一階段
     * 4: 邪惡騎士第二階段
     * 5: 邪惡騎士第三階段
     * 6: 遇敵狀態
     */

    private boolean overUpdate() {
        if (_jl1) {
            _jl1 = false;
            sendPackets(new S_PacketBoxProtection(0, 0));
            return true;
        }
        if (_jl2) {
            _jl2 = false;
            sendPackets(new S_PacketBoxProtection(1, 0));
            return true;
        }
        if (_jl3) {
            _jl3 = false;
            sendPackets(new S_PacketBoxProtection(2, 0));
            return true;
        }
        if (_el1) {
            _el1 = false;
            sendPackets(new S_PacketBoxProtection(3, 0));
            return true;
        }
        if (_el2) {
            _el2 = false;
            sendPackets(new S_PacketBoxProtection(4, 0));
            return true;
        }
        if (_el3) {
            _el3 = false;
            sendPackets(new S_PacketBoxProtection(5, 0));
            return true;
        }
        return false;
    }

    private boolean isEncounter() {
        return getLevel() <= ConfigOther.ENCOUNTER_LV;
    }

    public int guardianEncounter() {
        if (_jl1) {
            return 0;
        }
        if (_jl2) {
            return 1;
        }
        if (_jl3) {
            return 2;
        }
        if (_el1) {
            return 3;
        }
        if (_el2) {
            return 4;
        }
        if (_el3) {
            return 5;
        }
        return -1;
    }

    public long getExpo() {
        return _old_exp;
    }

    /**
     * 獲得經驗值的處理
     */
    public void onChangeExp() {
        if (_old_exp != getExp()) {
            _old_exp = getExp();
            int level = ExpTable.getLevelByExp(getExp());
            int char_level = getLevel();
            int gap = level - char_level;
            if (gap == 0) {
                // if (level <= 127) {
                // sendPackets(new S_Exp(this));
                // } else {
                sendPackets(new S_OwnCharStatus(this));
                // }
                return;
            }
            if (gap > 0) {
                levelUp(gap);
                if (ConfigOtherSet2.LEVEL_UP && getLevel() >= ConfigOtherSet2.LEVEL_UP_LV) { // 升級經驗獎勵狀態
                    setSkillEffect(L1SkillId.LEVEL_UP_BONUS, 10800000);
                    // sendPackets(new S_PacketBox(10800, true, true), true);
                    sendPackets(new S_PacketBox(10800, true, true));
                }
            } else if (gap < 0) {
                levelDown(gap);
                if (ConfigOtherSet2.LEVEL_UP) { // 升級經驗獎勵狀態
                    removeSkillEffect(L1SkillId.LEVEL_UP_BONUS);
                }
            }
            if (getLevel() > ConfigOther.ENCOUNTER_LV) {
                sendPackets(new S_PacketBoxProtection(6, 0));
            } else {
                sendPackets(new S_PacketBoxProtection(6, 1));
            }
        }
    }

    /**
     * TODO 接觸資訊
     */
    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (isGmInvis() || isGhost() || isInvisble()) {
                return;
            }
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_OtherCharPacks(this));
            
            // 如果對方開啟了 ClanGfx 或在攻城戰區域內，且本角色有盟徽，主動發送盟徽資料
            boolean shouldShowEmblem = perceivedFrom.isClanGfx() || 
                                       L1CastleLocation.checkInAllWarArea(perceivedFrom.getLocation());
            
            if (shouldShowEmblem && getClanid() > 0) {
                L1Clan clan = getClan();
                if (clan != null) {
                    // 檢查是否有盟徽資料
                    L1EmblemIcon emblemIcon = ClanEmblemReading.get().get(clan.getClanId());
                    if (emblemIcon != null) {
                        // 使用 clanId 建構 S_Emblem（第一個建構子）
                        perceivedFrom.sendPackets(new S_Emblem(clan.getClanId()));
                    }
                }
            }
            if (ConfigFreeKill.FREE_FIGHT_SWITCH) {
                if (CheckFightTimeController.getInstance().isFightMap(getMapId())) {
                    perceivedFrom.sendPackets(new S_PinkName(getId(), -1));
                }
            }
            // 隊伍成員HP狀態發送
            if (isInParty()) {
                if (getParty().isMember(perceivedFrom)) {// 對象是隊員
                    perceivedFrom.sendPackets(new S_HPMeter(this));
                }
            }
            // if (_isFishing) {
            // perceivedFrom.sendPackets(new S_Fishing(getId(), 71, get_fishX(),
            // get_fishY()));
            // }
            if (isFishing()) {
                if (fishX != 0 && fishY != 0) {
                    perceivedFrom.sendPackets(new S_Fishing(getId(), ActionCodes.ACTION_Fishing, fishX, fishY));
                } else {
                    perceivedFrom.sendPackets(new S_Fishing(getId(), ActionCodes.ACTION_Fishing, getX(), getY()));
                }
            }
            if (isPrivateShop()) { // src022
                int mapId = getMapId();
                if (mapId != 340 && mapId != 350 && mapId != 360 && mapId != 370 && mapId != 800) {
                    // if (mapId != 800) {
                    getSellList().clear();
                    getBuyList().clear();
                    setPrivateShop(false);
                    sendPacketsAll(new S_DoActionGFX(getId(), 3));
                } else {
                    perceivedFrom.sendPackets(new S_DoActionShop(getId(), getShopChat()));
                }
            }
            if (get_vipLevel() > 0) {
                final S_VipShow vipShow = new S_VipShow(getId(), get_vipLevel());
                sendPacketsAll(vipShow);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void removeOutOfRangeObjects() {
        for (L1Object known : getKnownObjects()) {
            if (known != null) {
                if (Config.PC_RECOGNIZE_RANGE == -1) {
                    if (!getLocation().isInScreen(known.getLocation())) {
                        removeKnownObject(known);
                        sendPackets(new S_RemoveObject(known));
                    }
                } else if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
                    removeKnownObject(known);
                    sendPackets(new S_RemoveObject(known));
                }
            }
        }
    }

    /**
     * 可見物更新處理
     */
    public void updateObject() {
        if (isOutGame()) {
            return;
        }
        removeOutOfRangeObjects();
        // 指定可視範圍資料更新
        for (final L1Object visible : World.get().getVisibleObjects(this, Config.PC_RECOGNIZE_RANGE)) {
            if (visible instanceof L1MerchantInstance) {// 對話NPC
                if (!knownsObject(visible)) {
                    final L1MerchantInstance npc = (L1MerchantInstance) visible;
                    // 未認知物件 執行物件封包發送
                    npc.onPerceive(this);
                }
                continue;
            }
            if (visible instanceof L1DwarfInstance) {// 倉庫NPC
                if (!knownsObject(visible)) {
                    final L1DwarfInstance npc = (L1DwarfInstance) visible;
                    // 未認知物件 執行物件封包發送
                    npc.onPerceive(this);
                }
                continue;
            }
            if (visible instanceof L1FieldObjectInstance) {// 景觀
                if (!knownsObject(visible)) {
                    final L1FieldObjectInstance npc = (L1FieldObjectInstance) visible;
                    // 未認知物件 執行物件封包發送
                    npc.onPerceive(this);
                }
                continue;
            }
            // 副本ID不相等 不相護顯示
            if (visible.get_showId() != get_showId()) {
                continue;
            }
            if (!knownsObject(visible)) {
                // 未認知物件 執行物件封包發送
                visible.onPerceive(this);
            } else {
                if (visible instanceof L1NpcInstance) {
                    final L1NpcInstance npc = (L1NpcInstance) visible;
                    if (getLocation().isInScreen(npc.getLocation()) && npc.getHiddenStatus() != 0) {
                        npc.approachPlayer(this);
                    }
                }
            }
            // 一般人物 HP可見設置
            if (isHpBarTarget(visible)) {
                final L1Character cha = (L1Character) visible;
                cha.broadcastPacketHP(this);
            }
            // GM HP 查看設置
            if (hasSkillEffect(GMSTATUS_HPBAR)) {
                if (isGmHpBarTarget(visible)) {
                    final L1Character cha = (L1Character) visible;
                    cha.broadcastPacketHP(this);
                }
            }
            // GM HP 查看設置(只看PC角色)
            if (hasSkillEffect(GMSTATUS_HPBAR_PC)) {
                if (GmHpBarForPc(visible)) {
                    final L1Character cha = (L1Character) visible;
                    cha.broadcastPacketHP(this);
                }
            }
        }
        // 特效驗證系統
        if (EffectAISet.START && this.hasSkillEffect(L1SkillId.AIFOREND)) {
            this.sendPackets(new S_EffectLocation(this.get_aixyz()[0], this.get_aixyz()[1], ServerAIEffectTable.getEffectId()));
            try {
                if (this.getX() != this.get_aixyz()[0] || this.getY() != this.get_aixyz()[1]) {
                    if (this.get_aistay() > 0) {
                        this.set_aistay(0);
                        String msg = "請 勿 在 驗 證 完 畢 前 移 動。";
                        // this.sendPackets(new S_BlueMessage(166, "\\f3" + msg));
                        // this.sendPackets(new S_ServerMessage(msg));
                        this.sendPackets(new S_AllChannelsChat(msg, 3));
                    }
                }
                switch (this.get_aistay()) {
                    case 0:
                        if (this.getX() == this.get_aixyz()[0] && this.getY() == this.get_aixyz()[1]) {
                            this.set_aistay(1);
                            String msg = "正在進行中  驗證倒數...3";
                            // this.sendPackets(new S_BlueMessage(166, "\\f3" +
                            // msg));
                            this.sendPackets(new S_ServerMessage("\\fUAI" + msg));
                        }
                        break;
                    case 1:
                        if (this.getX() == this.get_aixyz()[0] && this.getY() == this.get_aixyz()[1]) {
                            this.set_aistay(2);
                            String msg = "正在進行中  驗證倒數...2";
                            // this.sendPackets(new S_BlueMessage(166, "\\f3" +
                            // msg));
                            this.sendPackets(new S_ServerMessage("\\fUAI" + msg));
                        }
                        break;
                    case 2:
                        if (this.getX() == this.get_aixyz()[0] && this.getY() == this.get_aixyz()[1]) {
                            this.set_aistay(3);
                            String msg = "正在進行中  驗證倒數...1";
                            // this.sendPackets(new S_BlueMessage(166, "\\f3" +
                            // msg));
                            this.sendPackets(new S_ServerMessage("\\fUAI" + msg));
                        }
                        break;
                    case 3:
                        if (this.getX() == this.get_aixyz()[0] && this.getY() == this.get_aixyz()[1]) {
                            this.set_aixyz(null);
                            this.set_aistay(0);
                            this.sendPackets(new S_ServerMessage("\\fUAI驗證完畢，您可以自由活動了！"));
                            this.killSkillEffectTimer(L1SkillId.AIFOREND);
                        }
                        break;
                }
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (final Exception e) {
            }
        }
    }

    /**
     * 可以觀看HP的對象(特別定義)
     *
     */
    public boolean isHpBarTarget(final L1Object obj) {
        if (obj instanceof L1PcInstance) {// 加入陣營戰活動同隊血條顯示
            final L1PcInstance tgpc = (L1PcInstance) obj;
            if (this.get_redbluejoin() != 0) {
                if (this.get_redbluejoin() == tgpc.get_redbluejoin()) {
                    return true;
                }
            }
        }
        // 所在地圖位置
        switch (this.getMapId()) {
            case 400:// 大洞穴/大洞穴抵抗軍/隱遁者地區
                if (obj instanceof L1FollowerInstance) {
                    final L1FollowerInstance follower = (L1FollowerInstance) obj;
                    if (follower.getMaster().equals(this)) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    /**
     * GM HPBAR 可以觀看HP的對象
     *
     */
    public boolean isGmHpBarTarget(L1Object obj) {
        if (obj instanceof L1PetInstance) {
            return true;
        }
        if (obj instanceof L1MonsterInstance) {
            return true;
        }
        if (obj instanceof L1SummonInstance) {
            return true;
        }
        if (obj instanceof L1DeInstance) {
            return true;
        }
        return obj instanceof L1FollowerInstance;
    }

    /**
     * GM HPBAR 只看PC角色
     *
     */
    public boolean GmHpBarForPc(L1Object obj) {
        return obj instanceof L1PcInstance;
    }

    private void sendVisualEffect() {
        int poisonId = 0;
        if (getPoison() != null) {
            poisonId = getPoison().getEffectId();
        }
        if (getParalysis() != null) {
            poisonId = getParalysis().getEffectId();
        }
        if (poisonId != 0) {
            sendPacketsAll(new S_Poison(getId(), poisonId));
        }
    }

    public void sendVisualEffectAtLogin() {
        sendVisualEffect();
    }

    public boolean isCHAOTIC() {
        return _isCHAOTIC;
    }

    public void setCHAOTIC(boolean flag) {
        _isCHAOTIC = flag;
    }

    public void sendVisualEffectAtTeleport() {
        if (isDrink()) {
            sendPackets(new S_Liquor(getId()));
        }
        if (isCHAOTIC()) {
            sendPackets(new S_Liquor(getId(), 2));
        }
        sendVisualEffect();
    }

    public void setSkillMastery(int skillid) {
        if (!_skillList.contains(skillid)) {
            _skillList.add(skillid);
        }
    }

    public void removeSkillMastery(int skillid) {
        if (_skillList.contains(skillid)) {
            _skillList.remove(new Integer(skillid));
        }
    }

    public boolean isSkillMastery(int skillid) {
        return _skillList.contains(skillid);
    }

    public void clearSkillMastery() {
        _skillList.clear();
    }

    /**
     * 設定娃娃跟隨速度
     */
    public void setNpcSpeed() {
        try {
            if (!getDolls().isEmpty()) {
                for (Object obj : getDolls().values().toArray()) {
                    L1DollInstance doll = (L1DollInstance) obj;
                    if (doll != null) {
                        doll.setNpcMoveSpeed();
                    }
                }
            }
            if (!getDolls2().isEmpty()) {
                for (final Object obj : getDolls2().values().toArray()) {
                    final L1DollInstance2 doll2 = (L1DollInstance2) obj;
                    if (doll2 != null) {
                        doll2.setNpcMoveSpeed();
                    }
                }
            }
            // 取回娃娃
            if (get_power_doll() != null) {
                get_power_doll().setNpcMoveSpeed();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void setCurrentHp(int i) {
        int currentHp = Math.min(i, this.getMaxHp());
        if (this.getCurrentHp() == currentHp) {
            return;
        }
        if (currentHp <= 0) {
            if (this.isGm()) {
                currentHp = this.getMaxHp();
            } else {
                if (!this.isDead()) {
                    this.death(null); // HP小於1死亡
                }
            }
        }
        this.setCurrentHpDirect(currentHp);
        this.sendPackets(new S_HPUpdate(currentHp, this.getMaxHp()));
        if (this.isInParty()) { // 隊伍狀態
            this.getParty().updateMiniHP(this);
        }
    }

    public void setCurrentMp(int i) {
        // final int currentMp = Math.min(i, getMaxMp());
        int currentMp = Math.min(i, getMaxMp());
        if (getCurrentMp() == currentMp) {
            return;
        }
        if (currentMp <= 0) {
            currentMp = 0;
        }
        setCurrentMpDirect(currentMp);
        this.sendPackets(new S_MPUpdate(currentMp, getMaxMp()));
        if (isInParty()) {
            getParty().updateMiniHP(this);
        }
    }

    public L1PcInventory getInventory() {
        return _inventory;
    }

    public L1DwarfInventory getDwarfInventory() {
        return _dwarf;
    }

    public L1DwarfForChaInventory getDwarfForChaInventory() {
        return _dwarfForCha;
    }

    public L1DwarfForElfInventory getDwarfForElfInventory() {
        return _dwarfForElf;
    }

    /**
     * 輔助(自動刪物)
     *
     */
    public L1RemoveItemInventory getRemoveItemInventory() {
        return _dwarfForRemoveItem;
    }

    public boolean isGmInvis() {
        return _gmInvis;
    }

    public void setGmInvis(boolean flag) {
        _gmInvis = flag;
    }

    public int getCurrentWeapon() {
        return _currentWeapon;
    }

    public void setCurrentWeapon(int i) {
        _currentWeapon = i;
    }

    public int getType() {
        return _type;
    }

    public void setType(int i) {
        _type = i;
        _classFeature = L1ClassFeature.newClassFeature(i); // XXX add 7.6
    }

    public short getAccessLevel() {
        return _accessLevel;
    }

    public void setAccessLevel(short i) {
        _accessLevel = i;
    }

    public int getClassId() {
        return _classId;
    }

    public void setClassId(int i) {
        _classId = i;
        // XXX del 7.6
        // _classFeature = L1ClassFeature.newClassFeature(i);
    }

    public L1ClassFeature getClassFeature() {
        return _classFeature;
    }

    public synchronized long getExp() {
        return _exp;
    }

    public synchronized void setExp(long i) {
        if (!isAddExp(i)) {
            return;
        }
        _exp = i;
    }

    public int get_PKcount() {
        return _PKcount;
    }

    public void set_PKcount(int i) {
        _PKcount = i;
    }

    public int getPkCountForElf() {
        return _PkCountForElf;
    }

    public void setPkCountForElf(int i) {
        _PkCountForElf = i;
    }

    public int getClanid() {
        return _clanid;
    }

    public void setClanid(int i) {
        _clanid = i;
    }

    public String getClanname() {
        return clanname;
    }

    public void setClanname(String s) {
        clanname = s;
    }

    public L1Clan getClan() {
        return WorldClan.get().getClan(getClanname());
    }

    public int getClanRank() {
        return _clanRank;
    }

    public void setClanRank(int i) {
        _clanRank = i;
    }

    public byte get_sex() {
        return _sex;
    }

    public void set_sex(int i) {
        _sex = (byte) i;
    }

    public boolean isGm() {
        return _gm;
    }

    public void setGm(boolean flag) {
        _gm = flag;
    }

    /**
     * 是否有監看權限
     *
     */
    public boolean isMonitor() {
        return _monitor;
    }

    /**
     * 設定是否有監看權限
     *
     */
    public void setMonitor(boolean flag) {
        _monitor = flag;
    }

    private L1PcInstance getStat() {
        return null;
    }

    public void reduceCurrentHp(double d, L1Character l1character) {
        getStat().reduceCurrentHp(d, l1character);
    }
    private void notifyPlayersLogout(List<L1PcInstance> playersArray) {
        for (L1PcInstance player : playersArray) {
            if (player.knownsObject(this)) {
                player.removeKnownObject(this);
                player.sendPackets(new S_RemoveObject(this));
            }
        }
    }

    /**
     * 不在游戏世界中
     *
     * @return 无连接
     */
    public boolean isOutGame() {
        return _out == null || _netConnection == null;
    }

    public void logout() {
        // 刪除人物墓碑
        L1EffectInstance tomb = this.get_tomb();
        if (tomb != null) {
            tomb.broadcastPacketAll(new S_DoActionGFX(tomb.getId(), 8));
            tomb.deleteMe();
        }
        CharBuffReading.get().deleteBuff(this);
        CharBuffReading.get().saveBuff(this);
        getMap().setPassable(getLocation(), true);
        if (getClanid() != 0) {
            L1Clan clan = WorldClan.get().getClan(getClanname());
            if (clan != null && clan.getWarehouseUsingChar() == getId()) {
                clan.setWarehouseUsingChar(0);
            }
        }
        notifyPlayersLogout(getKnownPlayers());
        if (get_showId() != -1) {
            if (WorldQuest.get().isQuest(get_showId())) {
                WorldQuest.get().remove(get_showId(), this);
            }
        }
        set_showId(-1);
        PcHellTimer._HELL_PC_LIST.remove(this);
        World.get().removeVisibleObject(this);
        World.get().removeObject(this);
        notifyPlayersLogout(World.get().getRecognizePlayer(this));
        this._pandora.clearItems();
        UserMonsterBookLoader.store(this);
        if (ConfigLIN.Week_Quest) {
            UserWeekQuestLoader.store(this);
        }
        removeAllKnownObjects();
        stopHpRegeneration();
        stopMpRegeneration();
        // if (ConfigAutoAll.Auto_All) {
        stopSkillSound_autoHP1();// 停止自動喝水 -> 第一組
        stopSkillSound_autoHP2();// 停止自動喝水 -> 第二組
        stopSkillSound_autoHP3();// 停止自動喝水 -> 第三組
        stopSkillSound_autoMP1();// 停止自動補魔 -> 第一組 道具
        stopSkillSound_autoMP2();// 停止自動補魔 -> 第二組 魂體
        stopSkillSound_autoBackHome();// 停止自動回村
        stopSkillSound_autoItemAll();// 停止鋪助道具狀態
        stopSkillSound_autoSkillAll();// 停止自動輔助狀態(魔法) -> 全職業
        stopSkillSound_autoRemoveItem();// 停止輔助(自動刪物)
        // }
        setDead(true);
        setNetConnection(null);
        setPacketOutput(null);
    }

    public ClientExecutor getNetConnection() {
        return _netConnection;
    }

    public void setNetConnection(ClientExecutor clientthread) {
        _netConnection = clientthread;
    }

    public boolean isInParty() {
        return getParty() != null;
    }

    public L1Party getParty() {
        return _party;
    }

    public void setParty(L1Party p) {
        _party = p;
    }

    public boolean isInChatParty() {
        return getChatParty() != null;
    }

    public L1ChatParty getChatParty() {
        return _chatParty;
    }

    public void setChatParty(L1ChatParty cp) {
        _chatParty = cp;
    }

    public int getPartyID() {
        return _partyID;
    }

    public void setPartyID(int partyID) {
        _partyID = partyID;
    }

    public int getTradeID() {
        return _tradeID;
    }

    public void setTradeID(int tradeID) {
        _tradeID = tradeID;
    }

    public boolean getTradeOk() {
        return _tradeOk;
    }

    public void setTradeOk(boolean tradeOk) {
        _tradeOk = tradeOk;
    }

    public int getTempID() {
        return _tempID;
    }

    public void setTempID(int tempID) {
        _tempID = tempID;
    }

    public boolean isTeleport() {
        return _isTeleport;
    }

    public void setTeleport(boolean flag) {
        if (flag) {
            setNowTarget(null);
        }
        _isTeleport = flag;
    }

    public boolean isDrink() {
        return _isDrink;
    }

    public void setDrink(boolean flag) {
        _isDrink = flag;
    }

    public boolean isGres() {
        return _isGres;
    }

    public void setGres(boolean flag) {
        _isGres = flag;
    }

    public boolean isPinkName() {
        return _isPinkName;
    }

    public void setPinkName(boolean flag) {
        _isPinkName = flag;
    }

    public ArrayList<L1PrivateShopSellList> getSellList() {
        return _sellList;
    }

    public ArrayList<L1PrivateShopBuyList> getBuyList() {
        return _buyList;
    }

    public byte[] getShopChat() {
        return _shopChat;
    }

    public void setShopChat(byte[] chat) {
        _shopChat = chat;
    }

    public boolean isPrivateShop() {
        return _isPrivateShop;
    }

    public void setPrivateShop(boolean flag) {
        _isPrivateShop = flag;
    }

    public boolean isTradingInPrivateShop() {
        return _isTradingInPrivateShop;
    }

    public void setTradingInPrivateShop(boolean flag) {
        _isTradingInPrivateShop = flag;
    }

    public int getPartnersPrivateShopItemCount() {
        return _partnersPrivateShopItemCount;
    }

    public void setPartnersPrivateShopItemCount(int i) {
        _partnersPrivateShopItemCount = i;
    }

    public void setPacketOutput(EncryptExecutor out) {
        _out = out;
    }

    // 限制：如果交易清單已經有 16 個物品，就不再新增。
    /*
     * public void add_trade_item(L1TradeItem info) { if (_trade_items.size() == 16)
     * { return; } _trade_items.add(info); }
     *
     * public ArrayList<L1TradeItem> get_trade_items() { return _trade_items; }
     *
     * public void get_trade_clear() { _tradeID = 0; _trade_items.clear(); }
     */
    public void sendPackets(final ServerBasePacket packet) {
        if (this._out == null) {
            return;
        }
        // System.out.println(packet.toString());
        try {
            this._out.encrypt(packet);
        } catch (final Exception e) {
            this.logout();
            this.close();
        }
    }

    /**
     * 發送單體封包
     *
     * @param packet 封包
     */
    public void sendPackets2(final ServerBasePacket packet) {
        if (this._out == null) {
            return;
        }
        // System.out.println(packet.toString());
        try {
            this._out.encrypt(packet);
        } catch (final Exception e) {
            this.logout();
            this.close();
        }
    }

    public void sendPacketsBossWeaponAll(ServerBasePacket packet) {
        if (this._out == null) {
            return;
        }
        try {
            this._out.encrypt(packet);
            if (!isGmInvis() && !isInvisble()) {
                broadcastPacketBossWeaponAll(packet);
            }
        } catch (Exception e) {
            logout();
            close();
        }
    }

    /**
     * 發送單體封包 與可見範圍發送封包 自定義開關 防具
     *
     * @param packet 封包
     */
    public void sendPacketsArmorYN(final ServerBasePacket packet) {
        if (this._out == null) {
            return;
        }
        try {
            // 自己
            if (!L1CastleLocation.checkInAllWarArea(getX(), getY(), getMapId())) {
                this._out.encrypt(packet);
            }
            if (!this.isGmInvis() && !this.isInvisble()) {
                this.broadcastPacketArmorYN(packet);
            }
        } catch (final Exception e) {
            this.logout();
            this.close();
        }
    }

    /**
     * 發送單體封包 與可見範圍發送封包 自定義開關 武器
     *
     * @param packet 封包
     */
    public void sendPacketsYN(final ServerBasePacket packet) {
        if (this._out == null) {
            return;
        }
        try {
            // 自己
            if (!L1CastleLocation.checkInAllWarArea(getX(), getY(), getMapId())) {
                this._out.encrypt(packet);
            }
            if (!this.isGmInvis() && !this.isInvisble()) {
                this.broadcastPacketYN(packet);
            }
        } catch (final Exception e) {
            this.logout();
            this.close();
        }
    }

    /**
     * 發送單體封包 與可見範圍發送封包
     *
     * @param packet 封包
     */
    public void sendPacketsAll(final ServerBasePacket packet) {
        if (this._out == null) {
            return;
        }
        try {
            // 自己
            this._out.encrypt(packet);
            if (!this.isGmInvis() && !this.isInvisble()) {
                this.broadcastPacketAll(packet);
            }
        } catch (final Exception e) {
            this.logout();
            this.close();
        }
    }

    public void sendPacketsAllUnderInvis(ServerBasePacket packet) {
        if (_out == null) {
            return;
        }
        try {
            _out.encrypt(packet);
            if (!isGmInvis()) {
                broadcastPacketAll(packet);
            }
        } catch (Exception e) {
            logout();
            close();
        }
    }

    /**
     * 發送單體封包 與指定範圍發送封包(範圍8)
     *
     * @param packet 封包
     */
    public void sendPacketsX8(ServerBasePacket packet) {
        if (this._out == null) {
            return;
        }
        try {
            this._out.encrypt(packet);
            if (!isGmInvis() && !isInvisble()) {
                broadcastPacketX8(packet);
            }
        } catch (Exception e) {
            logout();
            close();
        }
    }

    public void sendPacketsUserAddHp(ServerBasePacket packet) // src015
    {
        if (this._out == null) {
            return;
        }
        try {
            this._out.encrypt(packet);
            final boolean castle_area = L1CastleLocation.checkInAllWarArea(getX(), getY(), getMapId());
            if (!castle_area && !isGmInvis() && !isInvisble()) {
                broadcastPacketX10(packet);
            }
        } catch (Exception e) {
            logout();
            close();
        }
    }

    /**
     * 發送單體封包 與指定範圍發送封包(範圍10)
     *
     * @param packet 封包
     */
    public void sendPacketsX10(ServerBasePacket packet) {
        if (_out == null) {
            return;
        }
        try {
            _out.encrypt(packet);
            if (!isGmInvis() && !isInvisble()) {
                broadcastPacketX10(packet);
            }
        } catch (Exception e) {
            logout();
            close();
        }
    }

    /**
     * 發送單體封包 與可見指定範圍發送封包
     *
     * @param packet 封包
     * @param r      範圍
     */
    public void sendPacketsXR(ServerBasePacket packet, int r) {
        if (_out == null) {
            return;
        }
        try {
            _out.encrypt(packet);
            if (!isGmInvis() && !isInvisble()) {
                broadcastPacketXR(packet, r);
            }
        } catch (Exception e) {
            logout();
            close();
        }
    }

    /**
     * 關閉連線線程
     */
    private void close() {
        try {
            getNetConnection().close();
        } catch (Exception ignored) {
        }
    }

    /**
     * 增加皮肤外观
     *
     * @param skin  npc类型
     * @param gfxid 主键(外观图档)
     */
    public void addSkin(L1SkinInstance skin, int gfxid) {
        this._skins.put(gfxid, skin);
    }

    public void removeSkin(int gfxid) {
        this._skins.remove(gfxid);
    }

    public L1SkinInstance getSkin(int gfxid) {
        return this._skins.get(gfxid);
    }

    public Map<Integer, L1SkinInstance> getSkins() {
        return _skins;
    }

    // （已移除）變身光圈特效（連播）support

    /**
     * vip 保護經驗
     */
    public void set_death_exp(boolean b) {
        this._death_exp = b;
    }

    /**
     * vip 保護物品
     */
    public void set_death_item(boolean b) {
        this._death_item = b;
    }

    /**
     * vip 保護技能
     */
    public void set_death_skill(boolean b) {
        this._death_skill = b;
    }

    /**
     * vip 保護技能
     */
    public void set_death_score(boolean b) {
        this._death_score = b;
    }

    /**
     * 對該物件攻擊的調用
     *
     * @param attacker 攻擊方
     */
    @Override
    public void onAction(L1PcInstance attacker) {
        if (attacker == null) {
            return;
        }
        if (isTeleport()) {
            return;
        }
        if (isSafetyZone() || attacker.isSafetyZone()) {
            L1AttackMode attack_mortion = new L1AttackPc(attacker, this);
            attack_mortion.action();
            return;
        }
        if (checkNonPvP(this, attacker)) {
            L1AttackMode attack_mortion = new L1AttackPc(attacker, this);
            attack_mortion.action();
            return;
        }
        if (getCurrentHp() > 0 && !isDead()) {
            // 攻擊行為產生解除隱身
            attacker.delInvis();

            // 初始化一個布林值 isCounterBarrier，表示是否觸發反擊屏障
            boolean isCounterBarrier = false;
            // 創建一個攻擊對象 attack，用於處理玩家攻擊的邏輯
            L1AttackMode attack = new L1AttackPc(attacker, this);
            // 判斷攻擊是否命中
            if (attack.calcHit()) {
                // 如果攻擊命中，檢查是否有反擊屏障技能效果
                if (hasSkillEffect(COUNTER_BARRIER)) {
                    // 創建一個魔法對象，用於計算技能的觸發機率
                    L1Magic magic = new L1Magic(this, attacker);
                    // 計算反擊屏障技能的觸發機率
                    boolean isProbability = magic.calcProbabilityMagic(COUNTER_BARRIER);
                    // 判斷攻擊是否為近戰攻擊
                    boolean isShortDistance = attack.isShortDistance();
                    // 獲取攻擊者的武器
                    L1ItemInstance weapon = attacker.getWeapon();
                    // 如果觸發機率滿足、且為近戰攻擊、並且攻擊者的武器類型不為 17，則觸發反擊屏障
                    if (isProbability && isShortDistance && weapon.getItem().getType() != 17) {
                        isCounterBarrier = true;
                    }
                }
                // 如果沒有觸發反擊屏障
                if (!isCounterBarrier) {
                    // 設置寵物的目標為當前對象
                    attacker.setPetTarget(this);
                    // 計算攻擊的傷害
                    attack.calcDamage();
                }
            }
            // 如果觸發了反擊屏障，執行反擊屏障的動作
            if (isCounterBarrier) {
                attack.commitCounterBarrier();
                // 判斷是否學習了 COUNTER_BARRIER_VETERAN
                if (this.isCOUNTER_BARRIER_VETERAN()) {
                    // 獲取攻擊造成的傷害
                    int incomingDamage = attack.calcDamage();
                    // 根據技能配置計算回血量
                    int healingAmount = incomingDamage * ConfigSkillKnight.BARRIER_HEAL_PERCENT / 100;
                    // 確保回血量不會超過當前血量上限
                    int currentHp = this.getCurrentHp();
                    int maxHp = this.getMaxHp();
                    int newHp = Math.min(currentHp + healingAmount, maxHp);
                    // 更新當前血量
                    this.setCurrentHp(newHp);
                }
            }

            // 送出攻擊動作；若為反擊屏障，僅送出動畫不提交原攻擊傷害
            attack.action();
            if (!isCounterBarrier) {
                attack.commit();
            }
        }
    }

    /**
     * 檢查玩家角色是否可以進行非PvP攻擊。
     *
     * @param pc     當前玩家實例
     * @param target 目標角色（可能是玩家、寵物或召喚獸）
     * @return 如果可以進行非PvP攻擊，返回 true；否則返回 false
     */
    public boolean checkNonPvP(L1PcInstance pc, L1Character target) {
        L1PcInstance targetpc = null; // 定義目標玩家實例
        // 判斷目標是玩家、寵物還是召喚獸，並獲取相應的玩家實例
        if (target instanceof L1PcInstance) {
            targetpc = (L1PcInstance) target; // 如果目標是玩家
        } else if (target instanceof L1PetInstance) {
            targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster(); // 如果目標是寵物，獲取其主人
        } else if (target instanceof L1SummonInstance) {
            targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster(); // 如果目標是召喚獸，獲取其主人
        }
        // 如果目標玩家實例為空，則無法進行非PvP攻擊
        if (targetpc == null) {
            return false;
        }
        // 檢查是否啟用了非PvP模式
        if (!ConfigAlt.ALT_NONPVP) {
            // 檢查當前地圖位置是否為戰鬥區域，如果是，則不能進行非PvP攻擊
            if (getMap().isCombatZone(getLocation())) {
                return false;
            }
            // 遍歷所有戰爭列表，檢查玩家和目標玩家是否在同一戰爭中
            for (L1War war : WorldWar.get().getWarList()) {
                // 如果兩者都有氏族ID，則進行進一步檢查
                if (pc.getClanid() != 0 && targetpc.getClanid() != 0) {
                    // 檢查兩個氏族是否在同一場戰爭中
                    boolean same_war = war.checkClanInSameWar(pc.getClanname(), targetpc.getClanname());
                    if (same_war) {
                        return false; // 如果在同一場戰爭中，不能進行非PvP攻擊
                    }
                }
            }
            // 如果目標是玩家實例，則進一步檢查是否在戰爭區域且戰爭時間中
            if (target instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) target;
                return !isInWarAreaAndWarTime(pc, targetPc); // 如果在戰爭區域且戰爭時間中，返回 false；否則返回 true
            }
            // 其他情況下，可以進行非PvP攻擊
            return true;
        }
        // 如果啟用了非PvP模式，則不能進行非PvP攻擊
        return false;
    }

    /**
     * 是否在戰爭旗中並在攻城時段中
     *
     */
    private boolean isInWarAreaAndWarTime(L1PcInstance pc, L1PcInstance target) {
        int castleId = L1CastleLocation.getCastleIdByArea(pc);
        int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
        return castleId != 0 && targetCastleId != 0 && castleId == targetCastleId && ServerWarExecutor.get().isNowWar(castleId);
    }

    public void setPetTarget(L1Character target) {
        if (target == null) {
            return;
        }
        if (target.isDead()) {
            return;
        }
        Map<Integer, L1NpcInstance> petList = getPetList();
        try {
            if (!petList.isEmpty()) {
                for (Iterator<?> iter = petList.values().iterator(); iter.hasNext(); ) {
                    L1NpcInstance pet = (L1NpcInstance) iter.next();
                    if (pet != null) {
                        if (pet instanceof L1PetInstance) {
                            L1PetInstance pets = (L1PetInstance) pet;
                            pets.setMasterTarget(target);
                        } else if (pet instanceof L1SummonInstance) {
                            L1SummonInstance summon = (L1SummonInstance) pet;
                            summon.setMasterTarget(target);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (_debug) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
        Map<Integer, L1IllusoryInstance> illList = get_otherList().get_illusoryList();
        try {
            if (!illList.isEmpty()) {
                if (getId() != target.getId()) {
                    for (Iterator<L1IllusoryInstance> iter = illList.values().iterator(); iter.hasNext(); ) {
                        L1IllusoryInstance ill = iter.next();
                        if (ill != null) {
                            ill.setLink(target);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (_debug) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
    /**
     * 刺客增加判斷
     */

    private boolean _assassinAttackNow = false;

    public void setAssassinAttackNow(boolean value) {
        _assassinAttackNow = value;
    }

    public boolean isAssassinAttackNow() {
        return _assassinAttackNow;
    }

    public void delInvis() {
        if (isDarkelf() && hasPassiveAssassin() && isAssassinAttackNow()) {
            // 黑妖刺客正在攻擊流程時，不在這裡解除隱身
            return;
        }
        // 其餘職業 或 非攻擊流程呼叫，一律立即解除
        if (hasSkillEffect(INVISIBILITY)) {
            killSkillEffectTimer(INVISIBILITY);
            sendPackets(new S_Invis(getId(), 0));
            broadcastPacketAll(new S_OtherCharPacks(this));
        }
        if (hasSkillEffect(BLIND_HIDING)) {
            killSkillEffectTimer(BLIND_HIDING);
            sendPackets(new S_Invis(getId(), 0));
            broadcastPacketAll(new S_OtherCharPacks(this));
        }
    }


    public void delBlindHiding() {
        killSkillEffectTimer(BLIND_HIDING);
        sendPackets(new S_Invis(getId(), 0));
        broadcastPacketAll(new S_OtherCharPacks(this));
    }

    /**
     * 受攻擊MP減少時的處理
     *
     */
    public void receiveManaDamage(L1Character attacker, int mpDamage) {
        if (mpDamage > 0 && !isDead()) {
            delInvis();// 解除隱身
            if (attacker instanceof L1PcInstance) {
                L1PinkName.onAction(this, attacker);
            }
            if (attacker instanceof L1PcInstance && ((L1PcInstance) attacker).isPinkName()) {
                for (L1Object object : World.get().getVisibleObjects(attacker)) {
                    if (object instanceof L1GuardInstance) {
                        L1GuardInstance guard = (L1GuardInstance) object;
                        guard.setTarget((L1PcInstance) attacker);
                    }
                }
            }
            int newMp = getCurrentMp() - mpDamage;
            if (newMp > getMaxMp()) {
                newMp = getMaxMp();
            }
            newMp = Math.max(newMp, 0);
            setCurrentMp(newMp);
        }
    }

    /**
     * 魔法傷害遞減計算
     *
     */
    public double isMagicDamager(double damage) {
        long nowTime = System.currentTimeMillis();
        long interval = nowTime - _oldTime;
        double newdmg = 0.0D;
        if (damage < 0.0D) {
            newdmg = damage;
        } else {
            Double tmpnewdmg = _magicDamagerList.get(interval);
            if (tmpnewdmg != null) {
                newdmg = damage * tmpnewdmg / 100.0D;
            } else {
                newdmg = damage;
            }
            newdmg = Math.max(newdmg, 0.0D);
            _oldTime = nowTime;
        }
        return newdmg;
    }

    /**
     * 受到傷害的處理
     *
     * @param attacker         攻擊者
     * @param damage           傷害值
     * @param isMagicDamage    是否魔法傷害遞減
     * @param isCounterBarrier 是否執行反彈 true不反彈 false計算反彈
     */
    public void receiveDamage(L1Character attacker, double damage, boolean isMagicDamage, boolean isCounterBarrier) {
        if (damage <= 0.0D) {// 傷害值小於0則返回
            return;
        }
        if (getCurrentHp() > 0 && !isDead()) {
            if (attacker != null) {
                if (attacker != this && !(attacker instanceof L1EffectInstance) && !knownsObject(attacker) && attacker.getMapId() == getMapId()) {
                    attacker.onPerceive(this);
                }
                if (isMagicDamage) {// 魔法傷害遞減
                    damage = isMagicDamager(damage);
                }
                L1PcInstance attackPc = null;
                L1NpcInstance attackNpc = null;
                if (attacker instanceof L1PcInstance) {
                    attackPc = (L1PcInstance) attacker;
                    if (isActivated() && IsAttackTeleport()) { //是否開啟內掛被攻擊瞬移狀態
                        setIsAttackTeleportHp(true);
                    }
                } else if (attacker instanceof L1NpcInstance) {
                    attackNpc = (L1NpcInstance) attacker;
                }
                if (damage > 0.0D) {
                    delInvis();
                    removeSkillEffect(66);
                    removeSkillEffect(212);
                    if (attackPc != null) {
                        L1PinkName.onAction(this, attackPc);
                        if (attackPc.isPinkName()) {
                            for (L1Object object : World.get().getVisibleObjects(attacker)) {
                                if (object instanceof L1GuardInstance) {
                                    L1GuardInstance guard = (L1GuardInstance) object;
                                    guard.setTarget((L1PcInstance) attacker);
                                }
                            }
                        }
                    }
                    // SRC0907
                    boolean useWeaponCheck = false;
                    if (attackPc != null) {
                        L1ItemInstance weapon = attackPc.getWeapon();
                        if (weapon != null && (weapon.getItem().getType1() == 20 || weapon.getItem().getType1() == 62)) {
                            useWeaponCheck = true;
                        }
                    }
                    if (!isMagicDamage && attackPc != null && this.getCounterattack() > 0) {
                        if (!useWeaponCheck) {
                            if (15 >= ThreadLocalRandom.current().nextInt(100) + 1) {
                                final int dmgYYY = this.getCounterattack();
                                this.sendPacketsX8(new S_EffectLocation(this.getX(), this.getY(), 6507));
                                attackPc.sendPacketsX10(new S_DoActionGFX(attackPc.getId(), ActionCodes.ACTION_Damage));
                                attackPc.receiveDamage(this, dmgYYY, false, true);
                            }
                        }
                    }
                    if (!isMagicDamage && attackPc != null && this.getBowcounterattack() > 0) {
                        if (useWeaponCheck) {
                            if (15 >= ThreadLocalRandom.current().nextInt(100) + 1) {
                                final int dmgXXX = this.getBowcounterattack();
                                this.sendPacketsX8(new S_EffectLocation(this.getX(), this.getY(), 10419));
                                attackPc.sendPacketsX10(new S_DoActionGFX(attackPc.getId(), ActionCodes.ACTION_Damage));
                                attackPc.receiveDamage(this, dmgXXX, false, true);
                            }
                        }
                    }
                    // SRC0907 END
                }
                if (!isCounterBarrier) {// 執行傷害反彈
                    if (hasSkillEffect(MORTAL_BODY) && getId() != attacker.getId()) {// 致命身軀反彈
                        int rnd = ThreadLocalRandom.current().nextInt(100);
                        if (damage > 0.0D && rnd < 23) {
                            int dmg = 40;
                            if (attackPc != null) {
                                if (attackPc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                    dmg /= 2;
                                }
                                attackPc.sendPacketsAll(new S_DoActionGFX(attackPc.getId(), 2));
                                this.sendPacketsAll(new S_SkillSound(this.getId(), 10710));
                                attackPc.receiveDamage(this, dmg, false, true);
                            } else if (attackNpc != null) {
                                if (attackNpc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                    dmg /= 2;
                                }
                                /*
                                 * if (dmg >= attackNpc.getCurrentHp()) {// 如果傷害大於等於目前HP dmg =
                                 * attackNpc.getCurrentHp() - 1;// 變更傷害為目前HP-1(避免使用反屏掛機) }
                                 */
                                attackNpc.broadcastPacketAll(new S_DoActionGFX(attackNpc.getId(), 2));
                                this.sendPacketsAll(new S_SkillSound(this.getId(), 10710));
                                attackNpc.receiveDamage(this, dmg);
                            }
                        }
                    }
                    if (!isMagicDamage && _elitePlateMail_Valakas > 0) { // 巴拉卡斯的弓箭反屏
                        int nowDamage = ThreadLocalRandom.current().nextInt(_valakas_dmgmax - _valakas_dmgmin + 1) + _valakas_dmgmin;
                        if (attackPc != null) {
                            L1ItemInstance weapon = attackPc.getWeapon();
                            if (weapon != null && (weapon.getItem().getType1() == 20 || weapon.getItem().getType1() == 62) && _random.nextInt(1000) < _elitePlateMail_Valakas) {
                                if (attackPc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                    nowDamage /= 2;
                                }
                                this.sendPacketsAll(new S_SkillSound(getId(), 10419));
                                attackPc.sendPacketsAll(new S_DoActionGFX(attackPc.getId(), 2));
                                attackPc.receiveDamage(this, nowDamage, false, true);
                            }
                        } else if (attackNpc != null && ThreadLocalRandom.current().nextInt(1000) < _elitePlateMail_Valakas) {
                            L1AttackMode attack = new L1AttackNpc(attackNpc, this);
                            boolean isShortDistance = attack.isShortDistance();
                            if (!isShortDistance) {
                                if (attackNpc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                    nowDamage /= 2;
                                }
                                /*
                                 * if (nowDamage >= attackNpc.getCurrentHp()) {// 如果傷害大於等於目前HP nowDamage =
                                 * attackNpc.getCurrentHp() - 1;// 變更傷害為目前HP-1(避免使用反屏掛機) }
                                 */
                                this.sendPacketsAll(new S_SkillSound(getId(), 10419));
                                attackNpc.broadcastPacketAll(new S_DoActionGFX(attackNpc.getId(), 2));
                                attackNpc.receiveDamage(this, nowDamage);
                            }
                        }
                    }
                    // TODO 黑帝斯斗篷反彈傷害
                    if (_hades_cloak > 0) {
                        int nowDamage = ThreadLocalRandom.current().nextInt(_hades_cloak_dmgmax - _hades_cloak_dmgmin + 1) + _hades_cloak_dmgmin;
                        if (attackPc != null && ThreadLocalRandom.current().nextInt(1000) < _hades_cloak) {
                            /*
                             * L1AttackMode attack = new L1AttackPc(attackPc, this); boolean isShortDistance
                             * = attack.isShortDistance(); if (isShortDistance) {
                             */
                            if (attackPc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                nowDamage /= 2;
                            }
                            attackPc.sendPacketsAll(new S_DoActionGFX(attackPc.getId(), 2));
                            this.sendPacketsAll(new S_SkillSound(this.getId(), 10710));
                            attackPc.receiveDamage(this, nowDamage, false, true);
                            damage = 0;
                        } else if (attackNpc != null && ThreadLocalRandom.current().nextInt(1000) < _hades_cloak) {
                            /*
                             * L1AttackMode attack = new L1AttackNpc(attackNpc, this); boolean
                             * isShortDistance = attack.isShortDistance(); if (isShortDistance) {
                             */
                            if (attackNpc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                nowDamage /= 2;
                            }
                            /*
                             * if (nowDamage >= attackNpc.getCurrentHp()) {// 如果傷害大於等於目前HP nowDamage =
                             * attackNpc.getCurrentHp() - 1;// 變更傷害為目前HP-1(避免使用反屏掛機) }
                             */
                            attackNpc.broadcastPacketAll(new S_DoActionGFX(attackNpc.getId(), 2));
                            this.sendPacketsAll(new S_SkillSound(this.getId(), 10710));
                            attackNpc.receiveDamage(this, nowDamage);
                            damage = 0;
                        }
                    }
                    // TODO 死亡騎士脛甲反彈傷害
                    if (_death_pant > 0) {
                        int nowDamage = ThreadLocalRandom.current().nextInt(_death_pant_dmgmax - _death_pant_dmgmin + 1) + _death_pant_dmgmin;
                        if (attackPc != null && ThreadLocalRandom.current().nextInt(1000) < _death_pant) {
                            /*
                             * L1AttackMode attack = new L1AttackPc(attackPc, this); boolean isShortDistance
                             * = attack.isShortDistance(); if (isShortDistance) {
                             */
                            if (attackPc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                nowDamage /= 2;
                            }
                            attackPc.sendPacketsAll(new S_DoActionGFX(attackPc.getId(), 2));
                            // this.sendPacketsAll(new S_SkillSound(this.getId(), 10710));
                            if (ConfigOtherSet2.Death_Pant_Gfx != 0) {
                                this.sendPacketsAll(new S_SkillSound(this.getId(), ConfigOtherSet2.Death_Pant_Gfx));
                            }
                            attackPc.receiveDamage(this, nowDamage, false, true);
                            damage = 0;
                        } else if (attackNpc != null && ThreadLocalRandom.current().nextInt(1000) < _death_pant) {
                            /*
                             * L1AttackMode attack = new L1AttackNpc(attackNpc, this); boolean
                             * isShortDistance = attack.isShortDistance(); if (isShortDistance) {
                             */
                            if (attackNpc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                nowDamage /= 2;
                            }
                            /*
                             * if (nowDamage >= attackNpc.getCurrentHp()) {// 如果傷害大於等於目前HP nowDamage =
                             * attackNpc.getCurrentHp() - 1;// 變更傷害為目前HP-1(避免使用反屏掛機) }
                             */
                            attackNpc.broadcastPacketAll(new S_DoActionGFX(attackNpc.getId(), 2));
                            // this.sendPacketsAll(new S_SkillSound(this.getId(), 10710));
                            if (ConfigOtherSet2.Death_Pant_Gfx != 0) {
                                this.sendPacketsAll(new S_SkillSound(this.getId(), ConfigOtherSet2.Death_Pant_Gfx));
                            }
                            attackNpc.receiveDamage(this, nowDamage);
                            damage = 0;
                        }
                    }
                    if (this.has_powerid(6612)) {// 附魔系統 還擊傷害
                        int rad = 15;// 機率
                        int dmg = 80;// 反彈傷害值
                        if (attackPc != null && damage > 0 && ThreadLocalRandom.current().nextInt(100) < rad) {
                            if (attackPc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                dmg /= 2;
                            }
                            attackPc.sendPacketsAll(new S_DoActionGFX(attackPc.getId(), 2));
                            this.sendPacketsAll(new S_SkillSound(this.getId(), 10710));
                            attackPc.receiveDamage(this, dmg, false, true);
                        } else if (attackNpc != null && damage > 0 && ThreadLocalRandom.current().nextInt(100) < rad) {
                            if (attackNpc.hasSkillEffect(IMMUNE_TO_HARM)) {// 聖界減傷
                                dmg /= 2;
                            }
                            if (dmg >= attackNpc.getCurrentHp()) {// 如果傷害大於等於目前HP
                                dmg = attackNpc.getCurrentHp() - 1;// 變更傷害為目前HP-1(避免使用反屏掛機)
                            }
                            attackNpc.broadcastPacketAll(new S_DoActionGFX(attackNpc.getId(), 2));
                            this.sendPacketsAll(new S_SkillSound(this.getId(), 10710));
                            attackNpc.receiveDamage(this, dmg);
                        }
                    }
                    // 「黑暗之盾」效果）
                    if (hasSkillEffect(GREATER_RESURRECTION)) { // 黑暗之盾
                        // 受到的傷害必須大於10，才會觸發分攤與反射機制（避免小傷害浪費資源）
                        if (damage > 10) { // 受到傷害 > 10 才可觸發 因為要考慮 % 的計算結果
                            // 如果角色目前在隊伍中
                            if (isInParty()) { // 在隊伍中
                                // 建立一個 List 來收集同畫面中的隊伍成員
                                final List<L1PcInstance> members = new ArrayList<>();
                                // 遍歷隊伍所有成員
                                for (L1PcInstance player : getParty().getMembers()) {
                                    // 判斷該成員是否與自己在同一畫面
                                    if (player.getLocation().isInScreen(this.getLocation())) {
                                        members.add(player); // 加入同畫面的隊友
                                    }
                                }
                                try {
                                    // 如果同畫面內有其他隊員
                                    if (!members.isEmpty()) {
                                        final int num = members.size();
                                        // 傷害分攤：剩餘的 40% 傷害，均分給所有同畫面隊友，每人最少 1 點
                                        final double party_dmg = Math.max(1, damage * 0.40D / num);
                                        for (L1PcInstance member : members) {
                                            if (member != null) {
                                                // 讓隊友分攤這 40% 傷害（不觸發死亡、也不反擊）
                                                member.receiveDamage(attacker, party_dmg, false, false);
                                                // 給隊友播放分攤傷害的特效（762：隕石特效）
                                                member.sendPacketsAll(new S_EffectLocation(member.getLocation(), 21449));
                                            }
                                        }
                                    }
                                } finally {
                                    // 清空清單，釋放資源
                                    members.clear();
                                }
                            } else { // 不在隊伍中，直接反射給攻擊者
                                if (attackPc != null) { // 攻擊者是玩家
                                    // 攻擊者自身播放受擊特效
                                    attackPc.sendPacketsAll(new S_DoActionGFX(attackPc.getId(), 2));
                                    // 自己顯示反射特效（10419：魔法盾遠程特效）
                                    this.sendPacketsAll(new S_EffectLocation(this.getLocation(), 10419));
                                    // 把 40% 傷害直接反彈給攻擊者
                                    attackPc.receiveDamage(this, damage * 0.4D, false, false);
                                }
                                if (attackNpc != null) { // 攻擊者是 NPC 怪物
                                    attackNpc.broadcastPacketAll(new S_DoActionGFX(attackNpc.getId(), 2));
                                    this.sendPacketsAll(new S_EffectLocation(this.getLocation(), 10419));
                                    attackNpc.receiveDamage(this, (int) (damage * 0.4D));
                                }
                            }
                            // 不論分攤或反射，自己都播放受擊特效（762：魔法盾反射特效）
                            this.sendPacketsAll(new S_EffectLocation(this.getLocation(), 21449));
                            // 自己受到的傷害減少 60%（只承受原本傷害的 60%）
                            damage *= 0.60D;
                        }
                    }
                }
            }
            if (getInventory().checkEquipped(145) || getInventory().checkEquipped(149)) {// 狂斧、牛人斧
                damage *= 1.5D;
            }
            if (this.hasSkillEffect(219)) {// 化身
                damage *= ConfigSkillIllusion.ILLUSION_AVATAR_DAMAGE; // 傷害提高1.05倍
                // damage *=1.06;//傷害提高1.05倍
            }
            if (this.hasSkillEffect(1219)) {// 化身 //SRC0808
                damage *= 1 + ConfigSkillIllusion.IS4;
                ;
            }
            if (this.isCrown() && this.hasSkillEffect(L1SkillId.ReiSkill_1)) { // 王族天賦技能金剛護體
                damage = 0.0;
            }
            if (attacker != null && this.isKnight() && this.getReincarnationSkill()[2] > 0) { // 騎士天賦技能神盾護體
                boolean isSameAttr = false;
                if (getHeading() == 0 && (attacker.getHeading() == 3 || attacker.getHeading() == 4 || attacker.getHeading() == 5)) {
                    isSameAttr = true;
                } else if (getHeading() == 1 && (attacker.getHeading() == 4 || attacker.getHeading() == 5 || attacker.getHeading() == 6)) {
                    isSameAttr = true;
                } else if (getHeading() == 2 && (attacker.getHeading() == 5 || attacker.getHeading() == 6 || attacker.getHeading() == 7)) {
                    isSameAttr = true;
                } else if (getHeading() == 3 && (attacker.getHeading() == 6 || attacker.getHeading() == 7 || attacker.getHeading() == 0)) {
                    isSameAttr = true;
                } else if (getHeading() == 4 && (attacker.getHeading() == 7 || attacker.getHeading() == 0 || attacker.getHeading() == 1)) {
                    isSameAttr = true;
                } else if (getHeading() == 5 && (attacker.getHeading() == 0 || attacker.getHeading() == 1 || attacker.getHeading() == 2)) {
                    isSameAttr = true;
                } else if (getHeading() == 6 && (attacker.getHeading() == 1 || attacker.getHeading() == 2 || attacker.getHeading() == 3)) {
                    isSameAttr = true;
                } else if (getHeading() == 7 && (attacker.getHeading() == 2 || attacker.getHeading() == 3 || attacker.getHeading() == 4)) {
                    isSameAttr = true;
                }
                if (isSameAttr && RandomArrayList.getInc(100, 1) > 100 - this.getReincarnationSkill()[2]) {
                    damage *= 0.70D;
                    // sendPackets(new S_SkillSound(getId(), 5377));
                    sendPackets(new S_SystemMessage(L1SystemMessage.ShowMessage(8045))); // 發動天賦技能 神盾護體 減少30%傷害。
                }
            }
            int addmp;
            if (_elitePlateMail_Lindvior > 0 && // 林德拜爾的魔力守護
                    ThreadLocalRandom.current().nextInt(1000) < _elitePlateMail_Lindvior) {
                sendPacketsAll(new S_SkillSound(getId(), 2188));
                addmp = ThreadLocalRandom.current().nextInt(_lindvior_mpmax - _lindvior_mpmin + 1) + _lindvior_mpmin;
                int newMp = getCurrentMp() + addmp;
                setCurrentMp(newMp);
            }
            int addhp = 0;
            if ((getInventory().checkEquipped(21204) || getInventory().checkEquipped(21205) || getInventory().checkEquipped(21206) || getInventory().checkEquipped(21207)) && _elitePlateMail_Fafurion > 0 && // 法利昂的治癒守護
                    ThreadLocalRandom.current().nextInt(1000) < _elitePlateMail_Fafurion) {
                sendPacketsAll(new S_SkillSound(getId(), 2187));
                addhp = ThreadLocalRandom.current().nextInt(_fafurion_hpmax - _fafurion_hpmin + 1) + _fafurion_hpmin;
            }
            if (_Hexagram_Magic_Rune > 0 && // 六芒星的淨化
                    _random.nextInt(1000) < _Hexagram_Magic_Rune) {
                sendPacketsAll(new S_SkillSound(getId(), _hexagram_gfx));
                addhp = _random.nextInt(_hexagram_hpmax - _hexagram_hpmin + 1) + _hexagram_hpmin;
            }
            if (_dimiter_bless > 0 && // 蒂蜜特的祝福
                    _random.nextInt(1000) < _dimiter_bless) {
                if (!this.hasSkillEffect(IMMUNE_TO_HARM)) {// 身上沒有聖界效果
                    sendPacketsAll(new S_SkillSound(getId(), 11101));
                    setSkillEffect(IMMUNE_TO_HARM, _dimiter_time * 1000);
                    sendPackets(new S_PacketBox(S_PacketBox.ICON_I2H, _dimiter_time));
                }
            }
            if (_dimiter_mpr_rnd > 0 && // 蒂蜜特的魔力回復
                    _random.nextInt(1000) < _dimiter_mpr_rnd) {
                sendPacketsAll(new S_SkillSound(getId(), 2188));
                addmp = _random.nextInt(_dimiter_mpmax - _dimiter_mpmin + 1) + _dimiter_mpmin;
                int newMp = getCurrentMp() + addmp;
                setCurrentMp(newMp);
            }
            int newHp = getCurrentHp() - (int) damage + addhp;
            if (newHp > getMaxHp()) {
                newHp = getMaxHp();
            }
            if (newHp <= 10) { // 精靈新技能 魔力護盾
                if (isElf() && hasSkillEffect(L1SkillId.SOUL_BARRIER)) {
                    this.setCurrentHp(10);
                    newHp = 10;
                    int newMp = getCurrentMp() - (int) damage;
                    // if (newMp <= 0) {
                    if (newMp <= 0 && !isGm()) {
                        this.setCurrentHp(0);
                        death(attacker);
                    }
                    this.setCurrentMp(newMp);
                }
            }
            if (newHp <= 0 && !isGm()) {
                death(attacker);
            }
            setCurrentHp(newHp);
            if (this.isCrown() && this.getReincarnationSkill()[0] > 0 && RandomArrayList.getInc(100, 1) > 100 - this.getReincarnationSkill()[0]) { // 王族天賦技能金剛護體
                this.setSkillEffect(L1SkillId.ReiSkill_1, 1000);
                this.sendPackets(new S_SystemMessage(L1SystemMessage.ShowMessage(8018))); // 發動天賦技能無敵1秒。
                // this.sendPackets(new S_SkillSound(this.getId(), 9800));
                // this.broadcastPacketAll(new S_SkillSound(this.getId(), 9800));
            }
            if (isGm()) {
                sendPackets(new S_SystemMessage("你受到 " + ((int)damage) + " 點傷害！"));
            }

        } else if (!isDead()) {
            _log.error("人物hp減少處理失敗 可能原因: 初始hp為0");
            death(attacker);
        }
    }

    public void death(L1Character lastAttacker) {
        synchronized (this) {
            if (isDead()) {
                return;
            }
            setIsAuto(false);
            set_Test_Auto(false);
            setRestartAuto(0);
            setRestartAutoStartSec(0);
            setNowTarget(null);
            setDead(true);
            setStatus(8);
        }
        GeneralThreadPool.get().execute(new Death(lastAttacker));
    }

    /**
     * 死亡噴出物品
     *
     */
    private void caoPenaltyResult(int count) {
        new Timestamp(System.currentTimeMillis());
        for (int i = 0; i < count; i++) {
            L1ItemInstance item = getInventory().caoPenalty();
            if (item != null) {
                if (item.getBless() >= 128) {
                    _log.warn("玩家：" + this.getName() + "封印裝備 死亡噴出遺失:" + item.getId() + "/" + item.getItem().getName());
                    // 死亡掉落物品
                    WriteLogTxt.Recording("死亡掉落物品", "IP(" + getNetConnection().getIp() + ")玩家【" + getName() + "】的(封印)【" + item.getNumberedViewName(item.getCount()) + ", (ObjId: " + item.getId() + ")】死亡後遺失.");
                    getInventory().deleteItem(item);
                } else {
                    _log.warn("玩家：" + this.getName() + "死亡噴出物品:" + item.getId() + "/" + item.getItem().getName());
                    item.set_showId(get_showId());
                    int x = getX();
                    int y = getY();
                    short m = getMapId();
                    // 死亡掉落物品
                    WriteLogTxt.Recording("死亡掉落物品", "IP(" + getNetConnection().getIp() + ")玩家【" + getName() + "】的【" + item.getNumberedViewName(item.getCount()) + ", (ObjId: " + item.getId() + ")】死亡後掉落.");
                    getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1L, World.get().getInventory(x, y, m));
                }
                // 638 您損失了 %0。
                sendPackets(new S_ServerMessage(638, item.getLogName()));
                RecordTable.get().recordeDeadItem(getName(), item.getAllName(), (int) item.getCount(), item.getId(), getIp());
            }
        }
    }

    /**
     * <FONT COLOR="#0000ff">死亡技能遺失</FONT>
     *
     * @param count 掉落數量
     */
    private void delSkill(int count) {
        new Timestamp(System.currentTimeMillis());
        for (int i = 0; i < count; i++) {
            int index = _random.nextInt(_skillList.size());
            Integer skillid = _skillList.get(index);
            if (_skillList.remove(skillid)) {
                sendPackets(new S_DelSkill(this, skillid));
                CharSkillReading.get().spellLost(getId(), skillid);
                L1Skills _skill = SkillsTable.get().getTemplate(skillid);
                // 死亡掉落技能
                WriteLogTxt.Recording("死亡掉落技能", "IP(" + getNetConnection().getIp() + ")玩家【" + getName() + "】的技能【" + _skill.getName() + "】死亡後掉落.");
            }
        }
    }

    public void stopPcDeleteTimer() {
        setDead(false);
        set_delete_time(0);
    }

    /**
     * <FONT COLOR="#0000ff">是否在參加攻城戰中</FONT>
     *
     * @return true:是 false:不是
     */
    public boolean castleWarResult() {
        if (this.getClanid() != 0 && this.isCrown()) { // 具有血盟的王族
            final L1Clan clan = WorldClan.get().getClan(this.getClanname());
            if (clan.getCastleId() == 0) {
                // 取回全部戰爭清單
                for (final L1War war : WorldWar.get().getWarList()) {
                    final int warType = war.getWarType();
                    final boolean isInWar = war.checkClanInWar(this.getClanname());
                    final boolean isAttackClan = war.checkAttackClan(this.getClanname());
                    if (this.getId() == clan.getLeaderId() && // 攻城戰中 攻擊方盟主死亡
                            // 退出戰爭
                            warType == 1 && isInWar && isAttackClan) {
                        final String enemyClanName = war.getEnemyClanName(this.getClanname());
                        if (enemyClanName != null) {
                            war.ceaseWar(this.getClanname(), enemyClanName); // 結束
                        }
                        break;
                    }
                }
            }
        }
        int castleId = 0;
        boolean isNowWar = false;
        castleId = L1CastleLocation.getCastleIdByArea(this);
        if (castleId != 0) { // 戰爭範圍旗幟內城堡ID
            isNowWar = ServerWarExecutor.get().isNowWar(castleId);
        }
        return isNowWar;
    }

    /**
     * 是否參加血盟戰中
     *
     */
    public boolean simWarResult(L1Character lastAttacker) {
        if (getClanid() == 0) {
            return false;
        }
        L1PcInstance attacker = null;
        String enemyClanName = null;
        boolean sameWar = false;
        if (lastAttacker instanceof L1PcInstance) {
            attacker = (L1PcInstance) lastAttacker;
        } else if (lastAttacker instanceof L1PetInstance) {
            attacker = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
        } else if (lastAttacker instanceof L1SummonInstance) {
            attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
        } else if (lastAttacker instanceof L1IllusoryInstance) {
            attacker = (L1PcInstance) ((L1IllusoryInstance) lastAttacker).getMaster();
        } else if (lastAttacker instanceof L1EffectInstance) {
            attacker = (L1PcInstance) ((L1EffectInstance) lastAttacker).getMaster();
        } else {
            return false;
        }
        L1Clan clan = WorldClan.get().getClan(getClanname());
        for (L1War war : WorldWar.get().getWarList()) {
            int warType = war.getWarType();
            if (warType != 1) {
                boolean isInWar = war.checkClanInWar(getClanname());
                if (isInWar) {
                    if (attacker != null && attacker.getClanid() != 0) {
                        sameWar = war.checkClanInSameWar(getClanname(), attacker.getClanname());
                    }
                    if (getId() == clan.getLeaderId()) {
                        enemyClanName = war.getEnemyClanName(getClanname());
                        if (enemyClanName != null) {
                            war.ceaseWar(getClanname(), enemyClanName);
                        }
                    }
                    if (warType == 2 && sameWar) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 恢復經驗值
     */
    public void resExp() {
        int oldLevel = getLevel();
        long needExp = ExpTable.getNeedExpNextLevel(oldLevel);
        long exp = 0L;
        switch (oldLevel) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
                exp = (long) (needExp * 0.05D);
                break;
            case 45:
                exp = (long) (needExp * 0.045D);
                break;
            case 46:
                exp = (long) (needExp * 0.04D);
                break;
            case 47:
                exp = (long) (needExp * 0.035D);
                break;
            case 48:
                exp = (long) (needExp * 0.03D);
                break;
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
                exp = (long) (needExp * 0.025D);
                break;
            default:
                exp = (long) (needExp * 0.025D);
        }
        if (exp == 0L) {
            return;
        }
        addExp(exp);
    }

    /**
     * 死亡損失經驗值
     *
     */
    private long deathPenalty() {
        int oldLevel = getLevel();
        long needExp = ExpTable.getNeedExpNextLevel(oldLevel);
        long exp = 0L;
        switch (oldLevel) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                exp = 0L;
                break;
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
                exp = (long) (needExp * 0.1D);
                break;
            case 45:
                exp = (long) (needExp * 0.09D);
                break;
            case 46:
                exp = (long) (needExp * 0.08D);
                break;
            case 47:
                exp = (long) (needExp * 0.07D);
                break;
            case 48:
                exp = (long) (needExp * 0.06D);
                break;
            case 49:
                exp = (long) (needExp * 0.05D);
                break;
            default:
                exp = (long) (needExp * 0.05D);
        }
        if (exp == 0L) {
            return 0L;
        }
        addExp(-exp);
        return exp;
    }

    public int getOriginalEr() {
        return _originalEr;
    }

    /**
     * 神秘提升ER +18
     *
     */
    public boolean hasPassiveErPlus18() {
        return com.lineage.server.datatables.lock.CharSkillReading.get()
                .spellCheck(this.getId(), L1SkillId.ER_PLUS18_PASSIVE);
    }

    /**
     * 暗隱恢復
     *
     */
    // 增加近戰閃避
    public void addTempMeleeEvasion(int value) {
        _tempMeleeEvasion += value;
    }

    // 取得近戰閃避
    public int getTempMeleeEvasion() {
        return _tempMeleeEvasion;
    }

    // 清除（BUFF結束時用）
    public void clearTempEvasion() {
        _tempMeleeEvasion = 0;
    }
    public int getEr() {
        if (hasSkillEffect(174)) {// 精準射擊
            return 0;
        }
        int er = 0;
        if (isKnight() || isWarrior()) {
            er = getLevel() >> 2;
        } else if (isCrown() || isElf()) {
            er = getLevel() >> 3;
        } else if (isDarkelf()) {
            er = getLevel() / 6;
        } else if (isWizard()) {
            er = getLevel() / 10;
        } else if (isDragonKnight()) {
            er = getLevel() / 7;
        } else if (isIllusionist()) {
            er = getLevel() / 9;
        }
        er += this.getDex() - 8 >> 1;/// 2;
        er += getOriginalEr();
        if (this.hasSkillEffect(AQUA_PROTECTER)) {// 水之防護
            er += 5;
        }
        if (this.hasSkillEffect(DRESS_EVASION)) {// 迴避提升
            er += 18;
        }
        if (this.hasSkillEffect(SOLID_CARRIAGE)) {// 堅固防護
            er += 15;
        }
        if (this.hasSkillEffect(Counter_attack)) {// 反制攻擊
            er += 15;
        }
        if (this.hasPassiveErPlus18()) {
            er += 18;
        }
        er += _er;

        er += getTempMeleeEvasion();
        return er;
    }

    public void addEr(int i) {
        _er += i;
    }

    public L1ItemInstance getWeapon() {
        return _weapon;
    }

    public void setWeapon(L1ItemInstance weapon) {
        _weapon = weapon;
    }

    public L1PcQuest getQuest() {
        return _quest;
    }

    public L1ActionPc getAction() {
        return _action;
    }

    public L1ActionPet getActionPet() {
        return _actionPet;
    }

    public L1ActionSummon getActionSummon() {
        return _actionSummon;
    }

    public boolean isCrown() {
        return getClassId() == 0 || getClassId() == 1;
    }

    public boolean isKnight() {
        return getClassId() == 61 || getClassId() == 48;
    }

    public boolean isElf() {
        return getClassId() == 138 || getClassId() == 37;
    }

    public boolean isWizard() {
        return getClassId() == 734 || getClassId() == 1186;
    }

    public boolean isDarkelf() {
        return getClassId() == 2786 || getClassId() == 2796;
    }

    public boolean isDragonKnight() {
        return getClassId() == 6658 || getClassId() == 6661;
    }

    public boolean isIllusionist() {
        return getClassId() == 6671 || getClassId() == 6650;
    }

    public boolean isWarrior() {
        return getClassId() == CLASSID_WARRIOR_MALE || getClassId() == CLASSID_WARRIOR_FEMALE;
    }

    public String getAccountName() {
        return _accountName;
    }

    public void setAccountName(String s) {
        _accountName = s;
    }

    public short getBaseMaxHp() {
        return _baseMaxHp;
    }

    public void addBaseMaxHp(short i) {
        i = (short) (i + _baseMaxHp);
        if (i >= 32767) {
            i = 32767;
        } else if (i < 1) {
            i = 1;
        }
        addMaxHp(i - _baseMaxHp);
        _baseMaxHp = i;
    }

    public short getBaseMaxMp() {
        return _baseMaxMp;
    }

    public void addBaseMaxMp(short i) {
        i = (short) (i + _baseMaxMp);
        if (i >= 32767) {
            i = 32767;
        } else if (i < 1) {
            i = 1;
        }
        addMaxMp(i - _baseMaxMp);
        _baseMaxMp = i;
    }

    public int getBaseAc() {
        return _baseAc;
    }

    public int getOriginalAc() {
        return _originalAc;
    }

    public int getBaseStr() {
        return _baseStr;
    }

    public void addBaseStr(int i) {
        i += _baseStr;
        if (i >= 254) {
            i = 254;
        } else if (i < 1) {
            i = 1;
        }
        addStr(i - _baseStr);
        _baseStr = i;
    }

    /**
     * 黑妖技能 (暗黑組合) 被動技能 STR+3 Dex+3
     */
    public boolean hasPassiveStrPlus3() {
        return com.lineage.server.datatables.lock.CharSkillReading.get()
                .spellCheck(this.getId(), L1SkillId.STR_PLUS3_PASSIVE);
    }
    public int getBaseCon() {
        return _baseCon;
    }

    public void addBaseCon(int i) {
        i += _baseCon;
        if (i >= 254) {
            i = 254;
        } else if (i < 1) {
            i = 1;
        }
        addCon(i - _baseCon);
        _baseCon = i;
    }

    public int getBaseDex() {
        return _baseDex;
    }

    public void addBaseDex(int i) {
        i += _baseDex;
        if (i >= 254) {
            i = 254;
        } else if (i < 1) {
            i = 1;
        }
        addDex(i - _baseDex);
        _baseDex = i;
    }

    public int getBaseCha() {
        return _baseCha;
    }

    public void addBaseCha(int i) {
        i += _baseCha;
        if (i >= 254) {
            i = 254;
        } else if (i < 1) {
            i = 1;
        }
        addCha(i - _baseCha);
        _baseCha = i;
    }

    public int getBaseInt() {
        return _baseInt;
    }

    public void addBaseInt(int i) {
        i += _baseInt;
        if (i >= 254) {
            i = 254;
        } else if (i < 1) {
            i = 1;
        }
        addInt(i - _baseInt);
        _baseInt = i;
    }

    public int getBaseWis() {
        return _baseWis;
    }

    public void addBaseWis(int i) {
        i += _baseWis;
        if (i >= 254) {
            i = 254;
        } else if (i < 1) {
            i = 1;
        }
        addWis(i - _baseWis);
        _baseWis = i;
    }
    /**
     * 暗影之牙(被動)
     */
    public boolean hasPassiveDmgPlus5() {
        return com.lineage.server.datatables.lock.CharSkillReading.get()
                .spellCheck(this.getId(), L1SkillId.DMG_PLUS5_PASSIVE);
    }
    /**
     * 刺客(被動)
     */
    public boolean hasPassiveAssassin() {
        return com.lineage.server.datatables.lock.CharSkillReading.get()
                .spellCheck(this.getId(), L1SkillId.M_ASSASSIN);
    }

    /**
     * 狂暴(被動)
     */
    public boolean hasDoubleBreakMaster() {
        return this.isSkillMastery(L1SkillId.DARKELF_BERSERK);
    }

    /**
     * 路西法(被動)
     */
    public boolean hasPassiveLucifer() {
        return com.lineage.server.datatables.lock.CharSkillReading.get()
                .spellCheck(this.getId(), L1SkillId.PASSIVE_LUCIFER);
    }

    /**
     * 暗影衝擊(被動)
     */
    public boolean hasPassiveShadowImpact() {
        return com.lineage.server.datatables.lock.CharSkillReading.get()
                .spellCheck(this.getId(), L1SkillId.PASSIVE_SHADOW_IMPACT);
    }

    public int getOriginalStr() {
        return _originalStr;
    }

    public void setOriginalStr(int i) {
        _originalStr = i;
    }

    public int getOriginalCon() {
        return _originalCon;
    }

    public void setOriginalCon(int i) {
        _originalCon = i;
    }

    public int getOriginalDex() {
        return _originalDex;
    }

    public void setOriginalDex(int i) {
        _originalDex = i;
    }

    public int getOriginalCha() {
        return _originalCha;
    }

    public void setOriginalCha(int i) {
        _originalCha = i;
    }

    public int getOriginalInt() {
        return _originalInt;
    }

    public void setOriginalInt(int i) {
        _originalInt = i;
    }

    public int getOriginalWis() {
        return _originalWis;
    }

    public void setOriginalWis(int i) {
        _originalWis = i;
    }

    public int getOriginalDmgup() {
        return _originalDmgup;
    }

    public int getOriginalBowDmgup() {
        return _originalBowDmgup;
    }

    public int getOriginalHitup() {
        return _originalHitup;
    }

    public int getOriginalBowHitup() {
        return _originalHitup + _originalBowHitup;
    }

    public int getOriginalMr() {
        return _originalMr;
    }

    public void addOriginalMagicHit(int i)// 增加魔法命中
    {
        _originalMagicHit += i;
    }

    public int getOriginalMagicHit() {
        return _originalMagicHit;
    }

    /**
     * 增加魔法暴擊率
     */
    public void addOriginalMagicCritical(int i) {
        _originalMagicCritical += i;
    }

    /**
     * 魔法暴擊
     *
     * @return 若有 古代啟示 BUFF 則額外提升50點暴擊 以及 50%暴擊傷害
     */
    public int getOriginalMagicCritical() {
        int critical = 0;
        if (hasSkillEffect(ICE_LANCE)) { // 古代啟示 一定時間內提升魔法暴擊
            critical += 50;
        }
        return _originalMagicCritical + critical;
    }

    public int getOriginalMagicConsumeReduction() {
        return _originalMagicConsumeReduction;
    }

    public int getOriginalMagicDamage() {
        return _originalMagicDamage;
    }

    public int getOriginalHpup() {
        return _originalHpup;
    }

    public int getOriginalMpup() {
        return _originalMpup;
    }


    public int getBaseMr() {
        return _baseMr;
    }

    public int getAdvenHp() {
        return _advenHp;
    }

    public void setAdvenHp(int i) {
        _advenHp = i;
    }

    public int getAdvenMp() {
        return _advenMp;
    }

    public void setAdvenMp(int i) {
        _advenMp = i;
    }

    public int getHighLevel() {
        return _highLevel;
    }

    public void setHighLevel(int i) {
        _highLevel = i;
    }

    public int getBonusStats() {
        return _bonusStats;
    }

    public void setBonusStats(int i) {
        _bonusStats = i;
    }

    public int getOtherStats() {
        return _otherStats;
    }

    public void setOtherStats(int i) {
        _otherStats = i;
    }

    public int getAddPoint() {
        return _addPoint;
    }

    public void setAddPoint(int i) {
        _addPoint = i;
    }

    public int getDelPoint() {
        return _delPoint;
    }

    public void setDelPoint(int i) {
        _delPoint = i;
    }

    public int getElixirStats() {
        return _elixirStats;
    }

    public void setElixirStats(int i) {
        _elixirStats = i;
    }

    public int getElfAttr() {
        return _elfAttr;
    }

    public void setElfAttr(int i) {
        _elfAttr = i;
    }

    public int getExpRes() {
        return _expRes;
    }

    public void setExpRes(int i) {
        _expRes = i;
    }

    public int getPartnerId() {
        return _partnerId;
    }

    public void setPartnerId(int i) {
        _partnerId = i;
    }

    public int getOnlineStatus() {
        return _onlineStatus;
    }

    public void setOnlineStatus(int i) {
        _onlineStatus = i;
    }

    public int getHomeTownId() {
        return _homeTownId;
    }

    public void setHomeTownId(int i) {
        _homeTownId = i;
    }


    // 村莊貢獻度
    public int getContribution() {
        return _contribution;
    }

    public void setContribution(int i) {
        _contribution = i;
    }

    // 村莊稅收支付
    public int getPay() {
        return _pay;
    }

    public void setPay(int i) {
        _pay = i;
    }

    public int getHellTime() {
        return _hellTime;
    }

    public void setHellTime(int i) {
        _hellTime = i;
    }

    public void addGF(int i) {
        if (i > 0) {
            _GF = DoubleUtil.sum(_GF, (double) i / 100D);
        } else {
            _GF = DoubleUtil.sub(_GF, (double) (i * -1) / 100D);
        }
    }

    public double getGF() {
        return Math.max(_GF, 0.0D);
    }

    public boolean isBanned() {
        return _banned;
    }

    public void setBanned(boolean flag) {
        _banned = flag;
    }

    public int get_food() {
        return _food;
    }

    public void set_food(int i) {
        if (i > 225) {
            i = 225;
        } else if (i < 0) {
            i = 0;
        }
        _food = i;
        if (_food == 225) {
            Calendar cal = Calendar.getInstance();
            long h_time = cal.getTimeInMillis() / 1000L;
            set_h_time(h_time);
        } else {
            set_h_time(-1L);
        }
    }
    public L1EquipmentSlot getEquipSlot() {
        return _equipSlot;
    }

    public L1WeaponProficiency getProficiency() {
        return _weaponProficiency;
    }

    /**
     * 人物資料存檔
     *
     */
    public void save() throws Exception {
        if (isGhost()) {
            return;
        }
        if (isInCharReset()) {
            return;
        }
        if (_other != null) {
            CharOtherReading.get().storeOther(getId(), _other);
        }
        if (this._other1 != null)
            CharOtherReading1.get().storeOther(getId(), this._other1);
        CharacterTable.get().storeCharacter(this);
    }

    /**
     * 人物VIP資料存檔
     *
     */
    public void saveVip() throws Exception {
        CharacterTable.get().updateVipTime(this);
    }

    /**
     * 背包資料存檔
     */
    public void saveInventory() {
        for (L1ItemInstance item : getInventory().getItems()) {
            getInventory().saveItem(item, item.getRecordingColumns());
        }
    }

    public double getMaxWeight() {
        int str = getStr();
        int con = getCon();
        // double maxWeight = 150.0D * Math.floor(0.6D * str + 0.4D * con + 1.0D) *
        // get_weightUP();
        // XXX 7.6 公式更新
        // 本身負重能力
        // double maxWeight = L1ClassFeature.calcAbilityMaxWeight(this.getStr(),
        // this.getCon());
        double maxWeight = L1ClassFeature.calcAbilityMaxWeight(str, con);
        double weightReductionByArmor = getWeightReduction();
        weightReductionByArmor /= 100.0D;
        int weightReductionByMagic = 0;
        if (hasSkillEffect(DECREASE_WEIGHT) || hasSkillEffect(JOY_OF_PAIN)) {
            weightReductionByMagic = 180;
        }
        // XXX 7.6取消計算初始能力負重減免
        // double originalWeightReduction = 0.0D;
        // originalWeightReduction += 0.04D * (getOriginalStrWeightReduction() +
        // getOriginalConWeightReduction());
        // double weightReduction = 1.0D + weightReductionByArmor +
        // originalWeightReduction;
        double weightReduction = 1.0D + weightReductionByArmor;// 7.6
        maxWeight *= weightReduction;
        maxWeight += weightReductionByMagic;
        maxWeight *= ConfigRate.RATE_WEIGHT_LIMIT;
        return maxWeight;
    }

    /**
     * 是否具有生命之樹果實效果
     *
     */
    public boolean isRibrave() { // 生命之樹果實 移速 * 1.15
        return hasSkillEffect(STATUS_RIBRAVE);
    }

    /**
     * 神聖疾走效果 行走加速效果 風之疾走效果 生命之樹果實效果
     *
     */
    public boolean isFastMovable() {
        return this.hasSkillEffect(HOLY_WALK) || this.hasSkillEffect(MOVING_ACCELERATION) || this.hasSkillEffect(WIND_WALK) || this.hasSkillEffect(STATUS_RIBRAVE);
    }

    public boolean isFastAttackable() {
        return false;
    }

    /**
     * 是否具有勇敢藥水效果
     *
     */
    public boolean isBrave() {
        return hasSkillEffect(STATUS_BRAVE) || hasSkillEffect(FIRE_BLESS) || hasSkillEffect(BLOODLUST);
    }

    /**
     * 荒神加速效果
     *
     */
    public boolean isSuperBrave() {
        return hasSkillEffect(STATUS_BRAVE2);
    }

    /**
     * 是否具有精靈餅乾效果
     *
     */
    public boolean isElfBrave() {
        return hasSkillEffect(STATUS_ELFBRAVE);
    }

    /**
     * 是否具有三段加速效果
     *
     */
    public boolean isBraveX() {
        return hasSkillEffect(STATUS_BRAVE3);
    }

    /**
     * 是否具有綠色藥水加速效果
     *
     */
    public boolean isHaste() {
        return hasSkillEffect(STATUS_HASTE) || hasSkillEffect(HASTE) || hasSkillEffect(GREATER_HASTE) || getMoveSpeed() == 1;
    }

    public boolean isInvisDelay() {
        return invisDelayCounter > 0;
    }

    public void addInvisDelayCounter(int counter) {
        synchronized (_invisTimerMonitor) {
            invisDelayCounter += counter;
        }
    }

    public void beginInvisTimer() {
        addInvisDelayCounter(1);
        GeneralThreadPool.get().pcSchedule(new L1PcInvisDelay(getId()), DELAY_INVIS);
    }

    public synchronized void addLawful(int i) {
        int lawful = getLawful() + i;
        if (lawful > 32767) {
            lawful = 32767;
        } else if (lawful < -32768) {
            lawful = -32768;
        }
        setLawful(lawful);
        onChangeLawful();
    }

    public synchronized void addExp(long exp) {
        long newexp = _exp + exp;
        if (!isAddExp(newexp)) {
            return;
        }
        setExp(newexp);
        onChangeExp();
    }

    private boolean isAddExp(long exp) {
        int level = ConfigOtherSet2.PcLevelUp + 1;
        long maxExp = ExpTable.getExpByLevel(level) - 44L;
        if (exp >= maxExp) {
            this._exp = maxExp;
            return false;
        }
        return true;
    }

    public synchronized void addContribution(int contribution) {
        _contribution += contribution;
    }

    private void levelUp(int gap) {
        resetLevel();
        for (int i = 0; i < gap; i++) {
            short randomHp = CalcStat.calcStatHp(getType(), getBaseMaxHp(), getBaseCon(), getOriginalHpup(), getType());
            short randomMp = CalcStat.calcStatMp(getType(), getBaseMaxMp(), getBaseWis(), getOriginalMpup());
            addBaseMaxHp(randomHp);
            addBaseMaxMp(randomMp);
        }
        if (ConfigTurn.METE_GIVE_POTION && getLevel() >= ConfigTurn.METE_LEVEL && getHighLevel() < ConfigTurn.METE_LEVEL) {
            try {
                L1Item l1item = ItemTable.get().getTemplate(43000);
                if (l1item != null && getInventory().checkAddItem(l1item, 1L) == 0) {
                    getInventory().storeItem(43000, 1L);
                    sendPackets(new S_ServerMessage(403, l1item.getName()));
                } else {
                    sendPackets(new S_SystemMessage("無法獲得轉生藥水。可能此道具不存在！"));
                }
            } catch (Exception e) {
                sendPackets(new S_SystemMessage("無法獲得轉生藥水。可能此道具不存在！"));
            }
        }
        resetBaseHitup();
        resetBaseDmgup();
        resetBaseAc();
        resetBaseMr();
        william.L1AutoLearnSkill.forAutoLearnSkill(this);
        if (getLevel() > getHighLevel()) {
            setHighLevel(getLevel());
        }
        setCurrentHp(getMaxHp());
        setCurrentMp(getMaxMp());
        try {
            save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            showWindows();
            getApprentice();
            sendPackets(new S_OwnCharStatus(this));
            Reward.getItem(this);
            MapLevelTable.get().get_level(getMapId(), this);
            L1WilliamLimitedReward.check_Task_For_Level(this);
            if (ConfigOtherSet2.APPRENTICE_SWITCH && getApprentice() != null && getApprentice().getMaster().getId() != getId() && getLevel() >= ConfigOtherSet2.APPRENTICE_LEVEL) {
                for (L1PcInstance character : getApprentice().getTotalList()) {
                    if (character.getId() == getId()) {
                        getApprentice().getTotalList().remove(character);
                        break;
                    }
                }
                CharApprenticeTable.getInstance().updateApprentice(getApprentice().getMaster().getId(), getApprentice().getTotalList());
                setApprentice(null);
                if (!getInventory().checkItem(ConfigOtherSet2.APPRENTICE_ITEM_ID)) {
                    CreateNewItem.createNewItem(this, ConfigOtherSet2.APPRENTICE_ITEM_ID, 1L);
                }
            }
            // 7.6
            if (getLevel() >= 51 && getLevel() - 50 > getBonusStats() || getLevel() >= 51 && getLevel() - 50 > getBonusStats() - 49) {
                if (getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt() + getBaseWis() + getBaseCha() < ConfigAlt.POWER * 6) {
                    // sendPackets(new S_bonusstats(getId(), 1));
                    int bonus = getLevel() - 50 - getBonusStats();// 可以點的點數
                    // XXX 7.6C
                    // ADD
                    sendPackets(new S_Message_YN(479, bonus));
                }
            }
            // XXX 能力基本資訊-力量
            this.sendPackets(new S_StrDetails(2, L1ClassFeature.calcStrDmg(this.getStr(), this.getBaseStr()), L1ClassFeature.calcStrHit(this.getStr(), this.getBaseStr()), L1ClassFeature.calcStrDmgCritical(this.getStr(), this.getBaseStr()), L1ClassFeature.calcAbilityMaxWeight(this.getStr(), this.getCon())));
            // XXX 重量程度資訊
            this.sendPackets(new S_WeightStatus(this.getInventory().getWeight100(), this.getInventory().getWeight(), (int) this.getMaxWeight()));
            // XXX 能力基本資訊-智力
            this.sendPackets(new S_IntDetails(2, L1ClassFeature.calcIntMagicDmg(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicHit(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicCritical(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicBonus(this.getType(), this.getInt()), L1ClassFeature.calcIntMagicConsumeReduction(this.getInt())));
            // XXX 能力基本資訊-精神
            this.sendPackets(new S_WisDetails(2, L1ClassFeature.calcWisMpr(this.getWis(), this.getBaseWis()), L1ClassFeature.calcWisPotionMpr(this.getWis(), this.getBaseWis()), L1ClassFeature.calcStatMr(this.getWis()) + L1ClassFeature.newClassFeature(this.getType()).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(this.getType(), this.getBaseWis())));
            // XXX 能力基本資訊-敏捷
            this.sendPackets(new S_DexDetails(2, L1ClassFeature.calcDexDmg(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexHit(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexDmgCritical(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexAc(this.getDex()), L1ClassFeature.calcDexEr(this.getDex())));
            // XXX 能力基本資訊-體質
            this.sendPackets(new S_ConDetails(2, L1ClassFeature.calcConHpr(this.getCon(), this.getBaseCon()), L1ClassFeature.calcConPotionHpr(this.getCon(), this.getBaseCon()), L1ClassFeature.calcAbilityMaxWeight(this.getStr(), this.getCon()), L1ClassFeature.calcBaseClassLevUpHpUp(this.getType()) + L1ClassFeature.calcBaseConLevUpExtraHpUp(this.getType(), this.getBaseCon())));
            // XXX 重量程度資訊
            this.sendPackets(new S_WeightStatus(this.getInventory().getWeight100(), this.getInventory().getWeight(), (int) this.getMaxWeight()));
        }
        // 新手任務
        if (getLevel() <= 5) {
            sendPackets(new S_MatizCloudia(0, getLevel()));
            sendPackets(new S_MatizCloudia(1));
        } else if (getLevel() == 8) {
            sendPackets(new S_MatizCloudia(1, 0));
        }
    }

    public void showWindows() {
        /*
         * if (QuestSet.ISQUEST) { //src016 int quest =
         * QuestTable.get().levelQuest(this, getLevel()); if (quest > 0) { isWindows();
         * } else if (power()) { sendPackets(new S_Bonusstats(getId())); }
         *
         * } else if (power()) { sendPackets(new S_Bonusstats(getId())); }
         */
        if (power()) {
            // sendPackets(new S_Bonusstats(getId()));
        }
    }

    public void isWindows() {
        if (power()) {
            sendPackets(new S_NPCTalkReturn(getId(), "y_qs_10"));
        } else {
            sendPackets(new S_NPCTalkReturn(getId(), "y_qs_00"));
        }
    }

    public boolean power() {
        /*
         * if ((getLevel() >= 51) && (getLevel() - 50 > getBonusStats())) { int power =
         * getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt() + getBaseWis() +
         * getBaseCha(); if (power < ConfigAlt.POWER * 6) { return true; } }
         *
         * return false;
         */
        if (getLevel() >= 51) {
            if (getLevel() - 50 > getBonusStats()) {
                final int power = getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt() + getBaseWis() + getBaseCha();
                return power < ConfigAlt.POWER * 6;
            }
        }
        return false;
    }
    private void levelDown(int gap) {
        resetLevel();
        for (int i = 0; i > gap; i--) {
            short randomHp = CalcStat.calcStatHp(getType(), 0, getBaseCon(), getOriginalHpup(), getType());
            short randomMp = CalcStat.calcStatMp(getType(), 0, getBaseWis(), getOriginalMpup());
            addBaseMaxHp((short) -randomHp);
            addBaseMaxMp((short) -randomMp);
        }
        if (getLevel() == 1) {
            int initHp = CalcInitHpMp.calcInitHp(this);
            int initMp = CalcInitHpMp.calcInitMp(this);
            addBaseMaxHp((short) -getBaseMaxHp());
            addBaseMaxHp((short) initHp);
            setCurrentHp((short) initHp);
            addBaseMaxMp((short) -getBaseMaxMp());
            addBaseMaxMp((short) initMp);
            setCurrentMp((short) initMp);
        }
        resetBaseHitup();
        resetBaseDmgup();
        resetBaseAc();
        resetBaseMr();
        getApprentice();
        try {
            save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            sendPackets(new S_OwnCharStatus(this));
            MapLevelTable.get().get_level(getMapId(), this);
            if (ConfigOtherSet2.APPRENTICE_SWITCH && getApprentice() != null && getApprentice().getMaster().getId() == getId() && getLevel() < ConfigOtherSet2.APPRENTICE_LEVEL) {
                L1Apprentice apprentice = CharApprenticeTable.getInstance().getApprentice(this);
                if (apprentice != null) {
                    CharApprenticeTable.getInstance().deleteApprentice(getId());
                    setApprentice(null);
                }
            }
        }
    }

    public boolean isGhost() {
        return _ghost;
    }

    private void setGhost(boolean flag) {
        _ghost = flag;
    }

    public int get_ghostTime() {
        return _ghostTime;
    }

    public void set_ghostTime(int ghostTime) {
        _ghostTime = ghostTime;
    }

    public boolean isGhostCanTalk() {
        return _ghostCanTalk;
    }

    private void setGhostCanTalk(boolean flag) {
        _ghostCanTalk = flag;
    }

    public boolean isReserveGhost() {
        return _isReserveGhost;
    }

    public void setReserveGhost(boolean flag) {
        _isReserveGhost = flag;
    }

    public void beginGhost(int locx, int locy, short mapid, boolean canTalk) {
        beginGhost(locx, locy, mapid, canTalk, 0);
    }

    public void beginGhost(int locx, int locy, short mapid, boolean canTalk, int sec) {
        if (isGhost()) {
            return;
        }
        setGhost(true);
        _ghostSaveLocX = getX();
        _ghostSaveLocY = getY();
        _ghostSaveMapId = getMapId();
        _ghostSaveHeading = getHeading();
        setGhostCanTalk(canTalk);
        L1Teleport.teleport(this, locx, locy, mapid, 5, true);
        if (sec > 0) {
            this.set_ghostTime(sec * 1000);
        }
    }

    public void makeReadyEndGhost() {
        setReserveGhost(true);
        L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY, _ghostSaveMapId, _ghostSaveHeading, true);
    }

    public void makeReadyEndGhost(boolean effectble) {
        setReserveGhost(true);
        L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY, _ghostSaveMapId, _ghostSaveHeading, effectble);
    }

    public void endGhost() {
        set_ghostTime(-1);
        setGhost(false);
        setGhostCanTalk(true);
        setReserveGhost(false);
    }

    /**
     * 地獄剩餘時間處理
     *
     * @param isFirst 是否傳送至地獄並計算地獄時間
     */
    public void beginHell(boolean isFirst) {
        if (this.getMapId() != 666) {// 如果人物不在地獄則傳送至地獄
            int locx = 32701;
            int locy = 32777;
            short mapid = 666;
            L1Teleport.teleport(this, locx, locy, mapid, 5, false);
        }
        if (isFirst) {
            if (get_PKcount() <= 10) {
                setHellTime(300);
            } else {
                setHellTime(300 * (get_PKcount() - 10) + 300);
            }
            sendPackets(new S_BlueMessage(552, String.valueOf(get_PKcount()), String.valueOf(getHellTime() / 60)));
        } else {
            sendPackets(new S_BlueMessage(637, String.valueOf(getHellTime())));
        }
        PcHellTimer._HELL_PC_LIST.put(this, getHellTime());
    }

    /**
     * 傳出地獄
     */
    public void endHell() {
        PcHellTimer._HELL_PC_LIST.remove(this);
        int[] loc = L1TownLocation.getGetBackLoc(4);
        L1Teleport.teleport(this, loc[0], loc[1], (short) loc[2], 5, true);
        try {
            save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void setPoisonEffect(int effectId) {
        sendPackets(new S_Poison(getId(), effectId));
        if (!isGmInvis() && !isGhost() && !isInvisble()) {
            broadcastPacketAll(new S_Poison(getId(), effectId));
        }
    }

    public void healHp(int pt) {
        super.healHp(pt);
        sendPackets(new S_HPUpdate(this));
    }

    public int getKarma() {
        return _karma.get();
    }

    public void setKarma(int i) {
        _karma.set(i);
    }

    public void addKarma(int i) {
        synchronized (_karma) {
            _karma.add(i);
            onChangeKarma();
        }
    }

    public int getKarmaLevel() {
        return _karma.getLevel();
    }

    public int getKarmaPercent() {
        return _karma.getPercent();
    }

    public Timestamp getLastPk() {
        return _lastPk;
    }

    //	private int _reduction_dmg = 0;// 套裝減免物理傷害
    //
    //	public void add_reduction_dmg(int add) {
    //		_reduction_dmg += add;
    //	}
    //
    //	public int get_reduction_dmg() {
    //		return _reduction_dmg;
    //	}
    public void setLastPk(Timestamp time) {
        _lastPk = time;
    }

    public void setLastPk() {
        _lastPk = new Timestamp(System.currentTimeMillis());
    }

    public boolean isWanted() {
        if (_lastPk == null) {
            return false;
        }
        if (System.currentTimeMillis() - _lastPk.getTime() > 3600000L) {
            setLastPk(null);
            return false;
        }
        return true;
    }

    public Timestamp getLastPkForElf() {
        return _lastPkForElf;
    }

    public void setLastPkForElf(Timestamp time) {
        _lastPkForElf = time;
    }

    public void setLastPkForElf() {
        _lastPkForElf = new Timestamp(System.currentTimeMillis());
    }

    public boolean isWantedForElf() {
        if (_lastPkForElf == null) {
            return false;
        }
        if (System.currentTimeMillis() - _lastPkForElf.getTime() > 86400000L) {
            setLastPkForElf(null);
            return false;
        }
        return true;
    }

    public Timestamp getDeleteTime() {
        return _deleteTime;
    }

    public void setDeleteTime(Timestamp time) {
        _deleteTime = time;
    }

    public int getMagicLevel() {
        return getClassFeature().getMagicLevel(getLevel());
    }

    public double get_weightUP() {
        return _weightUP;
    }

    public void add_weightUP(int i) {
        _weightUP += i / 100.0D;
    }

    public int getWeightReduction() {
        return _weightReduction;
    }

    public void addWeightReduction(int i) {
        _weightReduction += i;
    }

    public int getOriginalStrWeightReduction() {
        return _originalStrWeightReduction;
    }

    public int getOriginalConWeightReduction() {
        return _originalConWeightReduction;
    }

    public int getHasteItemEquipped() {
        return _hasteItemEquipped;
    }

    public void addHasteItemEquipped(int i) {
        _hasteItemEquipped += i;
    }

    public void removeHasteSkillEffect() {
        if (hasSkillEffect(29)) {
            removeSkillEffect(29);
        }
        if (hasSkillEffect(76)) {
            removeSkillEffect(76);
        }
        if (hasSkillEffect(152)) {
            removeSkillEffect(152);
        }
        if (hasSkillEffect(43)) {
            removeSkillEffect(43);
        }
        if (hasSkillEffect(54)) {
            removeSkillEffect(54);
        }
        if (hasSkillEffect(1001)) {
            removeSkillEffect(1001);
        }
    }

    // 傷害減免
    public int getDamageReductionByArmor() {
        return _damageReductionByArmor;
    }

    public void addDamageReductionByArmor(int i) {
        _damageReductionByArmor += i;
    }

    public int getHitModifierByArmor() {
        return _hitModifierByArmor;
    }

    public void addHitModifierByArmor(int i) {
        _hitModifierByArmor += i;
    }

    public int getDmgModifierByArmor() {
        return _dmgModifierByArmor;
    }

    public void addDmgModifierByArmor(int i) {
        _dmgModifierByArmor += i;
    }

    public int getBowHitModifierByArmor() {
        return _bowHitModifierByArmor;
    }

    public void addBowHitModifierByArmor(int i) {
        _bowHitModifierByArmor += i;
    }

    public int getBowDmgModifierByArmor() {
        return _bowDmgModifierByArmor;
    }

    public void addBowDmgModifierByArmor(int i) {
        _bowDmgModifierByArmor += i;
    }

    public boolean isGresValid() {
        return _gresValid;
    }

    private void setGresValid(boolean valid) {
        _gresValid = valid;
    }

    public boolean isFishing() {
        return _isFishing;
    }

    public void setFishing(boolean flag) {
        _isFishing = flag;
    }

    public boolean isFishingReady() {
        return _isFishingReady;
    }

    public void setFishingReady(boolean flag) {
        _isFishingReady = flag;
    }

    public long getFishingTime() {
        return _fishingTime;
    }

    public void setFishingTime(long i) {
        _fishingTime = i;
    }

    public L1ItemInstance getFishingItem() {
        return _fishingitem;
    }

    public void setFishingItem(L1ItemInstance item) {
        _fishingitem = item;
    }

    public int getCookingId() {
        return _cookingId;
    }

    public void setCookingId(int i) {
        _cookingId = i;
    }

    public int getDessertId() {
        return _dessertId;
    }

    public void setDessertId(int i) {
        _dessertId = i;
    }

    public void resetBaseDmgup() {
        int newBaseDmgup = 0;
        int newBaseBowDmgup = 0;
        if (isKnight() || isDarkelf() || isDragonKnight()) {
            newBaseDmgup = getLevel() / 10;
            newBaseBowDmgup = 0;
        } else if (isElf()) {
            newBaseDmgup = 0;
            newBaseBowDmgup = getLevel() / 10;
        }
        addDmgup(newBaseDmgup - _baseDmgup);
        addBowDmgup(newBaseBowDmgup - _baseBowDmgup);
        _baseDmgup = newBaseDmgup;
        _baseBowDmgup = newBaseBowDmgup;
    }

    public void resetBaseHitup() {
        int newBaseHitup = 0;
        int newBaseBowHitup = 0;
        // 職業命中加乘
        if (isKnight() || isWarrior() || isDarkelf()) {
            newBaseHitup = getLevel() / 6;
            newBaseBowHitup = getLevel() / 6;
        } else if (isCrown()) {
            newBaseHitup = getLevel() / 8;
            newBaseBowHitup = getLevel() / 8;
        } else if (isElf() || isIllusionist() || isDragonKnight()) {
            newBaseHitup = getLevel() / 10;
            newBaseBowHitup = getLevel() / 10;
        } else if (isWizard()) {
            newBaseHitup = getLevel() / 12;
            newBaseBowHitup = getLevel() / 12;
        }
        addHitup(newBaseHitup - _baseHitup);
        addBowHitup(newBaseBowHitup - _baseBowHitup);
        _baseHitup = newBaseHitup;
        _baseBowHitup = newBaseBowHitup;
    }

    public void resetBaseAc() {
        int newAc = CalcStat.calcAc(getType(), getLevel(), getBaseDex());
        addAc(newAc - _baseAc);
        _baseAc = newAc;
        sendPackets(new S_OwnCharAttrDef(this));
    }

    public void resetBaseMr() {
        int newMr = 0;
        if (isCrown()) {
            newMr = 10;
        } else if (isElf()) {
            newMr = 25;
        } else if (isWizard()) {
            newMr = 15;
        } else if (isDarkelf()) {
            newMr = 10;
        } else if (isDragonKnight()) {
            newMr = 18;
        } else if (isIllusionist()) {
            newMr = 20;
        }
        newMr += CalcStat.calcStatMr(getWis());
        newMr += getLevel() / 2;
        addMr(newMr - _baseMr);
        _baseMr = newMr;
    }

    public void resetLevel() {
        setLevel(ExpTable.getLevelByExp(_exp));
    }

    public void resetOriginalHpup() {
        _originalHpup = L1PcOriginal.resetOriginalHpup(this);
    }

    public void resetOriginalMpup() {
        _originalMpup = L1PcOriginal.resetOriginalMpup(this);
    }

    public void resetOriginalStrWeightReduction() {
        _originalStrWeightReduction = L1PcOriginal.resetOriginalStrWeightReduction(this);
    }

    public void resetOriginalDmgup() {
        _originalDmgup = L1PcOriginal.resetOriginalDmgup(this);
    }

    public void resetOriginalConWeightReduction() {
        _originalConWeightReduction = L1PcOriginal.resetOriginalConWeightReduction(this);
    }
    public void resetOriginalBowDmgup() {
        _originalBowDmgup = L1PcOriginal.resetOriginalBowDmgup(this);
    }

    public void resetOriginalHitup() {
        _originalHitup = L1PcOriginal.resetOriginalHitup(this);
    }

    public void resetOriginalBowHitup() {
        _originalBowHitup = L1PcOriginal.resetOriginalBowHitup(this);
    }

    public void resetOriginalMr() {
        _originalMr = L1PcOriginal.resetOriginalMr(this);
        addMr(_originalMr);
    }

    public void resetOriginalMagicHit() {
        _originalMagicHit = L1PcOriginal.resetOriginalMagicHit(this);
    }

    public void resetOriginalDiceDmg() {
        _diceDmg = 0;
    }

    public void resetOrignalDmg() {
        _dmg = 0;
    }

    public void resetOriginalMagicCritical() {
        _originalMagicCritical = L1PcOriginal.resetOriginalMagicCritical(this);
    }

    public void resetOriginalMagicConsumeReduction() {
        _originalMagicConsumeReduction = L1PcOriginal.resetOriginalMagicConsumeReduction(this);
    }

    public void resetOriginalMagicDamage() {
        _originalMagicDamage = L1PcOriginal.resetOriginalMagicDamage(this);
    }

    public void resetOriginalAc() {
        _originalAc = L1PcOriginal.resetOriginalAc(this);
        addAc(0 - _originalAc);
    }

    public void resetOriginalEr() {
        _originalEr = L1PcOriginal.resetOriginalEr(this);
    }

    //	// VIP能力資料
    //	private int _vipLevel;
    //
    //	private Timestamp _startTime;
    //
    //	private Timestamp _endTime;
    public void resetOriginalHpr() {
        _originalHpr = L1PcOriginal.resetOriginalHpr(this);
    }

    public void resetOriginalMpr() {
        _originalMpr = L1PcOriginal.resetOriginalMpr(this);
    }

    public void refresh() {
        resetLevel();
        resetBaseHitup();
        resetBaseDmgup();
        resetBaseMr();
        resetBaseAc();
        resetOriginalHpup();
        resetOriginalMpup();
        resetOriginalDmgup();
        resetOriginalBowDmgup();
        resetOriginalHitup();
        resetOriginalBowHitup();
        resetOriginalMr();
        resetOriginalDiceDmg();
        resetOrignalDmg();
        resetOriginalMagicHit();
        resetOriginalMagicCritical();
        resetOriginalMagicConsumeReduction();
        resetOriginalMagicDamage();
        resetOriginalAc();
        resetOriginalEr();
        resetOriginalHpr();
        resetOriginalMpr();
        resetOriginalStrWeightReduction();
        resetOriginalConWeightReduction();
    }

    /**
     * 傳回一個信件的黑名單 // 7.6
     *
     */
    // public L1ExcludingMailList getExcludingMailList() {
    // return _excludingMailList;
    // }
    public int getTeleportX() {
        return _teleportX;
    }

    public void setTeleportX(int i) {
        _teleportX = i;
    }

    public int getTeleportY() {
        return _teleportY;
    }

    public void setTeleportY(int i) {
        _teleportY = i;
    }

    public short getTeleportMapId() {
        return _teleportMapId;
    }

    //	public Timestamp getVipStartTime() {
    //		return _startTime;
    //	}
    //
    //	public void setVipStartTime(final Timestamp vipStartTime) {
    //		_startTime = vipStartTime;
    //	}
    //
    //	public Timestamp getVipEndTime() {
    //		return _endTime;
    //	}
    //
    //	public void setVipEndTime(final Timestamp vipEndTime) {
    //		_endTime = vipEndTime;
    //	}
    //
    //	public void setVipStatus() {
    //
    //		if ((_startTime != null) && (_endTime != null)) {
    //
    //			final long t = _endTime.getTime() - System.currentTimeMillis();
    //			if (t > 0L) {
    //
    //				final L1Vip tmp = VipSetsTable._list_vip.get(_vipLevel);
    //				if (tmp != null) {
    //
    //					addMaxHp(tmp.get_add_hp());
    //					addHpr(tmp.get_add_hpr());
    //					addMaxMp(tmp.get_add_mp());
    //					addMpr(tmp.get_add_mpr());
    //					addDmgup(tmp.get_add_dmg());
    //					addBowDmgup(tmp.get_add_bowdmg());
    //					addHitup(tmp.get_add_hit());
    //					addBowHitup(tmp.get_add_bowhit());
    //					addSp(tmp.get_add_sp());
    //					addMr(tmp.get_add_mr());
    //					addStr(tmp.getStr());
    //					addDex(tmp.getDex());
    //					addCon(tmp.getCon());
    //					addWis(tmp.getWis());
    //					addCha(tmp.getCha());
    //					addInt(tmp.getInt());
    //					set_expadd(tmp.getExpAdd());
    //				} else {
    //					this.sendPackets(new S_SystemMessage("VIP能力錯誤，請告知線上GM處理。"));
    //				}
    //
    //				sendPackets(new S_VipTime(_vipLevel, _startTime.getTime(), _endTime.getTime()));
    //				sendPackets(new S_VipShow(getId(), get_vipLevel()));
    //				sendPackets(new S_OwnCharStatus(this));
    //				sendPackets(new S_SPMR(this));
    //				setSkillEffect(L1SkillId.VIP, (int) t);
    //			} else {
    //				_startTime = null;
    //				_endTime = null;
    //				setVipStartTime(_startTime);
    //				setVipEndTime(_endTime);
    //				set_vipLevel(0);
    //				try {
    //					saveVip();
    //				} catch (Exception e) {
    //					_log.info(getName() + " 清除VIP發生錯誤:" + e.getMessage());
    //				}
    //				System.out.println(getName() + " vip到期清除");
    //
    //			}
    //		}
    //	}
    //
    //	public void addVipStatus(final int dayCount, final int level) {
    //		if ((_endTime != null) && ((_endTime.getTime() - System.currentTimeMillis()) > 0L)) {
    //			removeSkillEffect(L1SkillId.VIP);
    //		}
    //		final long t = System.currentTimeMillis();
    //		_startTime = new Timestamp(t);
    //		_endTime = new Timestamp(t + (86400000L * dayCount));
    //		_vipLevel = level;
    //
    //		setVipStatus();
    //	}
    //
    //	public void endVipStatus() {
    //		final L1Vip tmp = VipSetsTable._list_vip.get(_vipLevel);
    //		if (tmp != null) {
    //			addMaxHp(-tmp.get_add_hp());
    //			addHpr(-tmp.get_add_hpr());
    //			addMaxMp(-tmp.get_add_mp());
    //			addMpr(-tmp.get_add_mpr());
    //			addDmgup(-tmp.get_add_dmg());
    //			addBowDmgup(-tmp.get_add_bowdmg());
    //			addHitup(-tmp.get_add_hit());
    //			addBowHitup(-tmp.get_add_bowhit());
    //			addSp(-tmp.get_add_sp());
    //			addMr(-tmp.get_add_mr());
    //			addStr(-tmp.getStr());
    //			addDex(-tmp.getDex());
    //			addCon(-tmp.getCon());
    //			addWis(-tmp.getWis());
    //			addCha(-tmp.getCha());
    //			addInt(-tmp.getInt());
    //			set_expadd(-tmp.getExpAdd());
    //		} else {
    //			this.sendPackets(new S_SystemMessage("VIP能力錯誤，請告知線上GM處理。"));
    //		}
    //
    //		sendPackets(new S_VipTime(0, 0L, 0L));
    //		sendPacketsAll(new S_VipShow(getId(), 0));
    //		sendPackets(new S_OwnCharStatus(this));
    //		sendPackets(new S_SPMR(this));
    //
    //		_startTime = null;
    //		_endTime = null;
    //		_vipLevel = 0;
    //	}
    //	 VIP END
    public void setTeleportMapId(short i) {
        _teleportMapId = i;
    }

    public int getTeleportHeading() {
        return _teleportHeading;
    }

    public void setTeleportHeading(int i) {
        _teleportHeading = i;
    }

    public int getTempCharGfxAtDead() {
        return _tempCharGfxAtDead;
    }

    private void setTempCharGfxAtDead(int i) {
        _tempCharGfxAtDead = i;
    }

    public boolean attackme() {
        return this._attackme;
    }

    public void setattackme(final boolean _attackme) {
        this._attackme = _attackme;
    }

    public boolean attackhe() {
        return this._attackhe;
    }

    public void setattackhe(final boolean _attackhe) {
        this._attackhe = _attackhe;
    }

    public boolean armorme() {
        return this._armorme;
    }

    public void setarmorme(final boolean _armorme) {
        this._armorme = _armorme;
    }

    public boolean armorhe() {
        return this._armorhe;
    }

    public void setarmorhe(final boolean _armorhe) {
        this._armorhe = _armorhe;
    }

    public boolean droplist() {
        return this._droplist;
    }

    public void setdroplist(final boolean _droplist) {
        this._droplist = _droplist;
    }

    public boolean kill() {
        return this._kill;
    }

    public void setkill(final boolean _kill) {
        this._kill = _kill;
    }

    /**
     * 增加古文字魔法武器傷害
     *
     */
    public void addweaponMD(int weaponMD) {
        _weaponMD += weaponMD;
    }

    public int getweaponMD() {
        return _weaponMD;
    }

    /**
     * 增加古文字魔法武器機率
     *
     */
    public void addweaponMDC(int weaponMDC) {
        _weaponMDC += weaponMDC;
    }

    public int getweaponMDC() {
        return _weaponMDC;
    }

    /**
     * 增加古文字魔法武器機率
     *
     */
    public void addreducedmg(int reducedmg) {
        _reducedmg += reducedmg;
    }

    public int getreducedmg() {
        int damageReduction = 0;
        if (_reducedmg > 10) {
            damageReduction = 10 + _random.nextInt(_reducedmg - 10) + 1;
        } else {
            damageReduction = _reducedmg;
        }
        return damageReduction;
    }

    /**
     * 增加古文字魔法武器機率
     *
     */
    public void addreduceMdmg(int reduceMdmg) {
        _reduceMdmg += reduceMdmg;
    }

    public int getreduceMdmg() {
        int MdamageReduction = 0;
        if (_reduceMdmg > 10) {
            MdamageReduction = 10 + _random.nextInt(_reduceMdmg - 10) + 1;
        } else {
            MdamageReduction = _reduceMdmg;
        }
        return MdamageReduction;
    }

    /**
     * 全秘密語(收聽)
     *
     * @return flag true:接收 false:拒絕
     */
    public boolean isCanWhisper() {
        return _isCanWhisper;
    }

    public void setCanWhisper(boolean flag) {
        _isCanWhisper = flag;
    }

    public boolean isShowTradeChat() {
        return _isShowTradeChat;
    }

    public void setShowTradeChat(boolean flag) {
        _isShowTradeChat = flag;
    }

    public boolean isShowWorldChat() {
        return _isShowWorldChat;
    }

    public void setShowWorldChat(boolean flag) {
        _isShowWorldChat = flag;
    }

    public int getFightId() {
        return _fightId;
    }

    public void setFightId(int i) {
        _fightId = i;
    }

    public void checkChatInterval() {
        long nowChatTimeInMillis = System.currentTimeMillis();
        if (_chatCount == 0) {
            _chatCount = (byte) (_chatCount + 1);
            _oldChatTimeInMillis = nowChatTimeInMillis;
            return;
        }
        long chatInterval = nowChatTimeInMillis - _oldChatTimeInMillis;
        if (chatInterval > 2000L) {
            _chatCount = 0;
            _oldChatTimeInMillis = 0L;
        } else {
            if (_chatCount >= 3) {
                setSkillEffect(4002, 120000);
                sendPackets(new S_PacketBox(36, 120));
                sendPackets(new S_ServerMessage(153));
                _chatCount = 0;
                _oldChatTimeInMillis = 0L;
            }
            _chatCount = (byte) (_chatCount + 1);
        }
    }

    public int getCallClanId() {
        return _callClanId;
    }

    public void setCallClanId(int i) {
        _callClanId = i;
    }

    public int getCallClanHeading() {
        return _callClanHeading;
    }

    public void setCallClanHeading(int i) {
        _callClanHeading = i;
    }

    public boolean isInCharReset() {
        return _isInCharReset;
    }

    public void setInCharReset(boolean flag) {
        _isInCharReset = flag;
    }

    public int getTempLevel() {
        return _tempLevel;
    }

    public void setTempLevel(int i) {
        _tempLevel = i;
    }

    public int getTempMaxLevel() {
        return _tempMaxLevel;
    }

    public void setTempMaxLevel(int i) {
        _tempMaxLevel = i;
    }

    public boolean isSummonMonster() {
        return _isSummonMonster;
    }

    public void setSummonMonster(boolean SummonMonster) {
        _isSummonMonster = SummonMonster;
    }

    /**
     * 是否展開變身控制選單
     *
     */
    public boolean isShapeChange() {
        return _isShapeChange;
    }

    /**
     * 設定是否展開變身控制選單
     *
     */
    public void setShapeChange(boolean isShapeChange) {
        _isShapeChange = isShapeChange;
    }

    /**
     * 設定暫存文字串(收件者)
     */
    public String getText() {
        return _text;
    }

    public void setText(String text) {
        _text = text;
    }

    public byte[] getTextByte() {
        return _textByte;
    }

    /**
     * 記時地圖ID
     */
    // private int timemapid;
    public void setTextByte(byte[] textByte) {
        _textByte = textByte;
    }

    /**
     * 記時地圖計時器
     */
    // private TimeMap timemap;
    // /** 記時地圖ID */
    // public int getTimemapid() {
    // return timemapid;
    // }
    //
    // /** 記時地圖ID */
    // public void setTimemapid(int timemapid) {
    // this.timemapid = timemapid;
    // }
    public L1PcOther get_other() {
        return _other;
    }

    public void set_other(L1PcOther other) {
        _other = other;
    }

    // /** 記時地圖記時器 */
    // public TimeMap getTimemap() {
    // return timemap;
    // }
    //
    // /** 記時地圖記時器 */
    // public void setTimemap(TimeMap timemap) {
    // this.timemap = timemap;
    // }
    public L1PcOtherList get_otherList() {
        return _otherList;
    }

    public void set_otherList(L1PcOtherList other) {
        _otherList = other;
    }

    public int getOleLocX() {
        return _oleLocX;
    }

    public void setOleLocX(int oleLocx) {
        _oleLocX = oleLocx;
    }

    public int getOleLocY() {
        return _oleLocY;
    }

    public void setOleLocY(int oleLocy) {
        _oleLocY = oleLocy;
    }

    public L1PcInstance getNowTarget() {
        return _target;
    }

    public void setNowTarget(L1PcInstance target) {
        _target = target;
    }

    /**
     * 設置目前攻擊對像
     *
     */
    public void setNowTarget(final L1Character cha) {
        this._aiTarget = cha;
    }

    public void setPetModel() {
        try {
            for (L1NpcInstance petNpc : getPetList().values()) {
                if (petNpc != null) {
                    if (petNpc instanceof L1SummonInstance) {
                        L1SummonInstance summon = (L1SummonInstance) petNpc;
                        summon.set_tempModel();
                    } else if (petNpc instanceof L1PetInstance) {
                        L1PetInstance pet = (L1PetInstance) petNpc;
                        pet.set_tempModel();
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void getPetModel() {
        try {
            for (L1NpcInstance petNpc : getPetList().values()) {
                if (petNpc != null) {
                    if (petNpc instanceof L1SummonInstance) {
                        L1SummonInstance summon = (L1SummonInstance) petNpc;
                        summon.get_tempModel();
                    } else if (petNpc instanceof L1PetInstance) {
                        L1PetInstance pet = (L1PetInstance) petNpc;
                        pet.get_tempModel();
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public L1DeInstance get_outChat() {
        return _outChat;
    }

    /**
     * 設定對話輸出
     *
     */
    public void set_outChat(L1DeInstance b) {
        _outChat = b;
    }

    public long get_h_time() {
        return _h_time;
    }

    public void set_h_time(long time) {
        _h_time = time;
    }

    public boolean is_mazu() {
        return _mazu;
    }

    public void set_mazu(boolean b) {
        _mazu = b;
    }

    /**
     * 媽祖祝福時間
     *
     */
    public int get_mazu_time() {
        return _mazu_time;
    }

    public void set_mazu_time(int time) {
        _mazu_time = time;
    }

    /**
     * 機率增加傷害
     *
     * @param int2 發動機率
     */
    public void set_dmgAdd(int int1, int int2) {
        _int1 += int1;// 增加傷害
        _int2 += int2;// 發動機率
    }

    public int dmgAdd() {
        if (_int2 == 0) {
            return 0;
        }
        if (_random.nextInt(1000) + 1 <= _int2) {
            // 爆擊觸發，所有玩家都看到特效
            this.sendPacketsAll(new S_SkillSound(this.getId(), 16117));

            // 娃娃動作
            if (!getDolls().isEmpty()) {
                for (L1DollInstance doll : getDolls().values()) {
                    doll.show_action(1);
                }
            }
            if (!getDolls2().isEmpty()) {
                for (L1DollInstance2 doll : getDolls2().values()) {
                    doll.show_action(1);
                }
            }
            return _int1;
        }
        return 0;
    }


    /**
     * 取回完全閃避率 亂數1000
     */
    public int get_evasion() {
        return _evasion;
    }

    /**
     * 增加完全閃避率 亂數1000
     */
    public void set_evasion(int int1) {
        _evasion += int1;
    }

    /**
     * 增加經驗值
     */
    public void set_expadd(int int1) {
        _expadd += int1 / 100.0D;
    }

    public double getExpAdd() {
        return _expadd;
    }

    /**
     * 隨機傷害減免
     *
     * @param int1 減免質
     * @param int2 發動機率
     */
    public void set_dmgDowe(int int1, int int2) {
        _dd1 += int1;
        _dd2 += int2;
    }

    /**
     * 隨機傷害減免
     */
    public int dmgDowe() {
        if (this.has_powerid(6611)) {// 附魔系統 隨機傷害減免
            int rad = 10;// 機率
            if (_random.nextInt(100) < rad) {
                this.sendPacketsAll(new S_SkillSound(this.getId(), 9800));
                return _dd1;
            }
        }
        if (_dd2 == 0) {
            return 0;
        }
        if (_random.nextInt(100) + 1 <= _dd2) {
            if (!getDolls().isEmpty()) {
                for (L1DollInstance doll : getDolls().values()) {
                    doll.show_action(2);
                }
            }
            if (!getDolls2().isEmpty()) {
                for (L1DollInstance2 doll : getDolls2().values()) {
                    doll.show_action(2);
                }
            }
            return _dd1;
        }
        return 0;
    }

    public boolean isFoeSlayer() {
        return _isFoeSlayer;
    }

    public void setFoeSlayer(boolean FoeSlayer) {
        _isFoeSlayer = FoeSlayer;
    }

    /**
     * 弱點曝光時間
     *
     */
    public long get_weaknss_t() {
        return _weaknss_t;
    }

    /**
     * 弱點曝光階段
     *
     */
    public int get_weaknss() {
        return _weaknss;
    }

    /**
     * 弱點曝光階段
     *
     */
    public void set_weaknss(int lv, long t) {
        _weaknss = lv;
        _weaknss_t = t;
    }

    /**
     * 角色表情動作代號
     *
     */
    public int get_actionId() {
        return _actionId;
    }
    /**
     * 角色表情動作代號
     *
     */
    public void set_actionId(int actionId) {
        _actionId = actionId;
    }

    public Chapter01R get_hardinR() {
        return _hardin;
    }

    public void set_hardinR(Chapter01R hardin) {
        _hardin = hardin;
    }

    /**
     * 穿上附魔裝備處理
     */
    public void add_power(L1ItemPower_text value, L1ItemInstance eq) {
        if (!_allpowers.containsKey(value.get_id())) {
            _allpowers.put(value.get_id(), value);
        }
        if (eq.isEquipped()) {
            value.add_pc_power(this);
            sendPackets(new S_ServerMessage("\\fW獲得" + value.getMsg() + " 效果"));
        }
        if (value.getGfx() != null) {
            for (int gfx : value.getGfx()) {
                sendPacketsAll(new S_SkillSound(getId(), gfx));
            }
        }
    }

    /**
     * 脫下附魔裝備處理
     */
    public void remove_power(L1ItemPower_text value, L1ItemInstance eq) {
        _allpowers.remove(value.get_id());
        if (!eq.isEquipped()) {
            value.remove_pc_power(this);
            sendPackets(new S_ServerMessage("\\fY失去 " + value.getMsg() + " 效果"));
        }
    }

    /**
     * 是否含有特定編號的附魔效果
     */
    public boolean has_powerid(int powerid) {
        return _allpowers.containsKey(powerid);
    }

    /**
     * 取回人物身上所有附魔效果
     */
    public Map<Integer, L1ItemPower_text> get_allpowers() {
        return _allpowers;
    }

    public int get_unfreezingTime() {
        return _unfreezingTime;
    }

    public void set_unfreezingTime(int i) {
        _unfreezingTime = i;
    }

    public int get_misslocTime() {
        return _misslocTime;
    }

    public void set_misslocTime(int i) {
        _misslocTime = i;
    }

    public L1User_Power get_c_power() {
        return _c_power;
    }

    public void set_c_power(L1User_Power power) {
        _c_power = power;
    }

    /**
     * 增加吸血
     */
    public void add_dice_hp(int dice_hp, int sucking_hp) {
        _dice_hp += dice_hp;// 機率-吸血
        _sucking_hp += sucking_hp;// 機率-吸血機率
    }

    public int dice_hp() {
        return _dice_hp;
    }

    public int sucking_hp() {
        return _sucking_hp;
    }

    /**
     * 增加機率吸魔
     *
     */
    public void add_dice_mp(int dice_mp, int sucking_mp) {
        _dice_mp += dice_mp;
        _sucking_mp += sucking_mp;
    }

    public int dice_mp() {
        return _dice_mp;
    }

    public int sucking_mp() {
        return _sucking_mp;
    }

    public void add_double_dmg(int double_dmg) {
        _double_dmg += double_dmg;
    }

    public int get_double_dmg() {
        return _double_dmg;
    }

    /**
     * 增加機率防禦解除
     *
     */
    public void add_lift(int lift) {
        _lift += lift;
    }

    public int lift() {
        return _lift;
    }

    public void add_magic_modifier_dmg(int add) {
        _magic_modifier_dmg += add;
    }

    public int get_magic_modifier_dmg() {
        return _magic_modifier_dmg;
    }

    public void add_magic_reduction_dmg(int add) {
        _magic_reduction_dmg += add;
    }

    public int get_magic_reduction_dmg() {
        return _magic_reduction_dmg;
    }

    /**
     * 重設名稱
     *
     */
    public void rename(boolean b) {
        _rname = b;
    }

    /**
     * 重設名稱
     *
     */
    public boolean is_rname() {
        return _rname;
    }

    /**
     * 重設封號
     *
     */
    public boolean is_retitle() {
        return _retitle;
    }

    /**
     * 重設封號
     *
     */
    public void retitle(boolean b) {
        _retitle = b;
    }

    public int is_repass() {
        return _repass;
    }

    public void repass(int b) {
        _repass = b;
    }

    public int get_mode_id() {
        return _mode_id;
    }

    public void set_mode_id(int mode) {
        _mode_id = mode;
    }

    public boolean get_check_item() {
        return _check_item;
    }

    public void set_check_item(boolean b) {
        _check_item = b;
    }

    public void set_VIP1(boolean b) {
        _vip_1 = b;
    }

    public void set_VIP2(boolean b) {
        _vip_2 = b;
    }

    public void set_VIP3(boolean b) {
        _vip_3 = b;
    }

    public void set_VIP4(boolean b) {
        _vip_4 = b;
    }

    public long get_global_time() {
        return _global_time;
    }

    public void set_global_time(long global_time) {
        _global_time = global_time;
    }

    public int get_doll_hpr() {
        return _doll_hpr;
    }

    public void set_doll_hpr(int hpr) {
        _doll_hpr = hpr;
    }

    public int get_doll_hpr_time() {
        return _doll_hpr_time;
    }

    public void set_doll_hpr_time(int time) {
        _doll_hpr_time = time;
    }

    /**
     * 清除無效目標<BR>
     * 搜尋新目標
     */
    /*
     * private void clearTagert() { // System.out.println("清除無效目標 搜尋新目標"); if
     * (_AItarget != null) { // 清除目標 tagertClear(); }
     *
     * if (!_hateList.isEmpty()) { _AItarget = _hateList.getMaxHateCharacter();
     * checkTarget(); } }
     */
    public int get_doll_hpr_time_src() {
        return _doll_hpr_time_src;
    }

    /**
     * 現在目標消除(hateList)
     */
    /*
     * private void tagertClear() { // System.out.println("現在目標消除(hateList)"); if
     * (this._AItarget == null) { return; } this._hateList.remove(this._AItarget);
     * this._AItarget = null; }
     */
    public void set_doll_hpr_time_src(int time) {
        _doll_hpr_time_src = time;
    }

    public int get_doll_mpr() {
        return _doll_mpr;
    }

    public void set_doll_mpr(int mpr) {
        _doll_mpr = mpr;
    }

    public int get_doll_mpr_time() {
        return _doll_mpr_time;
    }

    public void set_doll_mpr_time(int time) {
        _doll_mpr_time = time;
    }
    // 自動攻擊end

    public int get_doll_mpr_time_src() {
        return _doll_mpr_time_src;
    }

    public void set_doll_mpr_time_src(int time) {
        _doll_mpr_time_src = time;
    }

    public int[] get_doll_get() {
        return _doll_get;
    }

    public void set_doll_get(int itemid, int count) {
        _doll_get[0] = itemid;
        _doll_get[1] = count;
    }

    public int get_doll_get_time() {
        return _doll_get_time;
    }

    // private L1MonsterInstance searchTarget(L1PcInstance pc) {
    // System.out.println("AI啟動55555");
    // L1MonsterInstance targetPlayer = null;
    //
    // for (final L1Object npc : World.get().getVisibleObjects(pc)) {
    // try {
    // TimeUnit.MILLISECONDS.sleep(10);
    // } catch (InterruptedException e) {
    // _log.error(e.getLocalizedMessage(), e);
    // }
    // if (npc instanceof L1MonsterInstance) {
    // final L1MonsterInstance mob = (L1MonsterInstance) npc;
    // if (mob.isDead()) {
    // continue;
    // }
    // if (mob.getCurrentHp() <= 0) {
    // continue;
    // }
    // if (mob.getHiddenStatus() > 0) {
    // continue;
    // }
    // if (mob.getAtkspeed() == 0) {
    // continue;
    // }
    // if(mob.hasSkillEffect(this.getId() + 100000)) {//暫不攻擊狀態 hjx1000
    // continue;
    // }
    //
    // targetPlayer = mob;
    // }
    // }
    // return targetPlayer;
    // }
    public void set_doll_get_time(int time) {
        _doll_get_time = time;
    }

    public int get_doll_get_time_src() {
        return _doll_get_time_src;
    }

    public void set_doll_get_time_src(int time) {
        _doll_get_time_src = time;
    }

    public String get_board_title() {
        return _board_title;
    }

    public void set_board_title(String text) {
        _board_title = text;
    }

    public String get_board_content() {
        return _board_content;
    }

    public void set_board_content(String text) {
        _board_content = text;
    }

    public long get_spr_move_time() {
        return _spr_move_time;
    }

    public void set_spr_move_time(long spr_time) {
        _spr_move_time = spr_time;
    }

    public long get_spr_attack_time() {
        return _spr_attack_time;
    }

    public void set_spr_attack_time(long spr_time) {
        _spr_attack_time = spr_time;
    }

    public long get_spr_skill_time() {
        return _spr_skill_time;
    }

    public void set_spr_skill_time(long spr_time) {
        _spr_skill_time = spr_time;
    }

    public int get_delete_time() {
        return _delete_time;
    }

    public void set_delete_time(int time) {
        _delete_time = time;
    }

    /**
     * 增加藥水回復量%
     *
     */
    public void add_up_hp_potion(int up_hp_potion) {
        _up_hp_potion += up_hp_potion;
    }

    public int get_up_hp_potion() {
        return _up_hp_potion;
    }

    /**
     * 增加藥水回復指定量
     *
     */
    public void add_uhp_number(int uhp_number) {
        _uhp_number += uhp_number;
    }

    public int get_uhp_number() {
        return _uhp_number;
    }

    public int get_venom_resist() {
        return _venom_resist;
    }

    public void set_venom_resist(int i) {
        _venom_resist += i;
    }

    /**
     * 加入邀請列表
     *
     */
    public void addInviteList(String playername) {
        if (_InviteList.contains(playername)) {
            return;
        }
        _InviteList.add(playername);
    }

    /**
     * 從邀請列表中移除
     *
     */
    public void removeInviteList(String name) {
        if (!_InviteList.contains(name)) {
            return;
        }
        _InviteList.remove(name);
    }

    /**
     * 傳回邀請列表
     *
     */
    public ArrayList<String> getInviteList() {
        return _InviteList;
    }

    /**
     * 加入血盟申請列表
     *
     * @param clanname 血盟名稱
     */
    public void addCMAList(String clanname) {
        if (_cmalist.contains(clanname)) {
            return;
        }
        _cmalist.add(clanname);
    }

    /**
     * 移除血盟申請列表
     *
     * @param name 申請人名稱
     */
    public void removeCMAList(String name) {
        if (!_cmalist.contains(name)) {
            return;
        }
        _cmalist.remove(name);
    }

    /**
     * 傳回血盟申請列表
     *
     */
    public ArrayList<String> getCMAList() {
        return _cmalist;
    }

    /**
     * 顯示盟徽：移除守護者限制，若無血盟則不顯示
     */
    public final int getEmblemId() {
        if (getClanid() <= 0) {
            return 0;
        }
        L1Clan clan = getClan();
        if (clan == null) {
            return 0;
        }
        // 如果有上傳過盟徽，使用 emblemId；否則使用 clanId
        int emblemId = clan.getEmblemId();
        if (emblemId == 0) {
            // 檢查是否有盟徽資料
            L1EmblemIcon emblemIcon = ClanEmblemReading.get().get(clan.getClanId());
            if (emblemIcon != null) {
                // 有盟徽資料，使用 clanId 作為 emblemId
                return clan.getClanId();
            }
            return 0; // 沒有盟徽資料
        }
        return emblemId;
    }

    public AcceleratorChecker speed_Attack() {
        return _speed;
    }

    public int get_arena() {
        return _arena;
    }

    public void set_arena(int i) {
        _arena = i;
    }

    public int get_temp_adena() {
        return _temp_adena;
    }

    public void set_temp_adena(int itemid) {
        _temp_adena = itemid;
    }

    public long get_ss_time() {
        return _ss_time;
    }

    public void set_ss_time(long ss_time) {
        _ss_time = ss_time;
    }

    public void set_ss_time(int ss) {
        _ss = ss;
    }

    public int get_ss() {
        return _ss;
    }

    public final int getKillCount() {
        return killCount;
    }

    public final void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    public int getMeteLevel() {
        return _meteLevel;
    }

    public void setMeteLevel(int i) {
        _meteLevel = i;
    }

    public final L1MeteAbility getMeteAbility() {
        return _meteAbility;
    }

    public final void resetMeteAbility() {
        if (_meteAbility != null) {
            ExtraMeteAbilityTable.effectBuff(this, _meteAbility, -1);
        }
        _meteAbility = ExtraMeteAbilityTable.getInstance().get(getMeteLevel(), getType());
        if (_meteAbility != null) {
            ExtraMeteAbilityTable.effectBuff(this, _meteAbility, 1);
        }
    }

    /**
     * 是否有真妲蒂斯魔石效果
     *
     */
    public final boolean isEffectDADIS() {
        return _EffectDADIS;
    }

    /**
     * 給予真妲蒂斯魔石效果
     *
     */
    public final void setDADIS(boolean checkFlag) {
        if (_EffectDADIS != checkFlag) {
            giveDADIS(checkFlag);
            sendPackets(new S_HPUpdate(this));
            if (isInParty()) {
                getParty().updateMiniHP(this);
            }
            sendPackets(new S_MPUpdate(this));
            sendPackets(new S_SPMR(this));
            L1PcUnlock.Pc_Unlock(this);
        }
    }

    /**
     * 給予真妲蒂斯魔石效果
     *
     */
    public final void giveDADIS(boolean checkFlag) {
        _EffectDADIS = checkFlag;
        if (checkFlag) {
            addMaxHp(100);
            addMaxMp(100);
            addDmgup(5);
            addBowDmgup(5);
            addSp(5);
            addDamageReductionByArmor(5);
            addHpr(5);
            addMpr(5);
            sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 553));// 開啟妲蒂斯魔石圖示
        } else {
            addMaxHp(-100);
            addMaxMp(-100);
            addDmgup(-5);
            addBowDmgup(-5);
            addSp(-5);
            addDamageReductionByArmor(-5);
            addHpr(-5);
            addMpr(-5);
            sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 553));// 關閉妲蒂斯魔石圖示
        }
    }

    /**
     * 是否有妲蒂斯魔石效果
     *
     */
    public final boolean isEffectGS() {
        return _EffectGS;
    }

    /**
     * 給予妲蒂斯魔石效果
     *
     */
    public final void setGS(boolean checkFlag) {
        if (_EffectGS != checkFlag) {
            giveGS(checkFlag);
            sendPackets(new S_HPUpdate(this));
            if (isInParty()) {
                getParty().updateMiniHP(this);
            }
            sendPackets(new S_MPUpdate(this));
            sendPackets(new S_SPMR(this));
            L1PcUnlock.Pc_Unlock(this);
        }
    }

    /**
     * 給予妲蒂斯魔石效果
     *
     */
    public final void giveGS(boolean checkFlag) {
        _EffectGS = checkFlag;
        if (checkFlag) {
            addMaxHp(30);
            addMaxMp(30);
            addDmgup(2);
            addBowDmgup(2);
            addSp(2);
            addDamageReductionByArmor(2);
            addHpr(2);
            addMpr(2);
            sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 553));// 開啟妲蒂斯魔石圖示
        } else {
            addMaxHp(-30);
            addMaxMp(-30);
            addDmgup(-2);
            addBowDmgup(-2);
            addSp(-2);
            addDamageReductionByArmor(-2);
            addHpr(-2);
            addMpr(-2);
            sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 553));// 關閉妲蒂斯魔石圖示
        }
    }

    /**
     * 是否有守護者的靈魂效果
     *
     */
    public final boolean isProtector() {
        return _isProtector;
    }

    /**
     * 給予守護者的靈魂效果
     *
     */
    public final void setProtector(boolean checkFlag) {
        if (_isProtector != checkFlag) {
            giveProtector(checkFlag);
            // sendPackets(new S_OwnCharPack(this)); //
            sendPackets(new S_HPUpdate(this));
            if (isInParty()) {
                getParty().updateMiniHP(this);
            }
            sendPackets(new S_MPUpdate(this));
            sendPackets(new S_SPMR(this));
            L1PcUnlock.Pc_Unlock(this);
        }
    }

    /**
     * 守護者靈魂效果
     *
     */
    public final void giveProtector(boolean checkFlag) {
        _isProtector = checkFlag;
        if (checkFlag) {
            addMaxHp(ProtectorSet.HP_UP);
            addMaxMp(ProtectorSet.MP_UP);
            addDmgup(ProtectorSet.DMG_UP);
            addBowDmgup(ProtectorSet.DMG_UP);
            addDamageReductionByArmor(ProtectorSet.DMG_DOWN);
            addSp(ProtectorSet.SP_UP);
            sendPackets(new S_PacketBox(S_PacketBox.ICON_SOUL_GUARDIAN, 1));// 守護者狀態 PackBox=144
        } else {
            addMaxHp(-ProtectorSet.HP_UP);
            addMaxMp(-ProtectorSet.MP_UP);
            addDmgup(-ProtectorSet.DMG_UP);
            addBowDmgup(-ProtectorSet.DMG_UP);
            addDamageReductionByArmor(-ProtectorSet.DMG_DOWN);
            addSp(-ProtectorSet.SP_UP);
            sendPackets(new S_PacketBox(S_PacketBox.ICON_SOUL_GUARDIAN, 0));
        }
    }

    /**
     * 是否有戰神之魂的效果
     *
     */
    public final boolean isMars() {
        return _isMars;
    }

    /**
     * 給予戰神之魂的效果
     *
     */
    public final void setMars(boolean checkFlag) {
        if (_isMars != checkFlag) {
            giveMars(checkFlag);
            sendPackets(new S_HPUpdate(this));
            if (isInParty()) {
                getParty().updateMiniHP(this);
            }
            sendPackets(new S_MPUpdate(this));
            sendPackets(new S_SPMR(this));
            L1PcUnlock.Pc_Unlock(this);
        }
    }

    /**
     * 給予戰神之魂的效果
     *
     */
    public final void giveMars(boolean checkFlag) {
        _isMars = checkFlag;
        if (checkFlag) {
            addMaxHp(120);
            addMaxMp(100);
            addDmgup(15);
            addBowDmgup(15);
            addDamageReductionByArmor(8);
            addSp(5);
            sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 619));// 開啟戰神之魂圖示
        } else {
            addMaxHp(-120);
            addMaxMp(-100);
            addDmgup(-15);
            addBowDmgup(-15);
            addDamageReductionByArmor(-8);
            addSp(-5);
            sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 619));// 關閉戰神之魂圖示
        }
    }

    public final L1Apprentice getApprentice() {
        return _apprentice;
    }

    public final void setApprentice(L1Apprentice apprentice) {
        _apprentice = apprentice;
    }
    public final void checkEffect() {
        int checkType = 0;
        if (getApprentice() != null) {
            L1PcInstance master = World.get().getPlayer(getApprentice().getMaster().getName());
            if (master != null) {
                L1Party party = getParty();
                if (party != null) {
                    checkType = party.checkMentor(getApprentice());
                } else {
                    checkType = 1;
                }
            }
        }
        // System.out.println("checkType: "+checkType);
        // System.out.println("_tempType: "+_tempType!=null?_tempType:"NULL");
        if (_tempType != checkType) {
            // 先還原狀態
            if (checkType > 0) {
                sendEffectBuff(_tempType, -1);
            }
            // 再更新狀態
            if (checkType > 0) {
                sendEffectBuff(checkType, 1);
            }
            sendPackets(new S_SPMR(this));
            sendPackets(new S_OwnCharStatus(this));
            sendPackets(new S_PacketBox(132, getEr()));
            if (checkType <= 0) {
                sendPackets(new S_PacketBox(147, 0, Math.max(_tempType - 1, 0)));
            } else {
                sendPackets(new S_PacketBox(147, checkType == 0 ? 0 : 1, Math.max(checkType - 1, 0)));
            }
            _tempType = checkType;
            // System.out.println("_tempType:
            // "+_tempType!=null?_tempType:"NULL");
        }
    }

    public void addOriginalEr(int i) {
        _originalEr += i;
    }

    private final void sendEffectBuff(int buffType, int negative) {
        switch (buffType) {
            case 1:
                addAc(-1 * negative);
                break;
            case 2:
                addAc(-1 * negative);
                addMr(1 * negative);
                break;
            case 3:
                addAc(-1 * negative);
                addMr(1 * negative);
                addWater(2 * negative);
                addWind(2 * negative);
                addFire(2 * negative);
                addEarth(2 * negative);
                break;
            case 4:
                addAc(-1 * negative);
                addMr(1 * negative);
                addWater(2 * negative);
                addWind(2 * negative);
                addFire(2 * negative);
                addEarth(2 * negative);
                addOriginalEr(1 * negative);
                break;
            case 5:
                addAc(-3 * negative);
                break;
            case 6:
                addAc(-3 * negative);
                addMr(3 * negative);
                break;
            case 7:
                addAc(-3 * negative);
                addMr(3 * negative);
                addWater(6 * negative);
                addWind(6 * negative);
                addFire(6 * negative);
                addEarth(6 * negative);
                break;
            case 8:
                addAc(-3 * negative);
                addMr(3 * negative);
                addWater(6 * negative);
                addWind(6 * negative);
                addFire(6 * negative);
                addEarth(6 * negative);
                addOriginalEr(3 * negative);
        }
    }

    public final Timestamp getPunishTime() {
        return _punishTime;
    }

    public final void setPunishTime(Timestamp timestamp) {
        _punishTime = timestamp;
    }

    public String getViewName() {
        return _viewName != null ? _viewName : getName();
    }

    public void setViewName(String name) {
        _viewName = name;
    }

    public final String getRealName() {
        StringBuffer sbr = new StringBuffer();
        sbr.append(getName());
        return sbr.toString();
    }

    public int getMagicDmgModifier() {
        return _magicDmgModifier;
    }

    public void addMagicDmgModifier(int i) {
        _magicDmgModifier += i;
    }

    public int getMagicDmgReduction() {
        return _magicDmgReduction;
    }

    public void addMagicDmgReduction(int i) {
        _magicDmgReduction += i;
    }

    // 法利昂的治癒守護 亂數機率1000
    public void set_elitePlateMail_Fafurion(int r, int hpmin, int hpmax) {
        _elitePlateMail_Fafurion = r;
        _fafurion_hpmin = hpmin;
        _fafurion_hpmax = hpmax;
    }

    // 奪魂T設置
    public void set_soulHp_val(int r, int hpmin, int hpmax) {
        _soulHp_r = r;
        _soulHp_hpmin = hpmin;
        _soulHp_hpmax = hpmax;
    }

    public int isSoulHp() {
        return isSoulHp;
    }

    public ArrayList<Integer> get_soulHp() {
        soulHp.add(0, _soulHp_r);
        soulHp.add(1, _soulHp_hpmin);
        soulHp.add(2, _soulHp_hpmax);
        return soulHp;
    }

    public void set_soulHp(int flag) {
        isSoulHp = flag;
    }

    // 林德拜爾的魔力守護 亂數機率1000
    public void set_elitePlateMail_Lindvior(int r, int mpmin, int mpmax) {
        _elitePlateMail_Lindvior = r;
        _lindvior_mpmin = mpmin;
        _lindvior_mpmax = mpmax;
    }

    // 巴拉卡斯的弓箭反屏 亂數機率1000
    public void set_elitePlateMail_Valakas(int r, int dmgmin, int dmgmax) {
        _elitePlateMail_Valakas = r;
        _valakas_dmgmin = dmgmin;
        _valakas_dmgmax = dmgmax;
    }

    // 黑帝斯斗篷 亂數機率1000
    public void set_hades_cloak(int r, int dmgmin, int dmgmax) {
        _hades_cloak = r;
        _hades_cloak_dmgmin = dmgmin;
        _hades_cloak_dmgmax = dmgmax;
    }

    // 黑帝斯斗篷 亂數機率1000
    public void set_death_pant(int r, int dmgmin, int dmgmax) {
        _death_pant = r;
        _death_pant_dmgmin = dmgmin;
        _death_pant_dmgmax = dmgmax;
    }

    // 六芒星的淨化 亂數機率1000
    public void set_Hexagram_Magic_Rune(int r, int hpmin, int hpmax, int gfx) {
        _Hexagram_Magic_Rune = r;
        _hexagram_hpmin = hpmin;
        _hexagram_hpmax = hpmax;
        _hexagram_gfx = gfx;
    }

    // 蒂蜜特的祝福 亂數機率1000
    public void set_DimiterBless(int r, int mpmin, int mpmax, int r2, int time) {
        _dimiter_mpr_rnd = r;
        _dimiter_mpmin = mpmin;
        _dimiter_mpmax = mpmax;
        _dimiter_bless = r2;
        _dimiter_time = time;
    }

    /**
     * 經驗加成%數
     *
     */
    public int getExpPoint() {
        return _expPoint;
    }

    /**
     * 設定經驗加成%數
     *
     */
    public void setExpPoint(int i) {
        _expPoint = i;
    }

    public int getSummonId() {
        return _SummonId;
    }

    public void setSummonId(int SummonId) {
        _SummonId = SummonId;
    }

    // 王者加護 end
    public int getLap() {
        return _lap;
    }

    public void setLap(int i) {
        _lap = i;
    }

    public int getLapCheck() {
        return _lapCheck;
    }

    // 王族新技能 恩典庇護 end
    public void setLapCheck(int i) {
        _lapCheck = i;
    }

    public int getLapScore() {
        return _lap * 29 + _lapCheck;
    }

    public boolean isInOrderList() {
        return _order_list;
    }

    // 幻術師新技能 衝突強化 end
    public void setInOrderList(boolean bool) {
        _order_list = bool;
    }

    public L1MobSpList getMobSplist() {
        return this._MobSpList;
    }

    public L1BigHotSpList getBigHotSplist() {
        return _BigHotSpList;
    }

    public int get_star() {
        return _isStar;
    }

    public void set_star(int i) {
        _isStar = i;
    }

    public int get_bighot() {
        return _isBigHot;
    }

    public void set_bighot(int i) {
        _isBigHot = i;
    }

    public String getBighot1() {
        return _bighot1;
    }

    public void setBighot1(String bighot1) {
        _bighot1 = bighot1;
    }

    // 8.1連擊系統 end
    public String getBighot2() {
        return _bighot2;
    }

    public void setBighot2(String bighot2) {
        _bighot2 = bighot2;
    }

    public String getBighot3() {
        return _bighot3;
    }

    // 安全區域右下顯示死亡懲罰狀態圖示 end
    public void setBighot3(String bighot3) {
        _bighot3 = bighot3;
    }

    public String getBighot4() {
        return _bighot4;
    }

    public void setBighot4(String bighot4) {
        _bighot4 = bighot4;
    }

    public String getBighot5() {
        return _bighot5;
    }

    // 底比斯大戰遊戲 end
    public void setBighot5(String bighot5) {
        _bighot5 = bighot5;
    }

    public String getBighot6() {
        return _bighot6;
    }

    public void setBighot6(String bighot6) {
        _bighot6 = bighot6;
    }

    public boolean isWindShackle() {
        return hasSkillEffect(167);
    }

    public boolean isEntangle() { // 地面障礙　移动速度1/2
        return this.hasSkillEffect(ENTANGLE);
    }

    public boolean isKnight(int passiveSword) {
        return true;
    }

    public boolean isHeping() {
        return _heping;
    }

    public boolean isATeam() {
        return this._isATeam;
    }

    public void setATeam(boolean bool) {
        this._isATeam = bool;
    }

    public boolean isBTeam() {
        return this._isBTeam;
    }

    public void setBTeam(boolean bool) {
        this._isBTeam = bool;
    }

    public Timestamp getRejoinClanTime() {
        return _rejoinClanTime;
    }

    public void setRejoinClanTime(Timestamp time) {
        _rejoinClanTime = time;
    }

    public int getPartyType() {
        return _partyType;
    }

    // 轉生天賦end
    public void setPartyType(int type) {
        _partyType = type;
    }

    public int getUbScore() {
        return _ubscore;
    }

    public void setUbScore(int i) {
        _ubscore = i;
    }

    public int getSuper() {
        return _super;
    }

    public void setSuper(int i) {
        _super = i;
    }

    public int getSuper2() {
        return _super2;
    }

    public void setSuper2(int i) {
        _super2 = i;
    }

    public int getInputError() {
        return _inputerror;
    }

    public void setInputError(int i) {
        _inputerror = i;
    }

    public int getSpeedError() {
        return _speederror;
    }

    public void setSpeedError(int i) {
        _speederror = i;
    }

    public int getBanError() {
        return _banerror;
    }

    public void setBanError(int i) {
        _banerror = i;
    }

    public int getInputBanError() {
        return _inputbanerror;
    }

    public void setInputBanError(int i) {
        _inputbanerror = i;
    }

    public int getAttBowDmgR() {
        return this._attbowdmgr;
    }

    public void addAttBowDmgR(final int i) {
        this._attbowdmgr += i;
    }

    public void setBirthday(final String time) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyyMMdd").parse(time);
            _birthday = date;
        } catch (final ParseException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    /*
     * private int _tamreserve;
     *
     * public int getTamReserve() { return _tamreserve; }
     *
     * public void setTamReserve(int i) { _tamreserve = i; }
     */
    // 成長果實系統(Tam幣)end

    public Date getBirthDay() {
        return _birthday;
    }

    public int getSlot() {
        return _Slot;
    }

    public void setSlot(int i) {
        _Slot = i;
    }

    public boolean isItemPoly() {
        return _itempoly;
    }

    public void setItemPoly(boolean itempoly) {
        _itempoly = itempoly;
    }

    public L1ItemInstance getPolyScroll() {
        return _polyscroll;
    }

    public void setPolyScroll(L1ItemInstance item) {
        _polyscroll = item;
    }

    public L1EffectInstance get_tomb() {
        return _tomb;
    }

    public void set_tomb(L1EffectInstance tomb) {
        _tomb = tomb;
    }

    public boolean isMagicCritical() {
        return _isMagicCritical;
    }

    public void setMagicCritical(boolean flag) {
        _isMagicCritical = flag;
    }

    // 特效驗證系統 end
    public boolean isPhantomTeleport() {
        return _isPhantomTeleport;
    }

    public void setPhantomTeleport(boolean flag) {
        _isPhantomTeleport = flag;
    }

    /**
     * 取回奇巖地監/古魯丁地監已使用時間(秒).
     *
     */
    public int getRocksPrisonTime() {
        return this._rocksPrisonTime;
    }

    /**
     * 設定奇巖地監/古魯丁地監已使用時間.
     *
     * @param time 時間
     */
    public void setRocksPrisonTime(final int time) {
        this._rocksPrisonTime = time;
    }

    /**
     * 取回拉斯塔巴德地監已使用時間(秒).
     *
     */
    public int getLastabardTime() {
        return this._lastabardTime;
    }

    /**
     * 設定拉斯塔巴德監獄已使用時間.
     *
     * @param time 時間
     */
    public void setLastabardTime(final int time) {
        this._lastabardTime = time;
    }

    /**
     * 取回象牙塔已使用時間(秒).
     *
     */
    public int getIvoryTowerTime() {
        return this._ivorytowerTime;
    }

    /**
     * 設定象牙塔已使用時間.
     *
     * @param time 時間
     */
    public void setIvoryTowerTime(final int time) {
        this._ivorytowerTime = time;
    }

    /**
     * 取回龍之谷地監已使用時間(秒).
     *
     */
    public int getDragonValleyTime() {
        return this._dragonvalleyTime;
    }

    /**
     * 設定龍之谷地監已使用時間.
     *
     * @param time 時間
     */
    public void setDragonValleyTime(final int time) {
        this._dragonvalleyTime = time;
    }

    /**
     * 取回噬魂塔副本已使用時間(秒).
     *
     */
    public int getSoulTime() {
        return this._SoulTime;
    }

    /**
     * 設定噬魂塔副本已使用時間.
     *
     * @param time 時間
     */
    public void setSoulTime(final int time) {
        this._SoulTime = time;
    }

    /**
     * 取回冰女副本已使用時間(秒).
     *
     */
    public int getIceTime() {
        return this._iceTime;
    }

    /**
     * 設定冰女副本已使用時間.
     *
     * @param time 時間
     */
    public void setIceTime(final int time) {
        this._iceTime = time;
    }

    /**
     * 取回媽祖已使用狀態.
     *
     */
    public int getMazuTime() {
        return this._MazuTime;
    }

    /**
     * 設定媽祖已使用狀態.
     *
     * @param i 1(已使用) or 0(未使用)
     */
    public void setMazuTime(final int i) {
        this._MazuTime = i;
    }

    /**
     * 重置所有限時地監已使用時間
     */
    public void resetAllMapTime() {
        this._rocksPrisonTime = 0;
        this._lastabardTime = 0;
        this._ivorytowerTime = 0;
        this._dragonvalleyTime = 0;
        this._MazuTime = 0;
    }

    /**
     * 取回計時地圖已使用時間(秒)
     *
     * @param mapid 玩家所在的地圖
     */
    public int getMapUseTime(final int mapid) {
        int result = 0;
        switch (mapid) {
            case 53: // 奇巖地監1F
            case 54: // 奇巖地監2F
            case 55: // 奇巖地監3F
            case 56: // 奇巖地監4F
            case 807:// 新版古魯丁地監 1樓
            case 808:// 新版古魯丁地監 2樓
            case 809:// 新版古魯丁地監 3樓
            case 810:// 新版古魯丁地監 4樓
            case 811:// 新版古魯丁地監 5樓
            case 812:// 新版古魯丁地監 6樓
            case 813:// 新版古魯丁地監 7樓
                result = this._rocksPrisonTime;
                break;
            case 75: // 象牙塔1F
            case 76: // 象牙塔2F
            case 77: // 象牙塔3F
            case 78: // 象牙塔4F
            case 79: // 象牙塔5F
            case 80: // 象牙塔6F
            case 81: // 象牙塔7F
            case 82: // 象牙塔8F
                result = this._ivorytowerTime;
                break;
            case 452: // 突擊隊訓練場
            case 453: // 魔獸軍王之室
            case 461: // 黑魔法研究室
            case 462: // 法令軍王之室
            case 471: // 惡靈之主祭壇
            case 475: // 冥法軍王之室
            case 479: // 拉斯塔巴德中央廣場
            case 492: // 暗殺軍王之室
            case 495: // 地下競技場
                result = this._lastabardTime;
                break;
            case 30:// 龍之谷地監 1樓
            case 31:// 龍之谷地監 2樓
            case 32:// 龍之谷地監 3樓
            case 33:// 龍之谷地監 4樓
            case 35:// 龍之谷地監 5樓
            case 36:// 龍之谷地監 6樓
            case 37:// 龍之谷地監 7樓
                result = this._dragonvalleyTime;
                break;
            case 2101:// 冰女副本
            case 2151:// 冰女副本
                result = this._iceTime;
                break;
        }
        return result;
    }

    /**
     * 設定地圖已使用時間(秒)
     *
     * @param time 時間
     */
    public void setMapUseTime(final int mapid, final int time) {
        switch (mapid) {
            case 53:// 奇巖地監1F
            case 54:// 奇巖地監2F
            case 55:// 奇巖地監3F
            case 56:// 奇巖地監4F
            case 807:// 新版古魯丁地監 1樓
            case 808:// 新版古魯丁地監 2樓
            case 809:// 新版古魯丁地監 3樓
            case 810:// 新版古魯丁地監 4樓
            case 811:// 新版古魯丁地監 5樓
            case 812:// 新版古魯丁地監 6樓
            case 813:// 新版古魯丁地監 7樓
                this.setRocksPrisonTime(time);
                break;
            case 75:// 象牙塔1F
            case 76:// 象牙塔2F
            case 77:// 象牙塔3F
            case 78:// 象牙塔4F
            case 79:// 象牙塔5F
            case 80:// 象牙塔6F
            case 81:// 象牙塔7F
            case 82:// 象牙塔8F
                this.setIvoryTowerTime(time);
                break;
            case 452: // 突擊隊訓練場
            case 453: // 魔獸軍王之室
            case 461: // 黑魔法研究室
            case 462: // 法令軍王之室
            case 471: // 惡靈之主祭壇
            case 475: // 冥法軍王之室
            case 479: // 拉斯塔巴德中央廣場
            case 492: // 暗殺軍王之室
            case 495: // 地下競技場
                this.setLastabardTime(time);
                break;
            case 30:// 龍之谷地監 1樓
            case 31:// 龍之谷地監 2樓
            case 32:// 龍之谷地監 3樓
            case 33:// 龍之谷地監 4樓
            case 35:// 龍之谷地監 5樓
            case 36:// 龍之谷地監 6樓
            case 37:// 龍之谷地監 7樓
                this.setDragonValleyTime(time);
                break;
            case 2101:// 冰女副本
            case 2151:// 冰女副本
                this.setIceTime(time);
                break;
        }
    }

    /**
     * 是否在計時地圖 <br>
     *
     * @return true:玩家位於限時的地圖內
     */
    public boolean isInTimeMap() {
        final int map = this.getMapId();
        final int maxMapUsetime = MapsTable.get().getMapTime(map);
        return maxMapUsetime > 0;
    }

    // 日版記憶座標end
    public int getClanMemberId() {
        return _clanMemberId;
    }

    public void setClanMemberId(int i) {
        _clanMemberId = i;
    }

    public String getClanMemberNotes() {
        return _clanMemberNotes;
    }

    public void setClanMemberNotes(String s) {
        _clanMemberNotes = s;
    }
    /**
     * 昏迷命中
     *
     */
    public void addStunLevel(int add) {
        _stunlevel += add;
    }

    public int getStunLevel() {
        return _stunlevel;
    }

    public void addAttackError(int add) {
        _attackError += add;
    }

    public int getAttackError() {
        return _attackError;
    }

    public void setAttackError(int add) {
        _attackError = add;
    }

    public String getAttackBossName() {
        return _attackBossName;
    }

    public void setAttackBossName(String bossName) {
        _attackBossName = bossName;
    }

    public int get_vipLevel() {
        return _vipLevel;
    }

    public void set_vipLevel(final int vipLevel) {
        _vipLevel = vipLevel;
    }

    public int getOnlineGiftIndex() {
        return _giftIndex;
    }

    public void setOnlineGiftIndex(final int onlineGiftIndex) {
        _giftIndex = onlineGiftIndex;
    }

    public boolean isOnlineGiftWiatEnd() {
        return _isWaitEnd;
    }

    public void setOnlineGiftWiatEnd(final boolean onlineGiftWiatEnd) {
        _isWaitEnd = onlineGiftWiatEnd;
    }

    public L1DwarfForGameMallInventry getDwarfForGameMall() {
        return _dwarfForMALL;
    }

    public void updateGameMallMoney() {
        long money = 0;
        final L1ItemInstance moneyItem = getInventory().checkItemX(44070, 1);
        money = moneyItem == null ? 0 : moneyItem.getCount();
        sendPackets(new S_GameMallItemMoney(money));
    }

    public void sendPackets(final ArrayList<ServerBasePacket> packs) {
        if (getNetConnection() == null) {
            return;
        }
        try {
            int i = 0;
            for (final int length = packs.size(); i < length; i++) {
                sendPackets(packs.get(i));
            }
        } catch (final Exception e) {
            logout();
            close();
        }
    }

    public int getRange() {
        return _range;
    }

    public void setRange(final int range) {
        _range = range;
    }

    public L1ItemInstance getWeaponWarrior() {
        return _weaponWarrior;
    }

    public void setWeaponWarrior(final L1ItemInstance weapon) {
        _weaponWarrior = weapon;
    }

    /**
     * 計算泰坦技能的武器傷害加成
     * @return 傷害值
     */
    public int colcTitanDmg() {
        // 如果沒有裝備武器，傷害就是0，空手道泰坦不太猛
        if (getWeapon() == null) {
            return 0;
        }
        L1ItemInstance weapon = getWeapon(); // 取得當前裝備的武器
        // 如果有戰士專用武器，且正在切換武器，就用戰士武器
        // 這段是為了支援7.0版本的戰士斬殺者
        if (getWeaponWarrior() != null && is_change_weapon()) {
            weapon = getWeaponWarrior();
        }
        // 基礎大目標傷害
        int dmg = weapon.getItem().getDmgLarge();
        // 加上武器的額外傷害修正值
        dmg += weapon.getItem().getDmgModifier();
        // 再加上武器強化(+n)的等級
        dmg += weapon.getEnchantLevel();
        // 傷害×2，因為泰坦出手不是兩倍速，是兩倍猛
        dmg *= 2;
        return dmg; // 回傳最終傷害值
    }
    public boolean isCrystal() {
        if (isSkillMastery(PASSIVE_TITANROCK) || isSkillMastery(PASSIVE_TITANBULLET) || isSkillMastery(PASSIVE_TITANMAGIC)) {
            return getInventory().consumeItem(41246, 10);
        }
        return false;
    }

    /**
     * 狂戰士-粉碎
     *
     */
    public boolean isCRASH() {
        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_CRASH);
    }

    /**
     * 狂戰士-狂暴
     *
     */
    public boolean isFURY() {
        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_FURY);
    }

    /**
     * 狂戰士-迅猛雙斧
     *
     */
    public boolean isSLAYER() {
        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_SLAYER);
    }

    /**
     * 狂戰士-泰坦：岩石
     *
     */
    public boolean isTITANROCK() {
        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_TITANROCK);
    }

    /**
     * 狂戰士-泰坦：子彈
     *
     */
    public boolean isTITANBULLET() {
        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_TITANBULLET);
    }
    //END

    /**
     * 狂戰士-護甲身軀
     *
     */
    public boolean isARMORGARDE() {
        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_ARMORGARDE);
    }

    /**
     * 狂戰士-泰坦：魔法
     *
     */
    public boolean isTITANMAGIC() {
        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_TITANMAGIC);
    }

    /**
     * 法師-神諭
     *
     */
    public boolean isOracle() {
        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_ORACLE);
    }

    /**
     * 騎士-單手劍
     *
     */
    public boolean isSWORD() {
        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_SWORD);
    }

    /**
     * 騎士-雙手劍
     *
     */
    public boolean isSWORD_2() {

        return CharSkillReading.get().spellCheck(this.getId(), PASSIVE_SWORD_2);
    }

    /**
     * 妖精-重擊之矢
     *
     */
    public boolean isHeavyStrikeArrow() {
        return CharSkillReading.get().spellCheck(this.getId(), HeavyStrikeArrow);
    }
    /**
     * 妖精-鷹眼(精神)
     *
     */
    public boolean isEagle() {
        return CharSkillReading.get().spellCheck(this.getId(), Eagle_Eye);
    }
    /**
     * 檢查玩家是否擁有妖精被動 - 重擊之矢技能
     *
     */

    public void addTripleArrowCount(L1Character enemyTarget) {
        if (!isHeavyStrikeArrow()) return;

        if (lastTripleArrowTarget == null || !lastTripleArrowTarget.equals(enemyTarget)) {
            tripleArrowCount = 1;
            lastTripleArrowTarget = enemyTarget;
        } else {
            tripleArrowCount++;
        }

        if (tripleArrowCount >= 10) {
            // 觸發回血與特效
            int healHp = (int) (getMaxHp() * 0.05);
            int healMp = (int) (getMaxMp() * 0.05);
            setCurrentHp(getCurrentHp() + healHp);
            setCurrentMp(getCurrentMp() + healMp);
            sendPackets(new S_SkillSound(getId(), 4394));
            sendPackets(new S_SkillSound(getId(), 11764));
            sendPackets(new S_SystemMessage("【重擊之矢】發動！恢復 HP + " + healHp + " / MP + " + healMp));

            S_SkillSound effect = new S_SkillSound(enemyTarget.getId(), 18846);
            if (enemyTarget instanceof L1PcInstance) {
                ((L1PcInstance) enemyTarget).sendPackets(effect);
            } else if (enemyTarget instanceof L1NpcInstance) {
                ((L1NpcInstance) enemyTarget).broadcastPacketAll(effect);
            }
            this.sendPackets(effect);

            tripleArrowCount = 0;
            lastTripleArrowTarget = null;
        }
    }

    public boolean is_change_weapon() {
        return _change_weapon;
    }

    public void set_change_weapon(final boolean flag) {
        _change_weapon = flag;
    }

    public int getGiganticHp() {
        return _giganticHp;
    }

    public void setGiganticHp(final int i) {
        _giganticHp = i;
    }

    public int getAttackTargetId() {
        return _attacktargetid;
    }

    public void setAttackTargetId(final int objid) {
        _attacktargetid = objid;
    }

    public int getPolyStatus() {
        int poly = 0;
        if (getLevel() <= 29) {
            poly = 0;
        } else if (getLevel() >= 30 && getLevel() <= 44) {
            poly = 1;
        } else if (getLevel() >= 45 && getLevel() <= 49) {
            poly = 2;
        } else if (getLevel() == 50) {
            poly = 3;
        } else if (getLevel() == 51) {
            poly = 4;
        } else if (getLevel() >= 52 && getLevel() <= 54) {
            poly = 5;
        } else if (getLevel() >= 55 && getLevel() <= 59) {
            poly = 6;
        } else if (getLevel() >= 60 && getLevel() <= 64) {
            poly = 7;
        } else if (getLevel() >= 65 && getLevel() <= 69) {
            poly = 8;
        } else if (getLevel() >= 70 && getLevel() <= 74) {
            poly = 9;
        } else if (getLevel() >= 75 && getLevel() <= 79) {
            poly = 10;
        } else if (getLevel() >= 80) {
            poly = 11;
        }
        return poly;
    }

    public int getMasterID() {
        return _masterid;
    }

    public void setMasterID(int i) {
        _masterid = i;
    }

    public Timestamp getRejoinMasterTime() {
        return _rejoinMasterTime;
    }

    public void setRejoinMasterTime(Timestamp time) {
        _rejoinMasterTime = time;
    }

    public void setRejoinMasterTime() {
        _rejoinMasterTime = new Timestamp(System.currentTimeMillis());
    }

    /**
     * 新外掛檢測
     **/
    public long getAICheck() {
        long count = 0;
        if (this != null) {
            long pcCount = this.getInventory().countItems(20000);
            if (pcCount > 0) {
                count = pcCount;
            }
        }
        return count;
    }

    public void removeAICheck(int itemid, long count) {
        if (this != null) {
            this.getInventory().consumeItem(itemid, count);
        }
    }

    /**
     * 更新記時地圖時間
     */
    public void updateMapTime(int time) {
        int mapid = getMapId();
        if (mapid >= 4001 && mapid <= 4050) {
            mapid = 4001;
        }
        if (mapTime.get(mapid) == null) {
            return;
        }
        int temp = mapTime.get(mapid);
        mapTime.put((int) this.getMapId(), temp + time);
    }

    /**
     * 返回記時地圖時間
     */
    public int getMapTime(int mapid) {
        if (mapTime.get(mapid) == null) {
            _log.error("記時地圖ID:" + mapid + "不存在");
            return -1;
        }
        return mapTime.get(mapid);
    }

    /**
     * 返回人物所有記時地圖信息
     */
    public ConcurrentHashMap<Integer, Integer> getMapTime() {
        return mapTime;
    }

    /**
     * 重置記時地圖信息
     */
    public void setMapTime(ConcurrentHashMap<Integer, Integer> map) {
        mapTime = map;
    }

    /**
     * 是否在記時地圖
     *
     * @return true 在記時地圖
     * @return false 不在記時地圖
     */
    public boolean isTimeMap() {
        return isTimeMap;
    }

    /**
     * 是否在記時地圖
     *
     */
    public void setTimeMap(boolean isTimeMap) {
        this.isTimeMap = isTimeMap;
    }

    /**
     * 記時地圖停止
     */
    public void stopTimeMap() {
        if (this.isTimeMap) {
            this.isTimeMap = false;
            // MapTimeTable.get().deluser(this);
            // this.sendPackets(new S_PacketBoxMapTimerOut(this));
        }
    }

    /**
     * 取回魔族實驗室已使用時間(秒)
     *
     */
    public int get_MonsterLabTime() {
        return _MonsterLabTime;
    }

    /**
     * 取回破滅戰場已使用時間(秒)
     *
     */
    public int get_WarzoneTime() {
        return _WarzoneTime;
    }

    /**
     * 記時地圖開始
     */
    public void startTimeMap() {
        // final int INTERVAL = 1000;
        if (!this.isTimeMap) {
            this.isTimeMap = true;
            MapTimeTable.get().adduser(this);
        }
    }

    public int get_twotimes() {
        return this._twotimes;
    }

    public void set_twotimes(int _twotimes) {
        this._twotimes = _twotimes;
    }

    public String get_vip_title() {
        return this._vip_title;
    }

    public void set_vip_title(String vip_title) {
        this._vip_title = vip_title;
    }

    public void startSustainEffect(L1PcInstance pc, int effect_id, int Interval) {
        if (!_checkSustainEffect) {
            SustainEffect = new SustainEffect(pc, effect_id);
            if (SustainEffectFuture != null) {
                SustainEffectFuture.cancel(false);
            }
            SustainEffectFuture = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(SustainEffect, Interval, Interval);
            _checkSustainEffect = true;
        }
    }

    public void stopSustainEffect() {
        if (_checkSustainEffect) {
            if (SustainEffectFuture != null) {
                SustainEffectFuture.cancel(false);
                SustainEffectFuture = null;
            }
            SustainEffect.cancel();
            SustainEffect = null;
            _checkSustainEffect = false;
        }
    }

    public int getmcDmgModifierByArmor() {
        return this._mcdmgModifierByArmor;
    }

    public void addmcDmgModifierByArmor(final int i) {
        this._mcdmgModifierByArmor += i;
    }

    public int getWeaponsprobability() {
        return this._Weaponsprobability;
    }

    public void addWeaponsprobability(final int i) {
        this._Weaponsprobability += i;
    }

    public int getWeapondmg() {
        return this._Weapondmg;
    }

    public void addWeapondmg(final int i) {
        this._Weapondmg += i;
    }

    public int getPropertyprobability() {
        return this._Propertyprobability;
    }

    public void addPropertyprobability(final int i) {
        this._Propertyprobability += i;
    }

    public int get_gfx() {
        return this._gfx;
    }

    public void set_gfx(int _gfx) {
        this._gfx = _gfx;
    }

    public int get_time() {
        return this._time;
    }

    public void set_time(int _time) {
        this._time = _time;
    }

    public final boolean isCastleAbility(final int value) {
        if (_castleAbility == null) {
            _castleAbility = new ArrayList<>();
        }
        return _castleAbility.contains(value);
    }

    public final void addCastleAbility(final int value) {
        if (_castleAbility == null) {
            _castleAbility = new ArrayList<>();
        }
        _castleAbility.add(value);
    }

    public final void removeCastleAbility(final int value) {
        if (_castleAbility == null) {
            _castleAbility = new ArrayList<>();
        }
        _castleAbility.remove(Integer.valueOf(value));
    }

    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    public final long getShopAdenaRecord() {
        return _shopAdenaRecord;
    }

    public final void setShopAdenaRecord(final long i) {
        _shopAdenaRecord = i;
    }

    public void add_power(L1ItemPower_text value) {
        if (!_powers.containsKey(value.get_id())) {
            _powers.put(value.get_id(), value);
            value.add_pc_power(this);
            // sendPackets(new S_ServerMessage("\\fW" +
            // value.getMsg()));//src002
            // 套裝效果動畫
            if (value.getGfx() != null) {
                for (int gfx : value.getGfx()) {
                    // 動畫效果
                    sendPacketsX8(new S_SkillSound(getId(), gfx));
                }
            }
        }
    }

    public void remove_power(L1ItemPower_text value) {
        if (_powers.containsKey(value.get_id())) {
            _powers.remove(value.get_id());
            value.remove_pc_power(this);
            // sendPackets(new S_ServerMessage("\\fY失去 " + value.getMsg() + "
            // 效果"));//src002
        }
    }

    public boolean get_power_contains(L1ItemPower_text value) {
        return _powers.containsValue(value);
    }

    public Map<Integer, L1ItemPower_text> get_powers() {
        return _powers;
    }

    public void addExpRateToPc(int s) {// src013
        if (s > 0) {
            _expRateToPc = DoubleUtil.sum(_expRateToPc, (double) s / 100D);
        } else {
            _expRateToPc = DoubleUtil.sub(_expRateToPc, (double) (s * -1) / 100D);
        }
    }

    public double getExpRateToPc() {
        if (_expRateToPc < 0.0D) {
            return 0.0D;
        } else {
            return _expRateToPc;
        }
    }

    public void setExpRateToPc(double s) {
        this._expRateToPc = s;
    }

    // TODO Roy 直接給予 Exp 值
    public synchronized void setExp_Direct(final long i) {
        this.setExp(i);
        this.onChangeExp();
    }

    public int getAmount() {
        return this._amount;
    }

    public void setAmount(int i) {
        this._amount = i;
    }

    public int get_DIY_gfx() {
        return this._gfx1;
    }

    public void set_DIY_gfx(int _gfx) {
        this._gfx1 = _gfx;
    }

    public int get_DIY_time() {
        return this._time1;
    }

    public void set_DIY_time(int _time) {
        this._time1 = _time;
    }

    public int getEffectId() {
        return _effectId;
    }

    public void setEffectId(int i) {
        _effectId = i;
    }

    public boolean isNewTeleport() {
        return _newTeleport;
    }

    public void setNewTeleport(boolean tel) {
        _newTeleport = tel;
    }

    // 抽抽樂
    public boolean isShow_Open_PandoraMsg() {
        return this._isShow_Open_PandoraMsg;
    }

    public void setShow_Open_PandoraMsg(boolean flag) {
        this._isShow_Open_PandoraMsg = flag;
    }

    public L1PandoraInventory getPandoraInventory() {
        return this._pandora;
    }// end

    public int getSleepTimeAI() {
        return this._sleepTimeAI;
    }

    public void setSleepTimeAI(int i) {
        this._sleepTimeAI = i;
    }

    /**
     * 行动间隔时间 <font color="00ffff">PcAI使用</font>
     *
     * @param type 行动模式<br>
     *             0 = 移动 限定最低为 237 (跑酷变身勇绿状态下移速)<br>
     *             1 = 攻击<br>
     *             2 = 有向施法<br>
     *             3 = 无向施法<br>
     * @return 间隔时间
     */
    public int calcSleepTime(final int type) {
        switch (type) {
            case MOVE_TYPE:
                return getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.MOVE);
            case ATTACK_TYPE:
                return getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.ATTACK);
            case SPELL_DIR_TYPE:
                return getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.SPELL_DIR);
            case SPELL_NO_DIR_TYPE:
                return getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.SPELL_NODIR);
            default:
                return 0;
        }
    }

    /**
     * 啟用PC AI
     */
    public void startPcAI() {
        boolean checkStart = this.getWeapon() == null;
        if (this.getWeapon() == null) {
            this.sendPackets(new S_ServerMessage(this.getName() + " 沒有裝備武器，無法進行掛機。"));
            return;
        }

        if (this.isDead()) {
            checkStart = true;
        }
        if (this.isGhost()) {
            checkStart = true;
        }
        if (this.getCurrentHp() <= 0) {
            checkStart = true;
        }
        if (this.isPrivateShop()) {
            checkStart = true;
        }
        if (this.isParalyzed()) {
            checkStart = true;
        }
        if (checkStart) {
            this.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生
            return;
        }
        if (_pcMove != null) { // 重複啟動
            _pcMove.clear();// 清除目標
        } else {
            _pcMove = new pcMove(this);
        }
        this.setActivated(true);
        final PcAI pcAI = new PcAI(this);
        pcAI.startAI();
        //        sendPackets(new S_ServerMessage("\\fn自動掛機啟動by 聖子默默"));
        // --------------------------------------------
        if (this.getLsRange() > 0) {
            this.setLsLocX(this.getX());
            this.setLsLocY(this.getY());
            this.setLsOpen(true);
        }
        this.setRestartAuto(ThreadPoolSetNew.RESTART_AUTO);
        this.setHH((int) (getMaxHp() * this.getH() * 0.01));
        this.setMM((int) (getMaxMp() * getAutoAttackMP() * 0.01));
    }

    /**
     * 停止掛機 by 聖子默默
     */
    public void stopPcAI() {
        if (!this.isActivated()) {
            return;
        }
        this.allTargetClear();
        this.setAiRunning(false);// PC AI時間軸 正在運行
        this.setActivated(false);// 啟用PC AI
        this.flushedLocation(this.getX(), this.getY(), this.getMapId());// 位置更新
        // --------------------------------------------
        this.setRestartAuto(0);// 重啟時間
        this.setRestartAutoStartSec(0);// 重啟時間
        if (this.IsDeathReturn()) {// 死亡後自動復活
            this.setDeathReturn(false);// 死亡後自動復活
        }
        sendPackets(new S_ServerMessage("\\aG自動掛機關閉！"));
    }

    /**
     * PC AI時間軸 正在運行
     *
     */
    protected boolean isAiRunning() {
        return this._aiRunning;
    }

    /**
     * PC AI時間軸 正在運行
     *
     */
    protected void setAiRunning(final boolean aiRunning) {
        this._aiRunning = aiRunning;
    }
    public void allTargetClear() {
        // XXX
        if (_pcMove != null) {
            _pcMove.clear();
        }
        _hateList.clear();
        _aiTarget = null;
        setFirstAttack(false);
    }

    /**
     * 清除單個目標
     */
    public void targetClear() {
        if (_aiTarget == null) {
            return;
        }
        _hateList.remove(_aiTarget);
        _aiTarget = null;
    }

    /**
     * 有效目標檢查
     */
    public void checkTarget() {
        // System.out.println("有效目標檢查");
        try {
            if (_aiTarget == null) {// 目標為空
                allTargetClear();
                return;
            }
            if (_aiTarget.getMapId() != getMapId()) {// 目標地圖不相等
                allTargetClear();
                return;
            }
            if (_aiTarget.getCurrentHp() <= 0) {// 目標HP小於等於0
                allTargetClear();
                return;
            }
            if (_aiTarget.isDead()) {// 目標死亡
                allTargetClear();
                return;
            }
            if (get_showId() != _aiTarget.get_showId()) {// 副本ID不相等
                allTargetClear();
                return;
            }
            if (!_hateList.containsKey(_aiTarget)) {// 目標不在已有攻擊清單中
                allTargetClear();
                return;
            }
            // if (_target.getMapId() == 504) {
            // sendPackets(new S_ServerMessage("\\aD非掛機地圖自動停止.."));
            // setActivated(false);
            // return;
            // }
            final int distance = getLocation().getTileDistance(_aiTarget.getLocation());// 距離
            if (distance > 15) {// 距離超過15格
                allTargetClear();// 清除目標
                return;
            }
        } catch (final Exception e) {
            return;
        }
    }

    /**
     * 現在目標
     */
    public L1Character is_now_target() {
        // System.out.println("現在目標");
        return _aiTarget;
    }

    /**
     * 對目標進行攻擊
     *
     */
    public void attackTarget(final L1Character target) {
        // System.out.println("對目標進行攻擊");
        if (this.getInventory().getWeight240() >= 197) { // 重量過重
            // 110 \f1當負重過重的時候，無法戰鬥。
            this.sendPackets(new S_ServerMessage(110));
            // _log.error("要求角色攻擊:重量過重");
            return;
        }
        if (hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
            return;
        }
        if (hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZED)) {
            return;
        }
        if (hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
            return;
        }
        if (target instanceof L1PcInstance) {
            final L1PcInstance player = (L1PcInstance) target;
            if (player.isTeleport()) { // 處理中
                return;
            }
            if (!player.isPinkName()) {
                this.allTargetClear();
                return;
            }
        } else if (target instanceof L1PetInstance) {
            final L1PetInstance pet = (L1PetInstance) target;
            final L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // 處理中
                    return;
                }
            }
        } else if (target instanceof L1SummonInstance) {
            final L1SummonInstance summon = (L1SummonInstance) target;
            final L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // 處理中
                    return;
                }
            }
        }
        if (target instanceof L1NpcInstance) {
            final L1NpcInstance npc = (L1NpcInstance) target;
            if (npc.getHiddenStatus() != 0) { // 地中潛、■
                this.allTargetClear();
                return;
            }
        }
        target.onAction(this);
        setSleepTimeAI(calcSleepTime(ATTACK_TYPE));
    }

    /**
     * 先確認是否有無敵人<br>
     * true = 有敵人 (執行飛行) <br>
     * false = 沒有敵人 (繼續往下執行)<br>
     */
    private boolean ConfirmTheEnemy() {
        try {
            if (this.IsAttackTeleportHp()) {//已經開啟被攻擊立即瞬移狀態並且假設正在被攻擊中
                autoRandomTeleport(0, 25);
                this.setIsAttackTeleportHp(false);
                return true;
            }
            if (this.IsEnemyTeleport()) {// 遇見仇人進行順移開啟
                boolean ok = false;
                for (L1PcInstance player : World.get().getRecognizePlayer(this)) {
                    if (this.isInEnemyList(player.getName())) {
                        autoRandomTeleport(0, 120);
                        ok = true;
                        break;
                    }
                }
                return ok;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 遇見BOSS飛
     */
    private boolean ConfirmTheBOSS() {
        try {
            if (!this.getBossfei()) {
                return false;
            }
            boolean ok = false;
            for (final L1Object object : World.get().getVisibleObjects(this, 5)) {
                if (!(object instanceof L1MonsterInstance)) {
                    continue;
                }
                final L1MonsterInstance monster = (L1MonsterInstance) object;
                if (monster.getNpcTemplate().is_boss()) {
                    autoRandomTeleport(0, 60);
                    ok = true;
                    break;
                }
            }
            return ok;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * <font color="00ff00">PcAI全部修改工程量大</font><br>
     * <font color="00ff00">先這樣看看效果</font><br>
     * <font color="00ffff">尋怪策略優化 by 聖子默默 2024/06/11 19:12</font>
     */
    public void searchTarget() {
        L1Character target = null; // 目標
        int minDistance = Short.MAX_VALUE;// 最小距離
        int distance = 0;// 距離
        if (isDead()) {
            return;
        }
        if (hasSkillEffect(STATUS_FREEZE)) {// 冰獄
            return;
        }
        // 禁止角色控制的负面魔法
        if (isParalyzed()) {
            return;
        }
        if (isParalyzedX()) {
            return;
        }
        Collection<L1Object> objects = World.get().getVisibleObjects(this, 13); // 可见范围
        if (objects.isEmpty()) {// 没有目标
            return;// 结束
        }
        for (L1Object object : objects) { // 循环目标
            if (ConfirmTheEnemy()) {
                continue;
            }
            if (!(object instanceof L1MonsterInstance)) { // 不是怪物
                continue;
            }
            L1MonsterInstance mob = (L1MonsterInstance) object;// 怪物
            if (NoSearchWithPcAI(mob)) {// 不搜索
                continue;
            }
            if (ConfirmTheBOSS()) {
                continue;
            }
            distance = getLocation().getTileLineDistance(mob.getLocation()); // 距离
            if (minDistance > distance) {// 最小距离
                minDistance = distance;// 更新最小距离
                target = mob;// 更新目标
            }
        }
        if (target != null) { // 有目标
            _hateList.add(target, distance);// 添加目标
            _aiTarget = target;// 更新目标
            return;// 结束
        }
        if (_aiTarget == null) { // 没有目标
            objects.clear();// 清空目标
            _hateList.clear();// 清空目标
            if (isTeleportPcAI()) { // 無目標 瞬移找怪
                _searchThink++;// 寻怪周期
                if (_searchThink > _maxThinkingCycle) {// 寻怪周期超出预设值
                    autoRandomTeleport(TELEPORT_NEED_COUNT, 120);// 瞬移
                }
            } else if (_moveDirectionError > _maxMoveDirection) { // 無目標 且无限绕圈
                autoRandomTeleport(0, 40);
            }
        }
    }

    /**
     * PcAI运行时是否可以瞬移
     */
    private boolean isTeleportPcAI() {
        if (this.getOnlineStatus() == 0) {
            return false;
        }
        if (this.isDead()) {
            return false;
        }
        if (this.isGhost()) {
            return false;
        }
        if (this.isParalyzedX()) {
            return false;
        }
        if (!this.isActivated()) {
            return false;
        }
        if (!IsAutoTeleport()) {
            return false;
        }
        return getMap().isTeleportable();
    }

    /**
     * 指定掛機時不攻擊的怪物 by 聖子默默<br>
     * <font color="00ff00">
     * 已删除<br>
     * 血量归0<br>
     * 攻速为0<br>
     * 飞天<br>
     * 遁地<br>
     * 直线不可达<br>
     * 无法接近目标<br>
     * 已死亡<br>
     * 指定的怪物编号:</font> 牛
     *
     * @param npc 指定掛機時不攻擊的怪物
     */
    private boolean NoSearchWithPcAI(L1NpcInstance npc) {
        if (npc._destroyed) {
            return true;
        }
        if (npc.getCurrentHp() <= 0) {
            return true;
        }
        if (npc.getAtkspeed() == 0) {
            return true;
        }
        if (npc.getHiddenStatus() == HIDDEN_STATUS_FLY) {
            return true;
        }
        if (npc.getHiddenStatus() == HIDDEN_STATUS_SINK) {
            return true;
        }
        final int npcId = npc.getNpcId();
        /* 更多指定不攻擊請自行添加 比如 Quest Mob */
        if (npcId == 70984) { // 牛
            return true;
        }
        /*
         * 測試效果以調整 實裝時根據情況決定要不要這一段
         * 只要不是直線可達目標 PcAI都不會將其加入攻擊清單
         */
        if (!glanceCheck(npc.getX(), npc.getY())) {
            return true;
        }
        if (npc.hasSkillEffect(this.getId() + 100000) && !this.isAttackPosition(npc.getX(), npc.getY(), 1)) {
            return true;
        }
        return npc.isDead();
    }

    /**
     * 掛機時使用的地圖位置更新 by 聖子默默<br>
     * 設定是否需要消耗金幣<br>
     * 若设定消耗数量为 -1则不执行任何动作且关闭瞬移
     * 設定位置的隨機範圍<br>
     *
     * @param count 0 = 無需消耗<br>數字表示需要消耗等量金币
     * @param range 隨機坐標範圍 (range)<br>
     *              range <= 0 则视为设定错误不执行任何动作
     */
    public void autoRandomTeleport(final int count, final int range) {
        if (TELEPORT_NEED_COUNT == -1) {
            if (this.IsAutoTeleport()) {
                this.setAutoTeleport(false);
            }
            return;
        }
        if (range <= 0) {
            _log.warn("autoRandomTeleport error: range must be greater than 0");
            return;
        }
        if (count != 0) {
            if (!this.getInventory().checkItem(40308, TELEPORT_NEED_COUNT)) {
                this.sendPackets(new S_SystemMessage("金幣不足，關閉掛機瞬移尋怪功能。"));
                if (this.IsAutoTeleport()) {
                    this.setAutoTeleport(false);
                }
                return;
            }
            this.getInventory().consumeItem(count, TELEPORT_NEED_COUNT);
        }
        this.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        L1Location newLocation = this.getLocation().randomLocation(range, false);
        int newX = newLocation.getX();
        int newY = newLocation.getY();
        short mapId = (short) newLocation.getMapId();
        this.allTargetClear();
        PcTelDelayAI.onPcAITeleportDelay(this, L1Teleport.TELEPORT, newX, newY, mapId);
    }

    /**
     * 掛機時使用的地圖位置更新 by 聖子默默<br>
     * 指定地圖坐標刷新位置<br>
     * 判斷角色是否可使用傳送的狀態<br>
     * 若前往的地圖變更則停止掛機<br>
     *
     * @param newX     指定x
     * @param newY     指定y
     * @param newMapId 指定mapId
     */
    public void flushedLocation(final int newX, final int newY, final int newMapId) {
        if (this.isParalyzedX()) {
            return;
        }
        this.setTeleportX(newX);
        this.setTeleportY(newY);
        this.setTeleportMapId((short) newMapId);
        this.setTeleportHeading(getHeading());
        Teleportation.teleportation(this);
        this.resetAI();
    }

    public void flushedLocation() {
        flushedLocation(this.getX(), this.getY(), this.getMapId());
    }

    /**
     * 具有目標的處理 (攻擊的判斷)
     */
    public void onTarget() {
        try {
            // System.out.println("具有目標的處理 (攻擊的判斷)");
            // setActivated(true);
            // 先_AItarget變影響出別領域參照確保
            final L1Character target = _aiTarget;
            if (target == null) {
                return;
            }
            attack(target);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * AI技能攻擊判斷 by 聖子默默
     *
     * @param target 攻擊目標
     */
    private void skillAttack(final L1Character target) {
        try {
            if (!getMap().isUsableSkill()) { // 該地圖不允許使用技能
                return;
            }
            if (target.isDead()) {
                return;
            }
            if (isSkillDelay()) {
                return;
            }
            if (getCurrentHp() <= getHH()) {
                return;
            }
            if (getCurrentMp() <= getMM()) {
                return;
            }
            if (_random.nextInt(100) <= ThreadPoolSetNew.ATTACK_SKILL_RANDOM) {
                final int skillId = AttackSkillId();
                if (!isSkillMastery(skillId)) { // 未学习
                    return;
                }
                final L1Skills skill = SkillsTable.get().getTemplate(skillId);
                if (skill == null) {
                    return;
                }
                if (getCurrentMp() < skill.getMpConsume()) {
                    return;
                }
                if (getCurrentHp() < skill.getHpConsume()) {
                    return;
                }
                if ((double) getCurrentMp() / getMaxMp() * 100 < getAutoAttackMP()) {
                    return;
                }
                if (_autoSkillDelay > System.currentTimeMillis() && _autoSkillDelay != 0) {
                    return;
                }
                if (skillId == TURN_UNDEAD) { // 起死回生術特別判斷
                    if (!(target instanceof L1MonsterInstance)) { // 目標不是怪物
                        return;
                    }
                    L1MonsterInstance mob = (L1MonsterInstance) target;
                    final int undeadType = mob.getNpcTemplate().get_undead();
                    if (undeadType != 1 && undeadType != 3) {// 不死系或殭屍系
                        return;
                    }
                }
                int skillRange;
                if (skill.getRanged() == -1) {
                    skillRange = 10;
                } else {
                    skillRange = Math.min(10, skill.getRanged());
                }
                if (!isAttackPosition(target.getX(), target.getY(), skillRange)) {
                    return;
                }
                final L1SkillUse skillUse = new L1SkillUse();
                skillUse.handleCommands(this, skillId, target.getId(), target.getX(), target.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_NORMAL);
                _autoSkillDelay = System.currentTimeMillis() + getAutoAttackSecond() * 1000L;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void attack(L1Character target) {
        if (this.getWeapon() == null) {
            stopPcAI();
            return;
        }
        if (AttackSkillSize() > 0) { // 技能攻擊判斷
            skillAttack(target);
        }
        // 攻擊可能位置
        int attack_Range = this.getWeapon().getItem().getRange();
        if (attack_Range < 0) {
            attack_Range = 15;
        }
        if (isAttackPosition(target.getX(), target.getY(), attack_Range)) {// 已經到達可以攻擊的距離
            setHeading(targetDirection(target.getX(), target.getY()));
            attackTarget(target);
            resetAI();
            // XXX
            if (_pcMove != null) {
                _pcMove.clear();
            }
        } else { // 攻擊不可能位置
            if (_pcMove != null) {
                final int dir = _pcMove.moveDirection(target.getX(), target.getY());
                if (dir == -1) {
                    target.setSkillEffect(this.getId() + 100000, 20000);// 給予20秒狀態
                    targetClear();
                } else {
                    _pcMove.setDirectionMove(dir);
                    if (_autoMove > _maxAutoMoveCycle) {
                        if (isTeleportPcAI()) {
                            autoRandomTeleport(0, 20);
                        } else {
                            target.setSkillEffect(this.getId() + 100000, 20000);// 給予20秒狀態
                        }
                        targetClear();
                        resetAI();
                    }
                    _autoMove++;
                }
            }
        }
    }

    /**
     * _moveDirection = 0;<br>
     * _move = 0;<br>
     * _thinkingCounter = 0;<br>
     */
    public void resetAI() {
        _moveDirectionError = 0;
        _autoMove = 0;
        _searchThink = 0;
    }

    /**
     * PC已經激活
     *
     * @return true:激活 false:無
     */
    public boolean isActivated() {
        return this._activated;
    }

    /**
     * PC已經激活
     *
     * @param actived true:激活 false:無
     */
    public void setActivated(final boolean actived) {
        this._activated = actived;
    }

    protected boolean isFirstAttack() {
        return this._firstAttack;
    }

    protected void setFirstAttack(final boolean firstAttack) {
        this._firstAttack = firstAttack;
    }

    /**
     * 攻擊目標設置
     *
     */
    public void setHate(final L1Character cha, int hate) {
        try {
            // System.out.println("攻擊目標設置");
            if (cha != null && /* (cha.getId() != getId()) */_aiTarget != null) {
                if (!isFirstAttack() && hate > 0) {
                    // hate += getMaxHp() / 10; // ＦＡ
                    setFirstAttack(true);
                    if (_pcMove != null) {
                        _pcMove.clear();// XXX
                    }
                    // System.out.println("isFirstAttack=" + isFirstAttack());
                    _hateList.add(cha, 5);
                    _aiTarget = _hateList.getMaxHateCharacter();
                    checkTarget();
                }
            }
        } catch (final Exception e) {
            return;
        }
    }

    /**
     * 目標為空掛機尋路中
     *
     */
    public boolean isPathfinding() {
        // System.out.println("目標為空掛機尋路中");
        return this._Pathfinding;
    }

    public void setPathfinding(final boolean fla) {
        this._Pathfinding = fla;
    }

    public int getrandomMoveDirection() {
        return _randomMoveDirection;
    }

    public void setrandomMoveDirection(int randomMoveDirection) {
        this._randomMoveDirection = randomMoveDirection;
    }

    /**
     * 沒有目標的處理<BR>
     */
    public void noTarget() {
        if (hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
            return;
        }
        if (hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZED)) {
            return;
        }
        if (hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
            return;
        }
        if (_pcMove != null) {
            int dir = _pcMove.checkObject(_randomMoveDirection);
            if (dir != -1) {
                _pcMove.setDirectionMove(dir);
            } else {
                if (_random.nextBoolean()) {
                    _randomMoveDirection++; // 有障碍就调整方向
                } else {
                    _randomMoveDirection--; // 有障碍就调整方向
                }
                if (_randomMoveDirection > 7) {
                    _randomMoveDirection = 0;
                }
                if (_randomMoveDirection < 0) {
                    _randomMoveDirection = 7;
                }
                _moveDirectionError++; // 记录遇到障碍次数
                setSleepTimeAI(calcSleepTime(MOVE_TYPE));
            }
        }
    }

    /**
     * 往隨機方向移動一格(判断障碍物)<br>
     * 黑暗之手 DEBUFF 移動無法自我控制
     */
    public void HandDarknessMove() {
        if (!hasSkillEffect(HAND_DARKNESS) || hasSkillEffect(L1SkillId.Phantom_Blade)) {
            return;
        }
        if (_pcMove != null) {
            _pcMove.clear();
        } else _pcMove = new pcMove(this);
        int direction = _random.nextInt(7);
        int dir = _pcMove.checkObject(direction);
        if (dir == -1) {
            if (_random.nextBoolean()) {
                direction++; // 有障碍就调整方向
            } else {
                direction--; // 有障碍就调整方向
            }
            direction = randomDirection(direction);
            dir = _pcMove.checkObject(direction);
        }
        if (dir != -1) {
            _pcMove.setDirectionMove(dir);
        }
    }

    /**
     * 随意取方向
     *
     * @param direction 初始值
     * @return 随机取 0- 7
     */
    private int randomDirection(int direction) {
        final int random = _random.nextInt(5) + 1; // 亂數(1-6)
        if (_random.nextBoolean()) {
            direction += random;
            if (direction > 7) { // 超7
                direction -= 7; // 減7
            }
        } else {
            direction -= random;
            if (direction < 0) { // 低于0
                direction += 7; // 加7
            }
        }
        return IntRange.ensure(direction, 0, 7); // 最終保險
    }

    /**
     * vip光圈跟随
     */
    public void setSkinMoveSpeed() {
        Map<Integer, L1SkinInstance> skinList = getSkins();
        if (skinList.size() > 0) {
            for (Integer gfx_id : skinList.keySet()) {
                L1SkinInstance skin = skinList.get(gfx_id);
                skin.setX(getX());
                skin.setY(getY());
                skin.setMap(getMap());
                skin.setHeading(getHeading());
                if (skin.getMoveType() == 0) {
                    L1PcInstance visiblePc;
                    for (Iterator<L1PcInstance> iterator1 = World.get().getVisiblePlayer(skin).iterator(); iterator1.hasNext(); visiblePc.removeKnownObject(skin)) {
                        visiblePc = iterator1.next();
                    }
                    skin.broadcastPacketAll(new S_NPCPack_Skin(skin));
                } else {
                    skin.setNpcMoveSpeed();
                    skin.broadcastPacketAll(new S_MoveCharPacket(skin));
                }
            }
        }
    }

    public final void setMapsList(final HashMap<Integer, Integer> list) {
        _mapsList = list;
    }

    public final int getMapsTime(final int key) {
        if (_mapsList == null || !_mapsList.containsKey(key)) {
            return 0;
        }
        return _mapsList.get(key);
    }

    public void putMapsTime(final int key, final int value) {
        if (_mapsList == null) {
            _mapsList = CharMapTimeReading.get().addTime(getId(), key, value);
        }
        _mapsList.put(key, value);
    }

    public void removeMapsTime(final int key) { // src022
        _mapsList.remove(key);
    }

    public boolean isTripleArrow() {
        return this.isTripleArrow;
    }

    public void setTripleArrow(boolean isTripleArrow) {
        this.isTripleArrow = isTripleArrow;
    }

    public final byte getRingsExpansion() {
        return this._ringsExpansion;
    }

    public final void setRingsExpansion(final byte i) {
        this._ringsExpansion = i;
    }

    public final byte getEarringsExpansion() {
        return this._earringsExpansion;
    }

    public final void setEarringsExpansion(final byte i) {
        this._earringsExpansion = i;
    }

    public final byte getEquipmentIndexAmulet() {
        return this._equipmentindexamulet;
    }

    public final void setEquipmentIndexAmulet(final byte i) {
        this._equipmentindexamulet = i;
    }

    // 7.6
    public final int getWeaponType(final int Type1) {
        if (Type1 == ActionCodes.ACTION_ChainswordWalk) {
            if (SprTable.get().containsChainswordSpr(getTempCharGfx())) {
                return ActionCodes.ACTION_ChainswordWalk;
            } else {
                return ActionCodes.ACTION_SpearWalk;
            }
        }
        return Type1;
    }

    public final byte getRedblueReward() {
        return this._redblueReward;
    }

    public final void setRedblueReward(final byte i) {
        this._redblueReward = i;
    }

    public final boolean isGetPolyPower()// src014
    {
        return this._isGetPolyPower;
    }
    public final void setGetPolyPower(boolean flag) {
        this._isGetPolyPower = flag;
    }

    public final L1PolyPower getPolyPower() {
        return this._polyPower;
    }

    public final void resetPolyPower() {
        if (getPolyPower() != null) {
            ExtraPolyPowerTable.effectBuff(this, getPolyPower(), -1);
        }
        this._polyPower = ExtraPolyPowerTable.getInstance().get(getTempCharGfx());
        if (getPolyPower() != null) {
            ExtraPolyPowerTable.effectBuff(this, this.getPolyPower(), 1);
        }
    }

    public void clearAstrologyPower() {
        ASTROLOGY_DATA_MAP.clear();
    }

    /**
     * 加載角色所有已解鎖的星盤能力值
     */
    public final void addAstrologyPower() {
        for (Integer key : Astrology1Table.get().getAstrologyIndex()) {
            AstrologyData data = Astrology1Table.get().getAstrology(key);
            Optional.ofNullable(data).ifPresent(effect -> {
                try {
                    if (getQuest().isEnd(data.get_questId())) {
                        // 舊星盤：僅自動套用非技能節點（skillId==0），技能節點需玩家點擊切換
                        if (data.get_skillId() == 0) {
                            addAstrologyPower(data, key);
                        }
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                } catch (InterruptedException e) {
                    _log.error(e.getMessage(), e);
                }
            });
        }
        if (ASTROLOGY_DATA_MAP.isEmpty()) {
//            sendPackets(new S_SystemMessage("當前角色沒有任何星盤屬性加成"));
        }
    }

    /**
     * 增加星盤內設定的屬性值加成
     *
     * @param data 對應星盤設定
     * @param key  星盤編號
     */
    public final void addAstrologyPower(AstrologyData data, int key) {
        if (ASTROLOGY_DATA_MAP.containsKey(key)) {
            return;
        }
        Astrology1Table.effectBuff(this, data, 1);
        ASTROLOGY_DATA_MAP.put(key, data);
    }

    // 新星盤 Atton
    public final void addAstrologyPower(AttonAstrologyData data, int key) {
        if (ATTON_ASTROLOGY_DATA_MAP.containsKey(key)) {
            return;
        }
        AttonAstrologyTable.effectBuff(this, data, 1);
        ATTON_ASTROLOGY_DATA_MAP.put(key, data);
    }

    // ======= 絲莉安/阿頓 共用的擴充屬性（被動減傷類） =======
    private int _tripleArrowReduction = 0; // 被三重矢攻擊時，固定減傷值
    private int _rangedDmgReductionPercent = 0; // 遭受遠距離攻擊時減免百分比
    // 新增：通用與近距離傷害減免百分比
    private int _allDmgReductionPercent = 0;     // 遭受任何傷害時的通用減免百分比
    private int _meleeDmgReductionPercent = 0;   // 遭受近距離攻擊時的減免百分比
    // 依詩蒂：減益狀態（暫存於玩家身上，期間內降低減免）
    private int _yishidiDebuffDown = 0;          // 減益狀態減傷值（期間內每擊增加傷害）
    private long _yishidiDebuffUntil = 0L;       // 減益狀態結束時間戳
    
    // 依詩蒂減益狀態參數（從技能節點讀取並儲存）
    private int _yishidiDebuffProcPercent = 0;   // 減益狀態觸發機率%
    private int _yishidiDebuffDmgDown = 0;       // 減益狀態減傷值
    private int _yishidiDebuffDurationSec = 0;   // 減益狀態持續時間(秒)
    private int _yishidiDebuffGfxId = 0;         // 減益狀態GFX
    private int _stunDmgReduction = 0; // 處於昏迷(眩暈)狀態時承受的每次固定減傷
    // 格立特：暴擊傷害提升百分比（僅在攻擊者觸發近/遠暴擊時生效）
    private int _gritCritDmgUpPercent = 0;
    private double _gritSkillProcChance = 0.0D;      // 格立特技能發動機率（%）
    private double _gritSkillCritDmgPercent = 0.0D;  // 格立特技能暴擊傷害（%）
    // 格立特：被動暴擊傷害提升%、暴擊傷害減免%、暴擊抗性%
    private int _gritCritDmgUpPassivePercent = 0;
    private int _gritCritDmgReductionPercent = 0;
    private int _gritCritResistPercent = 0;
    // 格立特：技能觸發時吸收 HP/MP（固定值）
    private int _gritSkillAbsorbHp = 0;
    private int _gritSkillAbsorbMp = 0;
    private int _gritSkillGfxId = 0;
    // 三重矢技能發動期間的臨時旗標（用於一般攻擊流程識別三重矢的三次射擊）
    private transient boolean _tripleArrowShooting = false;

    public int getTripleArrowReduction() {
        return _tripleArrowReduction;
    }

    public void addTripleArrowReduction(int amount) {
        _tripleArrowReduction += amount;
        if (_tripleArrowReduction < 0) _tripleArrowReduction = 0;
    }

    public int getRangedDmgReductionPercent() {
        return _rangedDmgReductionPercent;
    }

    public void addRangedDmgReductionPercent(int percent) {
        _rangedDmgReductionPercent += percent;
        if (_rangedDmgReductionPercent < 0) _rangedDmgReductionPercent = 0;
        if (_rangedDmgReductionPercent > 100) _rangedDmgReductionPercent = 100;
    }

    public int getAllDmgReductionPercent() {
        return _allDmgReductionPercent;
    }

    public void addAllDmgReductionPercent(int percent) {
        _allDmgReductionPercent += percent;
        if (_allDmgReductionPercent < 0) _allDmgReductionPercent = 0;
        if (_allDmgReductionPercent > 100) _allDmgReductionPercent = 100;
    }

    public int getMeleeDmgReductionPercent() {
        return _meleeDmgReductionPercent;
    }

    public void addMeleeDmgReductionPercent(int percent) {
        _meleeDmgReductionPercent += percent;
        if (_meleeDmgReductionPercent < 0) _meleeDmgReductionPercent = 0;
        if (_meleeDmgReductionPercent > 100) _meleeDmgReductionPercent = 100;
    }

    // 依詩蒂減益：設定/查詢
    // 設定減益狀態參數（從技能節點讀取）
    public void setYishidiDebuffProcPercent(int percent) {
        _yishidiDebuffProcPercent = Math.max(0, Math.min(100, percent));
    }
    public int getYishidiDebuffProcPercent() {
        return _yishidiDebuffProcPercent;
    }
    
    public void setYishidiDebuffDmgDown(int dmg) {
        _yishidiDebuffDmgDown = Math.max(0, dmg);
    }
    public int getYishidiDebuffDmgDown() {
        return _yishidiDebuffDmgDown;
    }
    
    public void setYishidiDebuffDurationSec(int sec) {
        _yishidiDebuffDurationSec = Math.max(0, sec);
    }
    public int getYishidiDebuffDurationSec() {
        return _yishidiDebuffDurationSec;
    }
    
    public void setYishidiDebuffGfxId(int gfx) {
        _yishidiDebuffGfxId = Math.max(0, gfx);
    }
    public int getYishidiDebuffGfxId() {
        return _yishidiDebuffGfxId;
    }
    
    // 觸發減益狀態（戰鬥時呼叫）- 支援自訂 ICON
    public void setYishidiDebuff(int down, int durationSec, int iconId, int stringId) {
        _yishidiDebuffDown = Math.max(0, down);
        _yishidiDebuffUntil = System.currentTimeMillis() + Math.max(0, durationSec) * 1000L;
        try {
            // 使用傳入的 iconId 和 stringId 顯示負面 ICON
            if (iconId > 0 && stringId > 0) {
                this.sendPackets(new com.lineage.server.serverpackets.S_InventoryIcon(iconId, true, stringId, durationSec));
            }
        } catch (Throwable ignore) {}
    }
    
    // 保留舊的方法簽名以相容
    public void setYishidiDebuff(int down, int durationSec) {
        setYishidiDebuff(down, durationSec, 10534, 2747); // 預設 ICON
    }
    public int getYishidiDebuffDownActive() {
        if (_yishidiDebuffDown <= 0) return 0;
        if (_yishidiDebuffUntil <= System.currentTimeMillis()) return 0;
        return _yishidiDebuffDown;
    }

    public int getStunDmgReduction() {
        return _stunDmgReduction;
    }

    public void addStunDmgReduction(int amount) {
        _stunDmgReduction += amount;
        if (_stunDmgReduction < 0) _stunDmgReduction = 0;
    }

    public int getGritCritDmgUpPercent() {
        return _gritCritDmgUpPercent;
    }

    public void setGritCritDmgUpPercent(int percent) {
        if (percent < 0) percent = 0;
        if (percent > 500) percent = 500; // 上限保護，避免異常倍率
        _gritCritDmgUpPercent = percent;
    }

    public double getGritSkillProcChance() { return _gritSkillProcChance; }
    public void setGritSkillProcChance(double percent) {
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;
        _gritSkillProcChance = percent;
    }
    public double getGritSkillCritDmgPercent() { return _gritSkillCritDmgPercent; }
    public void setGritSkillCritDmgPercent(double percent) {
        if (percent < 0) percent = 0;
        if (percent > 500) percent = 500;
        _gritSkillCritDmgPercent = percent;
    }

    public int getGritCritDmgUpPassivePercent() { return _gritCritDmgUpPassivePercent; }
    public void addGritCritDmgUpPassivePercent(int v) { _gritCritDmgUpPassivePercent += v; }

    public int getGritCritDmgReductionPercent() { return _gritCritDmgReductionPercent; }
    public void addGritCritDmgReductionPercent(int v) { _gritCritDmgReductionPercent += v; }

    public int getGritCritResistPercent() { return _gritCritResistPercent; }
    public void addGritCritResistPercent(int v) { _gritCritResistPercent += v; }

    public int getGritSkillAbsorbHp() { return _gritSkillAbsorbHp; }
    public void setGritSkillAbsorbHp(int v) { _gritSkillAbsorbHp = Math.max(0, v); }

    public int getGritSkillAbsorbMp() { return _gritSkillAbsorbMp; }
    public void setGritSkillAbsorbMp(int v) { _gritSkillAbsorbMp = Math.max(0, v); }

    public int getGritSkillGfxId() { return _gritSkillGfxId; }
    public void setGritSkillGfxId(int v) { _gritSkillGfxId = Math.max(0, v); }

    public boolean isTripleArrowShooting() {
        return _tripleArrowShooting;
    }

    public void setTripleArrowShooting(boolean shooting) {
        _tripleArrowShooting = shooting;
    }

    // 絲莉安專用 HOT 狀態追蹤（毫秒時間戳）。大於當前時間表示正在回復
    private long _silianRegenUntilMs = 0L;
    private long _silianCooldownUntilMs = 0L;

    public void setSilianRegenUntil(long untilMs) {
        _silianRegenUntilMs = untilMs;
    }

    public long getSilianRegenUntil() {
        return _silianRegenUntilMs;
    }

    public void setSilianCooldownUntil(long untilMs) {
        _silianCooldownUntilMs = untilMs;
        // 同步至 character_other（以秒為單位），使用技能1欄位作為共用備援
        try {
            if (get_other() != null) {
                int untilS = (int) (untilMs / 1000L);
                get_other().set_silian_cd1_until_s(untilS);
                com.lineage.server.datatables.sql.CharOtherTable tbl = new com.lineage.server.datatables.sql.CharOtherTable();
                tbl.storeOther(getId(), get_other());
            }
        } catch (Throwable ignore) {}
    }

    public long getSilianCooldownUntil() {
        return _silianCooldownUntilMs;
    }

    // 絲莉安三技能獨立冷卻
    private long _silianCooldown1UntilMs = 0L;
    private long _silianCooldown2UntilMs = 0L;
    private long _silianCooldown3UntilMs = 0L;

    public long getSilianCooldown1Until() { return _silianCooldown1UntilMs; }
    public long getSilianCooldown2Until() { return _silianCooldown2UntilMs; }
    public long getSilianCooldown3Until() { return _silianCooldown3UntilMs; }

    public void setSilianCooldown1Until(long untilMs) { _silianCooldown1UntilMs = untilMs; }
    public void setSilianCooldown2Until(long untilMs) { _silianCooldown2UntilMs = untilMs; }
    public void setSilianCooldown3Until(long untilMs) { _silianCooldown3UntilMs = untilMs; }

    public long getSilianCooldownUntilForSkill(int skillId) {
        switch (skillId) {
            case 2: return _silianCooldown2UntilMs;
            case 3: return _silianCooldown3UntilMs;
            case 1:
            default: return _silianCooldown1UntilMs;
        }
    }

    public void setSilianCooldownUntilForSkill(int skillId, long untilMs) {
        switch (skillId) {
            case 2: _silianCooldown2UntilMs = untilMs; break;
            case 3: _silianCooldown3UntilMs = untilMs; break;
            case 1:
            default: _silianCooldown1UntilMs = untilMs; break;
        }
        // 寫入 character_other 的對應欄位（以秒）
        try {
            if (get_other() != null) {
                int untilS = (int) (untilMs / 1000L);
                switch (skillId) {
                    case 2: get_other().set_silian_cd2_until_s(untilS); break;
                    case 3: get_other().set_silian_cd3_until_s(untilS); break;
                    case 1:
                    default: get_other().set_silian_cd1_until_s(untilS); break;
                }
                com.lineage.server.datatables.sql.CharOtherTable tbl = new com.lineage.server.datatables.sql.CharOtherTable();
                tbl.storeOther(getId(), get_other());
            }
        } catch (Throwable ignore) {}
    }

    /**
     * 加載阿頓星盤：自動套用所有「非技能節點」的已完成能力（不用點擊啟用）
     */
    public final void addAttonAstrologyPowers() {
        for (Integer key : AttonAstrologyTable.get().getIndexArray()) {
            AttonAstrologyData data = AttonAstrologyTable.get().getData(key);
            if (data == null) continue;
            try {
                if (getQuest().isEnd(data.getQuestId()) && data.getSkillId() == 0) {
                    addAstrologyPower(data, key);
                    java.util.concurrent.TimeUnit.MILLISECONDS.sleep(5);
                }
            } catch (InterruptedException e) {
                _log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 加載絲莉安星盤：自動套用所有「非技能節點」的已完成能力
     */
    public final void addSilianAstrologyPowers() {
        for (Integer key : com.add.Tsai.Astrology.SilianAstrologyTable.get().getIndexArray()) {
            com.add.Tsai.Astrology.SilianAstrologyData data = com.add.Tsai.Astrology.SilianAstrologyTable.get().getData(key);
            if (data == null) continue;
            try {
                if (getQuest().isEnd(data.getQuestId()) && data.getSkillId() == 0) {
                    // 轉為阿頓模型套用：僅用到 Hp/耐性/負重/HPR/MPR/減傷/特效
                    // 直接使用絲莉安專用 effectBuff
                    com.add.Tsai.Astrology.SilianAstrologyTable.effectBuff(this, data, 1);
                    java.util.concurrent.TimeUnit.MILLISECONDS.sleep(5);
                }
            } catch (InterruptedException e) {
                _log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 加載格立特星盤：自動套用所有「非技能節點」的已完成能力
     */
    public final void addGritAstrologyPowers() {
        for (Integer key : com.add.Tsai.Astrology.GritAstrologyTable.get().getIndexArray()) {
            com.add.Tsai.Astrology.GritAstrologyData data = com.add.Tsai.Astrology.GritAstrologyTable.get().getData(key);
            if (data == null) continue;
            try {
                if (getQuest().isEnd(data.getQuestId()) && data.getSkillId() == 0) {
                    com.add.Tsai.Astrology.GritAstrologyTable.effectBuff(this, data, 1);
                    java.util.concurrent.TimeUnit.MILLISECONDS.sleep(5);
                }
            } catch (InterruptedException e) {
                _log.error(e.getMessage(), e);
            }
        }
        // 套用完所有非技能節點後，重新統計注入一次永久爆擊吸收
        try {
            com.add.Tsai.Astrology.GritAstrologyTable.refreshPassiveAbsorb(this);
        } catch (Throwable ignore) {}
    }

    /**
     * 加載依詩蒂星盤：自動套用所有「非技能節點」的已完成能力
     */
    public final void addYishidiAstrologyPowers() {
        for (Integer key : com.add.Tsai.Astrology.YishidiAstrologyTable.get().getIndexArray()) {
            com.add.Tsai.Astrology.YishidiAstrologyData data = com.add.Tsai.Astrology.YishidiAstrologyTable.get().getData(key);
            if (data == null) continue;
            try {
                if (getQuest().isEnd(data.getQuestId()) && data.getSkillId() == 0) {
                    com.add.Tsai.Astrology.YishidiAstrologyTable.effectBuff(this, data, 1);
                    java.util.concurrent.TimeUnit.MILLISECONDS.sleep(5);
                }
            } catch (InterruptedException e) {
                _log.error(e.getMessage(), e);
            }
        }
    }

    // 移除所有阿頓星盤效果（確保同時僅一組技能生效）
    public final void clearAttonAstrologyPower() {
        try {
            for (AttonAstrologyData v : ATTON_ASTROLOGY_DATA_MAP.values()) {
                AttonAstrologyTable.effectBuff(this, v, -1);
            }
        } catch (Exception ignore) {
        } finally {
            ATTON_ASTROLOGY_DATA_MAP.clear();
            // 保險重置阿頓專屬參數
            setAttonProcChance(0);
            setAttonProcReduce(0);
            setLeechChance(0);
            setLeechAmount(0);
            setLeechGfx1(0);
            setLeechGfx2(0);
        }
    }

    public int getprobability() {
        return this._probability;
    }

    public void setprobability(int probability) {
        this._probability = probability;
    }

    public void setbbdmg(boolean bbdmg) {
        this._bbdmg = bbdmg;
    }

    public boolean getbbdmg() {
        return this._bbdmg;
    }

    public void setbbdmg1(boolean bbdmg1) {
        this._bbdmg1 = bbdmg1;
    }

    public boolean getbbdmg1() {
        return this._bbdmg1;
    }

    public void setbbdmg2(boolean bbdmg2) {
        this._bbdmg2 = bbdmg2;
    }

    public boolean getbbdmg2() {
        return this._bbdmg2;
    }

    public void setbbdmg3(boolean bbdmg3) {
        this._bbdmg3 = bbdmg3;
    }

    public boolean getbbdmg3() {
        return this._bbdmg3;
    }

    public int getCounterattack() {
        return counterattack;
    }

    public void addCounterattack(final int i) {
        this.counterattack += i;
    }

    public int getBowcounterattack() {
        return bowcounterattack;
    }

    public void addBowcounterattack(final int i) {
        this.bowcounterattack += i;
    }

    public int get_redbluejoin() {
        return this._redbluejoin;
    }

    public void set_redbluejoin(int redbluejoin) {
        this._redbluejoin = redbluejoin;
    }

    public int get_redblueroom() {
        return this._redblueroom;
    }

    public void set_redblueroom(int redblueroom) {
        this._redblueroom = redblueroom;
    }

    public int get_redblueleader() {
        return this._redblueleader;
    }

    public void set_redblueleader(int redblueleader) {
        this._redblueleader = redblueleader;
    }

    public int get_redbluepoint() {
        return this._redbluepoint;
    }

    public void set_redbluepoint(int redbluepoint) {
        this._redbluepoint = redbluepoint;
    }

    public void sendDetails() {
        // XXX 7.6 ADD
        this.sendPackets(new S_PacketBoxCharEr(this));// 角色迴避率更新
        // XXX 7.6 能力基本資訊-力量
        this.sendPackets(new S_StrDetails(2, L1ClassFeature.calcStrDmg(this.getStr(), this.getBaseStr()), L1ClassFeature.calcStrHit(this.getStr(), this.getBaseStr()), L1ClassFeature.calcStrDmgCritical(this.getStr(), this.getBaseStr()), L1ClassFeature.calcAbilityMaxWeight(this.getStr(), this.getCon())));
        // XXX 7.6 重量程度資訊
        this.sendPackets(new S_WeightStatus(this.getInventory().getWeight100(), this.getInventory().getWeight(), (int) this.getMaxWeight()));
        // XXX 7.6 能力基本資訊-智力
        this.sendPackets(new S_IntDetails(2, L1ClassFeature.calcIntMagicDmg(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicHit(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicCritical(this.getInt(), this.getBaseInt()), L1ClassFeature.calcIntMagicBonus(this.getType(), this.getInt()), L1ClassFeature.calcIntMagicConsumeReduction(this.getInt())));
        // XXX 7.6 能力基本資訊-精神
        this.sendPackets(new S_WisDetails(2, L1ClassFeature.calcWisMpr(this.getWis(), this.getBaseWis()), L1ClassFeature.calcWisPotionMpr(this.getWis(), this.getBaseWis()), L1ClassFeature.calcStatMr(this.getWis()) + L1ClassFeature.newClassFeature(this.getType()).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(this.getType(), this.getBaseWis())));
        // XXX 7.6 能力基本資訊-敏捷
        this.sendPackets(new S_DexDetails(2, L1ClassFeature.calcDexDmg(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexHit(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexDmgCritical(this.getDex(), this.getBaseDex()), L1ClassFeature.calcDexAc(this.getDex()), L1ClassFeature.calcDexEr(this.getDex())));
        // XXX 7.6 能力基本資訊-體質
        this.sendPackets(new S_ConDetails(2, L1ClassFeature.calcConHpr(this.getCon(), this.getBaseCon()), L1ClassFeature.calcConPotionHpr(this.getCon(), this.getBaseCon()), L1ClassFeature.calcAbilityMaxWeight(this.getStr(), this.getCon()), L1ClassFeature.calcBaseClassLevUpHpUp(this.getType()) + L1ClassFeature.calcBaseConLevUpExtraHpUp(this.getType(), this.getBaseCon())));
        // XXX 7.6 重量程度資訊
        this.sendPackets(new S_WeightStatus(this.getInventory().getWeight() * 100 / (int) this.getMaxWeight(), this.getInventory().getWeight(), (int) this.getMaxWeight()));
        // XXX 7.6 純能力詳細資訊 階段:25
        this.sendPackets(new S_BaseAbilityDetails(25));
        // XXX 7.6 純能力詳細資訊 階段:35
        this.sendPackets(new S_BaseAbilityDetails(35));
        // XXX 7.6 純能力詳細資訊 階段:45
        this.sendPackets(new S_BaseAbilityDetails(45));
        // XXX 7.6 純能力資訊
        this.sendPackets(new S_BaseAbility(this.getBaseStr(), this.getBaseInt(), this.getBaseWis(), this.getBaseDex(), this.getBaseCon(), this.getBaseCha()));
        // XXX 7.6 萬能藥使用數量
        this.sendPackets(new S_ElixirCount(this.getElixirStats()));
    }

    public boolean getPbavatar() {
        return _Pbavatar;
    }

    public void setPbavatar(boolean Pbavatar) {
        _Pbavatar = Pbavatar;
    }

    public boolean getPbavataron() {
        return _Pbavataron;
    }

    public void setPbavataron(boolean Pbavataron) {
        _Pbavataron = Pbavataron;
    }

    public int getPbacount() {
        return _Pbacount;
    }

    public void setPbacount(int i) {
        _Pbacount = i;
    }

    public int getGraceLv() {
        return graceLv;
    }

    public void setGraceLv(int i) {
        graceLv = i - 80;
        if (graceLv < 0) {
            graceLv = 0;
        } else if (graceLv > 5) {
            graceLv = 5;
        }
    }

    public int getImpactUp() {
        return impactUp;
    }

    public void setImpactUp(int i) {
        impactUp = i;
    }

    public int getRisingUp() {
        return risingUp;
    }

    public void setRisingUp(int i) {
        risingUp = i;
    }

    public L1ItemInstance getSecondWeapon() {
        return _secondweapon;
    }

    public void setSecondWeapon(L1ItemInstance weapon) {
        _secondweapon = weapon;
    }

    public int getComboCount() {
        return this.comboCount;
    }

    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    public boolean getSafetyZone() {
        return isSafetyZone;
    }

    public void setSafetyZone(boolean value) {
        isSafetyZone = value;
    }

    public int getTeam() {
        return SiegeTeam;
    }

    public void setTeam(int i) {
        SiegeTeam = i;
    }

    public UserMonsterBook getMonsterBook() {
        return _monsterBook;
    }

    public void setMonsterBook(UserMonsterBook book) {
        _monsterBook = book;
    }

    public UserWeekQuest getWeekQuest() {
        return _weekQuest;
    }

    public void setWeekQuest(UserWeekQuest quest) {
        _weekQuest = quest;
    }

    public int[] getReincarnationSkill() {
        return reincarnationSkill;
    }

    public int getTurnLifeSkillCount() {
        return _turnLifeSkillCount;
    }

    public void setTurnLifeSkillCount(final int i) {
        _turnLifeSkillCount = i;
    }

    public final int getSi() {
        return si;
    }

    public final void setSi(final int si) {
        this.si = si;
    }

    /**
     * 增加PVP傷害
     *
     */
    public int getPvpDmg() {
        return _PvpDmg;
    }

    /**
     * 增加PVP傷害
     *
     */
    public void setPvpDmg(final int i) {
        _PvpDmg += i;
    }

    /**
     * 减免PVP傷害
     *
     */
    public int getPvpDmg_R() {
        return _PvpDmg_R;
    }

    /**
     * 减免PVP傷害
     *
     */
    public void setPvpDmg_R(final int i) {
        _PvpDmg_R += i;
    }

    // 阿頓星盤 隨機減傷設定/取得
    public void setAttonProcChance(int value) { _attonProcChance = Math.max(0, value); }
    public int getAttonProcChance() { return _attonProcChance; }
    public void setAttonProcReduce(int value) { _attonProcReduce = Math.max(0, value); }
    public int getAttonProcReduce() { return _attonProcReduce; }
    public void setLeechChance(int value) { _leechChance = Math.max(0, value); }
    public int getLeechChance() { return _leechChance; }
    public void setLeechAmount(int value) { _leechAmount = Math.max(0, value); }
    public int getLeechAmount() { return _leechAmount; }
    public void setLeechGfx1(int v) { _leechGfx1 = Math.max(0, v); }
    public int getLeechGfx1() { return _leechGfx1; }
    public void setLeechGfx2(int v) { _leechGfx2 = Math.max(0, v); }
    public int getLeechGfx2() { return _leechGfx2; }

    /**
     * 正義滿角色名稱變黃
     *
     */
    public boolean isLawfulName() {
        return _LawfulName;
    }

    /**
     * 正義滿角色名稱變黃
     *
     */
    public void setLawfulName(final boolean flag) {
        _LawfulName = flag;
    }

    public final L1ArmorKitPower getArmorKitPower() {
        return _armorKitPower;
    }

    public final List<Integer> getArmorList() {
        if (_armorKit == null) {
            _armorKit = new ArrayList<>();
        }
        return _armorKit;
    }

    public final void addArmorKit(final int value) {
        if (value <= 0) {
            return;
        }
        if (_armorKit == null) {
            _armorKit = new ArrayList<>();
        }
        final Set<Integer> uniqueSet2 = new HashSet<>(this.getArmorList());
        for (int kitType : uniqueSet2) {
            final int kitCount = Collections.frequency(this.getArmorList(), kitType);
            _armorKitPower = ArmorKitPowerTable.getInstance().get(kitType, kitCount);
            if (_armorKitPower != null) {
                ArmorKitPowerTable.effectBuff(this, _armorKitPower, -1);
            }
        }
        _armorKit.add(value);
        final Set<Integer> uniqueSet = new HashSet<>(this.getArmorList());
        for (int kitType : uniqueSet) {
            final int kitCount = Collections.frequency(this.getArmorList(), kitType);
            _armorKitPower = ArmorKitPowerTable.getInstance().get(kitType, kitCount);
            if (_armorKitPower != null) {
                ArmorKitPowerTable.effectBuff(this, _armorKitPower, 1);
            }
        }
    }

    public final void removeArmorKit(final int value) {
        if (value <= 0) {
            return;
        }
        if (_armorKit == null) {
            _armorKit = new ArrayList<>();
        }
        final Set<Integer> uniqueSet = new HashSet<>(this.getArmorList());
        for (int kitType : uniqueSet) {
            final int kitCount = Collections.frequency(this.getArmorList(), kitType);
            _armorKitPower = ArmorKitPowerTable.getInstance().get(kitType, kitCount);
            if (_armorKitPower != null) {
                ArmorKitPowerTable.effectBuff(this, _armorKitPower, -1);
            }
        }
        _armorKit.remove(Integer.valueOf(value));
        final Set<Integer> uniqueSet2 = new HashSet<>(this.getArmorList());
        for (int kitType : uniqueSet2) {
            final int kitCount = Collections.frequency(this.getArmorList(), kitType);
            _armorKitPower = ArmorKitPowerTable.getInstance().get(kitType, kitCount);
            if (_armorKitPower != null) {
                ArmorKitPowerTable.effectBuff(this, _armorKitPower, 1);
            }
        }
    }

    public int get_aistay() {
        return this._aistay;
    }

    public void set_aistay(int aistay) {
        this._aistay = aistay;
    }

    public int[] get_aixyz() {
        return this._aixyz;
    }

    public void set_aixyz(int[] aixyz) {
        this._aixyz = aixyz;
    }

    public void addAITimer() {
        this.setAITimer(this._ai_timer - 1);
    }

    public int getAITimer() {
        return this._ai_timer;
    }

    public void setAITimer(int time) {
        this._ai_timer = time;
    }

    public void setAIERROR() {
        this._ai_error = 0;
    }

    public void addAIERROR() {
        this._ai_error += 1;
    }

    public int getAIERROR() {
        return this._ai_error;
    }

    // 成長果實系統(Tam幣)
    public int tamcount() {
        Connection con = null;
        Connection con2 = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        Timestamp tamtime = null;
        int count = 0;
        long sysTime = System.currentTimeMillis();
        int char_objid = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `characters` WHERE account_name = ?"); // キャラクターテーブルで選ん来
            pstm.setString(1, getAccountName());
            rs = pstm.executeQuery();
            while (rs.next()) {
                tamtime = rs.getTimestamp("TamEndTime");
                char_objid = rs.getInt("objid");
                if (tamtime != null) {
                    if (sysTime <= tamtime.getTime()) {
                        count++;
                    } else {
                        if (Tam_wait_count(char_objid) != 0) {
                            int day = Nexttam(char_objid);
                            if (day != 0) {
                                Timestamp deleteTime = null;
                                deleteTime = new Timestamp(sysTime + 86400000 * (long) day + 10000);// 7日
                                // deleteTime = new Timestamp(sysTime +
                                // 1000*60);//7日
                                if (getId() == char_objid) {
                                    setTamTime(deleteTime);
                                }
                                con2 = DatabaseFactory.get().getConnection();
                                pstm2 = con2.prepareStatement("UPDATE `characters` SET TamEndTime=? WHERE account_name = ? AND objid = ?"); // キャラクターテーブルで君主だけ上がってきて
                                pstm2.setTimestamp(1, deleteTime);
                                pstm2.setString(2, getAccountName());
                                pstm2.setInt(3, char_objid);
                                pstm2.executeUpdate();
                                tamdel(char_objid);
                                count++;
                            }
                        }
                    }
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return count;
        } finally {
            SQLUtil.close(pstm2);
            SQLUtil.close(con2);
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void tamdel(int objectId) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("delete from Tam where objid = ? order by id asc limit 1");
            pstm.setInt(1, objectId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public int Nexttam(int objectId) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int day = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT day FROM `tam` WHERE objid = ? order by id asc limit 1"); // キャラクターテーブルで君主だけを選んで来て
            pstm.setInt(1, objectId);
            rs = pstm.executeQuery();
            while (rs.next()) {
                day = rs.getInt("Day");
            }
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return day;
    }

    public int Tam_wait_count(int charid) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `tam` WHERE objid = ?");
            pstm.setInt(1, charid);
            rs = pstm.executeQuery();
            while (rs.next()) {
                count = getId();
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return count;
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public long TamTime() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Timestamp tamtime = null;
        long time = 0;
        long sysTime = System.currentTimeMillis();
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT `TamEndTime` FROM `characters` WHERE account_name = ? ORDER BY `TamEndTime` ASC"); // キャラクター
            pstm.setString(1, getAccountName());
            rs = pstm.executeQuery();
            while (rs.next()) {
                tamtime = rs.getTimestamp("TamEndTime");
                if (tamtime != null) {
                    if (sysTime < tamtime.getTime()) {
                        time = tamtime.getTime() - sysTime;
                        break;
                    }
                }
            }
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            return time;
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public Timestamp getTamTime() {
        return _tamTime;
    }

    public void setTamTime(Timestamp time) {
        _tamTime = time;
    }

    public L1BookMark[] getBookMarkArray() {
        return _bookmarks.toArray(new L1BookMark[_bookmarks.size()]);
    }

    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    public L1BookMark[] getSpeedBookMarkArray() {
        return _speedbookmarks.toArray(new L1BookMark[_speedbookmarks.size()]);
    }

    public int getMark_count() {
        return _markcount;
    }

    public void setMark_count(int i) {
        _markcount = i;
    }

    public L1BookMark getBookMark(String name) {
        L1BookMark element = null;
        int size = _bookmarks.size();
        for (int i = 0; i < size; i++) {
            element = _bookmarks.get(i);
            if (element == null) {
                continue;
            }
            if (element.getName().equalsIgnoreCase(name)) {
                return element;
            }
        }
        return null;
    }

    public L1BookMark getBookMark(int id) {
        L1BookMark element = null;
        int size = _bookmarks.size();
        for (int i = 0; i < size; i++) {
            element = _bookmarks.get(i);
            if (element == null) {
                continue;
            }
            if (element.getId() == id) {
                return element;
            }
        }
        return null;
    }

    public int getBookMarkSize() {
        return _bookmarks.size();
    }

    public void addBookMark(L1BookMark book) {
        _bookmarks.add(book);
    }

    // 自動鋪組 end
    public void removeBookMark(L1BookMark book) {
        _bookmarks.remove(book);
    }

    public L1Inventory getTradeWindowInventory() { // 交易視窗
        return this._tradewindow;
    }
    public void beginExpMonitor() { // l1j-tw連續攻擊
        _atkMonitorFuture = GeneralThreadPool.get().scheduleAtFixedRate(new L1PcAtkMonitor(getId()), 0L, 300);
    }

    public void beginMoveHandDarkness() { // 黑暗之手 左右移動邊更
        _moveHandDarkness = GeneralThreadPool.get().scheduleAtFixedRate(new L1DarknessMonitor(getId()), 0L, 500);
    }

    public void stopMoveHandDarkness() { // 黑暗之手 左右移動邊更
        if (_moveHandDarkness != null) {
            _moveHandDarkness.cancel(true);
            _moveHandDarkness = null;
        }
        this.flushedLocation();
    }

    /**
     * 自動補血道具 -> 第一組時間軸(開始)
     */
    public void startSkillSound_autoHP1(final int delay) {
        final int INTERVAL_HP1 = delay + 100; // 間隔時間
        if (!_SkillSoundActiveAutoHp1) {
            _SkillSoundAutoHp1 = new SkillSoundHp1(this);
            if (_autoHp1Future != null) {
                _autoHp1Future.cancel(false);
            }
            _autoHp1Future = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSoundAutoHp1, INTERVAL_HP1, INTERVAL_HP1);
            _SkillSoundActiveAutoHp1 = true;
        }
    }

    /**
     * 自動補血道具 -> 第一組時間軸(停止)
     */
    public void stopSkillSound_autoHP1() {
        if (_SkillSoundActiveAutoHp1) {
            if (_autoHp1Future != null) {
                _autoHp1Future.cancel(false);
                _autoHp1Future = null;
            }
            _SkillSoundAutoHp1.cancel();
            _SkillSoundAutoHp1 = null;
            _SkillSoundActiveAutoHp1 = false;
            setAutoHp1(false);
        }
    }

    /**
     * 自動補血道具 -> 第二組時間軸(開始)
     */
    public void startSkillSound_autoHP2(final int delay) {
        final int INTERVAL_HP2 = delay + 100; // 間隔時間
        if (!_SkillSoundActiveAutoHp2) {
            _SkillSoundAutoHp2 = new SkillSoundHp2(this);
            if (_autoHp2Future != null) {
                _autoHp2Future.cancel(false);
            }
            _autoHp2Future = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSoundAutoHp2, INTERVAL_HP2, INTERVAL_HP2);
            _SkillSoundActiveAutoHp2 = true;
        }
    }

    /**
     * 自動補血道具 -> 第二組時間軸(停止)
     */
    public void stopSkillSound_autoHP2() {
        if (_SkillSoundActiveAutoHp2) {
            if (_autoHp2Future != null) {
                _autoHp2Future.cancel(false);
                _autoHp2Future = null;
            }
            _SkillSoundAutoHp2.cancel();
            _SkillSoundAutoHp2 = null;
            _SkillSoundActiveAutoHp2 = false;
            setAutoHp2(false);
        }
    }

    /**
     * 自動補血道具 -> 第三組時間軸(開始)
     */
    public void startSkillSound_autoHP3(final int delay) {
        final int INTERVAL_HP3 = delay + 100; // 間隔時間
        if (!_SkillSoundActiveAutoHp3) {
            _SkillSoundAutoHp3 = new SkillSoundHp3(this);
            if (_autoHp3Future != null) {
                _autoHp3Future.cancel(false);
            }
            _autoHp3Future = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSoundAutoHp3, INTERVAL_HP3, INTERVAL_HP3);
            _SkillSoundActiveAutoHp3 = true;
        }
    }

    /**
     * 自動補血道具 -> 第三組時間軸(停止)
     */
    public void stopSkillSound_autoHP3() {
        if (_SkillSoundActiveAutoHp3) {
            if (_autoHp3Future != null) {
                _autoHp3Future.cancel(false);
                _autoHp3Future = null;
            }
            _SkillSoundAutoHp3.cancel();
            _SkillSoundAutoHp3 = null;
            _SkillSoundActiveAutoHp3 = false;
            setAutoHp3(false);
        }
    }

    /**
     * 自動補魔 -> 第一組 道具時間軸(開始)
     */
    public void startSkillSound_autoMP1() {
        final int INTERVAL_MP1 = 1000;
        if (!_SkillSoundActiveAutoMp1) {
            _SkillSoundAutoMp1 = new SkillSoundMp1(this);
            if (_autoMp1Future != null) {
                _autoMp1Future.cancel(false);
            }
            _autoMp1Future = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSoundAutoMp1, INTERVAL_MP1, INTERVAL_MP1);
            _SkillSoundActiveAutoMp1 = true;
        }
    }

    /**
     * 自動補魔 -> 第一組 道具時間軸(停止)
     */
    public void stopSkillSound_autoMP1() {
        if (_SkillSoundActiveAutoMp1) {
            if (_autoMp1Future != null) {
                _autoMp1Future.cancel(false);
                _autoMp1Future = null;
            }
            _SkillSoundAutoMp1.cancel();
            _SkillSoundAutoMp1 = null;
            _SkillSoundActiveAutoMp1 = false;
            setAutoMp1(false);
        }
    }

    /**
     * 自動補魔 -> 第二組 魂體時間軸(開始)
     */
    public void startSkillSound_autoMP2() {
        final int INTERVAL_MP2 = 2400;
        if (!_SkillSoundActiveAutoMp2) {
            _SkillSoundAutoMp2 = new SkillSoundMp2(this);
            if (_autoMp2Future != null) {
                _autoMp2Future.cancel(false);
            }
            _autoMp2Future = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSoundAutoMp2, INTERVAL_MP2, INTERVAL_MP2);
            _SkillSoundActiveAutoMp2 = true;
        }
    }

    /**
     * 自動補魔 -> 第二組 魂體時間軸(停止)
     */
    public void stopSkillSound_autoMP2() {
        if (_SkillSoundActiveAutoMp2) {
            if (_autoMp2Future != null) {
                _autoMp2Future.cancel(false);
                _autoMp2Future = null;
            }
            _SkillSoundAutoMp2.cancel();
            _SkillSoundAutoMp2 = null;
            _SkillSoundActiveAutoMp2 = false;
            setAutoMp2(false);
        }
    }

    /**
     * 自動回村時間軸(開始)
     */
    public void startSkillSound_autoBackHome() {
        final int INTERVAL_BackHome = 1000; // 間隔時間
        if (!_SkillSoundActiveAutoBackHome) {
            _SkillSoundAutoBackHome = new SkillSoundBackHome(this);
            if (_autoBackHomeFuture != null) {
                _autoBackHomeFuture.cancel(false);
            }
            _autoBackHomeFuture = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSoundAutoBackHome, INTERVAL_BackHome, INTERVAL_BackHome);
            _SkillSoundActiveAutoBackHome = true;
        }
    }

    /**
     * 自動回村時間軸(停止)
     */
    public void stopSkillSound_autoBackHome() {
        if (_SkillSoundActiveAutoBackHome) {
            if (_autoBackHomeFuture != null) {
                _autoBackHomeFuture.cancel(false);
                _autoBackHomeFuture = null;
            }
            _SkillSoundAutoBackHome.cancel();
            _SkillSoundAutoBackHome = null;
            _SkillSoundActiveAutoBackHome = false;
            setAutoBackHome(false);
        }
    }

    public boolean isAutoHpAll() {
        return _isAutoHpAll;
    }

    public void setAutoHpAll(final boolean flag) {
        _isAutoHpAll = flag;
    }

    public boolean isAutoHp1() {
        return _isAutoHp1;
    }

    public void setAutoHp1(final boolean flag) {
        _isAutoHp1 = flag;
    }

    public boolean isAutoHp2() {
        return _isAutoHp2;
    }

    public void setAutoHp2(final boolean flag) {
        _isAutoHp2 = flag;
    }

    public boolean isAutoHp3() {
        return _isAutoHp3;
    }

    public void setAutoHp3(final boolean flag) {
        _isAutoHp3 = flag;
    }

    public boolean isAutoBackHome() {
        return _isAutoBackHome;
    }

    public void setAutoBackHome(final boolean flag) {
        _isAutoBackHome = flag;
    }

    public boolean isAutoMp1() {
        return _isAutoMp1;
    }

    public void setAutoMp1(final boolean flag) {
        _isAutoMp1 = flag;
    }

    public boolean isAutoMp2() {
        return _isAutoMp2;
    }

    public void setAutoMp2(final boolean flag) {
        _isAutoMp2 = flag;
    }

    public int get_backpage() {
        return this._backpage;
    }

    public void set_backpage(int _backpage) {
        this._backpage = _backpage;
    }

    public int getTemporary() {
        return _temporary;
    }

    public void setTemporary(final int i) {
        _temporary = i;
    }

    public int getTextHp1() {
        return _texthp1;
    }

    public void setTextHp1(final int i) {
        _texthp1 = i;
    }

    public int getTextHp2() {
        return _texthp2;
    }

    public void setTextHp2(final int i) {
        _texthp2 = i;
    }

    public int getTextHp3() {
        return _texthp3;
    }

    public void setTextHp3(final int i) {
        _texthp3 = i;
    }

    public int getTextMp1() {
        return _textmp1;
    }

    public void setTextMp1(final int i) {
        _textmp1 = i;
    }

    public int getTextMp2() {
        return _textmp2;
    }

    public void setTextMp2(final int i) {
        _textmp2 = i;
    }

    public int getTextBh() {
        return _textbh;
    }

    public void setTextBh(final int i) {
        _textbh = i;
    }

    public int getAutoItemId1() {
        return _autoItemId1;
    }

    public void setAutoItemId1(final int i) {
        _autoItemId1 = i;
    }

    public int getAutoItemId2() {
        return _autoItemId2;
    }

    public void setAutoItemId2(final int i) {
        _autoItemId2 = i;
    }

    public int getAutoItemId3() {
        return _autoItemId3;
    }

    public void setAutoItemId3(final int i) {
        _autoItemId3 = i;
    }

    public int getAutoItemId4() {
        return _autoItemId4;
    }

    public void setAutoItemId4(final int i) {
        _autoItemId4 = i;
    }

    /**
     * 自動鋪助道具時間軸(開始)
     */
    public void startSkillSound_autoItemAll() {
        final int INTERVAL_ItemAll = 2500; // 間隔時間
        if (!_SkillSoundActiveAutoItemAll) {
            _SkillSoundAutoItemAll = new SkillSoundItemAll(this);
            if (_autoItemAllFuture != null) {
                _autoItemAllFuture.cancel(false);
            }
            _autoItemAllFuture = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSoundAutoItemAll, INTERVAL_ItemAll, INTERVAL_ItemAll);
            _SkillSoundActiveAutoItemAll = true;
        }
    }

    /**
     * 自動鋪助道具時間軸(停止)
     */
    public void stopSkillSound_autoItemAll() {
        if (_SkillSoundActiveAutoItemAll) {
            if (_autoItemAllFuture != null) {
                _autoItemAllFuture.cancel(false);
                _autoItemAllFuture = null;
            }
            _SkillSoundAutoItemAll.cancel();
            _SkillSoundAutoItemAll = null;
            _SkillSoundActiveAutoItemAll = false;
            setAutoItemAll(false);
        }
    }

    public boolean isAutoItemAll() {
        return _isAutoItemAll;
    }

    public void setAutoItemAll(final boolean flag) {
        _isAutoItemAll = flag;
    }

    public boolean isAutoItem1() {
        return _isAutoItem1;
    }

    public void setAutoItem1(final boolean flag) {
        _isAutoItem1 = flag;
    }

    public boolean isAutoItem2() {
        return _isAutoItem2;
    }

    public void setAutoItem2(final boolean flag) {
        _isAutoItem2 = flag;
    }

    public boolean isAutoItem3() {
        return _isAutoItem3;
    }

    public void setAutoItem3(final boolean flag) {
        _isAutoItem3 = flag;
    }

    public boolean isAutoItem4() {
        return _isAutoItem4;
    }

    public void setAutoItem4(final boolean flag) {
        _isAutoItem4 = flag;
    }

    public boolean isAutoItem5() {
        return _isAutoItem5;
    }

    public void setAutoItem5(final boolean flag) {
        _isAutoItem5 = flag;
    }

    public boolean isAutoItem6() {
        return _isAutoItem6;
    }

    public void setAutoItem6(final boolean flag) {
        _isAutoItem6 = flag;
    }

    public boolean isAutoItem7() {
        return _isAutoItem7;
    }

    public void setAutoItem7(final boolean flag) {
        _isAutoItem7 = flag;
    }

    public boolean isAutoItem8() {
        return _isAutoItem8;
    }

    public void setAutoItem8(final boolean flag) {
        _isAutoItem8 = flag;
    }

    public boolean isAutoItem9() {
        return _isAutoItem9;
    }

    public void setAutoItem9(final boolean flag) {
        _isAutoItem9 = flag;
    }

    public boolean isAutoItem10() {
        return _isAutoItem10;
    }

    public void setAutoItem10(final boolean flag) {
        _isAutoItem10 = flag;
    }

    public boolean isAutoItem11() {
        return _isAutoItem11;
    }

    public void setAutoItem11(final boolean flag) {
        _isAutoItem11 = flag;
    }

    public boolean isAutoItem12() {
        return _isAutoItem12;
    }

    public void setAutoItem12(final boolean flag) {
        _isAutoItem12 = flag;
    }

    public boolean isAutoItem13() {
        return _isAutoItem13;
    }

    public void setAutoItem13(final boolean flag) {
        _isAutoItem13 = flag;
    }

    /**
     * 自動輔助狀態(魔法) -> 全職時間軸(開始)
     */
    public void startSkillSound_autoSkillAll() {
        final int INTERVAL_SkillAll = 3000; // 間隔時間
        if (!_SkillSoundActiveAutoSkillAll) {
            _SkillSoundAutoSkillAll = new SkillSoundSkillAll(this);
            if (_autoSkillAllFuture != null) {
                _autoSkillAllFuture.cancel(false);
            }
            _autoSkillAllFuture = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSoundAutoSkillAll, INTERVAL_SkillAll, INTERVAL_SkillAll);
            _SkillSoundActiveAutoSkillAll = true;
        }
    }

    /**
     * 自動輔助狀態(魔法) -> 全職時間軸(停止)
     */
    public void stopSkillSound_autoSkillAll() {
        if (_SkillSoundActiveAutoSkillAll) {
            if (_autoSkillAllFuture != null) {
                _autoSkillAllFuture.cancel(false);
                _autoSkillAllFuture = null;
            }
            _SkillSoundAutoSkillAll.cancel();
            _SkillSoundAutoSkillAll = null;
            _SkillSoundActiveAutoSkillAll = false;
            setAutoSkillAll(false);
        }
    }

    public boolean isAutoSkillAll() {
        return _isAutoSkillAll;
    }

    public void setAutoSkillAll(final boolean flag) {
        _isAutoSkillAll = flag;
    }

    public boolean isAutoSkill_1() {
        return _AutoSkill_1;
    }

    public void setAutoSkill_1(final boolean flag) {
        _AutoSkill_1 = flag;
    }

    public boolean isAutoSkill_2() {
        return _AutoSkill_2;
    }

    public void setAutoSkill_2(final boolean flag) {
        _AutoSkill_2 = flag;
    }

    public boolean isAutoSkill_3() {
        return _AutoSkill_3;
    }

    public void setAutoSkill_3(final boolean flag) {
        _AutoSkill_3 = flag;
    }

    public boolean isAutoSkill_4() {
        return _AutoSkill_4;
    }

    public void setAutoSkill_4(final boolean flag) {
        _AutoSkill_4 = flag;
    }

    public boolean isAutoSkill_5() {
        return _AutoSkill_5;
    }

    public void setAutoSkill_5(final boolean flag) {
        _AutoSkill_5 = flag;
    }

    public boolean isAutoSkill_6() {
        return _AutoSkill_6;
    }

    public void setAutoSkill_6(final boolean flag) {
        _AutoSkill_6 = flag;
    }

    public boolean isAutoSkill_7() {
        return _AutoSkill_7;
    }

    public void setAutoSkill_7(final boolean flag) {
        _AutoSkill_7 = flag;
    }

    public boolean isAutoSkill_8() {
        return _AutoSkill_8;
    }

    public void setAutoSkill_8(final boolean flag) {
        _AutoSkill_8 = flag;
    }

    public boolean isAutoSkill_9() {
        return _AutoSkill_9;
    }

    public void setAutoSkill_9(final boolean flag) {
        _AutoSkill_9 = flag;
    }

    public boolean isAutoSkill_10() {
        return _AutoSkill_10;
    }

    public void setAutoSkill_10(final boolean flag) {
        _AutoSkill_10 = flag;
    }

    public boolean isAutoSkill_11() {
        return _AutoSkill_11;
    }

    public void setAutoSkill_11(final boolean flag) {
        _AutoSkill_11 = flag;
    }

    public boolean isAutoSkill_12() {
        return _AutoSkill_12;
    }

    public void setAutoSkill_12(final boolean flag) {
        _AutoSkill_12 = flag;
    }

    public boolean isAutoSkill_13() {
        return _AutoSkill_13;
    }

    public void setAutoSkill_13(final boolean flag) {
        _AutoSkill_13 = flag;
    }

    public boolean isAutoSkill_14() {
        return _AutoSkill_14;
    }

    public void setAutoSkill_14(final boolean flag) {
        _AutoSkill_14 = flag;
    }

    public boolean isAutoSkill_15() {
        return _AutoSkill_15;
    }

    public void setAutoSkill_15(final boolean flag) {
        _AutoSkill_15 = flag;
    }

    public boolean isAutoSkill_16() {
        return _AutoSkill_16;
    }

    public void setAutoSkill_16(final boolean flag) {
        _AutoSkill_16 = flag;
    }

    public boolean isAutoSkill_17() {
        return _AutoSkill_17;
    }

    public void setAutoSkill_17(final boolean flag) {
        _AutoSkill_17 = flag;
    }

    public boolean isAutoSkill_18() {
        return _AutoSkill_18;
    }

    public void setAutoSkill_18(final boolean flag) {
        _AutoSkill_18 = flag;
    }

    public boolean isAutoSkill_19() {
        return _AutoSkill_19;
    }

    public void setAutoSkill_19(final boolean flag) {
        _AutoSkill_19 = flag;
    }

    public boolean isAutoSkill_20() {
        return _AutoSkill_20;
    }

    public void setAutoSkill_20(final boolean flag) {
        _AutoSkill_20 = flag;
    }

    public boolean isAutoSkill_21() {
        return _AutoSkill_21;
    }

    public void setAutoSkill_21(final boolean flag) {
        _AutoSkill_21 = flag;
    }

    public boolean isAutoSkill_22() {
        return _AutoSkill_22;
    }

    public void setAutoSkill_22(final boolean flag) {
        _AutoSkill_22 = flag;
    }

    public boolean isAutoSkill_23() {
        return _AutoSkill_23;
    }

    public void setAutoSkill_23(final boolean flag) {
        _AutoSkill_23 = flag;
    }

    public boolean isAutoSkill_24() {
        return _AutoSkill_24;
    }

    public void setAutoSkill_24(final boolean flag) {
        _AutoSkill_24 = flag;
    }

    public boolean isAutoSkill_25() {
        return _AutoSkill_25;
    }

    public void setAutoSkill_25(final boolean flag) {
        _AutoSkill_25 = flag;
    }

    public boolean isAutoSkill_26() {
        return _AutoSkill_26;
    }

    public void setAutoSkill_26(final boolean flag) {
        _AutoSkill_26 = flag;
    }

    public boolean isAutoSkill_27() {
        return _AutoSkill_27;
    }

    public void setAutoSkill_27(final boolean flag) {
        _AutoSkill_27 = flag;
    }

    public boolean isAutoSkill_28() {
        return _AutoSkill_28;
    }

    public void setAutoSkill_28(final boolean flag) {
        _AutoSkill_28 = flag;
    }

    public boolean isAutoSkill_29() {
        return _AutoSkill_29;
    }

    public void setAutoSkill_29(final boolean flag) {
        _AutoSkill_29 = flag;
    }

    public boolean isAutoSkill_30() {
        return _AutoSkill_30;
    }

    public void setAutoSkill_30(final boolean flag) {
        _AutoSkill_30 = flag;
    }

    public boolean isAutoSkill_31() {
        return _AutoSkill_31;
    }

    public void setAutoSkill_31(final boolean flag) {
        _AutoSkill_31 = flag;
    }

    /**
     * 輔助(自動刪物)時間軸(開始)
     */
    public void startSkillSound_autoRemoveItem() {
        final int INTERVAL_RemoveItem = 3000; // 間隔時間
        if (!_SkillSoundActiveAutoRemoveItem) {
            _SkillSoundAutoRemoveItem = new SkillSoundRemoveItem(this);
            if (_autoRemoveItemFuture != null) {
                _autoRemoveItemFuture.cancel(false);
            }
            _autoRemoveItemFuture = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSoundAutoRemoveItem, INTERVAL_RemoveItem, INTERVAL_RemoveItem);
            _SkillSoundActiveAutoRemoveItem = true;
        }
    }

    /**
     * 輔助(自動刪物)時間軸(停止)
     */
    public void stopSkillSound_autoRemoveItem() {
        if (_SkillSoundActiveAutoRemoveItem) {
            if (_autoRemoveItemFuture != null) {
                _autoRemoveItemFuture.cancel(false);
                _autoRemoveItemFuture = null;
            }
            _SkillSoundAutoRemoveItem.cancel();
            _SkillSoundAutoRemoveItem = null;
            _SkillSoundActiveAutoRemoveItem = false;
            setAutoRemoveItem(false);
        }
    }

    public boolean isAutoRemoveItem() {
        return _AutoRemoveItem;
    }

    public void setAutoRemoveItem(final boolean flag) {
        _AutoRemoveItem = flag;
    }

    public void setcheck_lv(final boolean b) {
        check_lv = b;
    }

    public boolean getcheck_lv() {
        return check_lv;
    }

    public boolean getATK_ai() {
        return ATK_ai;
    }

    public void setATK_ai(final boolean b) {
        ATK_ai = b;
    }

    /**
     * 傳回怪物傷害減免(只對怪物有效)
     */
    public int getdolldamageReductionByArmor() {
        int dolldamageReduction = 0;
        if (_dolldamageReductionByArmor > 10) {
            dolldamageReduction = 10 + _random.nextInt(_dolldamageReductionByArmor - 10) + 1;
        } else {
            dolldamageReduction = _dolldamageReductionByArmor;
        }
        return dolldamageReduction;
    }

    /**
     * 增加怪物傷害減免(只對怪物有效)
     */
    public void adddollDamageReductionByArmor(final int i) {
        this._dolldamageReductionByArmor += i;
    }

    public void add_FearLevel(int add) {
        this._fearlevel += add;
    }
    public int get_FearLevel() {
        return this._fearlevel;
    }

    public AcceleratorChecker getAcceleratorChecker() {
        return _acceleratorChecker;
    }

    public boolean Test_Auto() {
        return _autoattack;
    }

    public void set_Test_Auto(boolean b) {
        _autoattack = b;
        if (_autoattack) {
            sendPackets(new S_SystemMessage("開始自動練功"));
        } else {
            sendPackets(new S_SystemMessage("停止自動練功"));
            set_AutoTimeCount(0);
        }
    }

    public int get_AutoTimeCount() {
        return _autocount;
    }

    public void set_AutoTimeCount(int l) {
        _autocount = l;
    }

    /**
     * 結束掛機
     */
    public void endAuto() {
        this.set_Test_Auto(false);
        Random random = new Random();
        final int nx = 33448 + random.nextInt(3);
        final int ny = 32793 + random.nextInt(3);
        L1Teleport.teleport(this, nx, ny, (short) 4, 5, true);
        this.sendPackets(new S_SystemMessage("掛機時間不足故無法掛機"));
        try {
            save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int get_AutoAttackCount() {
        return _autoattackcount;
    }

    public void set_AutoAttackCount(int l) {
        _autoattackcount = l;
    }

    public int get_AutoAttackNpcid() {
        return _autoattacknpcid;
    }

    public void set_AutoAttackNpcid(int l) {
        _autoattacknpcid = l;
    }

    public int get_autoBuyItem1() {
//        return _autoBuyItem1;
        return get_other1().get_type1();
    }

    public void set_autoBuyItem1(int l) {
//        _autoBuyItem1 = l;
        get_other1().set_type1(l);
    }

    public int get_autoBuyItemNum1() {
//        return _autoBuyItemNum1;
        return get_other1().get_type2();
    }

    public void set_autoBuyItemNum1(int l) {
//        _autoBuyItemNum1 = l;
        get_other1().set_type2(l);
    }

    public int get_autoBuyItemAdena1() {
        return _autoBuyItemAdena1;
    }

    public void set_autoBuyItemAdena1(int l) {
        _autoBuyItemAdena1 = l;
    }

    public int get_autoBuyItem2() {
//        return _autoBuyItem2;
        return get_other1().get_type3();
    }

    public void set_autoBuyItem2(int l) {
//        _autoBuyItem2 = l;
        get_other1().set_type3(l);
    }

    public int get_autoBuyItemNum2() {
//        return _autoBuyItemNum2;
        return get_other1().get_type4();
    }

    public void set_autoBuyItemNum2(int l) {
//        _autoBuyItemNum2 = l;
        get_other1().set_type4(l);
    }

    public int get_autoBuyItemAdena2() {
        return _autoBuyItemAdena2;
    }

    public void set_autoBuyItemAdena2(int l) {
        _autoBuyItemAdena2 = l;
    }

    public boolean IsFollowOn() {
        return _isfollowon;
    }

    public int getCmd() {
        return _cmd;
    }

    public void setCmd(int l) {
        _cmd = l;
    }

    public void setFollowOn(boolean b) {
        _isfollowon = b;
    }

    public int getFollowId() {
        return _followid;
    }

    public void setFollowId(int i) {
        _followid = i;
    }

    public int getBeFollowId() {
        return _befollowid;
    }

    public void setBeFollowId(int i) {
        _befollowid = i;
    }

    public int getTempFollowId() {
        return _tempfollowid;
    }

    public void setTempFollowId(int i) {
        _tempfollowid = i;
    }

    public boolean IsFollow() {
        return _isfollow;
    }

    public void setFollow(boolean b) {
        _isfollow = b;
    }

    public int getAttackId() {
        return _attackid;
    }

    public void setAttackId(int b) {
        _attackid = b;
    }

    public int getFollowMode() {
        return _followmode;
    }

    public void setFollowMode(int i) {
        _followmode = i;
    }

    /**
     * 離開遊戲
     *
     */
    public boolean IsDisconnect() {
        return _disconnect;
    }

    /**
     * 離開遊戲
     *
     */
    public void setDisconnect(boolean b) {
        _disconnect = b;
    }

    public ArrayList<String> ds() {
        return _ds;
    }

    /**
     * 是否開啟自動練功(新版)
     *
     */
    public boolean IsAuto() {
        return _isauto;
    }

    /**
     * 是否開啟自動練功(新版)
     *
     */
    public void setIsAuto(boolean b) {
        _isauto = b;
        if (_isauto) {
            if (!this.hasSkillEffect(4020)) {
                sendPackets(new S_ServerMessage("\\aG自動掛機啟動！"));
            } else {
                setIsAuto(false);
            }
        } else {
            if (!this.hasSkillEffect(4020)) {
                this.setSkillEffect(4020, 5000);
            }
            sendPackets(new S_ServerMessage("\\aG自動掛機關閉！"));
        }
    }

    public int getLsLocX() {
        return _lslocx;
    }

    public void setLsLocX(int i) {
        _lslocx = i;
    }

    public int getLsLocY() {
        return _lslocy;
    }

    public void setLsLocY(int i) {
        _lslocy = i;
    }

    public boolean isLsOpen() {
        return _lsOpen;
    }

    public void setLsOpen(boolean b) {
        _lsOpen = b;
    }

    public int getLsRange() {
        return _lsRange;
    }

    public void setLsRange(int i) {
        _lsRange = i;
    }

    /**
     * 內掛幾分鐘後進行線程重置
     *
     */
    public int getRestartAuto() {
        return _resetautosec;
    }

    /**
     * 內掛幾分鐘後進行線程重置
     *
     */
    public void setRestartAuto(int i) {
        _resetautosec = i;
    }

    /**
     * 內掛重置處理秒數
     *
     */
    public int getRestartAutoStartSec() {
        return _resetautostartsec;
    }

    /**
     * 內掛重置處理秒數
     *
     */
    public void setRestartAutoStartSec(int i) {
        _resetautostartsec = i;
    }

    /**
     * 掛機死亡回練功點X
     *
     */
    public int getDeathReturnX() {
        return _drx;
    }

    /*
     * 掛機死亡回練功點X
     *
     * @param i
     */
    public void setDeathReturnX(int i) {
        _drx = i;
    }

    /**
     * 掛機死亡回練功點Y
     *
     */
    public int getDeathReturnY() {
        return _dry;
    }

    /**
     * 掛機死亡回練功點Y
     *
     */
    public void setDeathReturnY(int i) {
        _dry = i;
    }

    /**
     * 掛機死亡回練功點map
     *
     */
    public int getDeathReturnMap() {
        return _drm;
    }

    /**
     * 掛機死亡回練功點map
     *
     */
    public void setDeathReturnMap(int i) {
        _drm = i;
    }

    /**
     * 死亡自動回到練功點
     *
     */
    public boolean IsDeathReturn() {
        return _deathreturn;
    }

    /**
     * 死亡自動回到練功點
     *
     */
    public void setDeathReturn(boolean b) {
        if (_deathreturn) {
            setDeathReturnX(0);
            setDeathReturnY(0);
            setDeathReturnMap(0);
            sendPackets(new S_ServerMessage("地圖標記刪除"));
        } else {
            if (getMap().isMarkable()) {
                setDeathReturnX(getX());
                setDeathReturnY(getY());
                setDeathReturnMap(getMapId());
                sendPackets(new S_ServerMessage("地圖標記完成"));
            } else {
                sendPackets(new S_ServerMessage(214));
                return;
            }
        }
        _deathreturn = b;
    }

    /**
     * 內掛自動購買箭
     *
     */
    public boolean IsBuyArrow() {
//        return _buyarrow;
        return get_other1().get_type5() == 1;
    }

    /**
     * 內掛自動購買箭
     *
     */
    public void setBuyArrow(boolean b) {
//        _buyarrow = b;
        get_other1().set_type5(b ? 1 : 0);
    }

    /**
     * 是否開啟內掛被攻擊瞬移狀態
     *
     */
    public boolean IsAttackTeleport() {
//        return _attackteleport;
        return get_other1().get_type6() == 1;
    }

    /**
     * 是否開啟內掛被攻擊瞬移狀態
     *
     */
    public void setIsAttackTeleport(boolean b) {
//        _attackteleport = b;
        get_other1().set_type6(b ? 1 : 0);
    }

    /**
     * 內掛被玩家攻擊瞬移
     *
     */
    public boolean IsAttackTeleportHp() {
//        return _attackteleporthp;
        return get_other1().get_type15() == 1;
    }

    /**
     * 內掛被玩家攻擊瞬移
     *
     */
    public void setIsAttackTeleportHp(boolean b) {
//        _attackteleporthp = b;
        get_other1().set_type15(b ? 1 : 0);
    }

    /**
     * 內掛專用AutoAttack2020_1<br>
     * 是否存在於不要被搜尋到的目標對像
     *
     */
    public boolean InTargetList(Integer id) {
        return _targetId.contains(id);
    }

    /**
     * 內掛專用AutoAttack2020_1<br>
     * 抓取不要搜尋到的目標對像
     *
     */
    public ArrayList<Integer> TargetList() {
        return _targetId;
    }

    /**
     * 內掛專用AutoAttack2020_1<br>
     * 清空目標對像暫存
     */
    public void clearTargetList() {
        _targetId.clear();
    }
    ////////////////////////////////娃娃合成結束///////////////////////////////////////

    public int TargetListSize() {
        return _targetId.size();
    }

    /**
     * 內掛專用AutoAttack2020_1<br>
     * 目標對像加入暫存
     *
     */
    public void addTargetList(Integer id) {
        if (!_targetId.contains(id)) {
            _targetId.add(id);
        }
    }

    /**
     * 看到仇人瞬移
     *
     */
    public boolean IsEnemyTeleport() {
//        return _enemyteleport;
        return get_other1().get_type7() == 1;
    }

    /**
     * 看到仇人瞬移
     *
     */
    public void setIsEnemyTeleport(boolean b) {
//        _enemyteleport = b;
        get_other1().set_type7(b ? 1 : 0);
    }

    /**
     * 寫入仇人名稱
     *
     */
    public boolean IsKeyInEnemy() {
        return _keyenemy;
    }

    /**
     * 寫入仇人名稱
     *
     */
    public void setKeyInEnemy(boolean b) {
        _keyenemy = b;
    }

    /**
     * 移除仇人名稱
     *
     */
    public boolean IsKeyOutEnemy() {
        return _outenemy;
    }

    /**
     * 移除仇人名稱
     *
     */
    public void setKeyOutEnemy(boolean b) {
        _outenemy = b;
    }

    /**
     * 加入仇人陣列
     *
     */
    public void setInEnemyList(String id) {
        if (!_attackenemy.contains(id)) {
            _attackenemy.add(id);
        }
    }

    /**
     * 移出仇人陣列
     *
     */
    public void removeInEnemyList(String id) {
        _attackenemy.remove(id);
    }

    /**
     * 是否在已知仇人陣列內
     *
     */
    public boolean isInEnemyList(String id) {
        return _attackenemy.contains(id);
    }

    public ArrayList<String> InEnemyList() {
        return _attackenemy;
    }

    /**
     * 清空仇人陣列
     */
    public void clearInEnemyList() {
        _attackenemy.clear();
    }

    public ArrayList<Integer> AutoAttackNew() {
        return _autoattackNew;
    }

    public Integer getAutoAttackMP() {
        return _autoAttackMP;
    }

    public void setAutoAttackMP(int i) {
        _autoAttackMP = i;
    }

    public Integer getAutoAttackSecond() {
        return _autoAttackSecond;
    }

    public void setAutoAttackSecond(int i) {
        _autoAttackSecond = i;
    }

    /**
     * 檢查是否已經存在內掛自動施放技能
     *
     */
    public boolean isAttackSkillList(Integer id) {
        return _autoattackNew.contains(id);
    }

    /**
     * 查詢目前已經紀錄技能數量
     *
     */
    public int AttackSkillSize() {
        return _autoattackNew.size();
    }

    /**
     * 傳回隨機技能id
     *
     */
    public int AttackSkillId() {
        int id = _random.nextInt(_autoattackNew.size());
        if (id == _autoattackNew.size()) {
            id--;
        }
        return _autoattackNew.get(id);
    }

    /**
     * 清空內掛自動施放技能陣列
     */
    public void clearAttackSkillList() {
        _autoattackNew.clear();
        // 同時清除數據庫記錄，避免登入時重新加載
        if (get_other1() != null) {
            get_other1().set_type11(0);
        }
    }

    /**
     * 加入內掛自動施放技能陣列
     *
     */
    public void setAttackSkillList(Integer id) {
        if (!_autoattackNew.contains(id)) {
            _autoattackNew.add(id);
            get_other1().set_type11(id);
        }
    }

    public ArrayList<Integer> AttackSkillList() {
        return _autoattackNew;
    }

    /**
     * 內掛紀錄自動施放技能
     *
     */
    public boolean IsAttackSkill() {
        return _attackskill;
    }

    /**
     * 內掛紀錄自動施放技能
     *
     */
    public void setAttackSkill(boolean b) {
        _attackskill = b;
    }

    /**
     * 內掛自動召喚模組
     *
     */
    public int getSummonIdOfAuto() {
        return _summonid;
    }

    /**
     * 內掛自動召喚模組
     *
     */
    public void setSummonIdOfAuto(int i) {
        _summonid = i;
    }

    /**
     * 是否正在設定自動召喚
     *
     */
    public boolean IsSetSummonFalg() {
        return _issetsummonfalg;
    }

    /**
     * 是否正在設定自動召喚
     *
     */
    public void setSetSummonFalg(boolean b) {
        _issetsummonfalg = b;
    }

    public int getM() {
        return _m;
    }

    public void addM(int i) {
        _m += i;
        if (_m < 0) {
            _m = 0;
        }
        if (_m > 100) {
            _m = 100;
        }
    }

    public int getH() {
        return _h;
    }

    public void addH(int i) {
        _h += i;
        if (_h < 0) {
            _h = 0;
        }
        if (_h > 100) {
            _h = 100;
        }
    }

    ////////////////////////////////魔法合成結束///////////////////////////////////////
    public int getHH() {
        return _h1;
    }

    public void setHH(int i) {
        _h1 = i;
    }

    public int getMM() {
        return _m1;
    }

    public void setMM(int i) {
        _m1 = i;
    }

    /**
     * 是否開啟內掛瞬移
     *
     */
    public void setAutoTeleport(boolean b) {
//        _autoteleport = b;
        get_other1().set_type8(b ? 1 : 0);
    }

    /**
     * 是否開啟內掛瞬移
     *
     */
    public boolean IsAutoTeleport() {
//        return _autoteleport;
        return get_other1().get_type8() == 1;
    }

    public boolean isCallClan() {
        return _callclan;
    }

    public void setCallClan(boolean flag) {
        _callclan = flag;
    }

    /**
     * 召喚盟友<br>
     * 一般用
     *
     */
    public boolean IsClanCall() {
        return _iscall;
    }

    /**
     * 召喚盟友<br>
     * 一般用
     *
     */
    public void setClanCall(boolean b) {
        _iscall = b;
    }

    /**
     * 召喚盟友<br>
     * 輸入盟友NAME<br>
     * 一般用
     *
     */
    public boolean IndexCallClanPcName() {
        return _indexcall;
    }

    /**
     * 召喚盟友<br>
     * 輸入盟友NAME<br>
     * 一般用
     *
     */
    public void setIndexCallClanPcName(boolean b) {
        _indexcall = b;
    }

    public int getBossSeachPage() {
        return _BossSeachPage;
    }

    public void setBossSeachPage(int i) {
        _BossSeachPage = i;
    }

    /**
     * 開怪後剩餘延遲秒數
     *
     */
    public int getNA432() {
        return _na432;
    }

    /**
     * 開怪後剩餘延遲秒數
     *
     */
    public void setNA432(int i) {
        _na432 = i;
    }

    /**
     * 實際運算<BR>
     * 自設定無怪幾秒飛
     *
     */
    public int getNA61() {
        return _na61;
    }

    /**
     * 實際運算<BR>
     * 自設定無怪幾秒飛
     *
     */
    public void setNA61(int i) {
        _na61 = i;
    }

    public void set_isClanGfx(boolean b) {
        _isClanGfx = b;
    }

    public boolean isClanGfx() {
        return _isClanGfx;
    }

    /**
     * 三重矢
     *
     */
    public boolean getIsTRIPLE_ARROW() {
        return _TRIPLEARROW;
    }

    /**
     * 三重矢
     *
     */
    public void setIsTRIPLE_ARROW(boolean s) {
        _TRIPLEARROW = s;
    }

    public int getNERan() {
        return _neran;
    }

    public void setNERan(int i) {
        _neran += i;
    }

    public int getNEExtraDmg() {
        return _neextradmg;
    }

    public void setNEExtraDmg(int i) {
        _neextradmg += i;
    }

    public int getNEHit() {
        return _nehit;
    }

    public void setNEHit(int i) {
        _nehit += i;
    }

    public int getNEExtraMagicDmg() {
        return _nemagicdmg;
    }

    public void setNEExtraMagicDmg(int i) {
        _nemagicdmg += i;
    }

    public double getNECritDmg() {
        return _critdmg;
    }

    public void setNECritDmg(double i) {
        _critdmg += i;
    }

    public int getNECritRate() {
        return _critrate;
    }

    public void setNECritRate(int i) {
        _critrate += i;
    }

    public int getNECritGfx() {
        return _critgfx;
    }

    public void setNECritGfx(int i) {
        _critgfx = i;
    }

    public int getNEExpUp() {
        return _neexpup;
    }

    public void setNEExpUp(int i) {
        _neexpup += i;
    }

    public int getNESkillId() {
        return _neskillid;
    }

    public void setNESkillId(int i) {
        _neskillid = i;
    }

    public int getNEGfx() {
        return _negfx;
    }

    public void setNEGfx(int i) {
        _negfx = i;
    }

    // 武器加成特效
    public void startSkillSound() {
        if (getNETimeGfx() > 0 && !_SkillSoundActive) {
            _SkillSound = new SkillSound(this);
            if (_skillSoundFuture != null) {
                _skillSoundFuture.cancel(false);
            }
            _skillSoundFuture = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSound, 10000, 10000);
            _SkillSoundActive = true;
        }
        if (getNETimeGfx2() > 0 && !_SkillSoundActive) {
            _SkillSound = new SkillSound(this);
            if (_skillSoundFuture != null) {
                _skillSoundFuture.cancel(false);
            }
            _skillSoundFuture = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(_SkillSound, 10000, 10000);
            _SkillSoundActive = true;
        }
    }

    public int getDollCount() {
        return _dollcount;
    }

    public void setDollCount(int i) {
        _dollcount = i;
    }

    public int getDollCount2() {
        return _dollcount2;
    }

    public void setDollCount2(int i) {
        _dollcount2 = i;
    }

    public int getDollCount3() {
        return _dollcount3;
    }

    public void setDollCount3(int i) {
        _dollcount3 = i;
    }

    public int getDollCount4() {
        return _dollcount4;
    }

    public void setDollCount4(int i) {
        _dollcount4 = i;
    }

    public int getDollrun2() {
        return _dollrun2;
    }

    public void setDollrun2(int i) {
        _dollrun2 = i;
    }

    public int getDollrun3() {
        return _dollrun3;
    }

    public void setDollrun3(int i) {
        _dollrun3 = i;
    }

    public int getDollrun4() {
        return _dollrun4;
    }

    public void setDollrun4(int i) {
        _dollrun4 = i;
    }

    public int getDollrun5() {
        return _dollrun5;
    }

    public void setDollrun5(int i) {
        _dollrun5 = i;
    }

    public int getMagicCount() {
        return _Magiccount;
    }

    public void setMagicCount(int i) {
        _Magiccount = i;
    }

    public int getMagicCount2() {
        return _Magiccount2;
    }

    public void setMagicCount2(int i) {
        _Magiccount2 = i;
    }

    public int getMagicCount3() {
        return _Magiccount3;
    }

    public void setMagicCount3(int i) {
        _Magiccount3 = i;
    }

    public int getMagicCount4() {
        return _Magiccount4;
    }

    public void setMagicCount4(int i) {
        _Magiccount4 = i;
    }

    public int getMagicrun2() {
        return _Magicrun2;
    }

    public void setMagicrun2(int i) {
        _Magicrun2 = i;
    }

    public int getMagicrun3() {
        return _Magicrun3;
    }

    public void setMagicrun3(int i) {
        _Magicrun3 = i;
    }

    public int getMagicrun4() {
        return _Magicrun4;
    }

    public void setMagicrun4(int i) {
        _Magicrun4 = i;
    }

    public int getMagicrun5() {
        return _Magicrun5;
    }

    public void setMagicrun5(int i) {
        _Magicrun5 = i;
    }

    public boolean getSkillSound() {
        return _SkillSoundActive;
    }

    public void stopSkillSound() {
        if (_SkillSoundActive) {
            if (_skillSoundFuture != null) {
                _skillSoundFuture.cancel(false);
                _skillSoundFuture = null;
            }
            _SkillSound.cancel();
            _SkillSound = null;
            _SkillSoundActive = false;
        }
    }

    public int getNETimeGfx() {
        return _netimegfx;
    }

    public void setNETimeGfx(int i) {
        _netimegfx = i;
    }

    public int getNESkillId2() {
        return _neskillid2;
    }

    public void setNESkillId2(int i) {
        _neskillid2 = i;
    }

    public int getNEGfx2() {
        return _negfx2;
    }

    public void setNEGfx2(int i) {
        _negfx2 = i;
    }

    public int getNETimeGfx2() {
        return _netimegfx2;
    }

    public void setNETimeGfx2(int i) {
        _netimegfx2 = i;
    }

    /**
     * 呼喚隊友
     *
     */
    public boolean iszudui() {
        return _zudui;
    }

    /**
     * 呼喚隊友
     *
     */
    public void setzudui(boolean flag) {
        _zudui = flag;
    }

    public int getArmmoeset() {
        return _Armmoeset;
    }

    public void setArmmoeset(int i) {
        _Armmoeset = i;
    }

    public int get魔法格檔() {
        return _魔法格檔;
    }

    public void add魔法格檔(int i) {
        _魔法格檔 += i;
    }

    public int getBlockWeapon() {
        return _blockWeapon;
    }

    public void addBlockWeapon(int i) {
        _blockWeapon += i;
    }

    public int getClan_ReductionDmg() {
        return _Clan_ReductionDmg;
    }

    public void setClan_ReductionDmg(final int i) {
        _Clan_ReductionDmg = i;
    }

    public void addClan_ReductionDmg(final int i) {
        this._Clan_ReductionDmg += i;
    }
    // VIP能力 end

    public void add_Clanmagic_reduction_dmg(final int add) {
        _Clanmagic_reduction_dmg += add;
    }

    public int get_Clanmagic_reduction_dmg() {
        return _Clanmagic_reduction_dmg;
    }

    public void addExpByArmor(final double i) {
        _addExpByArmor += i;
    }

    public double getExpByArmor() {
        return _addExpByArmor;
    }

    /**
     * 龍之祝福
     *
     */
    public int getDragonExp() {
        return _dragonexp;
    }

    /**
     * 龍之祝福
     *
     */
    public void setDragonExp(int i) {
        _dragonexp = i;
    }

    public int getPage() {
        return _listpage;
    }

    public void setPage(final int page) {
        _listpage = page;
    }

    public void addPage(final int page) {
        _listpage += page;
    }

    /**
     * 金幣交易 獲取輸入的金幣數量
     *
     */
    public long getAdenaTradeCount() {
        return _adenaTradeCount;
    }

    /**
     * 金幣交易 記錄輸入的金幣數量
     *
     */
    public void setAdenaTradeCount(final long adenaTradeCount) {
        _adenaTradeCount = adenaTradeCount;
    }

    /**
     * 金幣交易 獲取輸入的元寶數量
     *
     */
    public long getAdenaTradeAmount() {
        return _adenaTradeAmount;
    }

    /**
     * 金幣交易 記錄輸入的元寶數量
     *
     */
    public void setAdenaTradeAmount(final long adenaTradeAmount) {
        _adenaTradeAmount = adenaTradeAmount;
    }

    public void addAdenaTradeIndex(final int id) {
        _adenaTradeIndexList.add(id);
    }

    public void clearAdenaTradeIndexList() {
        _adenaTradeIndexList.clear();
    }

    public List<Integer> getAdenaTradeIndexList() {
        return _adenaTradeIndexList;
    }

    public void addAdenaTradeItem(final L1CharacterAdenaTrade adenaTrade) {
        _adenaTradeList.add(adenaTrade);
    }

    public void clearAdenaTradeList() {
        _adenaTradeList.clear();
    }

    public List<L1CharacterAdenaTrade> getAdenaTradeList() {
        return _adenaTradeList;
    }

    /**
     * 金幣交易 獲取選擇的流水號
     *
     */
    public int getAdenaTradeId() {
        return _adenaTradeId;
    }

    /**
     * 金幣交易 記錄選擇的流水號
     *
     */
    public void setAdenaTradeId(final int adenaTradeId) {
        _adenaTradeId = adenaTradeId;
    }

    public void addTempObject(final Object obj) {
        _tempObjects.add(obj);
    }

    public void clearTempObjects() {
        _tempObjects.clear();
    }

    public ArrayList<Object> getTempObjects() {
        return _tempObjects;
    }

    public int getZhufuPvp() {
        return _zhufupvp;
    }

    public void setZhufuPvp(int i) {
        _zhufupvp = i;
    }

    public int getzhufuPvpbai() {
        return _zhufupvpbai;
    }

    public void setzhufuPvpbai(int i) {
        _zhufupvpbai = i;
    }

    public int getzhufuMoPvp() {
        return _zhufumopvp;
    }

    public void setzhufuMoPvp(int i) {
        _zhufumopvp = i;
    }

    public int getzhufuMoPvpbai() {
        return _zhufumopvpbai;
    }

    public void setzhufuMoPvpbai(int i) {
        _zhufumopvpbai = i;
    }

    public int getPaCha() {
        return _paceacha;
    }

    public void setPaCha(int i) {
        _paceacha = i;
    }

    public int getPaCon() {
        return _paceacon;
    }

    public void setPaCon(int i) {
        _paceacon = i;
    }

    public int getPaDex() {
        return _paceadex;
    }

    public void setPaDex(int i) {
        _paceadex = i;
    }

    public int getPaInt() {
        return _paceaint;
    }

    public void setPaInt(int i) {
        _paceaint = i;
    }

    public int getPaStr() {
        return _paceastr;
    }

    public void setPaStr(int i) {
        _paceastr = i;
    }

    public int getPaWis() {
        return _paceawis;
    }

    public void setPaWis(int i) {
        _paceawis = i;
    }

    public int getVipLevel() {
        return _vipLevel;
    }

    public void setVipLevel(final int vipLevel) {
        _vipLevel = vipLevel;
    }

    public Timestamp getVipStartTime() {
        return _startTime;
    }

    public void setVipStartTime(final Timestamp vipStartTime) {
        _startTime = vipStartTime;
    }

    public Timestamp getVipEndTime() {
        return _endTime;
    }

    public void setVipEndTime(final Timestamp vipEndTime) {
        _endTime = vipEndTime;
    }

    public void setVipStatus() {
        if (_startTime != null && _endTime != null) {
            final long t = _endTime.getTime() - System.currentTimeMillis();
            if (t > 0L) {
                final L1Vip tmp = VipSetsTable._list_vip.get(_vipLevel);
                if (tmp != null) {
                    addMaxHp(tmp.get_add_hp());
                    addHpr(tmp.get_add_hpr());
                    addMaxMp(tmp.get_add_mp());
                    addMpr(tmp.get_add_mpr());
                    addDmgup(tmp.get_add_dmg());
                    addBowDmgup(tmp.get_add_bowdmg());
                    addHitup(tmp.get_add_hit());
                    addBowHitup(tmp.get_add_bowhit());
                    addSp(tmp.get_add_sp());
                    addMr(tmp.get_add_mr());
                    addStr(tmp.getStr());
                    addDex(tmp.getDex());
                    addCon(tmp.getCon());
                    addWis(tmp.getWis());
                    addCha(tmp.getCha());
                    addInt(tmp.getInt());
                    addDamageIncreasePVE(tmp.getPVEAttackUp());// 打怪物加傷 (PVE)
                    addDamageReductionPVE(tmp.getPVEReduction()); // 怪物打你減傷 (PVE)
                    set_expadd(tmp.getExpAdd());// 經驗值倍率
                } else {
                    this.sendPackets(new S_SystemMessage("VIP能力錯誤，請告知線上GM處理。"));
                }
                sendPackets(new S_VipTime(_vipLevel, _startTime.getTime(), _endTime.getTime()));
                sendPackets(new S_VipShow(getId(), getVipLevel()));
                sendPackets(new S_OwnCharStatus(this));
                sendPackets(new S_SPMR(this));
                setSkillEffect(L1SkillId.VIP, (int) t);
            } else {
                _startTime = null;
                _endTime = null;
                _vipLevel = 0;
                AccountReading.get().updateVip(this);
                System.out.println(getName() + " vip到期清除");
            }
        }
    }

    public void addVipStatus(final int dayCount, final int level) {
        if (_endTime != null && _endTime.getTime() - System.currentTimeMillis() > 0L) {
            removeSkillEffect(L1SkillId.VIP);
        }
        final long t = System.currentTimeMillis();
        _startTime = new Timestamp(t);
        _endTime = new Timestamp(t + 86400000L * dayCount);
        _vipLevel = level;
        setVipStatus();
        AccountReading.get().updateVip(this);
    }

    public void endVipStatus() {
        final L1Vip tmp = VipSetsTable._list_vip.get(_vipLevel);
        if (tmp != null) {
            addMaxHp(-tmp.get_add_hp());
            addHpr(-tmp.get_add_hpr());
            addMaxMp(-tmp.get_add_mp());
            addMpr(-tmp.get_add_mpr());
            addDmgup(-tmp.get_add_dmg());
            addBowDmgup(-tmp.get_add_bowdmg());
            addHitup(-tmp.get_add_hit());
            addBowHitup(-tmp.get_add_bowhit());
            addSp(-tmp.get_add_sp());
            addMr(-tmp.get_add_mr());
            addStr(-tmp.getStr());
            addDex(-tmp.getDex());
            addCon(-tmp.getCon());
            addWis(-tmp.getWis());
            addCha(-tmp.getCha());
            addInt(-tmp.getInt());
            addDamageIncreasePVE(-tmp.getPVEAttackUp());
            addDamageReductionPVE(-tmp.getPVEReduction());
            set_expadd(-tmp.getExpAdd());
        } else {
            this.sendPackets(new S_SystemMessage("VIP能力錯誤，請告知線上GM處理。"));
        }
        sendPackets(new S_VipTime(0, 0L, 0L));
        sendPacketsAll(new S_VipShow(getId(), 0));
        sendPackets(new S_OwnCharStatus(this));
        sendPackets(new S_SPMR(this));
        _startTime = null;
        _endTime = null;
        _vipLevel = 0;
        AccountReading.get().updateVip(this);
    }

    public String getzhaohuan() {
        return _zhaohuan;
    }

    public void setzhaohuan(String i) {
        _zhaohuan = i;
    }

    public boolean getchecksummid() {
        return _summid;
    }

    public void setchecksummid(boolean i) {
        _summid = i;
    }

    public int getsummon_skillid() {
        return _summon_skillid;
    }

    public void setsummon_skillid(final int i) {
        this._summon_skillid = i;
    }

    public int getsummon_skillidmp() {
        return _summon_skillidmp;
    }

    public void setsummon_skillidmp(final int i) {
        this._summon_skillidmp = i;
    }

    public boolean getchecksummidhp() {
        return this._checksummidhp;
    }

    public void setchecksummidhp(final boolean flag) {
        this._checksummidhp = flag;
    }

    public boolean getBossfei() {
        return _bossfei;
    }
    public void setBossfei(boolean i) {
        _bossfei = i;
    }

    public boolean getHeping() {
//        return _heping;
        return get_other1().get_type10() == 1;
    }

    public void setHeping(boolean i) {
//        _heping = i;
        get_other1().set_type10(i ? 1 : 0);
    }

    public void setCarId(int i) {
        _CardId += i;
    }

    public int getCardId() {
        return _CardId;
    }

    public int getDollId() {
        return _DollId;
    }

    public void setDollId(int i) {
        _DollId = i;
    }

    public int getHolyId() {
        return _HolyId;
    }

    public void setHolyId(int i) {
        _HolyId = i;
    }

    public int getFireDisId() {
        return _FireDisId;
    }

    public void setFireDisId(int i) {
        _FireDisId = i;
    }

    public final int getMagicDmgUp() {
        return _magicDmgUp;
    }

    public final void addMagicDmgUp(final int i) {
        _magicDmgUp += i;
    }

    public int getWenyangJiFen() {
        return _wenyangjifen;
    }

    public void setWenyangJiFen(int i) {
        _wenyangjifen = i;
    }

    public int getWenyangRate1() {
        return _wenyangRate1;
    }

    public void setWenyangRate1(int i) {
        _wenyangRate1 = i;
    }

    public int getWenyangRate2() {
        return _wenyangRate2;
    }

    public void setWenyangRate2(int i) {
        _wenyangRate2 = i;
    }

    public int getWenyangRate3() {
        return _wenyangRate3;
    }

    public void setWenyangRate3(int i) {
        _wenyangRate3 = i;
    }

    public int getWenyangRate4() {
        return _wenyangRate4;
    }

    public void setWenyangRate4(int i) {
        _wenyangRate4 = i;
    }

    public int getWenyangRate5() {
        return _wenyangRate5;
    }

    public void setWenyangRate5(int i) {
        _wenyangRate5 = i;
    }

    public int getWenyangRate6() {
        return _wenyangRate6;
    }

    public void setWenyangRate6(int i) {
        _wenyangRate6 = i;
    }

    public int getWyType1() {
        if (_wytype1 == 0) {
            _wytype1 = 1;
        }
        return _wytype1;
    }

    public void setWyType1(int i) {
        _wytype1 = i;
    }

    public int getWyType2() {
        if (_wytype2 == 0) {
            _wytype2 = 2;
        }
        return _wytype2;
    }

    public void setWyType2(int i) {
        _wytype2 = i;
    }

    public int getWyType3() {
        if (_wytype3 == 0) {
            _wytype3 = 3;
        }
        return _wytype3;
    }

    public void setWyType3(int i) {
        _wytype3 = i;
    }

    public int getWyType4() {
        if (_wytype4 == 0) {
            _wytype4 = 4;
        }
        return _wytype4;
    }

    public void setWyType4(int i) {
        _wytype4 = i;
    }

    public int getWyType5() {
        if (_wytype5 == 0) {
            _wytype5 = 5;
        }
        return _wytype5;
    }

    public void setWyType5(int i) {
        _wytype5 = i;
    }

    public int getWyType6() {
        if (_wytype6 == 0) {
            _wytype6 = 6;
        }
        return _wytype6;
    }

    public void setWyType6(int i) {
        _wytype6 = i;
    }

    public int getWyLevel1() {
        return _wylevel1;
    }

    public void setWyLevel1(int i) {
        _wylevel1 = i;
    }

    public int getWyLevel2() {
        return _wylevel2;
    }

    public void setWyLevel2(int i) {
        _wylevel2 = i;
    }

    public int getWyLevel3() {
        return _wylevel3;
    }

    public void setWyLevel3(int i) {
        _wylevel3 = i;
    }

    private int[][] _wyDist = new int[6][3]; // [slot][分布], slot=0..5, 分布= {p1,p2,p3}

    public int[] getWyDist(int slot) {
        return _wyDist[slot - 1]; // slot=1..6
    }

    public void setWyDist(int slot, int[] dist) {
        _wyDist[slot - 1] = dist;
    }
    ////////////////////////////////////紋樣系統結束//////////////////////////////////////
    public int getWyLevel4() {
        return _wylevel4;
    }

    public void setWyLevel4(int i) {
        _wylevel4 = i;
    }

    public int getWyLevel5() {
        return _wylevel5;
    }

    public void setWyLevel5(int i) {
        _wylevel5 = i;
    }

    public int getWyLevel6() {
        return _wylevel6;
    }

    public void setWyLevel6(int i) {
        _wylevel6 = i;
    }

    public void setWenyangTypeAndLevel(int type) {
        if (type == 1) {
            setWyType1(1);
            setWyLevel1(getWyLevel1() + 1);
        } else if (type == 2) {
            setWyType2(2);
            setWyLevel2(getWyLevel2() + 1);
        } else if (type == 3) {
            setWyType3(3);
            setWyLevel3(getWyLevel3() + 1);
        } else if (type == 4) {
            setWyType4(4);
            setWyLevel4(getWyLevel4() + 1);
        } else if (type == 5) {
            setWyType5(5);
            setWyLevel5(getWyLevel5() + 1);
        } else if (type == 6) {
            setWyType6(6);
            setWyLevel6(getWyLevel6() + 1);
        }
    }

    public int getWyLevel(int type) {
        if (type == 1) {
            return getWyLevel1();
        } else if (type == 2) {
            return getWyLevel2();
        } else if (type == 3) {
            return getWyLevel3();
        } else if (type == 4) {
            return getWyLevel4();
        } else if (type == 5) {
            return getWyLevel5();
        } else if (type == 6) {
            return getWyLevel6();
        }
        return 0;
    }

    /////////////////////////////////////娃娃招喚紀錄結束//////////////////////////////////


    public int getLastDollId() {
        return _lastDollId;
    }

    public void setLastDollId(int i) {
        _lastDollId = i;
    }
    public int getLastPolyCardId() {
        return _lastPolyCardId;
    }
    public void setLastPolyCardId(int lastPolyCardId) {
        _lastPolyCardId = lastPolyCardId;
    }
    /////////////////////////////////////簽到紀錄結束//////////////////////////////////
    public int getLastHolyId2() {
        return _lastHolyId2;
    }

    public void setLastHolyId2(int i) {
        _lastHolyId2 = i;
    }

    public int get_day_signature() {
        return _day_signature;
    }

    public void set_day_signature(int i) {
        _day_signature = i;
    }

    public Timestamp get_day_signature_time() {
        return _day_signature_time;
    }

    public void set_day_signature_time(Timestamp i) {
        _day_signature_time = i;
    }

    public int getpolyCount() {
        return _polycount;
    }

    public void setpolyCount(int i) {
        _polycount = i;
    }

    public int getpolyCount2() {
        return _polycount2;
    }

    public void setpolyCount2(int i) {
        _polycount2 = i;
    }

    public int getpolyCount3() {
        return _polycount3;
    }

    public void setpolyCount3(int i) {
        _polycount3 = i;
    }

    public int getpolyCount4() {
        return _polycount4;
    }

    public void setpolyCount4(int i) {
        _polycount4 = i;
    }

    public int getpolyrun2() {
        return _polyrun2;
    }

    public void setpolyrun2(int i) {
        _polyrun2 = i;
    }

    public int getpolyrun3() {
        return _polyrun3;
    }

    public void setpolyrun3(int i) {
        _polyrun3 = i;
    }

    public int getpolyrun4() {
        return _polyrun4;
    }

    public void setpolyrun4(int i) {
        _polyrun4 = i;
    }

    public int getpolyrun5() {
        return _polyrun5;
    }

    public void setpolyrun5(int i) {
        _polyrun5 = i;
    }
    //----------------------------聖物合成----------------------------------------------//
    public int getHolyCount() {
        return _holyCount;
    }
    public void setHolyCount(int count) {
        _holyCount = count;
    }

    public int getHolyCount2() {
        return _holyCount2;
    }
    public void setHolyCount2(int count) {
        _holyCount2 = count;
    }

    public int getHolyCount3() {
        return _holyCount3;
    }
    public void setHolyCount3(int count) {
        _holyCount3 = count;
    }

    public int getHolyCount4() {
        return _holyCount4;
    }
    public void setHolyCount4(int count) {
        _holyCount4 = count;
    }

    public int getHolyRun2() {
        return _holyRun2;
    }
    public void setHolyRun2(int run) {
        _holyRun2 = run;
    }

    public int getHolyRun3() {
        return _holyRun3;
    }
    public void setHolyRun3(int run) {
        _holyRun3 = run;
    }

    public int getHolyRun4() {
        return _holyRun4;
    }
    public void setHolyRun4(int run) {
        _holyRun4 = run;
    }

    public int getHolyRun5() {
        return _holyRun5;
    }
    public void setHolyRun5(int run) {
        _holyRun5 = run;
    }

    /**
     * 延迟循环发送html<br>
     * 延迟300毫秒发送<font color="00ff00">20355音效及22108动画</font><br>
     * 每间隔300毫秒发送 <font color="00ffff">wwhc01 - wwhc05</font> 的html
     *
     * @param data String[] 资料
     */
    public void sendHtmlCastGfx(String[] data) {
        this.sendPackets(new S_NPCTalkReturn(this, "wwhc", data));
        HtmlCastGfx htmlCastGfx = new HtmlCastGfx(this, data);
        com.lineage.server.thread.GeneralThreadPool.get().schedule(htmlCastGfx, 300);
    }

    public int[] getReward_Ac() {
        return _reward_ac;
    }

    public void setReward_Ac(final int[] i) {
        _reward_ac = i;
    }

    public int getIgnoreDmgR() { // 伤害减免
        return _IgnoreDmgR;// 伤害减免
    }

    public int getDownTgDmgR() {
        return _DownTgDmgR;
    }

    public int getIgnoreMagicR() {
        return _IgnoreMagicR;
    }

    public int getDownTgMagicR() {
        return _DownTgMagicR;
    }

    public int getTemporaryEffect(String effectName) {
        return temporaryEffects.getOrDefault(effectName, 0);
    }

    public void addTemporaryEffect(String effectName, int level, int durationMs) {
        temporaryEffects.put(effectName, level);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> removeTemporaryEffect(effectName), durationMs, TimeUnit.MILLISECONDS);
    }

    private void removeTemporaryEffect(String effectName) {
        temporaryEffects.remove(effectName);
    }

    /**
     * 設定昏迷抗性
     *
     * @param hunmi 新的昏迷抗性值
     */
    public void setHunmi(int hunmi) {
        _addhunmi = hunmi;
    }

    /**
     * 箭矢特效的管理類
     */
    public void setpolyarrow(int i) {
        this._polyarrow = i;
    }

    public int getpolyarrow() {
        return this._polyarrow;
    }

    /**
     * 移植7.6近距離爆擊率
     *
     */
    public int getCloseCritical() {
        return _closeCritical;
    }

    /**
     * 移植7.6近距離爆擊率
     *
     */
    public void addCloseCritical(final int i) {
        _closeCritical += i;
    }

    /**
     * 移植7.6遠距離爆擊率
     *
     */
    public int getBowCritical() {
        return _bowCritical;
    }

    /**
     * 移植7.6遠距離爆擊率
     *
     */
    public void addBowCritical(final int i) {
        _bowCritical += i;
    }

    // 增加PVE攻擊加成
    public void addDamageIncreasePVE(int value) {
        _DamageIncreasePVE += value;
    }

    // 取得PVE攻擊加成
    public int getDamageIncreasePVE() {
        return _DamageIncreasePVE;
    }

    // 增加PVE減傷
    public void addDamageReductionPVE(int value) {
        _DamageReductionPVE += value;
    }

    // 取得PVE減傷
    public int getDamageReductionPVE() {
        return _DamageReductionPVE;
    }

    public int getloginpoly() {
        return this._loginpoly;
    }
    //-------------------------------------------------------------------

    public void setloginpoly(int i) {
        this._loginpoly = i;
    }

    public String getIp() {
        return this._netConnection.getIp().toString();
    }

    /**
     * 取得某個技能的等級
     */
    public int getSkillLevel(int skillId) {
        Integer lv = _skillLevels.get(skillId);
        return lv != null ? lv : 0;
    }

    /**
     * 設定某個技能的等級
     */
    public void setSkillLevel(int skillId, int level) {
        _skillLevels.put(skillId, level);
    }

    public int getShockStunLevel() {
        int level = getSkillLevel(SHOCK_STUN);
        return level;
    }

    public void setShockStunLevel(int lv) {
        _shockStunLevel = lv;
    }

    public void loadSkillLevelsFromDB() {
        ArrayList<L1UserSkillTmp> skillList = CharSkillTable.get().skills(this.getId());
        if (skillList != null) {
            for (L1UserSkillTmp skill : skillList) {
                setSkillLevel(skill.get_skill_id(), skill.get_skill_level());
            }
        } else {
        }
    }

    public L1NpcInstance getLastTalkedNpc() {
        return _lastTalkedNpc;
    }

    public void setLastTalkedNpc(L1NpcInstance npc) {
        _lastTalkedNpc = npc;
    }

    /**
     * 聲望等級
     **/
    public int getHonorLevel() {
        return _HonorLevel;
    }

    /**
     * 聲望等級
     **/
    public void setHonorLevel(int i) {
        _HonorLevel = i;
    }

    /**
     * 增加積分
     *
     */
    public void add_Honor(int Honor) {
        if (Honor < 0) {
            Honor = 0;
        }
        _Honor += Honor;
        if (_Honor < 0) {
            _Honor = 0;
        }
    }

    /**
     * 傳回積分
     *
     */
    public int getHonor() {
        return _Honor;
    }

    /**
     * 設置積分
     *
     */
    public void setHonor(int honor) {
        if (honor < 0) {
            honor = 0;
        }

        _Honor = honor;

        int oldLevel = getHonorLevel();
        int newLevel = Honor.getInstance().getHonorLevel(honor);

        if (oldLevel != newLevel) {
            L1WilliamHonor.delHonorSkill(this, oldLevel);
            setHonorLevel(newLevel);
            L1WilliamHonor.getHonorSkill(this);
            sendPackets(new S_SystemMessage("★ 威望等級提升至 Lv." + newLevel + "，能力已更新！"));
        }
    }



    public String getHonorViewName() {
        String name = getViewName();

        // 威望稱號
        L1WilliamHonor Honor1 = Honor.getInstance().getTemplate(getHonorLevel());
        if (Honor1 != null) {
            name = name + Honor1.getHonorName();
        }
        return name;
    }
    private boolean _honorSkillApplied = false;  // ✅ 新增這一行


    public boolean isHonorSkillApplied() {
        return _honorSkillApplied;
    }

    public void setHonorSkillApplied(boolean flag) {
        _honorSkillApplied = flag;
    }

    // 分類星盤
    private int astrologyPlateType = 0; // 0=舊星盤, 1=新星盤（Atton）

    public void setAstrologyPlateType(int type) { astrologyPlateType = type; }

    public int getAstrologyPlateType() { return astrologyPlateType; }


    /**
     * 蓋亞冷卻處理
     */
    private final Map<Integer, Long> _skillReuse = new HashMap<>();
    public long getSkillReuse(int skillId) {
        return _skillReuse.getOrDefault(skillId, 0L);
    }
    public void setSkillReuse(int skillId, long endTime) {
        _skillReuse.put(skillId, endTime);
    }




    //END
    //--------------------------------------------------------------------
    private static class HtmlCastGfx implements Runnable {
        private final L1PcInstance _pc;
        private final String[] _data;

        private HtmlCastGfx(L1PcInstance pc, String[] data) {
            _pc = pc;
            _data = data;
        }

        @Override
        public void run() {
            try {
                String html = "wwhc0";
                _pc.sendPacketsX8(new S_Sound(20355)); // 音效
                _pc.sendPacketsAll(new S_EffectLocation(_pc.getLocation(), 22108));
                for (int i = 1; i <= 5; i++) { // 4 - 5
                    TimeUnit.MILLISECONDS.sleep(300);
                    _pc.sendPackets(new S_NPCTalkReturn(_pc, html + i, _data));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * 死亡處理
     */
    private class Death implements Runnable {
        private L1Character _lastAttacker;

        private Death(L1Character cha) {
            _lastAttacker = cha;
        }

        @Override
        public void run() {
            L1Character lastAttacker = _lastAttacker;
            _lastAttacker = null;
            /*
             * if ((lastAttacker instanceof L1PcInstance)) { L1PcInstance oPc =
             * (L1PcInstance)lastAttacker; if(oPc.getId() == L1PcInstance.this.getId()){
             * writeDeathlog(L1PcInstance.this,oPc,"自殺"); return; } }
             */
            setCurrentHp(0);
            L1PcInstance.this.setGresValid(false);
            if (get_redbluejoin() > 0 && get_redbluepoint() > 0) {
                if (lastAttacker instanceof L1PcInstance) {
                    L1PcInstance killer = (L1PcInstance) lastAttacker;
                    if (get_redblueleader() == 0) {
                        int nplus = Configcamp_war.RedBlueNormal_point;
                        if (nplus >= get_redbluepoint()) {
                            nplus = get_redbluepoint();
                        }
                        set_redbluepoint(get_redbluepoint() - nplus);
                        killer.set_redbluepoint(get_redbluepoint() + nplus);
                        killer.sendPackets(new S_ServerMessage("\\aG你殺死[敵軍成員]獲得\\aD" + nplus + "點\\aG積分！"));
                    } else if (get_redblueleader() > 0) {
                        int lplus = Configcamp_war.RedBlueLeader_point;
                        if (lplus >= get_redbluepoint()) {
                            lplus = get_redbluepoint();
                        }
                        set_redbluepoint(get_redbluepoint() - lplus);
                        killer.set_redbluepoint(get_redbluepoint() + lplus);
                        killer.sendPackets(new S_ServerMessage("\\aG你殺死[敵軍隊長]獲得\\aD" + lplus + "點\\aG積分了！"));
                    }
                }
            }
            while (isTeleport()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(300L);
                } catch (Exception localException) {
                }
            }
            /*
             * if (isInParty()) { // 7.6 註銷 刪除 S_PacketBoxParty for (final L1PcInstance
             * member : getParty().getMemberList()) { // 7.6 //member.sendPackets(new
             * S_PacketBoxParty(getParty(), L1PcInstance.this)); member.sendPackets(new
             * S_Party(0x6e, L1PcInstance.this)); } }
             */
            set_delete_time(300);
            // 更新 2015/06/30 修正人物離線,死亡召喚獸移除
            if (!getPetList().isEmpty()) {
                for (Object petList : getPetList().values().toArray()) {
                    if (petList instanceof L1SummonInstance) {
                        final L1SummonInstance summon = (L1SummonInstance) petList;
                        new S_NewMaster(summon);
                        if (summon != null) {
                            if (summon.destroyed()) {
                                return;
                            }
                            if (summon.tamed()) {
                                // 召喚獸解放
                                summon.liberate();
                            } else {
                                // 解散
                                summon.Death(null);
                            }
                        }
                    }
                }
            }
            // 娃娃刪除
            if (!getDolls().isEmpty()) {
                for (Object obj : getDolls().values().toArray()) {
                    final L1DollInstance doll = (L1DollInstance) obj;
                    doll.deleteDoll();
                }
            }
            // 娃娃刪除
            if (!getDolls2().isEmpty()) {
                for (Object obj : getDolls2().values().toArray()) {
                    final L1DollInstance2 doll = (L1DollInstance2) obj;
                    doll.deleteDoll2();
                }
            }
            // if (!getHierarchs().isEmpty()) {
            // for (Object obj : getHierarchs().values().toArray()) {
            // final L1HierarchInstance hierarch = (L1HierarchInstance) obj;
            // hierarch.deleteMe();
            // }
            // }
            // 超級娃娃
            if (get_power_doll() != null) {
                get_power_doll().deleteDoll();
            }
            stopHpRegeneration();
            stopMpRegeneration();
            getMap().setPassable(getLocation(), true);
            L1PcInstance.this.stopHpRegeneration();
            L1PcInstance.this.stopMpRegeneration();
            //            L1PcInstance.this.getId();
            L1PcInstance.this.getMap().setPassable(L1PcInstance.this.getLocation(), true);
            // 死亡時具有變身狀態
            int tempchargfx = 0;
            if (L1PcInstance.this.hasSkillEffect(SHAPE_CHANGE)) {
                tempchargfx = L1PcInstance.this.getTempCharGfx();
                L1PcInstance.this.setTempCharGfxAtDead(tempchargfx);
            } else {
                L1PcInstance.this.setTempCharGfxAtDead(L1PcInstance.this.getClassId());
            }
            // 死亡時 現有技能消除
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(L1PcInstance.this, 44, getId(), getX(), getY(), 0, 1);
            if (tempchargfx != 0) {
                // System.out.println("tempchargfx: " + tempchargfx);
                L1PcInstance.this.sendPacketsAll(new S_ChangeShape(L1PcInstance.this, tempchargfx));
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (final Exception e) {
                }
            }
            // 送出死亡動作
            sendPacketsAll(new S_DoActionGFX(L1PcInstance.this.getId(), 8));
            L1EffectInstance tomb = L1SpawnUtil.spawnEffect(86126, 300, L1PcInstance.this.getX(), L1PcInstance.this.getY(), L1PcInstance.this.getMapId(), L1PcInstance.this, 0);
            L1PcInstance.this.set_tomb(tomb);
            if (getMapId() >= 2600 && getMapId() <= 2699) {// 火龍副本 //src026
                getInventory().consumeItem(5010);
            }
            if (getMapId() == 5153) { // XXX 死亡競賽
                for (Object doll : getDolls().values().toArray()) {
                    L1ItemInstance item = getInventory().getItem(((L1DollInstance) doll).getItemObjId());
                    item.stopEquipmentTimer(null);
                    ((L1DollInstance) doll).deleteDoll();
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (Exception e) {
                }
                try {
                    resurrect(1);
                    setCurrentHp(1);
                    sendPacketsAll(new S_Resurrection(L1PcInstance.this, L1PcInstance.this, 0));
                    sendPacketsAll(new S_RemoveObject(L1PcInstance.this));
                    beginGhost(getX(), getY(), getMapId(), false, 1800);
                    sendPacketsAll(new S_CharVisualUpdate(L1PcInstance.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendPacketsAll(new S_ServerMessage(1271));// 在戰鬥中失敗了，變更為觀戰者。
                L1DeathMatch.getInstance().sendRemainder(L1PcInstance.this);
                return;
            }
            boolean isSafetyZone = false;// 是否為安全區域
            boolean isCombatZone = false;// 是否為戰鬥區域
            boolean isWar = false;// 是否在戰爭中
            if (isSafetyZone()) {
                isSafetyZone = true;
            }
            if (isCombatZone()) {
                isCombatZone = true;
            }
            if (lastAttacker instanceof L1GuardInstance) {
                if (get_PKcount() > 0) {
                    set_PKcount(get_PKcount() - 1);
                }
                setLastPk(null);
            }
            if (lastAttacker instanceof L1GuardianInstance) {
                if (getPkCountForElf() > 0) {
                    setPkCountForElf(getPkCountForElf() - 1);
                }
                setLastPkForElf(null);
            }
            L1PcInstance fightPc = null;
            if (lastAttacker instanceof L1PcInstance) {
                fightPc = (L1PcInstance) lastAttacker;
            } else if (lastAttacker instanceof L1PetInstance) {
                L1PetInstance npc = (L1PetInstance) lastAttacker;
                if (npc.getMaster() != null) {
                    fightPc = (L1PcInstance) npc.getMaster();
                }
            } else if (lastAttacker instanceof L1SummonInstance) {
                L1SummonInstance npc = (L1SummonInstance) lastAttacker;
                if (npc.getMaster() != null) {
                    fightPc = (L1PcInstance) npc.getMaster();
                }
            } else if (lastAttacker instanceof L1IllusoryInstance) {
                L1IllusoryInstance npc = (L1IllusoryInstance) lastAttacker;
                if (npc.getMaster() != null) {
                    fightPc = (L1PcInstance) npc.getMaster();
                }
            } else if (lastAttacker instanceof L1EffectInstance) {
                L1EffectInstance npc = (L1EffectInstance) lastAttacker;
                if (npc.getMaster() != null) {
                    fightPc = (L1PcInstance) npc.getMaster();
                }
            }
            if (fightPc != null && lastAttacker.getId() != getId()) {
                if (getFightId() == fightPc.getId() && fightPc.getFightId() == getId()) {
                    setFightId(0);
                    sendPackets(new S_PacketBox(5, 0, 0));
                    fightPc.setFightId(0);
                    fightPc.sendPackets(new S_PacketBox(5, 0, 0));
                    return;
                }
                if (L1PcInstance.this.isEncounter() && fightPc.getLevel() > getLevel() && fightPc.getLevel() - getLevel() >= 10) {
                    return;
                }
                if (castleWarResult()) {
                    isWar = true;
                }
                if (simWarResult(lastAttacker)) {
                    isWar = true;
                }
                if (L1PcInstance.this.isInWarAreaAndWarTime(L1PcInstance.this, fightPc)) {
                    isWar = true;
                }
                if (getLevel() >= ConfigKill.KILLLEVEL && !fightPc.isGm()) {
                    boolean isShow = false;// 是否顯示殺人訊息
                    if (isWar) {
                        isShow = true;
                    } else if (!isCombatZone) {
                        isShow = true;
                    }
                    RecordTable.get().killpc(fightPc.getName(), L1PcInstance.this.getName());
                    // 殺人公告
                    if (isShow) {
                        World.get().broadcastPacketToAllkill(new S_KillMessage(fightPc.getViewName(), getViewName()));
                        fightPc.get_other().add_killCount(1);
                        get_other().add_deathCount(1);
                    }
                }
            }
            if (ConfigFreeKill.FREE_FIGHT_SWITCH) {
                if (CheckFightTimeController.getInstance().isFightMap(L1PcInstance.this.getMapId())) {
                    if (L1PcInstance.this.getLawful() >= 0 && ThreadLocalRandom.current().nextInt(100) < ConfigFreeKill.FREE_FIGHT_DROP_CHANCE_A || L1PcInstance.this.getLawful() < 0 && ThreadLocalRandom.current().nextInt(100) < ConfigFreeKill.FREE_FIGHT_DROP_CHANCE_B) {
                        int dropCount = ThreadLocalRandom.current().nextInt(ConfigFreeKill.FREE_FIGHT_MAX_DROP) + 1;
                        L1PcInstance.this.caoPenaltyResult(dropCount);
                    }
                    return;
                }
            }

            if (isSafetyZone && !(lastAttacker instanceof L1MonsterInstance)) {
                return;
            }
            if (isCombatZone && !(lastAttacker instanceof L1MonsterInstance)) {
                return;
            }
            if (!getMap().isEnabledDeathPenalty()) {
                return;
            }
            // 位在戰爭旗中並且設定城戰中無死亡逞罰
            boolean castle_area = L1CastleLocation.checkInAllWarArea(getX(), getY(), getMapId());
            if (castle_area && !ConfigAlt.ALT_WARPUNISHMENT) {
                return;
            }
            if (Test_Auto()) {
                L1PcInstance sql;
                if (lastAttacker instanceof L1PcInstance) {
                    sql = (L1PcInstance) lastAttacker;
                    AutoTable.getInstance().AddAuto(L1PcInstance.this, sql);
                }
                set_Test_Auto(false);
            }
            if (IsDeathReturn() && IsAuto()) {
                L1PcInstance enemy;
                if (lastAttacker instanceof L1PcInstance) {
                    enemy = (L1PcInstance) lastAttacker;
                    NewAutoPractice.get().AddAutoList(L1PcInstance.this, enemy);
                }
            } else {
                if (IsAuto()) {
                    L1PcInstance enemy;
                    if (lastAttacker instanceof L1PcInstance) {
                        enemy = (L1PcInstance) lastAttacker;
                        NewAutoPractice.get().AddAutoList(L1PcInstance.this, enemy);
                    }
                    setIsAuto(false);
                    setRestartAuto(0);
                    setRestartAutoStartSec(0);
                }
            }
            // c1TypeRate();// 陣營積分掉落判斷
            c1TypeRate(fightPc);
            expRate();// 經驗值掉落的判斷
            if (lastAttacker instanceof L1MonsterInstance) {
                L1MonsterInstance mob = (L1MonsterInstance) lastAttacker;
                writeDeathlog2(L1PcInstance.this, mob, "掉%");
            } else {
                writeDeathlog(L1PcInstance.this, fightPc, "掉%");
            }
            if (getLawful() < 32767) {// 正義未滿
                if (castle_area) {// 位在戰爭旗中
                    return;
                }
                if (!isProtector() || ProtectorSet.DEATH_VALUE_ITEM) {// 不是守護者OR守護者設定會掉落道具
                    lostRate();// 物品掉落判斷
                    lostSkillRate();// 技能掉落的判斷
                }
            }
            if (fightPc != null && fightPc.getId() != getId()) {// 紫名、紅名判斷
                if (isWar) {// 戰爭中
                    return;
                }
                // 積分掉落的判斷
                // this.c1TypeRate();
                // 經驗值掉落的判斷
                // this.expRate();
                // 道具奪取系統 by terry0412
                if (fightPc != null) {
                    this.checkItemSteal(fightPc);
                }
                if (fightPc.getClan() != null && getClan() != null && WorldWar.get().isWar(fightPc.getClan().getClanName(), getClan().getClanName())) {
                    return;
                }
                if (fightPc.isSafetyZone()) {// 攻擊者在安區
                    return;
                }
                if (fightPc.isCombatZone()) {// 攻擊者在戰鬥區
                    return;
                }
                if (getLawful() >= 0 && !isPinkName()) {// 被攻擊者為正義屬性且沒有變紫
                    boolean isChangePkCount = false;
                    // if (fightPc.getLawful() < 30000) {
                    fightPc.set_PKcount(fightPc.get_PKcount() + 1);
                    isChangePkCount = true;
                    if (fightPc.isElf() && isElf()) {
                        fightPc.setPkCountForElf(fightPc.getPkCountForElf() + 1);
                    }
                    // }
                    fightPc.setLastPk();
                    /** 正義值滿不會被警衛追殺 */
                    if (fightPc.getLawful() == 32767) {
                        fightPc.setLastPk(null);
                    }
                    if (fightPc.isElf() && isElf()) {
                        fightPc.setLastPkForElf();
                    }
                    int lawful = 0;
                    if (fightPc.getLawful() >= 0) {// 藍人則紅10000
                        lawful = -32768;
                    } else if (fightPc.getLawful() < 0) {// 紅人殺人多500
                        lawful = fightPc.getLawful() - 500;
                    }
                    if (lawful <= -32768) {
                        lawful = -32768;
                    }
                    fightPc.setLawful(lawful);
                    // fightPc.addLawful(lawful);
                    fightPc.sendPacketsAll(new S_Lawful(fightPc));
                    if (ConfigAlt.ALT_PUNISHMENT) {// 下地獄處罰
                        if (isChangePkCount && fightPc.get_PKcount() >= 5) {
                            fightPc.sendPackets(new S_BlueMessage(166, "你目前的擊殺得分為:" + fightPc.get_PKcount()));
                        }
                        /*
                         * else if ((isChangePkCount) && (fightPc.get_PKcount() >= 100)) {
                         * fightPc.beginHell(true); }
                         */
                    }
                } else {
                    setPinkName(false);
                }
            }
        }

        /**
         * 陣營積分掉落判斷
         */
        /*
         * private void c1TypeRate() { if (CampSet.CAMPSTART) { if ((_c_power != null)
         * && (_c_power.get_c1_type() != 0) && (_c_power.get_c1_type() != 0)) { if
         * (_vip_2) { sendPackets(new S_ServerMessage("\\fU你已經啟動月卡積分保護！")); return; } //
         * vip系統//src013 if (L1PcInstance.this._death_score) {
         * L1PcInstance.this.sendPackets(new S_ServerMessage("\\fU積分保護啟動！")); return; }
         *
         * L1ItemInstance item1 = getInventory().checkItemX(44165, 1L); if (item1 !=
         * null) { getInventory().removeItem(item1, 1L); sendPackets(new
         * S_ServerMessage("\\fU你身上帶有" + item1.getName() + ",陣營積分受到守護!")); return; }
         * L1Name_Power power = _c_power.get_power(); int score = _other.get_score() -
         * power.get_down(); if (score > 0) _other.set_score(score); else {
         * _other.set_score(0); }
         *
         * int lv = C1_Name_Type_Table.get().getLv(_c_power.get_c1_type(),
         * _other.get_score()); if (lv != _c_power.get_power().get_c1_id()) {
         * _c_power.set_power(L1PcInstance.this, false); sendPackets(new
         * S_ServerMessage("\\fR階級變更:" + _c_power.get_power().get_c1_name_type()));
         * sendPacketsAll(new S_ChangeName(L1PcInstance.this, true)); } } } }
         */
        private void c1TypeRate(L1PcInstance fightPc) {
            if (CampSet.CAMPSTART) {
                // 陣營系統啟用 XXX
                if (_c_power != null && _c_power.get_c1_type() != 0) {
                    if (_c_power.get_c1_type() != 0) {
                        // 月卡積分保護
                        if (_vip_2) {
                            sendPackets(new S_ServerMessage("\\aD你已經啟動積分保護！"));
                            return;
                        }
                        // vip系統//src013
                        if (L1PcInstance.this._death_score) {
                            L1PcInstance.this.sendPackets(new S_ServerMessage("\\fU積分保護啟動！"));
                            return;
                        }
                        // 預設可保護陣營積分物品
                        final L1ItemInstance item1 = getInventory().checkItemX(44165, 1);// 44165
                        // 貢獻度護身符
                        if (item1 != null) {
                            getInventory().removeItem(item1, 1);// 删除1个药水
                            sendPackets(new S_ServerMessage("\\aD你身上帶有" + item1.getName() + ",陣營積分受到守護!"));
                            return;
                        }
                        final L1Name_Power power = _c_power.get_power();
                        final int score = _other.get_score() - power.get_down();
                        if (_other.get_score() > 0) {
                            sendPackets(new S_ServerMessage("\\aF您的陣營積分損失了:" + power.get_down()));
                        }
                        if (score > 0) {
                            _other.set_score(score);
                        } else {
                            _other.set_score(0);
                        }
                        final int lv = C1_Name_Type_Table.get().getLv(_c_power.get_c1_type(), _other.get_score());
                        if (lv != _c_power.get_power().get_c1_id()) {
                            _c_power.set_power(L1PcInstance.this, false);
                            sendPackets(new S_ServerMessage("\\aD您的位階將變更為:" + _c_power.get_power().get_c1_name_type()));
                            sendPacketsAll(new S_ChangeName(L1PcInstance.this, true));
                        }
                        try {
                            // Smile 加入威望搶奪機制 150428
                            if (fightPc != null && ConfigOtherSet2.Prestigesnatch) {
                                if (_c_power.get_c1_type() == fightPc._c_power.get_c1_type()) {
                                    final int fightPc_addscore = (int) (power.get_down() * ConfigOtherSet2.camp1); // 相同陣營
                                    fightPc.get_other().add_score(fightPc_addscore);
                                    fightPc.sendPackets(new S_ServerMessage("\\aL您殺死<相同陣營者>獲得了積分:" + fightPc_addscore));
                                } else {
                                    final int fightPc_addscore = (int) (power.get_down() * ConfigOtherSet2.camp2); // 不同陣營
                                    fightPc.get_other().add_score(fightPc_addscore);
                                    fightPc.sendPackets(new S_ServerMessage("\\aH您殺死<不同陣營者>獲得了積分:" + fightPc_addscore));
                                }
                                final int fightPc_lv = C1_Name_Type_Table.get().getLv(fightPc._c_power.get_c1_type(), fightPc.get_other().get_score());
                                if (fightPc_lv != fightPc._c_power.get_power().get_c1_id()) {
                                    fightPc._c_power.set_power(fightPc, false);
                                    fightPc.sendPackets(new S_ServerMessage("\\aD您的陣營位階變更為:" + fightPc._c_power.get_power().get_c1_name_type()));
                                    fightPc.sendPacketsAll(new S_ChangeName(fightPc, true));
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.getStackTrace());
                        }
                    }
                }
            }
        }

        /**
         * 道具奪取判斷 by terry0412
         */
        private void checkItemSteal(final L1PcInstance fightPc) {
            // 沒有設置列表...
            if (ExtraItemStealTable.getInstance().getList().isEmpty()) {
                return;
            }
            // new Timestamp(System.currentTimeMillis());
            for (final L1ItemSteal itemSteal : ExtraItemStealTable.getInstance().getList()) {
                // 檢查身上是否有可被奪取的道具
                final L1ItemInstance steal_item = getInventory().findItemId(itemSteal.getItemId());
                if (steal_item == null) {
                    continue;
                }
                // 限制可奪取玩家最低等級
                if (getLevel() < itemSteal.getLevel()) {
                    continue;
                }
                // 限制可奪取玩家最低轉生數
                if (getMeteLevel() < itemSteal.getMeteLevel()) {
                    continue;
                }
                // 死亡被奪取機率
                if (_random.nextInt(100) >= itemSteal.getStealChance()) {
                    continue;
                }
                // 檢查身上是否有防止奪取的道具
                if (itemSteal.getAntiStealItemId() > 0 && getInventory().consumeItem(itemSteal.getAntiStealItemId(), 1)) {
                    sendPackets(new S_SystemMessage("由於身上有[" + ItemTable.get().getTemplate(itemSteal.getAntiStealItemId()).getNameId() + "] 使你免於被對方奪取: " + steal_item.getLogName()));
                    continue;
                }
                // 計算奪取數量
                long steal_count;
                // 可重疊物品
                if (steal_item.isStackable()) {
                    // 設定隨機數量
                    steal_count = _random.nextInt(Math.max(itemSteal.getMaxStealCount() - itemSteal.getMinStealCount(), 0) + 1) + itemSteal.getMinStealCount();
                    // 檢查擁有數量
                    steal_count = steal_item.getCount() >= steal_count ? steal_count : steal_item.getCount();
                } else {
                    // 非重疊物品永遠數量1
                    steal_count = 1L;
                    // 解除使用狀態
                    getInventory().setEquipped(steal_item, false);
                }
                // 您損失了 %0。
                sendPackets(new S_ServerMessage(638, steal_item.getNumberedViewName(steal_count)));
                // 被奪取道具是否掉落在地面 (1=掉地面, 0=掉在攻擊者身上)
                if (itemSteal.isDropOnFloor()) {
                    steal_item.set_showId(get_showId());
                    // 轉移地面
                    getInventory().tradeItem(steal_item, steal_count, World.get().getInventory(getX(), getY(), getMapId()));
                    // 是否廣播
                    if (itemSteal.isBroadcast()) {
                        World.get().broadcastPacketToAll(new S_SystemMessage("玩家[" + getViewName() + "]死亡後, 不小心把[" + steal_item.getNumberedViewName(steal_count) + "]掉在地板上"));
                    }
                    WriteLogTxt.Recording("死亡奪取物品", "IP(" + getNetConnection().getIp() + ")玩家【" + getName() + "】的【" + steal_item.getNumberedViewName(steal_count) + ", (ObjId: " + steal_item.getId() + ")】死亡後掉在地板上.");
                } else {
                    // 轉移攻擊者身上
                    getInventory().tradeItem(steal_item, steal_count, fightPc.getInventory());
                    // 獲得%0%o 。
                    fightPc.sendPackets(new S_ServerMessage(403, steal_item.getNumberedViewName(steal_count)));
                    // 是否廣播
                    if (itemSteal.isBroadcast()) {
                        World.get().broadcastPacketToAll(new S_SystemMessage("玩家[" + getViewName() + "]死亡後, 不小心被玩家[" + fightPc.getViewName() + "]搶走了[" + steal_item.getNumberedViewName(steal_count) + "]"));
                    }
                }
            }
        }

        /**
         * 經驗值掉落的判斷
         */
        private void expRate() {
            if (isProtector() && !ProtectorSet.DEATH_VALUE_EXP) {
                return;
            }
            if (_vip_1) {
                sendPackets(new S_ServerMessage("\\fU你已經啟動月卡經驗保護！"));
                return;
            }
            // vip系統//src013
            if (L1PcInstance.this._death_exp) {
                L1PcInstance.this.sendPackets(new S_ServerMessage("\\fU經驗保護啟動！"));
                return;
            }
            L1ItemInstance item1 = getInventory().checkItemX(44164, 1L);
            if (item1 != null) {
                getInventory().removeItem(item1, 1L);
                sendPackets(new S_ServerMessage("\\fU你身上帶有" + item1.getName() + ",剛剛死掉沒有掉經驗!"));
                return;
            }
            if (hasSkillEffect(8000) && getMapId() == 537) {
                killSkillEffectTimer(8000);
                sendPackets(new S_ServerMessage("\\fU受到祝福之光的保護,剛剛死掉沒有掉%!"));
                return;
            }
            L1PcInstance.this.deathPenalty();
            L1PcInstance.this.setGresValid(true);
            if (getExpRes() == 0) {
                setExpRes(1);
            }
        }

        /**
         * 物品掉落的判斷
         */
        private void lostRate() {
            if (_vip_3) {
                sendPackets(new S_ServerMessage("\\fU你已經啟動月卡物品保護！"));
                return;
            }
            // vip 保護物品//src013
            if (L1PcInstance.this._death_item) {
                L1PcInstance.this.sendPackets(new S_ServerMessage("\\fU物品保護啟動！"));
                return;
            }
            L1ItemInstance item1 = getInventory().checkItemX(44163, 1L);
            if (item1 != null) {
                getInventory().removeItem(item1, 1L);
                sendPackets(new S_ServerMessage("\\fU你身上帶有" + item1.getName() + ",剛剛死掉沒有噴裝!"));
                return;
            }
            // 產生物品掉落機率
            // 正義質32000以上0%、每-1000增加0.4%
            // 正義質小於0 = 100%
            int lostRate = (int) (((getLawful() + 32768D) / 1000D - 65D) * 4D);
            if (lostRate < 0) {
                lostRate *= -1;
                if (getLawful() < 0) {
                    lostRate = 1000;
                }
                final int rnd = _random.nextInt(1000) + 1;
                if (rnd <= lostRate) {
                    int count = 0;
                    int lawful = getLawful();
                    if (lawful >= -32768 && lawful <= -30000) {
                        count = L1PcInstance._random.nextInt(4) + 1;
                    } else if (lawful > -30000 && lawful <= -20000) {
                        count = L1PcInstance._random.nextInt(3) + 1;
                    } else if (lawful > -20000 && lawful <= -10000) {
                        count = L1PcInstance._random.nextInt(2) + 1;
                    } else if (lawful > -10000 && lawful <= 32767) {
                        count = L1PcInstance._random.nextInt(1) + 1;
                    }
                    if (count > 0) {
                        L1PcInstance.this.caoPenaltyResult(count);
                    }
                }
            }
        }

        /**
         * 技能損失的判斷
         */
        private void lostSkillRate() {
            if (_vip_4) {
                sendPackets(new S_ServerMessage("\\fU你已經啟動月卡技能保護！"));
                return;
            }
            // vip 技能保護 //src013
            if (L1PcInstance.this._death_skill) {
                L1PcInstance.this.sendPackets(new S_ServerMessage("\\fU技能保護啟動！"));
                return;
            }
            int skillCount = _skillList.size();
            if (skillCount > 0) {
                int count = 0;
                int lawful = getLawful();
                int random = L1PcInstance._random.nextInt(200);
                if (lawful <= -32768) {
                    count = L1PcInstance._random.nextInt(4) + 1;
                } else if (lawful <= -30000) {
                    if (random <= skillCount + 1) {
                        count = L1PcInstance._random.nextInt(3) + 1;
                    }
                } else if (lawful <= -20000) {
                    if (random <= (skillCount >> 1) + 1) {
                        count = L1PcInstance._random.nextInt(2) + 1;
                    }
                } else if (lawful <= -10000 && random <= (skillCount >> 2) + 1) {
                    count = 1;
                }
                if (count > 0) {
                    L1PcInstance.this.delSkill(count);
                }
            }
        }
    }
    private class DeathReturnTime extends TimerTask {
        private final L1PcInstance _pc = null;
        private int _startTime = 0;

        private DeathReturnTime(L1PcInstance pc) {
            pc = _pc;
        }

        private void DeathTime() {
            _timeHandler.schedule(this, 1000, 1000);
            GeneralThreadPool.get().execute(this);
        }

        @Override
        public void run() {
            _startTime++;
            switch (_startTime) { // 1秒
                case 2:
                    L1Teleport.teleport(L1PcInstance.this, L1PcInstance.this.getDeathReturnX(), L1PcInstance.this.getDeathReturnY(), (short) L1PcInstance.this.getDeathReturnMap(), 5, true);
                    break;
                case 5:
                    //                    L1PcInstance.this.setIsAuto(true);
                    //                    AutoAttack2020_1 auto = new AutoAttack2020_1(L1PcInstance.this);
                    //                    auto.begin();
                    L1PcInstance.this.startPcAI();
                    this.cancel();
                    break;
            }
        }
    }
}