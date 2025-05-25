package com.lineage.server.templates;

/**
 * 套裝資料暫存
 *
 * @author daien
 */
public class L1ArmorSets {
    private int _id;// 套裝編號
    private String _sets;// 套裝組件編號
    private int _polyId;// 變身代號
    private int _polydesc; //變身名字編號
    private int _ac;// 防禦力增加
    private int _hp;// HP增加
    private int _mp;// MP增加
    private int _hpr;// HP回復增加
    private int _mpr;// MP回復增加
    private int _mr;// 抗魔增加
    private int _str;// 力量增加
    private int _dex;// 敏捷增加
    private int _con;// 體質增加
    private int _wis;// 精神增加
    private int _cha;// 魅力增加
    private int _intl;// 智力增加
    private int _defenseWater;// 水屬性增加
    private int _defenseWind;// 風屬性增加
    private int _defenseFire;// 火屬性增加
    private int _defenseEarth;// 地屬性增加
    private int _regist_stun;// 暈眩耐性增加
    private int _regist_stone;// 石化耐性增加
    private int _regist_sleep;// 睡眠耐性增加
    private int _regist_freeze;// 寒冰耐性增加
    private int _regist_sustain;// 支撐耐性增加
    private int _regist_blind;// 暗闇耐性增加
    private int[] _gfxs;// 套裝效果動畫
    private int _modifier_dmg;// 套裝增加物理傷害
    private int _reduction_dmg;// 套裝減免物理傷害
    private int _magic_modifier_dmg;// 套裝增加魔法傷害
    private int _magic_reduction_dmg;// 套裝減免魔法傷害
    private int _bow_modifier_dmg;// 套裝增加弓的物理傷害
    private int _haste;// 套裝增加加速效果
    private int _sp;// 套裝增加魔攻
    // 套裝增加近距離命中率
    private int _hit_modifier;
    // 套裝增加遠距離命中率
    private int _bow_hit_modifier;
    // 套裝增加魔法爆擊率
    private int _magiccritical_chance;
    private int _EffectId;
    private int _Interval;
    private String _quality1;  //src008
    private String _quality2;
    private String _quality3;
    private String _quality4;
    private String _quality5;
    private String _quality6;
    private String _quality7;
    private String _quality8;
    private String _quality9;

    public L1ArmorSets() {
    }

    /**
     * 套裝編號
     *
     */
    public int getId() {
        return this._id;
    }

    /**
     * 套裝編號
     *
     */
    public void setId(final int i) {
        this._id = i;
    }

    /**
     * 套裝組件編號
     *
     */
    public String getSets() {
        return this._sets;
    }

    /**
     * 套裝組件編號
     *
     */
    public void setSets(final String s) {
        this._sets = s;
    }

    /**
     * 變身代號
     *
     */
    public int getPolyId() {
        return this._polyId;
    }

    /**
     * 變身代號
     *
     */
    public void setPolyId(final int i) {
        this._polyId = i;
    }

    /**
     * 變身名字編號
     *
     */
    public int getPolyDesc() {
        return _polydesc;
    }

    /**
     * 變身名字編號
     *
     */
    public void setPolyDesc(int i) {
        this._polydesc = i;
    }

    /**
     * 防禦力增加
     *
     */
    public int getAc() {
        return this._ac;
    }

    /**
     * 防禦力增加
     *
     */
    public void setAc(final int i) {
        this._ac = i;
    }

    /**
     * HP增加
     *
     */
    public int getHp() {
        return this._hp;
    }

    /**
     * HP增加
     *
     */
    public void setHp(final int i) {
        this._hp = i;
    }

    /**
     * MP增加
     *
     */
    public int getMp() {
        return this._mp;
    }

    /**
     * MP增加
     *
     */
    public void setMp(final int i) {
        this._mp = i;
    }

    /**
     * HP回復增加
     *
     */
    public int getHpr() {
        return this._hpr;
    }

    /**
     * HP回復增加
     *
     */
    public void setHpr(final int i) {
        this._hpr = i;
    }

    /**
     * MP回復增加
     *
     */
    public int getMpr() {
        return this._mpr;
    }

    /**
     * MP回復增加
     *
     */
    public void setMpr(final int i) {
        this._mpr = i;
    }

