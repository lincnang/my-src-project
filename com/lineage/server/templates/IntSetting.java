package com.lineage.server.templates;

/** 智力對應加成資料（從「系統_強化智力設置」載入） */
public class IntSetting {
    public final int intelligence;        // 智力值
    public final int magicAttack;         // 魔攻加成
    public final int magicHit;            // 魔法命中加成
    public final int magicPenetration;    // 魔法穿透率（%）
    public final int ignoreMagicDefense;  // 忽略魔防
    public final int magicCritFx;         // 魔法爆擊特效（動畫ID）

    public IntSetting(int intelligence, int magicAttack, int magicHit, int magicPenetration, int ignoreMagicDefense, int magicCritFx) {
        this.intelligence = intelligence;
        this.magicAttack = magicAttack;
        this.magicHit = magicHit;
        this.magicPenetration = magicPenetration;
        this.ignoreMagicDefense = ignoreMagicDefense;
        this.magicCritFx = magicCritFx;
    }
}
