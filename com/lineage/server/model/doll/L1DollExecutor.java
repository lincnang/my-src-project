package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 娃娃各項能力設置抽像接口
 *
 * @author daien
 */
public abstract class L1DollExecutor {
    // 新增娃娃能力描述↓↓↓
    // 新增娃娃能力描述↓↓↓
    // 新增娃娃能力描述↓↓↓
    private int _DollAc = 0; // 娃娃能力描述 - 防禦
    private int _DollStr = 0; // 娃娃能力描述 - 力量
    private int _DollDex = 0; // 娃娃能力描述 - 敏捷
    private int _DollCon = 0; // 娃娃能力描述 - 體質
    private int _DollWis = 0; // 娃娃能力描述 - 精神
    private int _DollInt = 0; // 娃娃能力描述 - 智力
    private int _DollCha = 0; // 娃娃能力描述 - 魅力
    private int _DollHp = 0; // 娃娃能力描述 - 血量
    private int _DollMp = 0; // 娃娃能力描述 - 魔量
    private int _DollHpr = 0; // 娃娃能力描述 - 回血
    private int _DollMpr = 0; // 娃娃能力描述 - 回魔
    private int _DollMr = 0; // 娃娃能力描述 - 魔防
    private int _DollSp = 0; // 娃娃能力描述 - 魔攻
    private int _DollDmg = 0; // 娃娃能力描述 - 近戰攻擊
    private int _DollHit = 0; // 娃娃能力描述 - 近戰命中
    private int _DollBowDmg = 0; // 娃娃能力描述 - 遠攻攻擊
    private int _DollBowHit = 0; // 娃娃能力描述 - 遠攻命中
    private int _DollAllDmg_R = 0; // 娃娃能力描述 - 傷害減免
    private int _DollExp = 0; // 娃娃能力描述 - 狩獵經驗值
    private int _DollWeight = 0; // 娃娃能力描述 - 負重能力+%
    private int _DollWeight_R = 0; // 娃娃能力描述 - 增加負重 +X
    private int _DollEarth = 0; // 娃娃能力描述 - 地屬性
    private int _DollWind = 0; // 娃娃能力描述 - 風屬性
    private int _DollWater = 0; // 娃娃能力描述 - 水屬性
    private int _DollFire = 0; // 娃娃能力描述 - 火屬性
    private int _DollStun = 0; // 娃娃能力描述 - 昏迷耐性
    private int _DollStone = 0; // 娃娃能力描述 - 石化耐性
    private int _DollSleep = 0; // 娃娃能力描述 - 睡眠耐性
    private int _DollFreeze = 0; // 娃娃能力描述 - 寒冰耐性
    private int _DollSustain = 0; // 娃娃能力描述 - 支撐耐性
    private int _DollBlind = 0; // 娃娃能力描述 - 暗黑耐性
    private int _DollHaste = 0;// 娃娃能力描述 - 加速效果
    private int _DollStunLv = 0;// 娃娃能力描述 - 昏迷等級
    private int _DollBreakLv = 0;// 娃娃能力描述 - 破壞盔甲命中
    private int _DollFoeSlayer = 0;// 娃娃能力描述 - 屠宰者階段別傷害
    private int _DollTiTanHp = 0;// 娃娃能力描述 - 泰坦系列技能發動 HP 區間增加

    /**
     * 設置能力設定值
     *
     * @param int1
     * @param int2
     * @param int3
     */
    public abstract void set_power(int int1, int int2, int int3);

    /**
     * 取回娃娃能力描述
     */
    public abstract String get_note();

    /**
     * 設置娃娃能力描述
     *
     * @param note
     */
    public abstract void set_note(String note);

    /**
     * 裝備娃娃效果
     *
     * @param pc
     * @return
     */
    public abstract void setDoll(L1PcInstance pc);

    /**
     * 解除娃娃效果
     *
     * @param pc
     * @return
     */
    public abstract void removeDoll(L1PcInstance pc);

    /**
     * 是否具有輔助技能
     *
     * @return
     */
    public abstract boolean is_reset();

    /**
     * 娃娃能力描述 - 防禦
     */
    public int getDollAc() {
        return _DollAc;
    }

    /**
     * 娃娃能力描述 - 防禦
     */
    public void setDollAc(final int i) {
        _DollAc = i;
    }

    /**
     * 娃娃能力描述 - 力量
     */
    public int getDollStr() {
        return _DollStr;
    }

    /**
     * 娃娃能力描述 - 力量
     */
    public void setDollStr(final int i) {
        _DollStr = i;
    }

    /**
     * 娃娃能力描述 - 敏捷
     */
    public int getDollDex() {
        return _DollDex;
    }