    /**
     * 抗魔增加
     *
     */
    public int getMr() {
        return this._mr;
    }

    /**
     * 抗魔增加
     *
     */
    public void setMr(final int i) {
        this._mr = i;
    }

    /**
     * 力量增加
     *
     */
    public int getStr() {
        return this._str;
    }

    /**
     * 力量增加
     *
     */
    public void setStr(final int i) {
        this._str = i;
    }

    /**
     * 敏捷增加
     *
     */
    public int getDex() {
        return this._dex;
    }

    /**
     * 敏捷增加
     *
     */
    public void setDex(final int i) {
        this._dex = i;
    }

    /**
     * 體質增加
     *
     */
    public int getCon() {
        return this._con;
    }

    /**
     * 體質增加
     *
     */
    public void setCon(final int i) {
        this._con = i;
    }

    /**
     * 精神增加
     *
     */
    public int getWis() {
        return this._wis;
    }

    /**
     * 精神增加
     *
     */
    public void setWis(final int i) {
        this._wis = i;
    }

    /**
     * 魅力增加
     *
     */
    public int getCha() {
        return this._cha;
    }

    /**
     * 魅力增加
     *
     */
    public void setCha(final int i) {
        this._cha = i;
    }

    /**
     * 智力增加
     *
     */
    public int getIntl() {
        return this._intl;
    }

    /**
     * 智力增加
     *
     */
    public void setIntl(final int i) {
        this._intl = i;
    }

    /**
     * 水屬性增加
     *
     */
    public int getDefenseWater() {
        return this._defenseWater;
    }

    /**
     * 水屬性增加
     *
     */
    public void setDefenseWater(final int i) {
        this._defenseWater = i;
    }

    /**
     * 風屬性增加
     *
     */
    public int getDefenseWind() {
        return this._defenseWind;
    }

    /**
     * 風屬性增加
     *
     */
    public void setDefenseWind(final int i) {
        this._defenseWind = i;
    }

    /**
     * 火屬性增加
     *
     */
    public int getDefenseFire() {
        return this._defenseFire;
    }

    /**
     * 火屬性增加
     *
     */
    public void setDefenseFire(final int i) {
        this._defenseFire = i;
    }

    /**
     * 地屬性增加
     *
     */
    public int getDefenseEarth() {
        return this._defenseEarth;
    }

    /**
     * 地屬性增加
     *
     */
    public void setDefenseEarth(final int i) {
        this._defenseEarth = i;
    }

    /**
     * 暈眩耐性增加
     *
     */
    public int get_regist_stun() {
        return _regist_stun;
    }

    /**
     * 暈眩耐性增加
     *
     */
    public void set_regist_stun(final int i) {
        this._regist_stun = i;
    }

    /**
     * 石化耐性增加
     *
     */
    public int get_regist_stone() {
        return _regist_stone;
    }

    /**
     * 石化耐性增加
     *
     */
    public void set_regist_stone(final int i) {
        this._regist_stone = i;
    }

    /**
     * 睡眠耐性增加
     *
     */
    public int get_regist_sleep() {
        return _regist_sleep;
    }

    /**
     * 睡眠耐性增加
     *
     */
    public void set_regist_sleep(final int i) {
        this._regist_sleep = i;
    }

    /**
     * 寒冰耐性增加
     *
     */
    public int get_regist_freeze() {
        return _regist_freeze;
    }

    /**
     * 寒冰耐性增加
     *
     */
    public void set_regist_freeze(final int i) {
        this._regist_freeze = i;
    }

    /**
     * 支撐耐性增加
     *
     */
    public int get_regist_sustain() {
        return _regist_sustain;
    }

    /**
     * 支撐耐性增加
     *
     */
    public void set_regist_sustain(final int i) {
        this._regist_sustain = i;
    }

    /**
     * 暗闇耐性增加
     *
     */
    public int get_regist_blind() {
        return _regist_blind;
    }

    /**
     * 暗闇耐性增加
     *
     */
    public void set_regist_blind(final int i) {
        this._regist_blind = i;
    }

    /**
     * 套裝效果動畫
     *
     */
    public int[] get_gfxs() {
        return _gfxs;
    }

    /**
     * 套裝效果動畫
     *
     */
    public void set_gfxs(final int[] i) {
        this._gfxs = i;
    }

