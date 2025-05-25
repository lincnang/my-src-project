package com.lineage.server.model.skill;

public class L1SkillId {
    /* 人物技能效果 */
    public static final int SKILLS_BEGIN = 1;
    //	/**
    //	 * 初級治愈術
    //	 */
    //*	public static final int HEAL = 1;
    /**
     * 光箭1
     */
    public static final int ENERGY_BOLT = 1;
    /**
     * 日光術2
     */
    public static final int LIGHT = 2;
    /**
     * 保護罩3
     */
    public static final int SHIELD = 3;
    /**
     * 指定傳送4
     */
    public static final int TELEPORT = 4;
    /**
     * 解毒術9
     */
    public static final int CURE_POISON = 9;
    /**
     * 負重強化10
     */
    public static final int DECREASE_WEIGHT = 10;
    /**
     * 無所遁形術11
     */
    public static final int DETECTION = 11;
    /**
     * 岩牢12
     */
    public static final int EARTH_JAIL = 12;
    /**
     * 鎧甲護持17
     */
    public static final int BLESSED_ARMOR = 17;
    /**
     * 起死回生術18
     */
    public static final int TURN_UNDEAD = 18;
    /**
     * 冥想術19
     */
    public static final int MEDITATION = 19;
    /**
     * 寒冰氣息20
     */
    public static final int FROZEN_CLOUD = 20;
    /**
     * 燃燒的火球25
     */
    public static final int FIREBALL = 25;
    /**
     * 魔力奪取26
     */
    public static final int MANA_DRAIN = 26;
    /**
     * 吸血鬼之吻27
     */
    public static final int VAMPIRIC_TOUCH = 27;
    /**
     * 魔法屏障28
     */
    public static final int COUNTER_MAGIC = 28;
    /**
     * 冰錐33
     */
    public static final int CONE_OF_COLD = 33;
    /**
     * 高級治愈術34
     */
    public static final int GREATER_HEAL = 34;
    /**
     * 聖潔之光35
     */
    public static final int REMOVE_CURSE = 35;
    /**
     * 弱化術36
     */
    public static final int WEAKNESS = 36;
    /**
     * 加速術41
     */
    public static final int HASTE = 41;
    /**
     * 極道落雷42
     */
    public static final int CALL_LIGHTNING = 42;
    /**
     * 烈炎術43
     */
    public static final int SUNBURST = 43;
    /**
     * 體質強化
     */
    public static final int PHYSICAL_ENCHANT_INT_DEX_STR = 44;
    /**
     * /* 地裂術45
     */
    public static final int ERUPTION = 45;
    /**
     * 體力回復術49
     */
    public static final int HEAL_ALL = 49;
    /**
     * 神聖疾走50
     */
    public static final int HOLY_WALK = 50;
    /**
     * 疾病術51
     */
    public static final int DISEASE = 51;
    /**
     * 狂暴術52
     */
    public static final int BERSERKERS = 52;
    /**
     * 緩速術53
     */
    public static final int SLOW = 53;
    /**
     * 寒冰尖刺54
     */
    public static final int FREEZING_BLIZZARD = 54;
    /**
     * 祝福魔法武器55
     */
    public static final int BLESS_WEAPON = 55;
    /**
     * 魔法封印56
     */
    public static final int SILENCE = 56;
    /**
     * 靈魂升華57
     */
    public static final int ADVANCE_SPIRIT = 57;
    /**
     * 隱身術58
     */
    public static final int INVISIBILITY = 58;
    /**
     * 魔法相消術59
     */
    public static final int CANCELLATION = 59;
    /**
     * 火風暴60
     */
    public static final int FIRE_STORM = 60;
    /**
     * 聖結界61
     */
    public static final int IMMUNE_TO_HARM = 61;
    /**
     * 原火牢62 // 改黑暗之手
     */
    public static final int FIRE_WALL = 1662;  //火牢 給予其他NPC使用

