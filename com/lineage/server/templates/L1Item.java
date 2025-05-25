package com.lineage.server.templates;

import java.io.Serializable;

public abstract class L1Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private int _type2;
    private int _itemId;
    private String _name;
    private String _classname;
    private String _nameId;
    private int _type;
    private int _type1;
    private int _material;
    private int _weight;
    private int _gfxId;
    private int _groundGfxId;
    private int _minLevel;
    private int _itemDescId;
    private int _maxLevel;
    private int _bless;
    private boolean _tradable;
    private boolean _cantDelete;
    private boolean _save_at_once;
    private int _maxUseTime = 0;
    private int _foodVolume;
    private int _useType;
    private int _dmgSmall = 0;
    private int _dmgLarge = 0;
    private int[] _mode = null;
    private int _safeEnchant = 0;
    private boolean _useRoyal = false;
    private boolean _useKnight = false;
    private boolean _useElf = false;
    private boolean _useMage = false;
    private boolean _useDarkelf = false;
    private boolean _useDragonknight = false;
    private boolean _useIllusionist = false;
    private boolean _useWarrior = false;
    private byte _addstr = 0;
    private byte _adddex = 0;
    private byte _addcon = 0;
    private byte _addint = 0;
    private byte _addwis = 0;
    private byte _addcha = 0;
    private int _stunlvl = 0;
    private int _addhp = 0;
    private int _addmp = 0;
    private int _addhpr = 0;
    private int _addmpr = 0;
    private int _addsp = 0;
    private int _mdef = 0;
    private boolean _isHasteItem = false;
    private int _delay_effect;
    private int _expPoint;
    private String _identifiedNameId;//抽抽樂
    // 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用)
    private int _campSet;
    // 最低使用需求 (轉生次數) by terry0412
    private int _meteLevel;
    // 最高使用需求 (轉生次數) by terry0412
    private int _meteLevelMAX;
    // 是否可存倉
    private boolean _isWarehouseable = true;
    // 增加PVP傷害
    private int _PvpDmg = 0;
    // 减免PVP傷害
    private int _PvpDmg_R = 0;
    private int _fearlevel = 0;
    /***暗黑系統*/
    private int _isItemAttr;
    private int _useclass;
    private int _is_isColorWashout;
    private int _DollBreakLv = 0;// 娃娃能力描述 - 破壞盔甲命中

    /**
     * @return 0 if L1EtcItem, 1 if L1Weapon, 2 if L1Armor
     */
    public int getType2() {
        return _type2;
    }

    public void setType2(int type) {
        _type2 = type;
    }

    public int getItemId() {
        return _itemId;
    }

    public void setItemId(int itemId) {
        _itemId = itemId;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getclassname() {
        return _classname;
    }

    public void setClassname(String classname) {
        _classname = classname;
    }

    public String getNameId() {
        return _nameId;
    }

    public void setNameId(String nameid) {
        _nameId = nameid;
    }

    /**
     * 傳回物品分類<br>
     *
     * @return <p>
     * <font color=#ff0000>[etcitem]-道具類型</font><br>
     * 0:arrow, 1:wand, 2:light, 3:gem, 4:totem, 5:firecracker,
     * 6:potion, 7:food, 8:scroll, 9:questitem, 10:spellbook,
     * 11:petitem, 12:other, 13:material, 14:event, 15:sting,
     * 16:treasure_box
     * </p>
     * <p>
     * <font color=#ff0000>[weapon]-武器類型</font><br>
     * 1: sword <font color=#00800>劍(單手)</font><br>
     * 2: dagger <font color=#00800>匕首(單手)</font><br>
     * 3: tohandsword <font color=#00800>雙手劍(雙手)</font><br>
     * 4: bow <font color=#00800>弓(雙手)</font><br>
     * 5: spear <font color=#00800>矛(雙手)</font><br>
     * 6: blunt <font color=#00800>斧(單手)</font><br>
     * 7: staff <font color=#00800>魔杖(單手)</font><br>
     * 8: throwingknife <font color=#00800>■刀</font><br>
     * 9: arrow <font color=#00800>箭</font><br>
     * 10: gauntlet <font color=#00800>鐵手甲</font><br>
     * 11: claw <font color=#00800>鋼爪(雙手)</font><br>
     * 12: edoryu <font color=#00800>雙刀(雙手)</font><br>
     * 13: singlebow <font color=#00800>弓(單手)</font><br>
     * 14: singlespear <font color=#00800>矛(單手)</font><br>
     * 15: tohandblunt <font color=#00800>雙手斧(雙手)</font><br>
     * 16: tohandstaff <font color=#00800>魔杖(雙手)</font><br>
     * 17: kiringku <font color=#00800>奇古獸(單手)</font><br>
     * 18: chainsword <font color=#00800>鎖鏈劍(單手)</font><br>
     * </p>
     * <p>
     * <font color=#ff0000>[armor]-防具類型</font><br>
     * 1: helm <font color=#00800>頭盔</font><br>
     * 2: armor <font color=#00800>盔甲</font><br>
     * 3: T <font color=#00800>內衣</font><br>
     * 4: cloak <font color=#00800>斗篷</font><br>
     * 5: glove <font color=#00800>手套</font><br>
     * 6: boots <font color=#00800>靴子</font><br>
     * 7: shield <font color=#00800>盾</font><br>
     * 8: amulet <font color=#00800>項鏈</font><br>
     * 9: ring <font color=#00800>戒指</font><br>
     * 10: belt <font color=#00800>腰帶</font><br>
     * 11: ring2 <font color=#00800>戒指2</font><br>
     * 12: earring <font color=#00800>耳環</font> <br>
     * 13: guarder <font color=#00800>臂甲</font><br>
     * 14: runeword_left <font color=#00800>輔助左</font><br>
     * 15: runeword_right <font color=#00800>輔助右</font><br>
     * 16: runeword_middle <font color=#00800>輔助中</font><br>
     * 17: pants <font color=#00800>脛甲</font><br>
     * 18: talisman <font color=#00800>六芒星護身符系列</font><br>
     * 19: talisman2 <font color=#00800>蒂蜜特紋樣系列</font><br>
     * 20: talisman3 <font color=#00800>蒂蜜特符文</font><br>
     * 21: vip <font color=#00800>vip</font><br>
     * </p>
     */
    public int getType() {
        return _type;
    }

    public void setType(int type) {
        _type = type;
    }

    /**
     * 封包影響外型編號<br>
     *
     * @return <p>
     * <font color=#ff0000>[weapon]-武器類型</font><br>
     * sword: 4 <font color=#00800>劍</font><br>
     * dagger: 46 <font color=#00800>匕首</font><br>
     * tohandsword: 50 <font color=#00800>雙手劍</font><br>
     * bow: 20 <font color=#00800>弓</font><br>
     * blunt: 11 <font color=#00800>斧(單手)</font><br>
     * spear: 24 <font color=#00800>矛(雙手)</font><br>
     * staff: 40 <font color=#00800>魔杖</font><br>
     * throwingknife: 2922 <font color=#00800>■刀</font><br>
     * arrow: 66 <font color=#00800>箭</font><br>
     * gauntlet: 62 <font color=#00800>鐵手甲</font><br>
     * claw: 58 <font color=#00800>鋼爪</font><br>
     * edoryu: 54 <font color=#00800>雙刀</font><br>
     * singlebow: 20 <font color=#00800>弓(單手)</font><br>
     * singlespear: 24 <font color=#00800>矛(單手)</font><br>
     * tohandblunt: 11 <font color=#00800>雙手斧</font><br>
     * tohandstaff: 40 <font color=#00800>魔杖(雙手)</font><br>
     * kiringku: 58 <font color=#00800>奇古獸</font><br>
     * chainsword: 24 <font color=#00800>鎖鏈劍</font><br>
     * </p>
     */
    public int getType1() {
        return _type1;
    }

    public void setType1(int type1) {
        _type1 = type1;
    }

    /**
     * 素材返
     *
     * @return 0:none 1:液體 2:web 3:植物性 4:動物性 5:紙 6:布 7:皮 8:木 9:骨 10:龍鱗 11:鐵
     * 12:鋼鐵 13:銅 14:銀 15:金 16: 17: 18: 19: 20:寶石
     * 21:礦物 22:
     */
    public int getMaterial() {
        return _material;
    }

    public void setMaterial(int material) {
        _material = material;
    }

    public int getWeight() {
        return _weight;
    }

    public void setWeight(int weight) {
        _weight = weight;
    }

    public int getGfxId() {
        return _gfxId;
    }

    public void setGfxId(int gfxId) {
        _gfxId = gfxId;
    }

    public int getGroundGfxId() {
        return _groundGfxId;
    }

    public void setGroundGfxId(int groundGfxId) {
        _groundGfxId = groundGfxId;
    }

    public int getItemDescId() {
        return _itemDescId;
    }

    public void setItemDescId(int descId) {
        _itemDescId = descId;
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

    public void setMaxLevel(int maxlvl) {
        _maxLevel = maxlvl;
    }

    public int getBless() {
        return _bless;
    }

    public void setBless(int i) {
        _bless = i;
    }

    public boolean isTradable() {
        return _tradable;
    }

    public void setTradable(boolean flag) {
        _tradable = flag;
    }

    public boolean isCantDelete() {
        return _cantDelete;
    }

    public void setCantDelete(boolean flag) {
        _cantDelete = flag;
    }

    public boolean isToBeSavedAtOnce() {
        return _save_at_once;
    }

    public void setToBeSavedAtOnce(boolean flag) {
        _save_at_once = flag;
    }

    public int getMaxUseTime() {
        return _maxUseTime;
    }

    public void setMaxUseTime(int i) {
        _maxUseTime = i;
    }

    public int getFoodVolume() {
        return _foodVolume;
    }

    public void setFoodVolume(int volume) {
        _foodVolume = volume;
    }

    public int getLightRange() {
        int light = 0;
        switch (_itemId) {
            case 40001:
                light = 11;
                break;
            case 40002:
                light = 14;
                break;
            case 40004:
                light = 22;
                break;
            case 40005:
                light = 8;
            case 40003:
        }
        return light;
    }

    public int getLightFuel() {
        int time = 0;
        switch (_itemId) {
            case 40001:
                time = 6000;
                break;
            case 40002:
                time = 12000;
                break;
            case 40003:
                time = 12000;
                break;
            case 40004:
                time = 0;
                break;
            case 40005:
                time = 600;
        }
        return time;
    }

    /**
     * 物品使用封包類型
     *
     * @return <p>
     * petitem: -12 <font color=#00800>寵物道具</font><br>
     * other: -11 <font color=#00800>對讀取方法調用無法分類的物品</font><br>
     * power: -10 <font color=#00800>加速藥水</font><br>
     * book: -9 <font color=#00800>技術書</font><br>
     * makecooking: -8 <font color=#00800>料理書</font><br>
     * hpr: -7 <font color=#00800>增HP道具</font><br>
     * mpr: -6 <font color=#00800>增MP道具</font><br>
     * ticket: -5 <font color=#00800>食人妖精競賽票/死亡競賽票/彩票</font><br>
     * petcollar: -4 <font color=#00800>項圈</font><br>
     * sting: -3 <font color=#00800>■刀</font><br>
     * arrow: -2 <font color=#00800>箭</font><br>
     * none: -1 <font color=#00800>無法使用(材料等)</font><br>
     * normal: 0 <font color=#00800>一般物品</font><br>
     * weapon: 1 <font color=#00800>武器</font><br>
     * armor: 2 <font color=#00800>盔甲</font><br>
     * spell_1: 3 <font color=#00800>創造怪物魔杖(無須選取目標 -
     * 無數量:沒有任何事情發生)</font><br>
     * guarder: 4 <font color=#808080>希望魔杖 --- 未使用</font><br>
     * spell_long: 5 <font color=#00800>魔杖類型(須選取目標/座標)</font><br>
     * ntele: 6 <font color=#00800>瞬間移動卷軸</font><br>
     * identify: 7 <font color=#00800>鑒定卷軸</font><br>
     * res: 8 <font color=#00800>復活卷軸</font><br>
     * home: 9 <font color=#00800>傳送回家的卷軸</font><br>
     * light: 10 <font color=#00800>照明道具</font><br>
     * letter: 12 <font color=#00800>信紙</font><br>
     * letter_card: 13 <font color=#00800>信紙(寄出)</font><br>
     * choice: 14 <font color=#00800>請選擇一個物品(道具欄位)</font><br>
     * instrument: 15 <font color=#00800>哨子</font><br>
     * sosc: 16 <font color=#00800>變形卷軸</font><br>
     * spell_short: 17 <font color=#00800>選取目標 (近距離)</font><br>
     * T: 18 <font color=#00800>T恤</font><br>
     * cloak: 19 <font color=#00800>斗篷</font><br>
     * glove: 20 <font color=#00800>手套</font><br>
     * boots: 21 <font color=#00800>靴</font><br>
     * helm: 22 <font color=#00800>頭盔</font><br>
     * ring: 23 <font color=#00800>戒指</font><br>
     * amulet: 24 <font color=#00800>項鏈</font><br>
     * shield: 25 <font color=#00800>盾牌</font><br>
     * guarder: 25 <font color=#00800>臂甲</font><br>
     * dai: 26 <font color=#00800>對武器施法的卷軸</font><br>
     * zel: 27 <font color=#00800>對盔甲施法的卷軸</font><br>
     * blank: 28 <font color=#00800>空的魔法卷軸</font><br>
     * btele: 29 <font color=#00800>瞬間移動卷軸(祝福)</font><br>
     * spell_buff: 30 <font color=#00800>選取目標 (對NPC需要Ctrl 遠距離
     * 無XY座標傳回)</font><br>
     * ccard: 31 <font color=#00800>聖誕卡片</font><br>
     * ccard_w: 32 <font color=#00800>聖誕卡片(寄出)</font><br>
     * vcard: 33 <font color=#00800>情人節卡片</font><br>
     * vcard_w: 34 <font color=#00800>情人節卡片(寄出)</font><br>
     * wcard: 35 <font color=#00800>白色情人節卡片</font><br>
     * wcard_w: 36 <font color=#00800>白色情人節卡片(寄出)</font><br>
     * belt: 37 <font color=#00800>腰帶</font><br>
     * food: 38 <font color=#00800>食物</font><br>
     * spell_long2: 39 <font color=#00800>選取目標 (遠距離)</font><br>
     * earring: 40 <font color=#00800>耳環</font><br>
     * fishing_rod: 42 <font color=#00800>釣魚桿</font><br>
     * runeword_left: 43 <font color=#00800>輔助左</font><br>
     * runeword_right: 44 <font color=#00800>輔助右</font><br>
     * runeword_middle: 45 <font color=#00800>輔助中 </font><br>
     * enc: 46 <font color=#00800>飾品強化卷軸</font><br>
     * pants: 70 <font color=#00800>脛甲</font><br>
     * talisman: 48 <font color=#00800>六芒星護身符</font><br>
     * talisman2: 49 <font color=#00800>蒂蜜特的紋樣系列</font><br>
     * talisman3: 51 <font color=#00800>蒂蜜特的符文</font><br>
     * choice_doll: 55 <font color=#00800>請選擇魔法娃娃</font><br>
     * </p>
     */
    public int getUseType() {
        return _useType;
    }

    public void setUseType(int useType) {
        _useType = useType;
    }

    public int getDmgSmall() {
        return _dmgSmall;
    }

    public void setDmgSmall(int dmgSmall) {
        _dmgSmall = dmgSmall;
    }

    public int getDmgLarge() {
        return _dmgLarge;
    }

    public void setDmgLarge(int dmgLarge) {
        _dmgLarge = dmgLarge;
    }

    public int[] get_mode() {
        return _mode;
    }

    /**
     * 設定套裝效果資料
     *
     * @param mode
     */
    public void set_mode(int[] mode) {
        _mode = mode;
    }

    public int get_safeenchant() {
        return _safeEnchant;
    }

    public void set_safeenchant(int safeenchant) {
        _safeEnchant = safeenchant;
    }

    public boolean isUseRoyal() {
        return _useRoyal;
    }

    public void setUseRoyal(boolean flag) {
        _useRoyal = flag;
    }

    public boolean isUseKnight() {
        return _useKnight;
    }

    public void setUseKnight(boolean flag) {
        _useKnight = flag;
    }

    public boolean isUseElf() {
        return _useElf;
    }

    public void setUseElf(boolean flag) {
        _useElf = flag;
    }

    public boolean isUseMage() {
        return _useMage;
    }

    public void setUseMage(boolean flag) {
        _useMage = flag;
    }

    public boolean isUseDarkelf() {
        return _useDarkelf;
    }

    public void setUseDarkelf(boolean flag) {
        _useDarkelf = flag;
    }

    public boolean isUseDragonknight() {
        return _useDragonknight;
    }

    public void setUseDragonknight(boolean flag) {
        _useDragonknight = flag;
    }

    public boolean isUseIllusionist() {
        return _useIllusionist;
    }

    public void setUseIllusionist(boolean flag) {
        _useIllusionist = flag;
    }

    public boolean isUseWarrior() {
        return _useWarrior;
    }

    public void setUseWarrior(final boolean flag) {
        _useWarrior = flag;
    }

    public byte get_addstr() {
        return _addstr;
    }

    public void set_addstr(byte addstr) {
        _addstr = addstr;
    }

    public byte get_adddex() {
        return _adddex;
    }

    public void set_adddex(byte adddex) {
        _adddex = adddex;
    }

    public byte get_addcon() {
        return _addcon;
    }

    public void set_addcon(byte addcon) {
        _addcon = addcon;
    }

    public byte get_addint() {
        return _addint;
    }

    public void set_addint(byte addint) {
        _addint = addint;
    }

    public byte get_addwis() {
        return _addwis;
    }

    public void set_addwis(byte addwis) {
        _addwis = addwis;
    }

    public byte get_addcha() {
        return _addcha;
    }

    public void set_addcha(byte addcha) {
        _addcha = addcha;
    }

    public int get_stunlvl() {
        return _stunlvl;
    }

    public void set_stunlvl(int stunlvl) {
        _stunlvl = stunlvl;
    }

    public int get_addhp() {
        return _addhp;
    }

    public void set_addhp(int addhp) {
        _addhp = addhp;
    }

    public int get_addmp() {
        return _addmp;
    }

    public void set_addmp(int addmp) {
        _addmp = addmp;
    }

    public int get_addhpr() {
        return _addhpr;
    }

    public void set_addhpr(int addhpr) {
        _addhpr = addhpr;
    }

    public int get_addmpr() {
        return _addmpr;
    }

    public void set_addmpr(int addmpr) {
        _addmpr = addmpr;
    }

    public int get_addsp() {
        return _addsp;
    }

    public void set_addsp(int addsp) {
        _addsp = addsp;
    }

    public int get_mdef() {
        return _mdef;
    }

    public void set_mdef(int i) {
        _mdef = i;
    }

    public boolean isHasteItem() {
        return _isHasteItem;
    }

    public void setHasteItem(boolean flag) {
        _isHasteItem = flag;
    }

    public boolean isStackable() {
        return false;
    }

    public int get_delayid() {
        return 0;
    }

    public int get_delaytime() {
        return 0;
    }

    public int getMaxChargeCount() {
        return 0;
    }

    public int get_delayEffect() {
        return _delay_effect;
    }

    public void set_delayEffect(int delay_effect) {
        _delay_effect = delay_effect;
    }

    public int get_add_dmg() {
        return 0;
    }

    public int getRange() {
        return 0;
    }

    /**
     * 武器命中
     */
    public int getHitModifier() {
        return 0;
    }

    public int getDmgModifier() {
        return 0;
    }

    public int getDoubleDmgChance() {
        return 0;
    }

    public int get_canbedmg() {
        return 0;
    }

    public boolean isTwohandedWeapon() {
        return false;
    }

    public int get_ac() {
        return 0;
    }

    /**
     * 傷害減免
     */
    public int getDamageReduction() {
        return 0;
    }

    public int getWeightReduction() {
        return 0;
    }

    /**
     * 防具增加近距離命中率
     */
    public int getHitModifierByArmor() {
        return 0;
    }

    public int getDmgModifierByArmor() {
        return 0;
    }

    /**
     * 防具增加遠距離命中率
     */
    public int getBowHitModifierByArmor() {
        return 0;
    }

    public int getBowDmgModifierByArmor() {
        return 0;
    }

    /**
     * 防具增加魔法命中
     */
    public int getMagicHitModifierByArmor() {
        return 0;
    }

    public int get_dice_dmg() {
        return 0;
    }

    public int getDmg() {
        return 0;
    }

    public int getMagicDmgModifier() {
        return 0;
    }

    public int get_defense_water() {
        return 0;
    }

    public int get_defense_fire() {
        return 0;
    }

    public int get_defense_earth() {
        return 0;
    }

    public int get_defense_wind() {
        return 0;
    }

    public int get_regist_stun() {
        return 0;
    }

    public int get_regist_stone() {
        return 0;
    }

    public int get_regist_sleep() {
        return 0;
    }

    public int get_regist_freeze() {
        return 0;
    }

    public int get_regist_sustain() {
        return 0;
    }

    public int get_regist_blind() {
        return 0;
    }

    public int get_greater() {
        return 3;
    }

    public int getExpPoint() {
        return _expPoint;
    }

    public void setExpPoint(int i) {
        _expPoint = i;
    }

    public int getCampSet() {
        return this._campSet;
    }

    public void setCampSet(final int i) {
        this._campSet = i;
    }

    public int getMeteLevel() {
        return this._meteLevel;
    }

    public void setMeteLevel(final int i) {
        this._meteLevel = i;
    }

    public int getMeteLevelMAX() {
        return this._meteLevelMAX;
    }

    public void setMeteLevelMAX(final int i) {
        this._meteLevelMAX = i;
    }

    public boolean isWarehouseable() {
        return _isWarehouseable;
    }

    public void setWarehouseable(boolean flag) {
        _isWarehouseable = flag;
    }

    public int get_up_hp_potion() {
        return 0;
    }

    public int get_uhp_number() {
        return 0;
    }

    //抽抽樂
    public String getIdentifiedNameId() {
        return this._identifiedNameId;
    }

    public void setIdentifiedNameId(String identifiedNameId) {
        this._identifiedNameId = identifiedNameId;
    }

    /**
     * 強化值影響的增減魔防值 by terry0412
     *
     * @return
     */
    public int getInfluenceMr() {
        return 0;
    }

    /**
     * 強化值影響的增減魔攻值 by terry0412
     *
     * @return
     */
    public int getInfluenceSp() {
        return 0;
    }

    /**
     * 強化值影響的增減HP值 by terry0412
     *
     * @return
     */
    public int getInfluenceHp() {
        return 0;
    }

    /**
     * 強化值影響的增減MP值 by terry0412
     *
     * @return
     */
    public int getInfluenceMp() {
        return 0;
    }

    /**
     * 強化值影響的增減傷害減免值 by terry0412
     *
     * @return
     */
    public int getInfluenceDmgR() {
        return 0;
    }

    /**
     * 強化值影響的增減近距離命中以及近距離攻擊值 by terry0412
     *
     * @return
     */
    public int getInfluenceHitAndDmg() {
        return 0;
    }

    /**
     * 強化值影響的增減遠距離命中以及遠距離攻擊值 by terry0412
     *
     * @return
     */
    public int getInfluenceBowHitAndDmg() {
        return 0;
    }

    /**
     * 是否為活動戒指或收費戒指 by terry0412
     *
     * @return
     */
    public boolean isActivity() { //src013
        return false;
    }

    public boolean isSuperRune() {
        return false;
    }

    public int getPvpDmg() {
        return _PvpDmg;
    }

    public void setPvpDmg(final int i) {
        _PvpDmg = i;
    }

    public int getPvpDmg_R() {
        return _PvpDmg_R;
    }

    public void setPvpDmg_R(final int i) {
        _PvpDmg_R = i;
    }

    /**
     * 娃娃能力描述 - 破壞盔甲命中
     */
    public int getDollBreakLv() {
        return _DollBreakLv;
    }

    /**
     * 娃娃能力描述 - 破壞盔甲命中
     */
    public void setDollBreakLv(final int i) {
        _DollBreakLv = i;
    }


    public int get_kitType() {
        return 0;
    }

    public int get_FearLevel() {
        return this._fearlevel;
    }

    public void set_FearLevel(int fearlevel) {
        this._fearlevel = fearlevel;
    }

    public int get_isItemAttr() {
        return this._isItemAttr;
    }

    public void set_isItemAttr(final int _isItemAttr) {
        this._isItemAttr = _isItemAttr;
    }

    public int getUseClass() {
        return _useclass;
    }

    public void setUseClass(final int useclass) {
        _useclass = useclass;
    }

    public int get_isColorWashout() {
        return this._is_isColorWashout;
    }

    public void set_isColorWashout(final int _isColorWashout) {
        this._is_isColorWashout = _isColorWashout;
    }
}
