package com.add.Tsai.Astrology;

/**
 * 阿頓星盤資料容器 (Data container for Atton Astrology)
 */
public class AttonAstrologyData {
    private final int _buttonOrder;
    private final String _note;
    private final int _needQuestId; // 前置任務編號
    private final int _questId;
    private final int _cards;
    private final int _skillId;
    private final int _incompleteGfxId;
    private final int _completeGfxId;
    private final String _needItemId;
    private final String _needItemCount;
    private final int _addStr;
    private final int _addDex;
    private final int _addCon;
    private final int _addInt;
    private final int _addWis;
    private final int _addCha;
    private final int _addAc;
    private final int _addSp;
    private final int _addHp;
    private final int _addMp;
    private final int _addMeleeDmg;
    private final int _addMissileDmg;
    private final int _addMeleeHit;
    private final int _addMissileHit;
    private final int _addDmgReduction;
    private final int _addMagicDmgReduction;
    private final int _addWeightLimit;
    private final int _stunLevel;
    private final int _stunHit;
    private final int _addMagicDmg;
    private final int _pvpDamageUp;
    private final int _pveDmgReduction;
    private final int _ignoreDmgReductionPercent;
    private final int _tripleArrowReduction;
    private final int _critResistPercent;
    private final int _critChancePercent;
    private final int _critAbsorbMp;
    private final int _critAbsorbHp;
    private final int _missileDmgReductionPercent;
    private final int _stunDmgReduction;
    private final int _ignoreMissileDmgReduction;
    private final int _critDmgReduction;
    private final int _critDmgUp;

    public AttonAstrologyData(int buttonOrder, String note, int needQuestId, int questId, int cards, int skillId,
                              int incompleteGfxId, int completeGfxId, String needItemId, String needItemCount,
                              int addStr, int addDex, int addCon, int addInt, int addWis, int addCha, int addAc,
                              int addSp, int addHp, int addMp, int addMeleeDmg, int addMissileDmg, int addMeleeHit,
                              int addMissileHit, int addDmgReduction, int addMagicDmgReduction, int addWeightLimit,
                              int stunLevel, int stunHit, int addMagicDmg, int pvpDamageUp, int pveDmgReduction,
                              int ignoreDmgReductionPercent, int tripleArrowReduction, int critResistPercent,
                              int critChancePercent, int critAbsorbMp, int critAbsorbHp, int missileDmgReductionPercent,
                              int stunDmgReduction, int ignoreMissileDmgReduction, int critDmgReduction, int critDmgUp) {
        _buttonOrder = buttonOrder;
        _note = note;
        _needQuestId = needQuestId;
        _questId = questId;
        _cards = cards;
        _skillId = skillId;
        _incompleteGfxId = incompleteGfxId;
        _completeGfxId = completeGfxId;
        _needItemId = needItemId;
        _needItemCount = needItemCount;
        _addStr = addStr;
        _addDex = addDex;
        _addCon = addCon;
        _addInt = addInt;
        _addWis = addWis;
        _addCha = addCha;
        _addAc = addAc;
        _addSp = addSp;
        _addHp = addHp;
        _addMp = addMp;
        _addMeleeDmg = addMeleeDmg;
        _addMissileDmg = addMissileDmg;
        _addMeleeHit = addMeleeHit;
        _addMissileHit = addMissileHit;
        _addDmgReduction = addDmgReduction;
        _addMagicDmgReduction = addMagicDmgReduction;
        _addWeightLimit = addWeightLimit;
        _stunLevel = stunLevel;
        _stunHit = stunHit;
        _addMagicDmg = addMagicDmg;
        _pvpDamageUp = pvpDamageUp;
        _pveDmgReduction = pveDmgReduction;
        _ignoreDmgReductionPercent = ignoreDmgReductionPercent;
        _tripleArrowReduction = tripleArrowReduction;
        _critResistPercent = critResistPercent;
        _critChancePercent = critChancePercent;
        _critAbsorbMp = critAbsorbMp;
        _critAbsorbHp = critAbsorbHp;
        _missileDmgReductionPercent = missileDmgReductionPercent;
        _stunDmgReduction = stunDmgReduction;
        _ignoreMissileDmgReduction = ignoreMissileDmgReduction;
        _critDmgReduction = critDmgReduction;
        _critDmgUp = critDmgUp;
    }

    public int getButtonOrder() { return _buttonOrder; }
    public String getNote() { return _note; }
    public int getNeedQuestId() { return _needQuestId; }
    public int getQuestId() { return _questId; }
    public int get_cards() { return _cards; }
    public int getSkillId() { return _skillId; }
    public int getIncompleteGfxId() { return _incompleteGfxId; }
    public int getCompleteGfxId() { return _completeGfxId; }
    public String getNeedItemId() { return _needItemId; }
    public String getNeedItemCount() { return _needItemCount; }
    public int getAddStr() { return _addStr; }
    public int getAddDex() { return _addDex; }
    public int getAddCon() { return _addCon; }
    public int getAddInt() { return _addInt; }
    public int getAddWis() { return _addWis; }
    public int getAddCha() { return _addCha; }
    public int getAddAc() { return _addAc; }
    public int getAddSp() { return _addSp; }
    public int getAddHp() { return _addHp; }
    public int getAddMp() { return _addMp; }
    public int getAddMeleeDmg() { return _addMeleeDmg; }
    public int getAddMissileDmg() { return _addMissileDmg; }
    public int getAddMeleeHit() { return _addMeleeHit; }
    public int getAddMissileHit() { return _addMissileHit; }
    public int getAddDmgReduction() { return _addDmgReduction; }
    public int getAddMagicDmgReduction() { return _addMagicDmgReduction; }
    public int getAddWeightLimit() { return _addWeightLimit; }
    public int getStunLevel() { return _stunLevel; }
    public int getStunHit() { return _stunHit; }
    public int getAddMagicDmg() { return _addMagicDmg; }
    public int getPvpDamageUp() { return _pvpDamageUp; }
    public int getPveDmgReduction() { return _pveDmgReduction; }
    public int getIgnoreDmgReductionPercent() { return _ignoreDmgReductionPercent; }
    public int getTripleArrowReduction() { return _tripleArrowReduction; }
    public int getCritResistPercent() { return _critResistPercent; }
    public int getCritChancePercent() { return _critChancePercent; }
    public int getCritAbsorbMp() { return _critAbsorbMp; }
    public int getCritAbsorbHp() { return _critAbsorbHp; }
    public int getMissileDmgReductionPercent() { return _missileDmgReductionPercent; }
    public int getStunDmgReduction() { return _stunDmgReduction; }
    public int getIgnoreMissileDmgReduction() { return _ignoreMissileDmgReduction; }
    public int getCritDmgReduction() { return _critDmgReduction; }
    public int getCritDmgUp() { return _critDmgUp; }
}
