package com.lineage.server.templates;

/**
 * 物品凹槽強化暫存
 * 強化擴充能力
 */
public class L1CharItemPower {
    private int _id;// 強化物品OBJID
    // 近戰攻擊
    private int _updateDmgModifier;
    // 近戰命中
    private int _updateHitModifier;
    // 遠攻攻擊
    private int _updateBowDmgModifier;
    // 遠攻命中
    private int _updateBowHitModifier;
    // 力量
    private int _updateStr;
    // 敏捷
    private int _updateDex;
    // 體質
    private int _updateCon;
    // 精神
    private int _updateWis;
    // 智力
    private int _updateInt;
    // 魅力
    private int _updateCha;
    // 血量
    private int _updateHp;
    // 魔量
    private int _updateMp;
    // 地屬性
    private int _updateEarth;
    // 風屬性
    private int _updateWind;
    // 水屬性
    private int _updateWater;
    // 火屬性
    private int _updateFire;
    // 抗魔
    private int _updateMr;
    // 防禦
    private int _updateAc;
    // 回血
    private int _updateHpr;
    // 回魔
    private int _updateMpr;
    // 魔法攻擊
    private int _updateSp;
    // 增加PVP傷害
    private int _PvpDmg;
    // 减免PVP傷害
    private int _PvpDmg_R;
    // 武器劍靈系統
    private int _Weapon_Soul;

    public L1CharItemPower() {
    }

    /**
     * 傳回強化物品OBJID
     *
     * @return
     */
    public int getId() {
        return _id;
    }

    /**
     * 設置強化物品的OBJID
     *
     * @param i
     */
    public void setId(int i) {
        _id = i;
    }

    public int getUpdateDmgModifier() {
        return _updateDmgModifier;
    }

    public void setUpdateDmgModifier(int i) {
        _updateDmgModifier = i;
    }

    public int getUpdateHitModifier() {
        return _updateHitModifier;
    }

    public void setUpdateHitModifier(int i) {
        _updateHitModifier = i;
    }

    public int getUpdateBowDmgModifier() {
        return _updateBowDmgModifier;
    }

    public void setUpdateBowDmgModifier(int i) {
        _updateBowDmgModifier = i;
    }

    public int getUpdateBowHitModifier() {
        return _updateBowHitModifier;
    }

    public void setUpdateBowHitModifier(int i) {
        _updateBowHitModifier = i;
    }

    public int getUpdateStr() {
        return _updateStr;
    }

    public void setUpdateStr(int i) {
        _updateStr = i;
    }

    public int getUpdateDex() {
        return _updateDex;
    }

    public void setUpdateDex(int i) {
        _updateDex = i;
    }

    public int getUpdateCon() {
        return _updateCon;
    }

    public void setUpdateCon(int i) {
        _updateCon = i;
    }

    public int getUpdateWis() {
        return _updateWis;
    }

    public void setUpdateWis(int i) {
        _updateWis = i;
    }

    public int getUpdateInt() {
        return _updateInt;
    }

    public void setUpdateInt(int i) {
        _updateInt = i;
    }

    public int getUpdateCha() {
        return _updateCha;
    }

    public void setUpdateCha(int i) {
        _updateCha = i;
    }

    public int getUpdateHp() {
        return _updateHp;
    }

    public void setUpdateHp(int i) {
        _updateHp = i;
    }

    public int getUpdateMp() {
        return _updateMp;
    }

    public void setUpdateMp(int i) {
        _updateMp = i;
    }

    public int getUpdateEarth() {
        return _updateEarth;
    }

    public void setUpdateEarth(int i) {
        _updateEarth = i;
    }

    public int getUpdateWind() {
        return _updateWind;
    }

    public void setUpdateWind(int i) {
        _updateWind = i;
    }

    public int getUpdateWater() {
        return _updateWater;
    }

    public void setUpdateWater(int i) {
        _updateWater = i;
    }

    public int getUpdateFire() {
        return _updateFire;
    }

    public void setUpdateFire(int i) {
        _updateFire = i;
    }

    public int getUpdateMr() {
        return _updateMr;
    }

    public void setUpdateMr(int i) {
        _updateMr = i;
    }

    public int getUpdateAc() {
        return _updateAc;
    }

    public void setUpdateAc(int i) {
        _updateAc = i;
    }

    public int getUpdateHpr() {
        return _updateHpr;
    }

    public void setUpdateHpr(int i) {
        _updateHpr = i;
    }

    public int getUpdateMpr() {
        return _updateMpr;
    }

    public void setUpdateMpr(int i) {
        _updateMpr = i;
    }

    public int getUpdateSp() {
        return _updateSp;
    }

    public void setUpdateSp(int i) {
        _updateSp = i;
    }

    public int getUpdatePvpDmg() {
        return _PvpDmg;
    }

    public void setUpdatePvpDmg(final int i) {
        _PvpDmg = i;
    }

    public int getUpdatePvpDmg_R() {
        return _PvpDmg_R;
    }

    public void setUpdatePvpDmg_R(final int i) {
        _PvpDmg_R = i;
    }

    /**
     * 武器劍靈值
     *
     * @return
     */
    public int getUpdateWeaponSoul() {
        return _Weapon_Soul;
    }

    /**
     * 武器劍靈值
     *
     * @param i
     */
    public void setUpdateWeaponSoul(final int i) {
        _Weapon_Soul = i;
    }
}