    /**
     * 娃娃能力描述 - 敏捷
     */
    public void setDollDex(final int i) {
        _DollDex = i;
    }

    /**
     * 娃娃能力描述 - 體質
     */
    public int getDollCon() {
        return _DollCon;
    }

    /**
     * 娃娃能力描述 - 體質
     */
    public void setDollCon(final int i) {
        _DollCon = i;
    }

    /**
     * 娃娃能力描述 - 精神
     */
    public int getDollWis() {
        return _DollWis;
    }

    /**
     * 娃娃能力描述 - 精神
     */
    public void setDollWis(final int i) {
        _DollWis = i;
    }

    /**
     * 娃娃能力描述 - 智力
     */
    public int getDollInt() {
        return _DollInt;
    }

    /**
     * 娃娃能力描述 - 智力
     */
    public void setDollInt(final int i) {
        _DollInt = i;
    }

    /**
     * 娃娃能力描述 - 魅力
     */
    public int getDollCha() {
        return _DollCha;
    }

    /**
     * 娃娃能力描述 - 魅力
     */
    public void setDollCha(final int i) {
        _DollCha = i;
    }

    /**
     * 娃娃能力描述 - 血量
     */
    public int getDollHp() {
        return _DollHp;
    }

    /**
     * 娃娃能力描述 - 血量
     */
    public void setDollHp(final int i) {
        _DollHp = i;
    }

    /**
     * 娃娃能力描述 - 魔量
     */
    public int getDollMp() {
        return _DollMp;
    }

    /**
     * 娃娃能力描述 - 魔量
     */
    public void setDollMp(final int i) {
        _DollMp = i;
    }

    /**
     * 娃娃能力描述 - 回血
     */
    public int getDollHpr() {
        return _DollHpr;
    }

    /**
     * 娃娃能力描述 - 回血
     */
    public void setDollHpr(final int i) {
        _DollHpr = i;
    }

    /**
     * 娃娃能力描述 - 回魔
     */
    public int getDollMpr() {
        return _DollMpr;
    }

    /**
     * 娃娃能力描述 - 回魔
     */
    public void setDollMpr(final int i) {
        _DollMpr = i;
    }

    /**
     * 娃娃能力描述 - 抗魔
     */
    public int getDollMr() {
        return _DollMr;
    }

    /**
     * 娃娃能力描述 - 抗魔
     */
    public void setDollMr(final int i) {
        _DollMr = i;
    }

    /**
     * 娃娃能力描述 - 魔法攻擊
     */
    public int getDollSp() {
        return _DollSp;
    }

    /**
     * 娃娃能力描述 - 魔法攻擊
     */
    public void setDollSp(final int i) {
        _DollSp = i;
    }

    /**
     * 娃娃能力描述 - 近戰攻擊
     */
    public int getDollDmg() {
        return _DollDmg;
    }

    /**
     * 娃娃能力描述 - 近戰攻擊
     */
    public void setDollDmg(final int i) {
        _DollDmg = i;
    }

    /**
     * 娃娃能力描述 - 近戰命中
     */
    public int getDollHit() {
        return _DollHit;
    }

    /**
     * 娃娃能力描述 - 近戰命中
     */
    public void setDollHit(final int i) {
        _DollHit = i;
    }

    /**
     * 娃娃能力描述 - 遠攻攻擊
     */
    public int getDollBowDmg() {
        return _DollBowDmg;
    }

    /**
     * 娃娃能力描述 - 遠攻攻擊
     */
    public void setDollBowDmg(final int i) {
        _DollBowDmg = i;
    }

    /**
     * 娃娃能力描述 - 遠攻命中
     */
    public int getDollBowHit() {
        return _DollBowHit;
    }

    /**
     * 娃娃能力描述 - 遠攻命中
     */
    public void setDollBowHit(final int i) {
        _DollBowHit = i;
    }

    /**
     * 娃娃能力描述 - 傷害減免
     */
    public int getDollAllDmg_R() {
        return _DollAllDmg_R;
    }

    /**
     * 娃娃能力描述 - 傷害減免
     */
    public void setDollAllDmg_R(final int i) {
        _DollAllDmg_R = i;
    }

    /**
     * 娃娃能力描述 - 狩獵經驗值
     */
    public int getDollExp() {
        return _DollExp;
    }

    /**
     * 娃娃能力描述 - 狩獵經驗值
     */
    public void setDollExp(final int i) {
        _DollExp = i;
    }

    /**
     * 娃娃能力描述 - 負重能力+%
     */
    public int getDollWeight() {
        return _DollWeight;
    }

