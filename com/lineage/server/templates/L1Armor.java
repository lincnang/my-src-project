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

    private int _influence_safe = -1;
    private int _influence_str = 0;
    private int _influence_dex = 0;
    private int _influence_con = 0;
    private int _influence_int = 0;
    private int _influence_wis = 0;
    private int _influence_cha = 0;
    private int _influence_sp = 0;
    private int _influence_mr = 0;
    private int _influence_hp = 0;
    private int _influence_mp = 0;
    private int _influence_dmgR = 0;
    private int _influence_hitAndDmg = 0;
    private int _influence_bowHitAndDmg = 0;

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

    public int get_influence_safe() { return _influence_safe; }
    public void set_influence_safe(int i) { _influence_safe = i; }

    public int get_influence_str() { return _influence_str; }
    public void set_influence_str(int i) { _influence_str = i; }

    public int get_influence_dex() { return _influence_dex; }
    public void set_influence_dex(int i) { _influence_dex = i; }

    public int get_influence_con() { return _influence_con; }
    public void set_influence_con(int i) { _influence_con = i; }

    public int get_influence_int() { return _influence_int; }
    public void set_influence_int(int i) { _influence_int = i; }

    public int get_influence_wis() { return _influence_wis; }
    public void set_influence_wis(int i) { _influence_wis = i; }

    public int get_influence_cha() { return _influence_cha; }
    public void set_influence_cha(int i) { _influence_cha = i; }

    public int get_influence_sp() { return _influence_sp; }
    public void set_influence_sp(int i) { _influence_sp = i; }

    public int get_influence_mr() { return _influence_mr; }
    public void set_influence_mr(int i) { _influence_mr = i; }

    public int get_influence_hp() { return _influence_hp; }
    public void set_influence_hp(int i) { _influence_hp = i; }

    public int get_influence_mp() { return _influence_mp; }
    public void set_influence_mp(int i) { _influence_mp = i; }

    public int get_influence_dmgR() { return _influence_dmgR; }
    public void set_influence_dmgR(int i) { _influence_dmgR = i; }

    public int get_influence_hitAndDmg() { return _influence_hitAndDmg; }
    public void set_influence_hitAndDmg(int i) { _influence_hitAndDmg = i; }

    public int get_influence_bowHitAndDmg() { return _influence_bowHitAndDmg; }
    public void set_influence_bowHitAndDmg(int i) { _influence_bowHitAndDmg = i; }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Armor JD-Core Version: 0.6.2
 */