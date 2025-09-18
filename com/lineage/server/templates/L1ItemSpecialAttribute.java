package com.lineage.server.templates;

/**
 * 物品特殊屬性記錄<br>
 * 類名稱：L1ItemSpecialAttribute<br>
 * 創建人:xljnet<br>
 * 修改時間：2018年4月25日 下午1:18:25<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 *
 * @version Rev:3.2 Bin:81222<br>
 */
public class L1ItemSpecialAttribute {
    private int _id;
    private String _colour;
    private int _type;
    private String _name;
    private int _dmg_small;
    private int _dmg_large;
    private int _hitmodifier;
    private int _dmgmodifier;
    private int _add_str;
    private int _add_con;
    private int _add_dex;
    private int _add_int;
    private int _add_wis;
    private int _add_cha;
    private int _add_hp;
    private int _add_mp;
    private int _add_hpr;
    private int _add_mpr;
    private int _add_sp;
    private int _add_rand;
    private int _add_m_def;
    private int _add_drain_min_hp;
    private int _add_drain_max_hp;
    private int _drain_hp_rand;
    private int _add_drain_min_mp;
    private int _add_drain_max_mp;
    private int _drain_mp_rand;
    private int _add_skill_rand;
    private int _add_skill_gfxid;
    private boolean _add_skill_arrow;
    private int _add_skill_dmg;
    private int _add_Special_magic;
    private int _Special_magic_rand;
    private int _魔法格檔;
    private int _blockWeapon;
    private int _shanghaijianmian;

    /**
     * 傳回特殊屬性ID
     *
     */
    public int get_id() {
        return _id;
    }

    /**
     * 設置特殊屬性ID
     *
     */
    public void set_id(int id) {
        _id = id;
    }

    /**
     * 傳回顏色代碼
     *
     */
    public String get_colour() {
        return _colour;
    }

    /**
     * 設置顏色代碼
     *
     */
    public void set_colour(String colour) {
        _colour = colour;
    }

    /** 正確的武器類型 2025.06.08 Honglin
     * 傳回特殊屬性所屬類型<br>
     * 1:sword   劍(單手)<br>
     * 2:dagger   匕首(單手)<br>
     * 3:tohandsword   雙手劍(雙手)<br>
     * 4:bow   弓(雙手)<br>
     * 5:spear   矛(雙手)<br>
     * 6:blunt   斧(單手)<br>
     * 7:staff   魔杖(單手)<br>
     * 11:claw   鋼爪(雙手)<br>
     * 12:edoryu   雙刀(雙手)<br>
     * 13:singlebow   弓(單手)<br>
     * 14:singlespear   矛(單手)<br>
     * 15:tohandblunt   雙手斧(雙手)<br>
     * 16:tohandstaff   魔杖(雙手)<br>
     * 17:kiringku   奇古獸(單手)<br>
     * 18:chainsword   鎖鏈劍(單手)<br>
     */
    public int get_type() {
        return _type;
    }

    /**
     * 設置特殊屬性所屬類型
     *
     */
    public void set_type(int type) {
        this._type = type;
    }

    /**
     * 傳回特殊屬性名稱
     *
     */
    public String get_name() {
        return this._name;
    }

    /**
     * 設置特殊屬性名稱
     *
     */
    public void set_name(String name) {
        this._name = name;
    }

    /**
     * 傳回最小攻擊力
     *
     */
    public int get_dmg_small() {
        return this._dmg_small;
    }

    /**
     * 設置最小攻擊力
     *
     */
    public void set_dmg_small(int dmg_small) {
        this._dmg_small = dmg_small;
    }

    /**
     * 傳回最大攻擊力
     *
     */
    public int get_dmg_large() {
        return this._dmg_large;
    }

    /**
     * 設置最大攻擊力
     *
     */
    public void set_dmg_large(int dmg_large) {
        this._dmg_large = dmg_large;
    }

    /**
     * 傳回命中
     *
     */
    public int get_hitmodifier() {
        return this._hitmodifier;
    }