    /**
     * 黑暗之手 新增技能  左右移動邊更
     */
    public static final int HAND_DARKNESS = 62; //黑暗之手
    /**
     * 全部治愈術63
     */
    public static final int FULL_HEAL = 63;
    /**
     * 變形術64
     */
    public static final int SHAPE_CHANGE = 64;
    /**
     * 流星雨65
     */
    public static final int METEOR_STRIKE = 65;
    /**
     * 究極光裂術66
     */
    public static final int DISINTEGRATE = 66;
    /**
     * 絕對屏障67
     */
    public static final int ABSOLUTE_BARRIER = 67;
    /**
     * 藥水霜化術 改 神諭 68
     */
    public static final int PASSIVE_ORACLE = 68; //神諭
    //	public static final int DECAY_POTION = 68;
    /**
     * 集體傳送術 改 意志專注
     */
    public static final int MASS_TELEPORT = 69;
    /**
     * 冰矛圍籬 改 古代啟示 一定時間內提升魔法暴擊，提升魔法穿透效果
     */
    public static final int ICE_LANCE = 70;
    /**
     * 治愈能量風暴 改 破壞之鏡 被動型 鏡反射
     */
    public static final int LIFE_STREAM = 71;
    /**
     * 強力無所遁形術72
     */
    public static final int COUNTER_DETECTION = 72;
    /**
     * 冰雪暴 改 究極光裂術(古代)
     */
    //*	public static final int BLIZZARD = 73;
    public static final int DISINTEGRATE_2 = 73;
    /**
     * 雷霆風暴 改 召喚守護者74
     */
    //  public static final int LIGHTNING_STORM = 74;
    public static final int SUMMON_MONSTER = 74;
    /**
     * 召喚術 改 神聖迅猛:古代 先學習神聖疾走 , 才可學習神聖迅猛的技能(被動) (採用蛋糕三段方式)
     */
    public static final int HOLY_WALK2 = 75;
    /**
     * 沉睡之霧 改 魔法大師
     */
    public static final int FOG_OF_SLEEPING = 76;
    /**
     * 龍捲風 改 黑暗之星
     */
    public static final int TORNADO = 77;
    /**
     * 終極返生術 改 黑暗之盾<br>
     * 若在隊伍中 隊友分攤傷害 40%<br>
     * 若不在隊伍中 將受到的傷害40%反射給攻擊者<br>
     * 受到傷害 > 10 才可以觸發此效果
     */
    public static final int GREATER_RESURRECTION = 78;
    /**
     * 集體緩術 改 集體聖結界79
     */
    //	public static final int MASS_SLOW = 76;
    public static final int IMMUNE_TO_HARM2 = 79;
    // public static final int CREATE_MAGICAL_WEAPON = 73;//原本 -> 創造魔法武器
    /**
     * 法師新技能 治癒逆行80
     */
    public static final int DEATH_HEAL = 80;
    //	/**
    //	 * 冰箭6
    //	 */
    //	public static final int ICE_DAGGER = 6;
    //	/**
    //	 * 風刃7
    //	 */
    //	public static final int WIND_CUTTER = 7;
    //	/**
    //	 * 神聖武器8
    //	 */
    //	public static final int HOLY_WEAPON = 8;
    //	/**
    //	 * 寒冷戰慄10
    //	 */
    //	public static final int CHILL_TOUCH = 10;
    //	/**
    //	 * 毒咒11
    //	 */
    //	public static final int CURSE_POISON = 11;
    //	/**
    //	 * 擬似魔法武器12
    //	 */
    //	public static final int ENCHANT_WEAPON = 12;
    //	/**
    //	 * 地獄之牙15
    //	 */
    //	public static final int FIRE_ARROW = 15;
    //	/**
    //	 * 火箭16
    //	 */
    //	public static final int STALAC = 16;
    /**
     * 極光雷電31
     */
    public static final int LIGHTNING = 31;
    /**
     * 中級治愈術24
     */
    public static final int EXTRA_HEAL = 24;
    /**
     * 闇盲咒術 22
     */
    public static final int CURSE_BLIND = 22;
    /**
     * 能量感測21
     */
    public static final int WEAK_ELEMENTAL = 21;
    // none = 24
    /**
     * 通暢氣脈術32
     */
    public static final int PHYSICAL_ENCHANT_DEX = 32;
    /**
     * 壞物術40
     */
    public static final int WEAPON_BREAK = 40;
    /**
     * 木乃伊的詛咒30
     */
    public static final int CURSE_PARALYZE = 30;
    /**
     * 迷魅術46
     */
    public static final int TAMING_MONSTER = 46;
    /**
     * 黑闇之影37
     */
    public static final int DARKNESS = 37;
    /**
     * 造屍術38
     */
    public static final int CREATE_ZOMBIE = 38;
    /**
     * 體魄強健術39
     */
    public static final int PHYSICAL_ENCHANT_STR = 39;
    /**
     * 強力加速術29
     */
    public static final int GREATER_HASTE = 29;
    /**
     * 返生術47
     */
    public static final int RESURRECTION = 47;
    /**
     * 震裂術45
     */
    public static final int EARTHQUAKE = 48;
    //////////////////////////////騎士////////////////////////////////////////////////
    /**
     * 天M 單手劍
     */
    public static final int PASSIVE_SWORD = 5;
    /**
     * 天M 雙手劍
     */
    public static final int PASSIVE_SWORD_2 = 6;

    /**
     * 暈眩之劍85
     */
    public static final int EMPIRE = 85;

    /**
     * 反擊屏障:專家86
     */
    public static final int COUNTER_BARRIER_VETERAN = 86;

    /**
     * 衝擊之暈87
     */
    public static final int SHOCK_STUN = 87;
    /**
     * 增幅防禦88
     */
    public static final int REDUCTION_ARMOR = 88;
    /**
     * 尖刺盔甲89
     */
    public static final int BOUNCE_ATTACK = 89;
    /**
     * 堅固防護90
     */
    public static final int SOLID_CARRIAGE = 90;
    /**
     * 反擊屏障91
     */
    public static final int COUNTER_BARRIER = 91;
    /**
     * 騎士新技能 絕禦之刃
     */
    public static final int ABSOLUTE_BLADE = 92;
    /**
     * 騎士M技能 幻影之刃
     */
    public static final int Phantom_Blade = 93;


    /*** 反制攻擊*/
    public static final int Counter_attack = 94;

    /**
     * 換位術
     */
    public static final int LICH_CHANGE_LOCATION = 95;

    /**
     * 騎士榮耀96
     */
    public static final int PRIDE = 96;


