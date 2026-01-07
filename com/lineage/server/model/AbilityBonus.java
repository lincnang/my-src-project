package com.lineage.server.model;

import com.lineage.server.datatables.StrSettingTable;
import com.lineage.server.datatables.DexSettingTable;
import com.lineage.server.templates.StrSetting;
import com.lineage.server.templates.DexSetting;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 力量 vs 敏捷 加成整合工具
 * 規則：比大小，取較高的一邊套用。
 * 支援相等時偏好設定、可回報採用來源（STR/DEX/NONE）。
 */
public final class AbilityBonus {

    private AbilityBonus() {}

    /** 相等時的偏好策略 */
    public enum TiePolicy { PREFER_STR, PREFER_DEX }

    /** 實際採用來源（方便 DEBUG 或記錄） */
    public enum Source { STR, DEX, NONE }

    /** 統一回傳物件 */
    public static class Sum {
        public int atk;          // 固定攻擊加成
        public int hit;          // 命中加成（建議加在命中流程的局部變數）
        public int critChance;   // 爆擊率（%）
        public int critPercent;  // 爆擊倍率（%）
        public int critFx;       // 爆擊特效ID
        public Source source = Source.NONE; // 這次是吃 STR 還是 DEX
    }

    /** 預設：相等時偏好 STR（行為與你現版本相同） */
    public static Sum from(L1PcInstance pc) {
        return from(pc, TiePolicy.PREFER_STR);
    }

    /** 依照 Str/Dex 高低，取對應表加成；相等時可指定偏好 */
    public static Sum from(L1PcInstance pc, TiePolicy tiePolicy) {
        Sum sum = new Sum();
        if (pc == null) return sum;

        int str = pc.getStr();
        int dex = pc.getDex();

        boolean useStr = (str > dex) || (str == dex && tiePolicy == TiePolicy.PREFER_STR);

        if (useStr) {
            StrSetting s = StrSettingTable.getInstance().findByStr(str);
            if (s != null) {
                sum.atk = s.pvpAtk;
                sum.hit = s.pvpHit;
                sum.critChance = s.critChance;
                sum.critPercent = s.critPercent;
                sum.critFx = s.critFx;
                sum.source = Source.STR;
            }
        } else {
            DexSetting d = DexSettingTable.getInstance().findByDex(dex);
            if (d != null) {
                sum.atk = d.pvpAtk;
                sum.hit = d.pvpHit;
                sum.critChance = d.critChance;
                sum.critPercent = d.critPercent;
                sum.critFx = d.critFx;
                sum.source = Source.DEX;
            }
        }

        // 防呆夾限
        if (sum.critChance < 0) sum.critChance = 0;
        if (sum.critChance > 100) sum.critChance = 100;
        if (sum.critPercent < 0) sum.critPercent = 0;

        return sum;
    }

    /** 只做爆擊倍率套用（最終階段使用）。預設相等時偏好 STR。*/
    public static int applyCrit(L1PcInstance pc, int baseDmg) {
        return applyCrit(pc, baseDmg, TiePolicy.PREFER_STR);
    }

    /** 只做爆擊倍率套用（最終階段使用），可指定相等時偏好。*/
    public static int applyCrit(L1PcInstance pc, int baseDmg, TiePolicy tiePolicy) {
        Sum sum = from(pc, tiePolicy);
        if (sum.critChance > 0 && sum.critPercent > 0) {
            int roll = ThreadLocalRandom.current().nextInt(100) + 1;
            if (roll <= sum.critChance) {
                double factor = 1.0 + sum.critPercent / 100.0;
                return (int) Math.round(baseDmg * factor);
            }
        }
        return baseDmg;
    }

    /** 便捷：回傳本次要加的固定攻擊（在早期就可加到 dmg）。*/
    public static int flatAtk(L1PcInstance pc) {
        return from(pc, TiePolicy.PREFER_STR).atk;
    }

    /** 便捷：回傳本次要加的命中（建議在命中流程的局部變數上加）。*/
    public static int hitBonus(L1PcInstance pc) {
        return from(pc, TiePolicy.PREFER_STR).hit;
    }
}