    /**
     * 設置命中
     *
     */
    public void set_hitmodifier(int hitmodifier) {
        this._hitmodifier = hitmodifier;
    }

    /**
     * 傳回額外攻擊力
     *
     */
    public int get_dmgmodifier() {
        return this._dmgmodifier;
    }

    /**
     * 設置額外攻擊力
     *
     */
    public void set_dmgmodifier(int dmgmodifier) {
        this._dmgmodifier = dmgmodifier;
    }

    /**
     * 傳回力量
     *
     */
    public int get_add_str() {
        return this._add_str;
    }

    /**
     * 設置力量
     *
     */
    public void set_add_str(int add_str) {
        this._add_str = add_str;
    }

    /**
     * 傳回體質
     *
     */
    public int get_add_con() {
        return this._add_con;
    }

    /**
     * 設置體質
     *
     */
    public void set_add_con(int add_con) {
        this._add_con = add_con;
    }

    /**
     * 傳回敏捷
     *
     */
    public int get_add_dex() {
        return this._add_dex;
    }

    /**
     * 設置敏捷
     *
     */
    public void set_add_dex(int add_dex) {
        this._add_dex = add_dex;
    }

    /**
     * 傳回智力
     *
     */
    public int get_add_int() {
        return this._add_int;
    }

    /**
     * 設置智力
     *
     */
    public void set_add_int(int add_int) {
        this._add_int = add_int;
    }

    /**
     * 傳回精神
     *
     */
    public int get_add_wis() {
        return this._add_wis;
    }

    /**
     * 設置精神
     *
     */
    public void set_add_wis(int add_wis) {
        this._add_wis = add_wis;
    }

    /**
     * 傳回魅力
     *
     */
    public int get_add_cha() {
        return this._add_cha;
    }

    /**
     * 設置魅力
     *
     */
    public void set_add_cha(int add_cha) {
        this._add_cha = add_cha;
    }

    /**
     * 傳回HP
     *
     */
    public int get_add_hp() {
        return this._add_hp;
    }

    /**
     * 設置HP
     *
     */
    public void set_add_hp(int add_hp) {
        this._add_hp = add_hp;
    }

    /**
     * 傳回MP
     *
     */
    public int get_add_mp() {
        return this._add_mp;
    }

    /**
     * 設置MP
     *
     */
    public void set_add_mp(int add_mp) {
        this._add_mp = add_mp;
    }

    /**
     * 傳回回血量
     *
     */
    public int get_add_hpr() {
        return this._add_hpr;
    }

    /**
     * 設置回血量
     *
     */
    public void set_add_hpr(int add_hpr) {
        this._add_hpr = add_hpr;
    }

    /**
     * 傳回回魔量
     *
     */
    public int get_add_mpr() {
        return this._add_mpr;
    }

    /**
     * 設置回魔量
     *
     */
    public void set_add_mpr(int add_mpr) {
        this._add_mpr = add_mpr;
    }

    /**
     * 傳回魔攻
     *
     */
    public int get_add_sp() {
        return this._add_sp;
    }

    /**
     * 設置魔攻
     *
     */
    public void set_add_sp(int add_sp) {
        this._add_sp = add_sp;
    }

    /**
     * 傳回獲取幾率
     *
     */
    public int get_add_rand() {
        return this._add_rand;
    }

    /**
     * 設置獲取幾率
     *
     */
    public void set_add_rand(int add_rand) {
        this._add_rand = add_rand;
    }

    /**
     * 傳回魔防
     *
     */
    public int get_add_m_def() {
        return this._add_m_def;
    }

    /**
     * 設置魔防
     *
     */
    public void set_add_m_def(int add_m_def) {
        this._add_m_def = add_m_def;
    }

    /**
     * 傳回最小吸血量
     *
     */
    public int get_add_drain_min_hp() {
        return this._add_drain_min_hp;
    }

    /**
     * 設置最小吸血量
     *
     */
    public void set_add_drain_min_hp(int drain_min_hp) {
        this._add_drain_min_hp = drain_min_hp;
    }

    /**
     * 傳回最大吸血量
     *
     */
    public int get_add_drain_max_hp() {
        return this._add_drain_max_hp;
    }

