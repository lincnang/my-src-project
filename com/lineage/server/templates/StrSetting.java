package com.lineage.server.templates;

/** 力量對應加成資料（從「能力力量設置」載入） */
public class StrSetting {
    public final int str;              // 力量值
    public final int atk;              // 攻擊加成
    public final int hit;              // 命中加成
    public final int critChance;       // 爆擊機率（%）
    public final int critPercent;      // 爆擊傷害百分比倍率（例：50 = +50%）
    public final int critFx;           // 爆擊特效（動畫ID）

    public StrSetting(int str, int atk, int hit, int critChance, int critPercent, int critFx) {
        this.str = str;
        this.atk = atk;
        this.hit = hit;
        this.critChance = critChance;
        this.critPercent = critPercent;
        this.critFx = critFx;
    }
}
