package com.lineage.server.templates;

/** 力量對應加成資料（從「能力力量設置」載入） */
public class StrSetting {
    public final int str;              // 力量值
    // PVP 加成
    public final int pvpAtk;           // PVP 攻擊加成
    public final int pvpHit;           // PVP 命中加成
    // PVE 加成
    public final int pveAtk;           // PVE 攻擊加成
    public final int pveHit;           // PVE 命中加成
    // 爆擊相關（PVP/PVE 共用）
    public final int critChance;       // 爆擊機率（%）
    public final int critPercent;      // 爆擊傷害百分比倍率（例：50 = +50%）
    public final int critFx;           // 爆擊特效（動畫ID）

    public StrSetting(int str, int pvpAtk, int pvpHit, int pveAtk, int pveHit, int critChance, int critPercent, int critFx) {
        this.str = str;
        this.pvpAtk = pvpAtk;
        this.pvpHit = pvpHit;
        this.pveAtk = pveAtk;
        this.pveHit = pveHit;
        this.critChance = critChance;
        this.critPercent = critPercent;
        this.critFx = critFx;
    }
}