    /**
     * 設置最大吸血量
     *
     */
    public void set_add_drain_max_hp(int drain_max_hp) {
        this._add_drain_max_hp = drain_max_hp;
    }

    /**
     * 傳回發動吸血的幾率
     *
     */
    public int get_drain_hp_rand() {
        return this._drain_hp_rand;
    }

    /**
     * 設置發動吸血的幾率
     *
     */
    public void set_drain_hp_rand(int drain_hp_rand) {
        this._drain_hp_rand = drain_hp_rand;
    }

    /**
     * 傳回最小吸魔量
     *
     */
    public int get_add_drain_min_mp() {
        return this._add_drain_min_mp;
    }

    /**
     * 設置最小吸魔量
     *
     */
    public void set_add_drain_min_mp(int drain_min_mp) {
        this._add_drain_min_mp = drain_min_mp;
    }

    /**
     * 傳回最大吸魔量
     *
     */
    public int get_add_drain_max_mp() {
        return this._add_drain_max_mp;
    }

    /**
     * 設置最大吸魔量
     *
     */
    public void set_add_drain_max_mp(int drain_max_mp) {
        this._add_drain_max_mp = drain_max_mp;
    }

    /**
     * 傳回吸魔幾率
     *
     */
    public int get_drain_mp_rand() {
        return this._drain_mp_rand;
    }

    /**
     * 設置吸魔幾率
     *
     */
    public void set_drain_mp_rand(int drain_mp_rand) {
        this._drain_mp_rand = drain_mp_rand;
    }

    /**
     * 傳回魔法施展幾率
     *
     */
    public int get_add_skill_rand() {
        return this._add_skill_rand;
    }

    /**
     * 設置魔法施展幾率
     *
     */
    public void set_add_skill_rand(int skill_rand) {
        this._add_skill_rand = skill_rand;
    }

    /**
     * 傳回施展魔法gfxid
     *
     */
    public int get_add_skill_gfxid() {
        return this._add_skill_gfxid;
    }

    /**
     * 設置施展魔法gfxid
     *
     */
    public void set_add_skill_gfxid(int skill_gfxid) {
        this._add_skill_gfxid = skill_gfxid;
    }

    /**
     * 傳回是否為遠程魔法
     *
     */
    public boolean Is_add_skill_arrow() {
        return this._add_skill_arrow;
    }

    /**
     * 設置是否為遠程魔法
     *
     */
    public void set_add_skill_arrow(boolean skill_arrow) {
        this._add_skill_arrow = skill_arrow;
    }

    /**
     * 傳回魔法傷害
     *
     */
    public int get_add_skill_dmg() {
        return this._add_skill_dmg;
    }

    /**
     * 設置魔法傷害
     *
     */
    public void set_add_skill_dmg(int skill_dmg) {
        this._add_skill_dmg = skill_dmg;
    }

    /**
     * 傳回施展魔法類型
     *
     */
    public int get_add_Special_magic() {
        return this._add_Special_magic;
    }

    /**
     * 設置施展魔法類型
     *
     */
    public void set_add_Special_magic(int Special_magic) {
        this._add_Special_magic = Special_magic;
    }

    /**
     * 傳回施展魔法類型對應成功幾率
     *
     */
    public int get_Special_magic_rand() {
        return this._Special_magic_rand;
    }

    /**
     * 設置施展魔法類型對應成功幾率
     *
     */
    public void set_Special_magic_rand(int Special_magic_rand) {
        this._Special_magic_rand = Special_magic_rand;
    }

    public int get魔法格檔() {
        return this._魔法格檔;
    }

    public void add魔法格檔(int i) {
        this._魔法格檔 = i;
    }

    public int getBlockWeapon() {
        return this._blockWeapon;
    }

    public void addBlockWeapon(int i) {
        this._blockWeapon = i;
    }

    public int getShanghaijianmian() {
        return this._shanghaijianmian;
    }

    public void addShanghaijianmian(int i) {
        this._shanghaijianmian = i;
    }
}