    /**
     * 娃娃能力描述 - 負重能力+%
     */
    public void setDollWeight(final int i) {
        _DollWeight = i;
    }

    /**
     * 娃娃能力描述 - 增加負重 +X
     */
    public int getDollWeight_R() {
        return _DollWeight_R;
    }

    /**
     * 娃娃能力描述 - 增加負重 +X
     */
    public void setDollWeight_R(final int i) {
        _DollWeight_R = i;
    }

    /**
     * 娃娃能力描述 - 地屬性
     */
    public int getDollEarth() {
        return _DollEarth;
    }

    /**
     * 娃娃能力描述 - 地屬性
     */
    public void setDollEarth(int i) {
        _DollEarth = i;
    }

    /**
     * 娃娃能力描述 - 風屬性
     */
    public int getDollWind() {
        return _DollWind;
    }

    /**
     * 娃娃能力描述 - 風屬性
     */
    public void setDollWind(int i) {
        _DollWind = i;
    }

    /**
     * 娃娃能力描述 - 水屬性
     */
    public int getDollWater() {
        return _DollWater;
    }

    /**
     * 娃娃能力描述 - 水屬性
     */
    public void setDollWater(int i) {
        _DollWater = i;
    }

    /**
     * 娃娃能力描述 - 火屬性
     */
    public int getDollFire() {
        return _DollFire;
    }

    /**
     * 娃娃能力描述 - 火屬性
     */
    public void setDollFire(int i) {
        _DollFire = i;
    }

    /**
     * 娃娃能力描述 - 昏迷耐性
     */
    public int getDollStun() {
        return _DollStun;
    }

    /**
     * 娃娃能力描述 - 昏迷耐性
     */
    public void setDollStun(final int i) {
        _DollStun = i;
    }

    /**
     * 娃娃能力描述 - 石化耐性
     */
    public int getDollStone() {
        return _DollStone;
    }

    /**
     * 娃娃能力描述 - 石化耐性
     */
    public void setDollStone(final int i) {
        _DollStone = i;
    }

    /**
     * 娃娃能力描述 - 睡眠耐性
     */
    public int getDollSleep() {
        return _DollSleep;
    }

    /**
     * 娃娃能力描述 - 睡眠耐性
     */
    public void setDollSleep(final int i) {
        _DollSleep = i;
    }

    /**
     * 娃娃能力描述 - 寒冰耐性
     */
    public int getDollFreeze() {
        return _DollFreeze;
    }

    /**
     * 娃娃能力描述 - 寒冰耐性
     */
    public void setDollFreeze(final int i) {
        _DollFreeze = i;
    }

    /**
     * 娃娃能力描述 - 支撐耐性
     */
    public int getDollSustain() {
        return _DollSustain;
    }

    /**
     * 娃娃能力描述 - 支撐耐性
     */
    public void setDollSustain(final int i) {
        _DollSustain = i;
    }

    /**
     * 娃娃能力描述 - 暗黑耐性
     */
    public int getDollBlind() {
        return _DollBlind;
    }

    /**
     * 娃娃能力描述 - 暗黑耐性
     */
    public void setDollBlind(final int i) {
        _DollBlind = i;
    }

    /**
     * 娃娃能力描述 - 加速效果
     */
    public int getDollHaste() {
        return _DollHaste;
    }

    /**
     * 娃娃能力描述 - 加速效果
     */
    public void setDollHaste(final int i) {
        _DollHaste = i;
    }

    /**
     * 娃娃能力描述 - 昏迷等級
     */
    public int getDollStunLv() {
        return _DollStunLv;
    }

    /**
     * 娃娃能力描述 - 昏迷等級
     */
    public void setDollStunLv(final int i) {
        _DollStunLv = i;
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

    /**
     * 娃娃能力描述 - 屠宰者階段別傷害
     */
    public int getDollFoeSlayer() {
        return _DollFoeSlayer;
    }

    /**
     * 娃娃能力描述 - 屠宰者階段別傷害
     */
    public void setDollFoeSlayer(final int i) {
        _DollFoeSlayer = i;
    }

    /**
     * 娃娃能力描述 - 泰坦系列技能發動 HP 區間增加
     */
    public int getDollTiTanHp() {
        return _DollTiTanHp;
    }

    /**
     * 娃娃能力描述 - 泰坦系列技能發動 HP 區間增加
     */
    public void setDollTiTanHp(final int i) {
        _DollTiTanHp = i;
    }
    // 新增娃娃能力描述↑↑↑
    // 新增娃娃能力描述↑↑↑
    // 新增娃娃能力描述↑↑↑
}