    /**
     * 暗隱術97
     */
    public static final int BLIND_HIDING = 97;
    /**
     * 附加劇毒98
     */
    public static final int ENCHANT_VENOM = 98;
    /**
     * 影之防護99
     */
    public static final int SHADOW_ARMOR = 99;
    /**
     * 提煉魔石100
     */
    public static final int BRING_STONE = 100;
    /**
     * 行走加速101
     */
    public static final int MOVING_ACCELERATION = 101;
    /**
     * 燃燒鬥志102
     */
    public static final int BURNING_SPIRIT = 102;
    /**
     * 暗黑盲咒103
     */
    public static final int DARK_BLIND = 103;
    //	/**
    //	 * 毒性抵抗104
    //	 */
    //	public static final int VENOM_RESIST = 104;
    /**
     * 暗影加速104
     */
    public static final int Shadow_ACCELERATION = 104;
    /**
     * 雙重破壞
     */
    public static final int DOUBLE_BREAK = 105;
    /**
     * 暗影閃避
     */
    public static final int UNCANNY_DODGE = 106;
    /**
     * 暗影之牙
     */
    public static final int SHADOW_FANG = 107;
    /**
     * 會心一擊108
     */
    public static final int FINAL_BURN = 108;
    /**
     * 力量提升109
     */
    public static final int DRESS_MIGHTY = 109;
    /**
     * 敏捷提升110
     */
    public static final int DRESS_DEXTERITY = 110;
    /**
     * 閃避提升111
     */
    public static final int DRESS_EVASION = 111;
    /**
     * 破壞盔甲112
     */
    public static final int ARMOR_BREAK = 112;
    /**
     * 黑妖新技能 暗殺者
     **/
    public static final int ASSASSIN = 233; // 242
    /**
     * 黑妖新技能 熾烈鬥志
     **/
    public static final int BLAZING_SPIRITS = 241;
    /**
     * 精準目標
     **/
    public static final int TRUE_TARGET = 113;
    /**
     * 灼熱武器
     **/
    public static final int GLOWING_AURA = 114;//(灼熱靈氣：1階段)
    /**
     * 閃亮之盾
     **/
    public static final int SHINING_AURA = 115;//(灼熱靈氣：2階段)
    //	/** 呼喚盟友 **/
    //	public static final int CALL_CLAN = 116;
    public static final int GLOWING_AURA_2 = 116; //勇猛武器
    /**
     * 勇猛意志
     **/
    public static final int BRAVE_AURA = 117;
    //	/** 援護盟友 **/
    //	public static final int RUN_CLAN = 118;
    /**
     * 勇猛盔甲 *新增
     */
    public static final int Armor_Valor = 118;
    /**
     * 王者之劍 119
     */
    public static final int KINGDOM_STUN = 119;
    /**
     * 王族新技能 王者加護
     **/
    public static final int BRAVE_AVATAR = 121;
    /**
     * 王族新技能 王者加護 1階段
     **/
    public static final int BRAVE_AVATAR_1ST = 7100;
    /**
     * 王族新技能 王者加護 2階段
     **/
    public static final int BRAVE_AVATAR_2ND = 7101;
    /**
     * 王族新技能 王者加護 3階段
     **/
    public static final int BRAVE_AVATAR_3RD = 7102;
    /**
     * 王族新技能 恩典庇護
     **/
    //修改操控靈氣
    public static final int GRACE_AVATAR = 122;
    /**
     * 魔法防禦129
     */
    public static final int RESIST_MAGIC = 129;
    /**
     * 心靈轉換130
     */
    public static final int BODY_TO_MIND = 130;
    /**
     * 世界樹的呼喚131
     */
    public static final int TELEPORT_TO_MATHER = 131;
    /**
     * 三重矢132
     */
    public static final int TRIPLE_ARROW = 132;
    /**
     * 弱化屬性133
     */
    public static final int ELEMENTAL_FALL_DOWN = 133;
    /**
     * 鏡反射134
     */
    public static final int COUNTER_MIRROR = 134;
    /**
     * 精靈新技能 魔力護盾135
     */
    public static final int SOUL_BARRIER = 135;

    /**
     * 妖精 重擊之矢
     */
    public static final int HeavyStrikeArrow = 136;
    /**
     * 淨化精神137
     */
    public static final int CLEAR_MIND = 137;
    /**
     * 屬性防禦138
     */
    public static final int RESIST_ELEMENTAL = 138;
    /**
     * 釋放元素145
     */
    public static final int RETURN_TO_NATURE = 145;
    /**
     * 魂體轉換146
     */
    public static final int BLOODY_SOUL = 146;
    /**
     * 單屬性防禦147
     */
    public static final int ELEMENTAL_PROTECTION = 147;
    /**
     * 火焰武器148
     */
    public static final int FIRE_WEAPON = 148;
    /**
     * 風之神射149
     */
    public static final int WIND_SHOT = 149;
    /**
     * 風之疾走150
     */
    public static final int WIND_WALK = 150;
    /**
     * 大地防護151
     */
    public static final int EARTH_SKIN = 151;
    /**
     * 地面障礙152
     */
    public static final int ENTANGLE = 152;
    /**
     * 魔法消除153
     */
    public static final int ERASE_MAGIC = 153;
    /**
     * 召喚屬性精靈154
     */
    public static final int LESSER_ELEMENTAL = 154;
    /**
     * 舞躍之火155
     */
    public static final int FIRE_BLESS = 155;
    /**
     * 暴風之眼156
     */
    public static final int STORM_EYE = 156;
    /**
     * 大地屏障157
     */
    public static final int EARTH_BIND = 157;
    /**
     * 生命之泉158
     */
    public static final int NATURES_TOUCH = 158;
    /**
     * 大地的護衛159
     */
    public static final int EARTH_BLESS = 159;
    /**
     * 水之防護160
     */
    public static final int AQUA_PROTECTER = 160;
    /**
     * 封印禁地161
     */
    public static final int AREA_OF_SILENCE = 161;
    /**
     * 召喚強力屬性精靈162
     */
    public static final int GREATER_ELEMENTAL = 162;
    /**
     * 烈炎武器163
     */
    public static final int BURNING_WEAPON = 163;
    /**
     * 生命的祝福164
     */
    public static final int NATURES_BLESSING = 164;
    /**
     * 生命呼喚165
     */
    public static final int CALL_OF_NATURE = 165;
    /**
     * 暴風神射166
     */
    public static final int STORM_SHOT = 166;
    /**
     * 風之枷鎖167
     */
    public static final int WIND_SHACKLE = 167;
    /**
     * 鋼鐵防護168
     */
    public static final int IRON_SKIN = 168;
    /**
     * 體能激發169
     */
    public static final int EXOTIC_VITALIZE = 169;
    /**
     * 水之元氣170
     */
    public static final int WATER_LIFE = 170;
    /**
     * 屬性之火171
     */
    public static final int ELEMENTAL_FIRE = 171;
    /**
     * 暴風疾走172
     */
    public static final int STORM_WALK = 172;
    /**
     * 污濁之水173
     */
    public static final int POLLUTE_WATER = 173;
    /**
     * 精準射擊174
     */
    public static final int STRIKER_GALE = 174;

