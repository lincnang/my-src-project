package com.add.Tsai.Astrology;

/**
 * 守護星盤_依詩蒂 資料容器
 * 欄位配置比照「守護星盤_格立特」資料表，並依你提供效果需求對應。
 */
public class YishidiAstrologyData {
    private final int buttonOrder;
    private final String note;
    private final int questId;
    private final int cards;
    private final int skillId; // 0=一般節點，>0=技能節點
    private final int incompleteGfxId;
    private final int completeGfxId;
    private final String needItemId;
    private final String needItemCount;

    private final int addAc;
    private final int addHp;
	private final int addMp;
    private final int addMeleeDmg;
    private final int addMissileDmg;
    private final int addMeleeHit;
    private final int addMissileHit;
    private final int addMagicHit;         // 魔法命中
    private final int addDmgReduction;      // 物理傷害減免（DR）
    private final int addMagicDmgReduction; // 魔法傷害減免（MDR）
    private final int addMagicDmg;          // 魔法傷害（MATK）
    private final int pvpDamageUp;          // PVP 傷害提升
    private final int pvpDmgReduction;      // PVP 傷害減免（採用 setPvpDmg_R）
    private final int critResistPercent;    // 遠近距離爆擊抵抗+%
    private final int critChanceClose;      // 近距離爆擊+%
    private final int critChanceBow;        // 遠距離爆擊+%
    private final int critDmgReduction;     // 爆擊傷害減免（保留）
    private final int critDmgUp;            // 爆擊傷害提升（保留）
    private final double procChance;        // 技能發動機率（%）
    private final int skillProcGfxId;       // 技能觸發GFX
    private final int skillRange;           // 技能範圍（格）
    private final int skillRangeDamagePercent; // 技能範圍傷害%
    // 減益狀態
    private final int debuffProcPercent;       // 減益狀態觸發機率%
    private final int debuffDmgDown;           // 減益狀態減傷（攻擊方每擊固定扣減）
    private final int debuffGfxId;             // 減益狀態GFX
    private final int debuffDurationSec;       // 減益狀態恢復時間（秒）
    private final int debuffIconId;            // 減益狀態ICON ID
    private final int debuffStringId;          // 減益狀態字串 ID
    // 基本屬性
    private final int addStr;               // 力量
    private final int addDex;               // 敏捷
    private final int addCon;               // 體質
    private final int addInt;               // 智力
    private final int addWis;               // 精神
    // 百分比減傷
    private final int allDmgReductionPercent;   // 全傷害減免 +%
    private final int meleeDmgReductionPercent; // 近距離傷害減免 +%
    private final int rangedDmgReductionPercent; // 遠距離傷害減免 +%
    private final int blockWeaponPercent;        // 阻擋武器 +%

