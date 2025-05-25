package com.lineage.server.templates;

/**
 * VIP能力資料
 */
public class L1Vip {

    // HP / MP 加成
    private int _add_hp;
    private int _add_mp;
    private int _add_hpr;
    private int _add_mpr;

    // 攻擊/命中加成
    private int _add_dmg;
    private int _add_bow_dmg;
    private int _add_hit;
    private int _add_bow_hit;

    // 魔法攻擊/抗性加成
    private int _add_sp;
    private int _add_mr;

    // 六大屬性加成
    private int _add_str;
    private int _add_dex;
    private int _add_con;
    private int _add_wis;
    private int _add_cha;
    private int _add_int;

    // 經驗值加成
    private int _exp;

    // 減傷設定
    private int _PVPdmgReduction; // PVP 減傷
    private int _PVEReduction;    // PVE 減傷

    // 打怪物時增加的傷害
    private int _PVEAttackUp;

    // -------------------- Getter / Setter --------------------

    public int get_add_hp() { return _add_hp; }
    public void set_add_hp(final int i) { _add_hp = i; }

    public int get_add_mp() { return _add_mp; }
    public void set_add_mp(final int i) { _add_mp = i; }

    public int get_add_hpr() { return _add_hpr; }
    public void set_add_hpr(final int i) { _add_hpr = i; }

    public int get_add_mpr() { return _add_mpr; }
    public void set_add_mpr(final int i) { _add_mpr = i; }

    public int get_add_dmg() { return _add_dmg; }
    public void set_add_dmg(final int i) { _add_dmg = i; }

    public int get_add_bowdmg() { return _add_bow_dmg; }
    public void set_add_bowdmg(final int i) { _add_bow_dmg = i; }

    public int get_add_hit() { return _add_hit; }
    public void set_add_hit(final int i) { _add_hit = i; }

    public int get_add_bowhit() { return _add_bow_hit; }
    public void set_add_bowhit(final int i) { _add_bow_hit = i; }

    public int get_add_sp() { return _add_sp; }
    public void set_add_sp(final int i) { _add_sp = i; }

    public int get_add_mr() { return _add_mr; }
    public void set_add_mr(final int i) { _add_mr = i; }

    public int getStr() { return _add_str; }
    public void setStr(final int i) { _add_str = i; }

    public int getDex() { return _add_dex; }
    public void setDex(final int i) { _add_dex = i; }

    public int getCon() { return _add_con; }
    public void setCon(final int i) { _add_con = i; }

    public int getWis() { return _add_wis; }
    public void setWis(final int i) { _add_wis = i; }

    public int getCha() { return _add_cha; }
    public void setCha(final int i) { _add_cha = i; }

    public int getInt() { return _add_int; }
    public void setInt(final int i) { _add_int = i; }

    public int getExpAdd() { return _exp; }
    public void set_expadd(final int i) { _exp = i; }

    public int getPVPdmgReduction() { return _PVPdmgReduction; }
    public void setPVPdmgReduction(final int i) { _PVPdmgReduction = i; }

    public int getPVEReduction() { return _PVEReduction; }
    public void setPVEReduction(final int i) { _PVEReduction = i; }

    public int getPVEAttackUp() { return _PVEAttackUp; }
    public void setPVEAttackUp(final int i) { _PVEAttackUp = i; }
}
