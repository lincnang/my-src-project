package com.lineage.server.templates;

public class L1Armor extends L1Item {
    private static final long serialVersionUID = 1L;
    private int _ac = 0;
    private int _damageReduction = 0;
    private int _weightReduction = 0;
    private int _hitModifierByArmor = 0;
    private int _dmgModifierByArmor = 0;
    private int _bowHitModifierByArmor = 0;
    private int _bowDmgModifierByArmor = 0;
    private int _magicHitModifierByArmor = 0;
    private int _defense_water = 0;
    private int _defense_wind = 0;
    private int _defense_fire = 0;
    private int _defense_earth = 0;
    private int _regist_stun = 0;
    private int _regist_stone = 0;
    private int _regist_sleep = 0;
    private int _regist_freeze = 0;
    private int _regist_sustain = 0;
    private int _regist_blind = 0;
    private int _greater = 3;
    //private static int _stunPVP2;
    private int _dice_dmg = 0;// 機率給予爆擊
    private int _dmg = 0;// 機率給予爆擊質
    // 增加藥水回復量%
    private int _up_hp_potion = 0;
    // 增加藥水回復指定量
    private int _uhp_number = 0;
    // 是否為活動戒指或收費戒指 by terry0412  //src013
    private boolean _activity;
    private int _kitType;
    private boolean _superRune;

    public int get_ac() {
        return _ac;
    }

    public void set_ac(int i) {
        _ac = i;
    }

    public int getDamageReduction() {
        return _damageReduction;
    }

    public void setDamageReduction(int i) {
        _damageReduction = i;
    }

    public int getWeightReduction() {
        return _weightReduction;
    }

    public void setWeightReduction(int i) {
        _weightReduction = i;
    }

    public int getHitModifierByArmor() {
        return _hitModifierByArmor;
    }

    public void setHitModifierByArmor(int i) {
        _hitModifierByArmor = i;
    }

    public int getDmgModifierByArmor() {
        return _dmgModifierByArmor;
    }

    public void setDmgModifierByArmor(int i) {
        _dmgModifierByArmor = i;
    }

    public int getBowHitModifierByArmor() {
        return _bowHitModifierByArmor;
    }

    public void setBowHitModifierByArmor(int i) {
        _bowHitModifierByArmor = i;
    }

    public int getBowDmgModifierByArmor() {
        return _bowDmgModifierByArmor;
    }

    public void setBowDmgModifierByArmor(int i) {
        _bowDmgModifierByArmor = i;
    }

    public int get_defense_water() {
        return _defense_water;
    }

    public void set_defense_water(int i) {
        _defense_water = i;
    }

    public int get_defense_wind() {
        return _defense_wind;
    }

    public void set_defense_wind(int i) {
        _defense_wind = i;
    }

    public int get_defense_fire() {
        return _defense_fire;
    }

    public void set_defense_fire(int i) {
        _defense_fire = i;
    }

    public int get_defense_earth() {
        return _defense_earth;
    }

    public void set_defense_earth(int i) {
        _defense_earth = i;
    }

    public int get_regist_stun() {
        return _regist_stun;
    }

    public void set_regist_stun(int i) {
        _regist_stun = i;
    }

    public int get_regist_stone() {
        return _regist_stone;
    }

    public void set_regist_stone(int i) {
        _regist_stone = i;
    }

    public int get_regist_sleep() {
        return _regist_sleep;
    }

    public void set_regist_sleep(int i) {
        _regist_sleep = i;
    }

    public int get_regist_freeze() {
        return _regist_freeze;
    }

    public void set_regist_freeze(int i) {
        _regist_freeze = i;
    }

    public int get_regist_sustain() {
        return _regist_sustain;
    }

    public void set_regist_sustain(int i) {
        _regist_sustain = i;
    }

    public int get_regist_blind() {
        return _regist_blind;
    }

    public void set_regist_blind(int i) {
        _regist_blind = i;
    }

    public int get_greater() {
        return _greater;
    }

    public void set_greater(int greater) {
        _greater = greater;
    }

    public int getMagicHitModifierByArmor() {
        return _magicHitModifierByArmor;
    }

    public void setMagicHitModifierByArmor(int i) {
        _magicHitModifierByArmor = i;
    }

    /**
     * 機率給予爆擊機率
     */
    public int get_dice_dmg() {
        return _dice_dmg;
    }

    public void setDice_dmg(int dice_dmg) {
        this._dice_dmg = dice_dmg;
    }

    /**
     * 機率給予爆擊質數
     */
    public int getDmg() {
        return _dmg;
    }

    public void setDmg(int dmg) {
        this._dmg = dmg;
    }


    public int get_up_hp_potion() {
        return _up_hp_potion;
    }

    public void set_up_hp_potion(int uhp) {
        _up_hp_potion = uhp;
    }

    public int get_uhp_number() {
        return _uhp_number;
    }

    public void set_uhp_number(int uhp) {
        _uhp_number = uhp;
    }

    @Override
    public boolean isActivity() {
        return this._activity;
    }

    public void setActivity(final boolean i) {
        this._activity = i;
    }

    @Override
    public int get_kitType() {
        return this._kitType;
    }

    public void set_kitType(final int kitType) {
        this._kitType = kitType;
    }

    @Override
    public boolean isSuperRune() {
        return this._superRune;
    }

    public void setSuperRune(final boolean i) {
        this._superRune = i;
    }
    //public static int get_stunPVP2() {
    //return _stunPVP2;
    //}
    //public void set_stunPVP2(int stunPVP2) {
    //_stunPVP2 = stunPVP2;
    //}
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Armor JD-Core Version: 0.6.2
 */