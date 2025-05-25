package com.lineage.server.templates;

/**
 * 道具VIP系統
 */
public class L1ItemVIP {
    private int _type;
    private int _gif;
    private int _gif_time;
    private int _add_wmd;
    private int _add_wmc;
    private int _add_str;
    private int _add_dex;
    private int _add_con;
    private int _add_int;
    private int _add_wis;
    private int _add_cha;
    private int _add_ac;
    private int _add_hp;
    private int _add_mp;
    private int _add_hpr;
    private int _add_mpr;
    private int _add_dmg;
    private int _add_hit;
    private int _add_bow_dmg;
    private int _add_bow_hit;
    private int _add_dmg_r;
    private int _add_magic_r;
    private int _add_mr;
    private int _add_sp;
    private int _add_fire;
    private int _add_wind;
    private int _add_earth;
    private int _add_water;
    private int _add_stun;
    private int _add_stone;
    private int _add_sleep;
    private int _add_freeze;
    private int _add_sustain;
    private int _add_blind;
    private int _add_exp;
    private boolean _death_exp;
    private boolean _death_item;
    private boolean _death_skill;
    private boolean _death_score;
    private int _add_adena;
    private int _add_mf;
    private int _skin_id;
    private int _add_potion; //藥水回復
    private int _add_PVP; //PVP攻擊
    private int _add_PVP_R; //PVP減免
    private int _add_magic_hit; //魔法命中
    private int _add_StunLv; //昏迷命中
    private int _add_CloseCritical; //近距離爆擊
    private int _add_BowCritical; //遠距離爆擊
    private int _adddice_dmg = 0;// 機率給予爆擊率
    private int _add_dice_dmgharm = 0; //機率給予傷害值
    private int _DamageReductionPVE = 0; // PvE 傷害增加
    private int _DamageReductionPVE_R = 0; // PvE 傷害減免

    private int _add_dice_hp = 0; //吸血
    private int _add_sucking_hp = 0;  //吸血機率
    private int _add_dice_mp = 0; //吸魔
    private int _add_sucking_mp = 0; //吸魔


    public L1ItemVIP() {
        _type = 0;
        _gif = 0;
        _gif_time = 0;
        _add_wmd = 0;
        _add_wmc = 0;
        _add_str = 0;
        _add_dex = 0;
        _add_con = 0;
        _add_int = 0;
        _add_wis = 0;
        _add_cha = 0;
        _add_ac = 0;
        _add_hp = 0;
        _add_mp = 0;
        _add_hpr = 0;
        _add_mpr = 0;
        _add_dmg = 0;
        _add_hit = 0;
        _add_bow_dmg = 0;
        _add_bow_hit = 0;
        _add_dmg_r = 0;
        _add_magic_r = 0;
        _add_mr = 0;
        _add_sp = 0;
        _add_fire = 0;
        _add_wind = 0;
        _add_earth = 0;
        _add_water = 0;
        _add_stun = 0;
        _add_stone = 0;
        _add_sleep = 0;
        _add_freeze = 0;
        _add_sustain = 0;
        _add_blind = 0;
        _add_exp = 0;
        _death_exp = false;
        _death_item = false;
        _death_skill = false;
        _death_score = false;
        _add_adena = 0;
        _add_mf = 0;
        _skin_id = 0;
        _add_potion = 0;
        _add_PVP = 0;
        _add_PVP_R = 0;
        _add_magic_hit = 0;
        _add_StunLv = 0;
        _add_CloseCritical = 0;
        _add_BowCritical = 0;
        _adddice_dmg = 0;
        _add_dice_dmgharm = 0;
        _DamageReductionPVE = 0; //  PvE 怪物增傷
        _DamageReductionPVE_R = 0;
        _add_dice_hp = 0;
        _add_sucking_hp = 0;
        _add_dice_mp = 0;
        _add_sucking_mp = 0;

    }

    public int get_type() {
        return _type;
    }

    public void set_type(int i) {
        _type = i;
    }

    public int get_gif() {
        return _gif;
    }

    public void set_gif(int gif) {
        _gif = gif;
    }

    public int get_gif_time() {
        return _gif_time;
    }

    public void set_gif_time(int gif_time) {
        _gif_time = gif_time;
    }

    public int get_add_wmd() {
        return _add_wmd;
    }

    public void set_add_wmd(int add_wmd) {
        _add_wmd = add_wmd;
    }

    public int get_add_wmc() {
        return _add_wmc;
    }

    public void set_add_wmc(int add_wmc) {
        _add_wmc = add_wmc;
    }

    public int get_add_str() {
        return _add_str;
    }