    /**
     * 套裝增加物理傷害
     *
     */
    public int get_modifier_dmg() {
        return _modifier_dmg;
    }

    /**
     * 套裝增加物理傷害
     *
     */
    public void set_modifier_dmg(int _modifier_dmg) {
        this._modifier_dmg = _modifier_dmg;
    }

    /**
     * 套裝減免物理傷害
     *
     */
    public int get_reduction_dmg() {
        return _reduction_dmg;
    }

    /**
     * 套裝減免物理傷害
     *
     */
    public void set_reduction_dmg(int _reduction_dmg) {
        this._reduction_dmg = _reduction_dmg;
    }

    /**
     * 套裝增加魔法傷害
     *
     */
    public int get_magic_modifier_dmg() {
        return _magic_modifier_dmg;
    }

    /**
     * 套裝增加魔法傷害
     *
     */
    public void set_magic_modifier_dmg(int _magic_modifier_dmg) {
        this._magic_modifier_dmg = _magic_modifier_dmg;
    }

    /**
     * 套裝減免魔法傷害
     *
     */
    public int get_magic_reduction_dmg() {
        return _magic_reduction_dmg;
    }

    /**
     * 套裝減免魔法傷害
     *
     */
    public void set_magic_reduction_dmg(int _magic_reduction_dmg) {
        this._magic_reduction_dmg = _magic_reduction_dmg;
    }

    /**
     * 套裝增加弓的物理傷害
     *
     */
    public int get_bow_modifier_dmg() {
        return _bow_modifier_dmg;
    }

    /**
     * 套裝增加弓的物理傷害
     *
     */
    public void set_bow_modifier_dmg(int _bow_modifier_dmg) {
        this._bow_modifier_dmg = _bow_modifier_dmg;
    }

    /**
     * 套裝增加加速效果
     *
     */
    public int get_haste() {
        return _haste;
    }

    /**
     * 套裝增加加速效果
     *
     */
    public void set_haste(int haste) {
        this._haste = haste;
    }

    /**
     * 套裝增加魔攻
     *
     */
    public int get_sp() {
        return _sp;
    }

    /**
     * 套裝增加魔攻
     *
     */
    public void set_sp(int sp) {
        this._sp = sp;
    }

    public int get_hit_modifier() {
        return _hit_modifier;
    }

    public void set_hit_modifier(int hit) {
        this._hit_modifier = hit;
    }

    public int get_bow_hit_modifier() {
        return _bow_hit_modifier;
    }

    public void set_bow_hit_modifier(int hit) {
        this._bow_hit_modifier = hit;
    }

    public int get_magiccritical_chance() {
        return _magiccritical_chance;
    }

    public void set_magiccritical_chance(int chance) {
        this._magiccritical_chance = chance;
    }

    public int getEffectId() {
        return _EffectId;
    }

    public void setEffectId(int i) {
        this._EffectId = i;
    }

    public int getInterval() {
        return _Interval;
    }

    public void setInterval(int i) {
        this._Interval = i;
    }

    public String getQuality1() {
        return this._quality1;
    }

    public void setQuality1(String quality) {
        this._quality1 = quality;
    }

    public String getQuality2() {
        return this._quality2;
    }

    public void setQuality2(String quality) {
        this._quality2 = quality;
    }

    public String getQuality3() {
        return this._quality3;
    }

    public void setQuality3(String quality) {
        this._quality3 = quality;
    }

    public String getQuality4() {
        return this._quality4;
    }

    public void setQuality4(String quality) {
        this._quality4 = quality;
    }

    public String getQuality5() {
        return this._quality5;
    }

    public void setQuality5(String quality) {
        this._quality5 = quality;
    }

    public String getQuality6() {
        return this._quality6;
    }

    public void setQuality6(String quality) {
        this._quality6 = quality;
    }

    public String getQuality7() {
        return this._quality7;
    }

    public void setQuality7(String quality) {
        this._quality7 = quality;
    }

    public String getQuality8() {
        return this._quality8;
    }

    public void setQuality8(String quality) {
        this._quality8 = quality;
    }

    public String getQuality9() {
        return this._quality9;
    }

    public void setQuality9(String quality) {
        this._quality9 = quality;
    }
}