	public YishidiAstrologyData(int buttonOrder, String note, int questId, int cards, int skillId,
								int incompleteGfxId, int completeGfxId, String needItemId, String needItemCount,
								int addAc, int addHp, int addMp, int addMeleeDmg, int addMissileDmg, int addMeleeHit, int addMissileHit, int addMagicHit,
                                int addDmgReduction, int addMagicDmgReduction, int addMagicDmg,
                                int pvpDamageUp, int pvpDmgReduction,
                                int critResistPercent,
                                int critDmgReduction, int critDmgUp,
                                double procChance, int skillProcGfxId,
                                int critChanceClose, int critChanceBow,
                                int addStr, int addDex, int addCon, int addInt, int addWis,
                                int allDmgReductionPercent, int meleeDmgReductionPercent,
                                int rangedDmgReductionPercent, int blockWeaponPercent,
                                int skillRange, int skillRangeDamagePercent,
                                int debuffProcPercent, int debuffDmgDown, int debuffGfxId,
                                int debuffDurationSec, int debuffIconId, int debuffStringId) {
        this.buttonOrder = buttonOrder;
        this.note = note;
        this.questId = questId;
        this.cards = cards;
        this.skillId = skillId;
        this.incompleteGfxId = incompleteGfxId;
        this.completeGfxId = completeGfxId;
        this.needItemId = needItemId;
        this.needItemCount = needItemCount;
        this.addAc = addAc;
        this.addHp = addHp;
		this.addMp = addMp;
		this.addMeleeDmg = addMeleeDmg;
        this.addMissileDmg = addMissileDmg;
        this.addMeleeHit = addMeleeHit;
        this.addMissileHit = addMissileHit;
        this.addMagicHit = addMagicHit;
        this.addDmgReduction = addDmgReduction;
        this.addMagicDmgReduction = addMagicDmgReduction;
        this.addMagicDmg = addMagicDmg;
        this.pvpDamageUp = pvpDamageUp;
        this.pvpDmgReduction = pvpDmgReduction;
        this.critResistPercent = critResistPercent;
        this.critChanceClose = critChanceClose;
        this.critChanceBow = critChanceBow;
        this.critDmgReduction = critDmgReduction;
        this.critDmgUp = critDmgUp;
        this.procChance = procChance;
        this.skillProcGfxId = skillProcGfxId;
        this.skillRange = Math.max(0, skillRange);
        this.skillRangeDamagePercent = Math.max(0, skillRangeDamagePercent);
        this.debuffProcPercent = Math.max(0, Math.min(100, debuffProcPercent));
        this.debuffDmgDown = Math.max(0, debuffDmgDown);
        this.debuffGfxId = Math.max(0, debuffGfxId);
        this.debuffDurationSec = Math.max(0, debuffDurationSec);
        this.debuffIconId = Math.max(0, debuffIconId);
        this.debuffStringId = Math.max(0, debuffStringId);
        this.addStr = addStr;
        this.addDex = addDex;
        this.addCon = addCon;
        this.addInt = addInt;
        this.addWis = addWis;
        this.allDmgReductionPercent = allDmgReductionPercent;
        this.meleeDmgReductionPercent = meleeDmgReductionPercent;
        this.rangedDmgReductionPercent = rangedDmgReductionPercent;
        this.blockWeaponPercent = blockWeaponPercent;
    }

    public int getButtonOrder() { return buttonOrder; }
    public String getNote() { return note; }
    public int getQuestId() { return questId; }
    public int getCards() { return cards; }
    public int getSkillId() { return skillId; }
    public int getIncompleteGfxId() { return incompleteGfxId; }
    public int getCompleteGfxId() { return completeGfxId; }
    public String getNeedItemId() { return needItemId; }
    public String getNeedItemCount() { return needItemCount; }
    public int getAddAc() { return addAc; }
    public int getAddHp() { return addHp; }
	public int getAddMp() { return addMp; }
    public int getAddMeleeDmg() { return addMeleeDmg; }
    public int getAddMissileDmg() { return addMissileDmg; }
    public int getAddMeleeHit() { return addMeleeHit; }
    public int getAddMissileHit() { return addMissileHit; }
    public int getAddMagicHit() { return addMagicHit; }
    public int getAddDmgReduction() { return addDmgReduction; }
    public int getAddMagicDmgReduction() { return addMagicDmgReduction; }
    public int getAddMagicDmg() { return addMagicDmg; }
    public int getPvpDamageUp() { return pvpDamageUp; }
    public int getPvpDmgReduction() { return pvpDmgReduction; }
    public int getCritResistPercent() { return critResistPercent; }
    public int getCritChanceClose() { return critChanceClose; }
    public int getCritChanceBow() { return critChanceBow; }
    public int getCritDmgReduction() { return critDmgReduction; }
    public int getCritDmgUp() { return critDmgUp; }
    public double getProcChance() { return procChance; }
    public int getSkillProcGfxId() { return skillProcGfxId; }
    public int getSkillRange() { return skillRange; }
    public int getSkillRangeDamagePercent() { return skillRangeDamagePercent; }
    public int getDebuffProcPercent() { return debuffProcPercent; }
    public int getDebuffDmgDown() { return debuffDmgDown; }
    public int getDebuffGfxId() { return debuffGfxId; }
    public int getDebuffDurationSec() { return debuffDurationSec; }
    public int getDebuffIconId() { return debuffIconId; }
    public int getDebuffStringId() { return debuffStringId; }
    public int getAddStr() { return addStr; }
    public int getAddDex() { return addDex; }
    public int getAddCon() { return addCon; }
    public int getAddInt() { return addInt; }
    public int getAddWis() { return addWis; }
    public int getAllDmgReductionPercent() { return allDmgReductionPercent; }
    public int getMeleeDmgReductionPercent() { return meleeDmgReductionPercent; }
    public int getRangedDmgReductionPercent() { return rangedDmgReductionPercent; }
    public int getBlockWeaponPercent() { return blockWeaponPercent; }
}