    public void set_add_str(int add_str) {
        _add_str = add_str;
    }

    public int get_add_dex() {
        return _add_dex;
    }

    public void set_add_dex(int add_dex) {
        _add_dex = add_dex;
    }

    public int get_add_con() {
        return _add_con;
    }

    public void set_add_con(int add_con) {
        _add_con = add_con;
    }

    public int get_add_int() {
        return _add_int;
    }

    public void set_add_int(int add_int) {
        _add_int = add_int;
    }

    public int get_add_wis() {
        return _add_wis;
    }

    public void set_add_wis(int add_wis) {
        _add_wis = add_wis;
    }

    public int get_add_cha() {
        return _add_cha;
    }

    public void set_add_cha(int add_cha) {
        _add_cha = add_cha;
    }

    public int get_add_ac() {
        return _add_ac;
    }

    public void set_add_ac(int add_ac) {
        _add_ac = add_ac;
    }

    public int get_add_hp() {
        return _add_hp;
    }

    public void set_add_hp(int add_hp) {
        _add_hp = add_hp;
    }

    public int get_add_mp() {
        return _add_mp;
    }

    public void set_add_mp(int add_mp) {
        _add_mp = add_mp;
    }

    public int get_add_hpr() {
        return _add_hpr;
    }

    public void set_add_hpr(int add_hpr) {
        _add_hpr = add_hpr;
    }

    public int get_add_mpr() {
        return _add_mpr;
    }

    public void set_add_mpr(int add_mpr) {
        _add_mpr = add_mpr;
    }

    public int get_add_dmg() {
        return _add_dmg;
    }

    public void set_add_dmg(int add_dmg) {
        _add_dmg = add_dmg;
    }

    public int get_add_hit() {
        return _add_hit;
    }

    public void set_add_hit(int add_hit) {
        _add_hit = add_hit;
    }

    public int get_add_bow_dmg() {
        return _add_bow_dmg;
    }

    public void set_add_bow_dmg(int add_bow_dmg) {
        _add_bow_dmg = add_bow_dmg;
    }

    public int get_add_bow_hit() {
        return _add_bow_hit;
    }

    public void set_add_bow_hit(int add_bow_hit) {
        _add_bow_hit = add_bow_hit;
    }

    public int get_add_dmg_r() {
        return _add_dmg_r;
    }

    public void set_add_dmg_r(int add_dmg_r) {
        _add_dmg_r = add_dmg_r;
    }

    public int get_add_magic_r() {
        return _add_magic_r;
    }

    public void set_add_magic_r(int add_magic_r) {
        _add_magic_r = add_magic_r;
    }

    public int get_add_mr() {
        return _add_mr;
    }

    public void set_add_mr(int add_mr) {
        _add_mr = add_mr;
    }

    public int get_add_sp() {
        return _add_sp;
    }

    public void set_add_sp(int add_sp) {
        _add_sp = add_sp;
    }

    public int get_add_fire() {
        return _add_fire;
    }

    public void set_add_fire(int add_fire) {
        _add_fire = add_fire;
    }

    public int get_add_wind() {
        return _add_wind;
    }

    public void set_add_wind(int add_wind) {
        _add_wind = add_wind;
    }

    public int get_add_earth() {
        return _add_earth;
    }

    public void set_add_earth(int add_earth) {
        _add_earth = add_earth;
    }

    public int get_add_water() {
        return _add_water;
    }

    public void set_add_water(int add_water) {
        _add_water = add_water;
    }

    public int get_add_stun() {
        return _add_stun;
    }

    public void set_add_stun(int add_stun) {
        _add_stun = add_stun;
    }

    public int get_add_stone() {
        return _add_stone;
    }

    public void set_add_stone(int add_stone) {
        _add_stone = add_stone;
    }

    public int get_add_sleep() {
        return _add_sleep;
    }

    public void set_add_sleep(int add_sleep) {
        _add_sleep = add_sleep;
    }

    public int get_add_freeze() {
        return _add_freeze;
    }

    public void set_add_freeze(int add_freeze) {
        _add_freeze = add_freeze;
    }

    public int get_add_sustain() {
        return _add_sustain;
    }

    public void set_add_sustain(int add_sustain) {
        _add_sustain = add_sustain;
    }

    public int get_add_blind() {
        return _add_blind;
    }

    public void set_add_blind(int add_blind) {
        _add_blind = add_blind;
    }

    public boolean get_death_exp() {
        return _death_exp;
    }

    public void set_death_exp(boolean death_exp) {
        _death_exp = death_exp;
    }

    public boolean get_death_item() {
        return _death_item;
    }

    public void set_death_item(boolean death_item) {
        _death_item = death_item;
    }

