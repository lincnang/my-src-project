package com.lineage.server.templates;

/** 敏捷對應加成資料（從「能力敏捷設置」載入） */
public class DexSetting {
    public final int dex;              // 敏捷值
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

    public DexSetting(int dex, int pvpAtk, int pvpHit, int pveAtk, int pveHit, int critChance, int critPercent, int critFx) {
        this.dex = dex;
        this.pvpAtk = pvpAtk;
        this.pvpHit = pvpHit;
        this.pveAtk = pveAtk;
        this.pveHit = pveHit;
        this.critChance = critChance;
        this.critPercent = critPercent;
        this.critFx = critFx;
    }
}