    public static final int STRIKER_GALE2 = 1174;
    /**
     * 烈焰之魂175
     */
    public static final int SOUL_OF_FLAME = 175;
    /**
     * 能量激發176
     */
    public static final int ADDITIONAL_FIRE = 176;
    /**
     * 龍之護鎧181
     */
    public static final int DRAGON_SKIN = 181;
    /**
     * 燃燒擊砍182
     */
    public static final int BURNING_SLASH = 182;
    /**
     * 護衛毀滅183
     */
    public static final int GUARD_BRAKE = 183;
    /**
     * 岩漿噴吐184
     */
    public static final int MAGMA_BREATH = 184;
    /**
     * 覺醒：安塔瑞斯185
     */
    public static final int AWAKEN_ANTHARAS = 185;
    /**
     * 血之渴望186
     */
    public static final int BLOODLUST = 186;
    /**
     * 屠宰者187
     */
    public static final int FOE_SLAYER = 187;
    /**
     * 恐懼無助188
     */
    public static final int RESIST_FEAR = 188;
    /**
     * 衝擊之膚189
     */
    public static final int SHOCK_SKIN = 189;
    /**
     * 覺醒：法利昂190
     */
    public static final int AWAKEN_FAFURION = 190;
    /**
     * 致命身軀191
     */
    public static final int MORTAL_BODY = 191;
    /**
     * 奪命之雷192
     */
    public static final int THUNDER_GRAB = 192;
    /**
     * 驚悚死神193
     */
    public static final int HORROR_OF_DEATH = 193;
    /**
     * 寒冰噴吐194
     */
    public static final int FREEZING_BREATH = 194;
    /**
     * 覺醒：巴拉卡斯195
     */
    public static final int AWAKEN_VALAKAS = 195;
    /**
     * 龍騎士新技能 撕裂護甲196
     */
    public static final int DESTROY = 196;
    /**
     * 鏡像201
     */
    public static final int MIRROR_IMAGE = 201;
    /**
     * 混亂202
     */
    public static final int CONFUSION = 202;
    /**
     * 暴擊203
     */
    public static final int SMASH = 203;
    /**
     * 幻覺：歐吉204
     */
    public static final int ILLUSION_OGRE = 204;
    /**
     * 立方：燃燒205
     */
    public static final int CUBE_IGNITION = 205;
    /**
     * 專注206
     */
    public static final int CONCENTRATION = 206;
    /**
     * 心靈破壞207
     */
    public static final int MIND_BREAK = 207;
    /**
     * 骷髏毀壞208
     */
    public static final int BONE_BREAK = 208;
    /**
     * 幻覺：巫妖209
     */
    public static final int ILLUSION_LICH = 209;
    /**
     * 立方：地裂210
     */
    public static final int CUBE_QUAKE = 210;
    /**
     * 耐力211
     */
    public static final int PATIENCE = 211;
    /**
     * 幻想212
     */
    public static final int PHANTASM = 212;
    /**
     * 武器破壞者213
     */
    public static final int ARM_BREAKER = 213;
    /**
     * 幻覺：鑽石高侖214
     */
    public static final int ILLUSION_DIA_GOLEM = 214;
    /**
     * 立方：衝擊215
     */
    public static final int CUBE_SHOCK = 215;
    /**
     * 洞察216
     */
    public static final int INSIGHT = 216;
    /**
     * 恐慌217
     */
    public static final int PANIC = 217;
    /**
     * 降低負重218
     */
    public static final int JOY_OF_PAIN = 218;
    /**
     * 幻覺：化身219
     */
    public static final int ILLUSION_AVATAR = 219;
    public static final int ILLUSION_AVATAR2 = 1219; // SRC0808
    /**
     * 立方：和諧220
     */
    public static final int CUBE_BALANCE = 220;
    public static final int SKILLS_END = 220;
    /**
     * 幻術師新技能 衝突強化
     */
    public static final int IMPACT = 222;
    /* 狀態效果 */
    public static final int STATUS_BEGIN = 998;
    /**
     * 三段加速
     */
    public static final int STATUS_BRAVE3 = 998;
    /**
     * 勇敢藥水效果
     */
    public static final int STATUS_BRAVE = 1000;
    /**
     * 加速藥水效果
     */
    public static final int STATUS_HASTE = 1001;
    /**
     * 魔力回覆藥水效果
     */
    public static final int STATUS_BLUE_POTION = 1002;
    /**
     * 伊娃的祝福藥水效果
     */
    public static final int STATUS_UNDERWATER_BREATH = 1003;
    /**
     * 慎重藥水效果
     */
    public static final int STATUS_WISDOM_POTION = 1004;
    /**
     * 毒素效果
     */
    public static final int STATUS_POISON = 1006;
    /**
     * 沈默毒素效果
     */
    public static final int STATUS_POISON_SILENCE = 1007;
    /**
     * 麻痺毒素效果
     */
    public static final int STATUS_POISON_PARALYZING = 1008;
    /**
     * 麻痺毒素效果開始
     */
    public static final int STATUS_POISON_PARALYZED = 1009;
    /**
     * 詛咒型麻痺效果
     */
    public static final int STATUS_CURSE_PARALYZING = 1010;
    /**
     * 詛咒型麻痺效果開始
     */
    public static final int STATUS_CURSE_PARALYZED = 1011;
    /**
     * 漂浮之眼肉效果
     */
    public static final int STATUS_FLOATING_EYE = 1012;
    /**
     * 聖水效果
     */
    public static final int STATUS_HOLY_WATER = 1013;
    /**
     * 神聖的米索莉粉效果
     */
    public static final int STATUS_HOLY_MITHRIL_POWDER = 1014;
    /**
     * 伊娃的聖水效果
     */
    public static final int STATUS_HOLY_WATER_OF_EVA = 1015;
    /**
     * 精靈餅乾效果
     */
    public static final int STATUS_ELFBRAVE = 1016;
    /**
     * 生命之樹果實效果
     */
    public static final int STATUS_RIBRAVE = 1017;
    /**
     * 幻術師技能(立方：燃燒)
     */
    public static final int STATUS_CUBE_IGNITION_TO_ALLY = 1018;
    /**
     * 幻術師技能(立方：燃燒)
     */
    public static final int STATUS_CUBE_IGNITION_TO_ENEMY = 1019;
    /**
     * 幻術師技能(立方：地裂)
     */
    public static final int STATUS_CUBE_QUAKE_TO_ALLY = 1020;
    /**
     * 幻術師技能(立方：地裂)
     */
    public static final int STATUS_CUBE_QUAKE_TO_ENEMY = 1021;
    /**
     * 幻術師技能(立方：衝擊)
     */
    public static final int STATUS_CUBE_SHOCK_TO_ALLY = 1022;
    /**
     * 幻術師技能(立方：衝擊)
     */
    public static final int STATUS_CUBE_SHOCK_TO_ENEMY = 1023;
    /**
     * 幻術師技能(立方：衝擊)
     */
    public static final int STATUS_MR_REDUCTION_BY_CUBE_SHOCK = 1024;
    /**
     * 幻術師技能(立方：和諧)
     */
    public static final int STATUS_CUBE_BALANCE = 1025;
    /**
     * 武器破壞者的效果1026
     */
    public static final int DMGUP2 = 1026;
    public static final int STATUS_SUNRISE = 1027;// 日出之國
    /**
     * 狂戰士 咆哮
     */
    public static final int HOWL = 225;// 咆哮
    /**
     * 狂戰士 體能強化
     */
    public static final int GIGANTIC = 226;// 體能強化
    // 無名怒火227
    /**
     * 狂戰士 拘束移動
     */
    public static final int POWERGRIP = 228;// 拘束移動
    /**
     * 狂戰士 戰斧投擲
     */
    public static final int TOMAHAWK = 229;// 戰斧投擲
    /**
     * 狂戰士 亡命之徒
     */
    public static final int DESPERADO = 230;// 亡命之徒
    /**
     * 狂戰士新技能 泰坦狂暴
     */
    public static final int TITANL_RISING = 231; // 泰坦狂暴
    /**
     * M版狂戰士新技能 力量之血
     */
    public static final int Blood_strength = 232; // 力量之血

