package com.add.Tsai.Astrology;

/**
 * 守護星盤_格立特 資料容器
 */
public class GritAstrologyData {
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
    private final int addMeleeDmg;
    private final int addMissileDmg;
    private final int addMeleeHit;
    private final int addMissileHit;
    private final int addDmgReduction;
    private final int addMagicDmgReduction;
    private final int addMagicDmg;
    private final int pvpDamageUp;
    private final int pveDmgReduction;
    private final int critResistPercent;     // 遠近距離暴擊抵抗+%
    private final int critChanceClose;       // 近距離爆擊+%（新）
    private final int critChanceBow;         // 遠距離爆擊+%（新）
    private final int critAbsorbMp;          // 爆擊時吸收mp（暫不掛鉤特定事件）
    private final int critAbsorbHp;          // 爆擊時吸收hp（暫不掛鉤特定事件）
    private final int critDmgReduction;      // 暴擊傷害減免（暫未掛鉤）
    private final int critDmgUp;             // 暴擊傷害提升（暫未掛鉤）
    private final double procChance;         // 技能發動機率（%）(double)
    private final double skillCritDmg;       // 技能暴擊傷害（% 或 乘數，見用法）
    private final int skillProcGfxId;        // 技能觸發GFX（播放於目標）

    public GritAstrologyData(int buttonOrder, String note, int questId, int cards, int skillId,
                             int incompleteGfxId, int completeGfxId, String needItemId, String needItemCount,
                             int addAc, int addHp, int addMeleeDmg, int addMissileDmg, int addMeleeHit, int addMissileHit,
                             int addDmgReduction, int addMagicDmgReduction, int addMagicDmg,
                             int pvpDamageUp, int pveDmgReduction,
                             int critResistPercent, int critAbsorbMp, int critAbsorbHp,
                             int critDmgReduction, int critDmgUp,
                             double procChance, double skillCritDmg, int skillProcGfxId,
                             int critChanceClose,
                             int critChanceBow) {
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
        this.addMeleeDmg = addMeleeDmg;
        this.addMissileDmg = addMissileDmg;
        this.addMeleeHit = addMeleeHit;
        this.addMissileHit = addMissileHit;
        this.addDmgReduction = addDmgReduction;
        this.addMagicDmgReduction = addMagicDmgReduction;
        this.addMagicDmg = addMagicDmg;
        this.pvpDamageUp = pvpDamageUp;
        this.pveDmgReduction = pveDmgReduction;
        this.critResistPercent = critResistPercent;
        this.critChanceClose = critChanceClose;
        this.critChanceBow = critChanceBow;
        this.critAbsorbMp = critAbsorbMp;
        this.critAbsorbHp = critAbsorbHp;
        this.critDmgReduction = critDmgReduction;
        this.critDmgUp = critDmgUp;
        this.procChance = procChance;
        this.skillCritDmg = skillCritDmg;
        this.skillProcGfxId = skillProcGfxId;
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
    public int getAddMeleeDmg() { return addMeleeDmg; }
    public int getAddMissileDmg() { return addMissileDmg; }
    public int getAddMeleeHit() { return addMeleeHit; }
    public int getAddMissileHit() { return addMissileHit; }
    public int getAddDmgReduction() { return addDmgReduction; }
    public int getAddMagicDmgReduction() { return addMagicDmgReduction; }
    public int getAddMagicDmg() { return addMagicDmg; }
    public int getPvpDamageUp() { return pvpDamageUp; }
    public int getPveDmgReduction() { return pveDmgReduction; }
    public int getCritResistPercent() { return critResistPercent; }
    public int getCritChanceClose() { return critChanceClose; }
    public int getCritChanceBow() { return critChanceBow; }
    public int getCritAbsorbMp() { return critAbsorbMp; }
    public int getCritAbsorbHp() { return critAbsorbHp; }
    public int getCritDmgReduction() { return critDmgReduction; }
    public int getCritDmgUp() { return critDmgUp; }
    public double getProcChance() { return procChance; }
    public double getSkillCritDmg() { return skillCritDmg; }
    public int getSkillProcGfxId() { return skillProcGfxId; }
}