    public boolean get_death_skill() {
        return _death_skill;
    }

    public void set_death_skill(boolean death_skill) {
        _death_skill = death_skill;
    }

    public boolean get_death_score() {
        return _death_score;
    }

    public void set_death_score(boolean death_score) {
        _death_score = death_score;
    }

    public int get_add_adena() {
        return _add_adena;
    }

    public void set_add_adena(int add_adena) {
        _add_adena = add_adena;
    }


    public int get_add_mf() {
        return _add_mf;
    }

    public void set_add_mf(int mf) {
        _add_mf = mf;
    }

    public int get_skin_id() {
        return _skin_id;
    }

    public void set_skin_id(int i) {
        _skin_id = i;
    }


    public int get_add_exp() {

        return _add_exp;
    }

    public void set_add_exp(int add_exp) {

        _add_exp = add_exp;
    }

    /**
     * 增加藥水回復量%
     *
     */
    public int get_up_hp_potion() {
        // 假設有相應的屬性
        return _add_potion;
    }

    public void set_add_potion(int add_potion) {
        _add_potion = add_potion;
    }

    /**
     * 增加PVP攻擊
     *
     */
    public int getPvpDmg() {
        return _add_PVP;
    }

    public void set_add_PVP(int add_PVP) {
        _add_PVP = add_PVP;
    }

    /**
     * 增加PVP減免
     *
     */
    public int getPvpDmg_R() {
        return _add_PVP_R;
    }

    public void set_add_PVP_R(int add_PVP_R) {
        _add_PVP_R = add_PVP_R;
    }

    /**
     * 添加魔法命中
     */
    public int getOriginalMagicHit() {
        return _add_magic_hit;
    }

    public void set_magic_hit(int add_magic_hit) {
        _add_magic_hit = add_magic_hit;
    }

    /**
     * 添加昏迷命中
     */
    public int getStunLevel() {

        return _add_StunLv;
    }

    public void set_StunLevel(int add_StunLv) {
        _add_StunLv = add_StunLv;
    }

    /**
     * 添加7.6近距離爆擊
     */
    public int getCloseCritical() {

        return _add_CloseCritical;
    }

    public void set_CloseCritical(int add_CloseCritical) {
        _add_CloseCritical = add_CloseCritical;
    }

    /**
     * 移植7.6遠距離爆擊率
     *
     */
    public int getBowCritical() {
        return _add_BowCritical;
    }

    public void set_BowCritical(int add_BowCritical) {
        _add_BowCritical += add_BowCritical;
    }


    /**
     * PVE攻擊
     */
    public int get_DamageReductionPVE() { // PvE 傷害減免
        return _DamageReductionPVE;
    }

    public void set_DamageReductionPVE(int add_DamageReductionPVE) {
        _DamageReductionPVE = add_DamageReductionPVE;
    }

    /**
     * PVE減免
     */
    public int get_DamageReductionPVE_R() { // PvE 傷害減免
        return _DamageReductionPVE_R;
    }

    public void set_DamageReductionPVE_R(int add_DamageReductionPVE_R) {
        _DamageReductionPVE_R = add_DamageReductionPVE_R;
    }

    /**
     * 機率給予爆擊機率
     */

    public int getDiceDmg() {
        return _adddice_dmg;
    }

    public void set_DiceDmg(int add_DiceDmg) {
        _adddice_dmg = add_DiceDmg;
    }

    public int set_DiceDmg() {
        return 0;
    }

    /**
     * 機率給予爆擊質數
     */
    public int getDmg() {
        return _add_dice_dmgharm;
    }

    public void set_DiceDmg_dmherm(int add_DiceDmg_dmherm) {
        _add_dice_dmgharm = add_DiceDmg_dmherm;
    }


    /**
     * 吸血
     */
    public int get_dice_hp() {
        return _add_dice_hp;
    }

    public void set_dice_hp(int add_dice_hp) {
        _add_dice_hp = add_dice_hp;
    }

    public int get_sucking_hp() {
        return _add_sucking_hp;
    }

    public void set_sucking_hp(int _sucking_hp) {
        _add_sucking_hp = _sucking_hp;
    }

    /**
     * 吸魔
     */
    public int get_dice_mp() {
        return _add_dice_mp;
    }

    public void set_dice_mp(int add_dice_mp) {
        _add_dice_mp = add_dice_mp;
    }

    public int get_sucking_mp() {
        return _add_sucking_mp;
    }

    public void set_sucking_mp(int _sucking_mp) {
        _add_sucking_mp = _sucking_mp;
    }

    public int set_DiceDmg_dmherm() {
        return 0;
    }

}
