package com.lineage.server.templates;

/** 體質對應加成資料（從「系統_強化體質設置」載入） */
public class ConSetting {
    public final int con;           // 體質值
    public final int dmgReduction;  // 受到傷害減少（DR）
    public final int pvpDamage;     // PVP傷害
    public final int pvpReduction;  // PVP減免
    public final int mr;            // 抗魔
    public final int magicAttack;   // 魔攻
    public final int hp;            // 血量

    public ConSetting(int con, int dmgReduction, int pvpDamage, int pvpReduction, int mr, int magicAttack, int hp) {
        this.con = con;
        this.dmgReduction = dmgReduction;
        this.pvpDamage = pvpDamage;
        this.pvpReduction = pvpReduction;
        this.mr = mr;
        this.magicAttack = magicAttack;
        this.hp = hp;
    }
}