    /**常駐技能群**/
    /**
     * 狂戰士 粉碎
     */
    public static final int PASSIVE_CRASH = 241;// 粉碎
    /**
     * 狂戰士 狂暴
     */
    public static final int PASSIVE_FURY = 242;// 狂暴
    /**
     * 狂戰士 迅猛雙斧
     */
    public static final int PASSIVE_SLAYER = 243;// 迅猛雙斧
    /**
     * 狂戰士 護甲身軀
     */
    public static final int PASSIVE_ARMORGARDE = 244;// 護甲身軀
    /**
     * 狂戰士 泰坦：岩石
     */
    public static final int PASSIVE_TITANROCK = 245;// 泰坦：岩石
    /**
     * 狂戰士 泰坦：子彈
     */
    public static final int PASSIVE_TITANBULLET = 246;// 泰坦：子彈
    /**
     * 狂戰士 泰坦：魔法
     */
    public static final int PASSIVE_TITANMAGIC = 247;// 泰坦：魔法


    public static final int STATUS_END = 1027;
    /* GM狀態效果 */
    public static final int GMSTATUS_BEGIN = 2000;
    public static final int GMSTATUS_INVISIBLE = 2000;
    public static final int GMSTATUS_HPBAR = 2001;
    public static final int GMSTATUS_SHOWTRAPS = 2002;
    public static final int GMSTATUS_HPBAR_PC = 2003;
    public static final int GMSTATUS_END = 2003;
    /* 料理效果 */
    public static final int COOKING_NOW = 2999;
    public static final int COOKING_BEGIN = 3000;
    public static final int COOKING_1_0_N = 3000;
    public static final int COOKING_1_1_N = 3001;
    public static final int COOKING_1_2_N = 3002;
    public static final int COOKING_1_3_N = 3003;
    public static final int COOKING_1_4_N = 3004;
    public static final int COOKING_1_5_N = 3005;
    public static final int COOKING_1_6_N = 3006;
    public static final int COOKING_1_7_N = 3007;
    public static final int COOKING_1_0_S = 3008;
    public static final int COOKING_1_1_S = 3009;
    public static final int COOKING_1_2_S = 3010;
    public static final int COOKING_1_3_S = 3011;
    public static final int COOKING_1_4_S = 3012;
    public static final int COOKING_1_5_S = 3013;
    public static final int COOKING_1_6_S = 3014;
    public static final int COOKING_1_7_S = 3015;
    public static final int COOKING_2_0_N = 3016;
    public static final int COOKING_2_1_N = 3017;
    public static final int COOKING_2_2_N = 3018;
    public static final int COOKING_2_3_N = 3019;
    public static final int COOKING_2_4_N = 3020;
    public static final int COOKING_2_5_N = 3021;
    public static final int COOKING_2_6_N = 3022;
    public static final int COOKING_2_7_N = 3023;
    public static final int COOKING_2_0_S = 3024;
    public static final int COOKING_2_1_S = 3025;
    public static final int COOKING_2_2_S = 3026;
    public static final int COOKING_2_3_S = 3027;
    public static final int COOKING_2_4_S = 3028;
    public static final int COOKING_2_5_S = 3029;
    public static final int COOKING_2_6_S = 3030;
    public static final int COOKING_2_7_S = 3031;
    public static final int COOKING_3_0_N = 3032;
    public static final int COOKING_3_1_N = 3033;
    public static final int COOKING_3_2_N = 3034;
    public static final int COOKING_3_3_N = 3035;
    public static final int COOKING_3_4_N = 3036;
    public static final int COOKING_3_5_N = 3037;
    public static final int COOKING_3_6_N = 3038;
    public static final int COOKING_3_7_N = 3039;
    public static final int COOKING_3_0_S = 3040;
    public static final int COOKING_3_1_S = 3041;
    public static final int COOKING_3_2_S = 3042;
    public static final int COOKING_3_3_S = 3043;
    public static final int COOKING_3_4_S = 3044;
    public static final int COOKING_3_5_S = 3045;
    public static final int COOKING_3_6_S = 3046;
    public static final int COOKING_3_7_S = 3047;
    /**
     * 強壯的牛排
     */
    public static final int COOKING_4_0_N = 3048;
    /**
     * 敏捷的煎鮭魚
     */
    public static final int COOKING_4_1_N = 3049;
    /**
     * 炭烤的火雞
     */
    public static final int COOKING_4_2_N = 3050;
    /**
     * 養生的雞湯
     */
    public static final int COOKING_4_3_N = 3051;
    public static final int COOKING_END = 3051;
    /* 任務及怪物效果 */
    /**
     * 魔法傚果:凍結
     */
    public static final int STATUS_FREEZE = 4000;
    /**
     * 魔法傚果:麻痺
     */
    public static final int CURSE_PARALYZE2 = 4001;
    /**
     * 禁言效果
     */
    public static final int STATUS_CHAT_PROHIBITED = 4002;
    /**
     * 倫得施放的古代咒術-7245
     */
    public static final int DE_LV30 = 4003;
    /**
     * 可以攻擊炎魔(原生魔族-7246)
     */
    public static final int STATUS_CURSE_BARLOG = 4005;
    /**
     * 可以攻擊火焰之影(不死魔族-7247)
     */
    public static final int STATUS_CURSE_YAHEE = 4006;
    /**
     * 可攻擊再生之祭壇的力量(7248)
     */
    public static final int CKEW_LV50 = 4007;
    /**
     * 可攻擊艾爾摩索夏依卡將軍的冤魂
     */
    public static final int I_LV30 = 4008;
    /**
     * 卡瑞的祝福(地龍副本)
     */
    public static final int ADLV80_1 = 4009;// 卡瑞的祝福(地龍副本)
    /**
     * 莎爾的祝福(水龍副本)
     */
    public static final int ADLV80_2 = 4010;// 莎爾的祝福(水龍副本)
    /**
     * 藥水侵蝕術(水龍副本 治療變為傷害)
     */
    public static final int ADLV80_2_1 = 4011;// 藥水侵蝕術
    /**
     * 污濁的水流(水龍副本 回覆量1/2倍)
     */
    public static final int ADLV80_2_2 = 4012;// 污濁的水流
    /**
     * 治愈侵蝕術(水龍副本 治療回覆量1/2倍)
     */
    public static final int ADLV80_2_3 = 4013;// 治癒侵蝕術
    /**
     * 甘特的祝福(風龍副本)
     */
    public static final int ADLV80_3 = 4018;// 甘特的祝福(風龍副本)
    public static final int KIRTAS_BARRIER1 = 11060;
    public static final int KIRTAS_BARRIER2 = 11059;
    public static final int KIRTAS_BARRIER3 = 11058;
    public static final int KIRTAS_BARRIER4 = 11057;
    public static final int LINDVIOR_SKY_SPIKED = 11061;
    /**
     * 禁止移動(奪命之雷/)
     */
    public static final int MOVE_STOP = 4017;
    /* 附魔石效果 */
    public static final int BS_GX01 = 4401;
    public static final int BS_GX02 = 4402;
    public static final int BS_GX03 = 4403;
    public static final int BS_GX04 = 4404;
    public static final int BS_GX05 = 4405;
    public static final int BS_GX06 = 4406;
    public static final int BS_GX07 = 4407;
    public static final int BS_GX08 = 4408;
    public static final int BS_GX09 = 4409;// 附魔石 (近戰)
    public static final int BS_AX01 = 4411;
    public static final int BS_AX02 = 4412;
    public static final int BS_AX03 = 4413;
    public static final int BS_AX04 = 4414;
    public static final int BS_AX05 = 4415;
    public static final int BS_AX06 = 4416;
    public static final int BS_AX07 = 4417;
    public static final int BS_AX08 = 4418;
    public static final int BS_AX09 = 4419;// 附魔石 (遠攻)
    public static final int BS_WX01 = 4421;
    public static final int BS_WX02 = 4422;
    public static final int BS_WX03 = 4423;
    public static final int BS_WX04 = 4424;
    public static final int BS_WX05 = 4425;
    public static final int BS_WX06 = 4426;
    public static final int BS_WX07 = 4427;
    public static final int BS_WX08 = 4428;
    public static final int BS_WX09 = 4429;// 附魔石 (恢復)
    public static final int BS_ASX01 = 4431;
    public static final int BS_ASX02 = 4432;
    public static final int BS_ASX03 = 4433;
    public static final int BS_ASX04 = 4434;
    public static final int BS_ASX05 = 4435;
    public static final int BS_ASX06 = 4436;
    public static final int BS_ASX07 = 4437;
    public static final int BS_ASX08 = 4438;
    public static final int BS_ASX09 = 4439;// 附魔石 (防禦)
    /* 龍印魔石效果 */
    public static final int DS_GX00 = 4500;
    public static final int DS_GX01 = 4501;
    public static final int DS_GX02 = 4502;
    public static final int DS_GX03 = 4503;
    public static final int DS_GX04 = 4504;
    public static final int DS_GX05 = 4505;
    public static final int DS_GX06 = 4506;
    public static final int DS_GX07 = 4507;
    public static final int DS_GX08 = 4508;
    public static final int DS_GX09 = 4509;// 9階 鬥士
    public static final int DS_AX00 = 4510;
    public static final int DS_AX01 = 4511;
    public static final int DS_AX02 = 4512;
    public static final int DS_AX03 = 4513;
    public static final int DS_AX04 = 4514;
    public static final int DS_AX05 = 4515;
    public static final int DS_AX06 = 4516;
    public static final int DS_AX07 = 4517;
    public static final int DS_AX08 = 4518;
    public static final int DS_AX09 = 4519;// 9階 弓手
    public static final int DS_WX00 = 4520;
    public static final int DS_WX01 = 4521;
    public static final int DS_WX02 = 4522;
    public static final int DS_WX03 = 4523;
    public static final int DS_WX04 = 4524;
    public static final int DS_WX05 = 4525;
    public static final int DS_WX06 = 4526;
    public static final int DS_WX07 = 4527;
    public static final int DS_WX08 = 4528;
    public static final int DS_WX09 = 4529;// 9階 賢者
    public static final int DS_ASX00 = 4530;
    public static final int DS_ASX01 = 4531;
    public static final int DS_ASX02 = 4532;
    public static final int DS_ASX03 = 4533;
    public static final int DS_ASX04 = 4534;
    public static final int DS_ASX05 = 4535;
    public static final int DS_ASX06 = 4536;
    public static final int DS_ASX07 = 4537;
    public static final int DS_ASX08 = 4538;
    public static final int DS_ASX09 = 4539;// 9階 衝鋒
    /* 1段經驗加倍效果 */
    public static final int EXP13 = 6666;
    public static final int EXP15 = 6667;
    public static final int EXP17 = 6668;
    public static final int EXP20 = 6669;
    public static final int EXP25 = 6670;
    public static final int EXP30 = 6671;
    public static final int EXP35 = 6672;
    public static final int EXP40 = 6673;
    public static final int EXP45 = 6674;
    public static final int EXP50 = 6675;
    public static final int EXP55 = 6676;
    public static final int EXP60 = 6677;
    public static final int EXP65 = 6678;
    public static final int EXP70 = 6679;
    public static final int EXP75 = 6680;
    public static final int EXP80 = 6681;
    public static final int EXP85 = 26682;
    public static final int EXP90 = 26683;
    public static final int EXP95 = 26684;
    public static final int EXP100 = 26685;
    /* 魔眼效果 */
    public static final int DRAGON1 = 6683;
    public static final int DRAGON2 = 6684;
    public static final int DRAGON3 = 6685;
    public static final int DRAGON4 = 6686;
    public static final int DRAGON5 = 6687;
    public static final int DRAGON6 = 6688;
    public static final int DRAGON7 = 6689;
    /* 龍之血痕效果 */
    public static final int DRAGON_BLOOD_1 = 6797;
    public static final int DRAGON_BLOOD_2 = 6798;
    public static final int DRAGON_BLOOD_3 = 6799;
    public static final int DRAGON_BLOOD_4 = 6800;
    /* 自創道具效果 */
    public static final int SCORE02 = 7002;
    public static final int SCORE03 = 7003;
    public static final int CHAT_STOP = 7004;
    public static final int C3_FIRE = 7005;
    public static final int C3_WATER = 7006;
    public static final int C3_WIND = 7007;
    public static final int C3_EARTH = 7008;
    public static final int C3_RESTART = 7009;
    /**
     * 廣播卡判斷時間 by terry0412
     */
    public static final int BROADCAST_CARD = 5010;
    /**
     * 元寶偵測紀錄 (每XX秒判斷一次)
     */
    public static final int ADENA_CHECK_TIMER = 5011;
    /* 2段經驗加倍效果 */
    public static final int SEXP25 = 35001;
    public static final int SEXP30 = 35002;
    public static final int SEXP35 = 35003;
    public static final int SEXP40 = 35004;
    public static final int SEXP45 = 35005;
    public static final int SEXP50 = 35006;
    public static final int SEXP55 = 35007;
    public static final int SEXP60 = 35008;
    public static final int SEXP65 = 35009;
    public static final int SEXP70 = 35010;
    public static final int SEXP75 = 35011;
    public static final int SEXP80 = 35012;
    public static final int SEXP85 = 35013;
    public static final int SEXP90 = 35014;
    public static final int SEXP95 = 35015;
    public static final int SEXP100 = 35016;
    /**
     * 1.3倍經驗加倍
     */
    public static final int SEXP13 = 5006;
    public static final int SEXP150 = 5000;
    public static final int SEXP175 = 5001;
    public static final int SEXP200 = 5002;
    public static final int SEXP225 = 5003;
    public static final int SEXP250 = 5004;
    public static final int SEXP300 = 5007;
    public static final int SEXP500 = 5005;
    /**
     * 祝福之光
     */
    public static final int HOLYLIGHT = 8000;
    /**
     * 火焰之暈
     */
    public static final int FIRESTUN = 8010;
    /**
     * 真 火焰之暈
     */
    public static final int TRUEFIRESTUN = 8020;
    /**
     * 月亮項鏈 煙霧攻擊
     */
    public static final int MOONATTACK = 8025;
    /**
     * 神秘的魔法變身書
     */
    public static final int POLYBOOKBUFF = 8030;
    /**
     * TOP10變身buff
     */
    public static final int TOP10BUFF = 8040;
    /**
     * LV90變身buff
     */
    public static final int LV90BUFF = 8041;
    /**
     * 三段式魔武攻擊狀態
     */
    public static final int BEINGLICKING = 8050;
    /**
     * 強化戰鬥卷軸
     */
    public static final int EFFECT_ENCHANTING_BATTLE = 8060;
    /**
     * 定時外掛檢測
     */
    public static final int AI_1 = 30028;
    public static final int AI_2 = 30029;
    public static final int VIP_GFX = 30000;
    public static final int DELAY_TIME = 40000;
    /**
     * 大樂透
     */
    public static final int BigHot = 30036;
    /**
     * 媽祖效果
     **/
    public static final int Mazu = 20000;
    /**
     * 巴風特雕像效果
     **/
    public static final int Baphomet = 40001;
    public static final int VIP = 2008;
    public static final int ONLINE_GIFT = 2009;
    public static final int WAR_INFORMATION = 2013;
    /**
     * 荒神加速
     */
    public static final int STATUS_BRAVE2 = 999;
    /**
     * 師徒系統
     **/
    // 師徒系統懲罰
    public static final int MASTER_PUNISH = 25007;
    /**
     * 沒有與師父組隊 一名徒弟
     */
    public static final int EFFECT_NO_MASTER_1 = 4062;
    /**
     * 沒有與師父組隊 二名徒弟
     */
    public static final int EFFECT_NO_MASTER_2 = 4063;
    /**
     * 沒有與師父組隊 三名徒弟
     */
    public static final int EFFECT_NO_MASTER_3 = 4064;
    /**
     * 沒有與師父組隊 四名徒弟
     */
    public static final int EFFECT_NO_MASTER_4 = 4065;
    /**
     * 有與師父組隊 一名徒弟
     */
    public static final int EFFECT_WITH_MASTER_1 = 4066;
    /**
     * 有與師父組隊 二名徒弟
     */
    public static final int EFFECT_WITH_MASTER_2 = 4067;
    /**
     * 有與師父組隊 三名徒弟
     */
    public static final int EFFECT_WITH_MASTER_3 = 4068;
    /**
     * 有與師父組隊 四名徒弟
     */
    public static final int EFFECT_WITH_MASTER_4 = 4069;
    /**
     * 攻擊無效狀態
     **/
    public static final int attack_fanlse = 20002; // add hjx1000 對所有目標攻擊無效buff
    /**
     * 攻擊狀態
     **/
    public static final int attack_ing = 20003;// 檢測外掛狀態 hjx1000
    /**
     * 收費計時狀態
     **/
    public static final int Card_Fee = 20004; // 在線點卡計費狀態 hjx1000
    /**
     * 掃街系統
     **/ // src015
    public static final int FREE_FIGHT_SHOW_INFO = 6820;
    /**
     * 下層戰鬥強化卷軸
     **/
    public static final int LOWER_FLOOR_GREATER_BATTLE_SCROLL = 50370;
    /**
     * 下層防禦強化卷軸
     **/
    public static final int LOWER_FLOOR_GREATER_DEFENSE_SCROLL = 50371;
    /**
     * 連擊系統狀態
     **/
    public static final int COMBO_BUFF = 80006;
    /**
     * 升級經驗獎勵狀態
     **/
    public static final int LEVEL_UP_BONUS = 80007;
    /**
     * 潘朵拉銅像延遲狀態
     **/
    public static final int StatueMagic = 9998;
    /**
     * 葉子(暫無用)徽章狀態
     **/
    public static final int MiniGameIcon_1 = 9501;
    /**
     * 骷髏(暗殺者)徽章狀態
     **/
    public static final int MiniGameIcon_2 = 9502;
    /**
     * 火焰(復仇者)徽章狀態
     **/
    public static final int MiniGameIcon_3 = 9503;
    /**
     * 底比斯大戰專用(黃)徽章狀態
     **/
    public static final int MiniGameIcon_4 = 9504;
    /**
     * 底比斯大戰專用(藍)徽章狀態
     **/
    public static final int MiniGameIcon_5 = 9505;
    /**
     * 底比斯大戰專用(紅)徽章狀態
     **/
    public static final int MiniGameIcon_6 = 9506;
    /**
     * 紅騎士團徽章狀態
     **/
    public static final int Red_Knight = 9999;
    /**
     * 潘朵拉近戰魔石狀態
     **/
    public static final int Pandora_Magic_Stone_1 = 4072;
    /**
     * 潘朵拉遠攻魔石狀態
     **/
    public static final int Pandora_Magic_Stone_2 = 4073;
    /**
     * 潘朵拉魔攻魔石狀態
     **/
    public static final int Pandora_Magic_Stone_3 = 4074;
    /**
     * 潘朵拉防禦魔石狀態
     **/
    public static final int Pandora_Magic_Stone_4 = 4075;
    /**
     * 古老秘藥狀態
     **/
    public static final int Ancient_Secretn = 9406;
    /**
     * 重置天賦技能掉線狀態
     **/
    public static final int ReiSkill_Disconnect = 9410;
    /**
     * 王族天賦技能金剛護體狀態
     **/
    public static final int ReiSkill_1 = 9411;
    /**
     * 戰士天賦技能冰封心智狀態 or 放逐-使對方無法攻擊
     **/
    public static final int ReiSkill_2 = 9412;
    /**
     * 特效驗證系統-AI驗證用-開始
     */
    public static final int AIFORSTART = 7020;
    /**
     * 特效驗證系統-AI驗證用-結束
     */
    public static final int AIFOREND = 7021;
    /**
     * 破甲-無減傷-PVP減傷減半-狀態
     */
    public static final int negativeId13 = 9413;
    /**
     * 虛弱-攻擊減半狀態
     */
    public static final int negativeId15 = 9414;
    /**
     * 武器劍靈系統狀態
     */
    public static final int WeaponSoul = 9415;
    /**
     * 1階段成長果實狀態
     */
    public static final int Tam_Fruit1 = 7791; // 成長果實系統(Tam幣)
    /**
     * 2階段成長果實狀態
     */
    public static final int Tam_Fruit2 = 7792; // 成長果實系統(Tam幣)
    /**
     * 3階段成長果實狀態
     */
    public static final int Tam_Fruit3 = 7793; // 成長果實系統(Tam幣)
    /**
     * 傳送選擇鎖定中
     */
    public static final int PC_TEL_LOCK = 9416;
    /**
     * 攻擊速度減益
     */
    public static final int HIT_BUFF = 9002; // 攻擊速度減益
    /**
     * 星盤技能使用,降低目標回避率(回避率只對玩家有效)
     */
    public static final int ASTROLOGY_DG_ER = 51000;
}
