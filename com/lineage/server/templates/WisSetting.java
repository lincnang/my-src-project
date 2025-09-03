package com.lineage.server.templates;

/** 精神對應加成資料（從「系統_強化精神設置」載入） */
public class WisSetting {
    public final int wis;   // 精神值
    public final int mr;    // 抗魔
    public final int mpr;   // 回魔

    public WisSetting(int wis, int mr, int mpr) {
        this.wis = wis;
        this.mr = mr;
        this.mpr = mpr;
    }
}




