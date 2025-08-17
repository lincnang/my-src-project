package com.lineage.server.templates;

/** 敏捷對應加成資料（從「能力敏捷設置」載入） */
public class DexSetting {
    public final int dex;              // 敏捷值
    public final int atk;              // 攻擊加成
    public final int hit;              // 命中加成
    public final int critChance;       // 爆擊機率（%）
    public final int critPercent;      // 爆擊傷害百分比倍率（例：50 = +50%）
    public final int critFx;           // 爆擊特效（動畫ID）

    public DexSetting(int dex, int atk, int hit, int critChance, int critPercent, int critFx) {
        this.dex = dex;
        this.atk = atk;
        this.hit = hit;
        this.critChance = critChance;
        this.critPercent = critPercent;
        this.critFx = critFx;
    }
}
