package com.add.Tsai.Astrology;

/**
 * 絲莉安星盤
 */
public class SilianAstrologyData {
    private final int buttonOrder;
    private final String note;
    private final int needQuestId; // 目前未用，保留欄位
    private final int questId;
    private final int cards;
    private final int skillId; // 0=一般節點，>0=技能節點
    private final int incompleteGfxId;
    private final int completeGfxId;
    private final String needItemId;
    private final String needItemCount;

    private final int addHp;
    private final int stunResist;
    private final int addWeightLimit;
    private final int tripleArrowReduction;
    private final int rangedDmgReductionPercent;
    private final int stunDmgReduction;
    private final int hpr;
    private final int mpr;
    private final int gfxid1;
    private final int gfxid2;
    private final int castItemId;
    private final int castItemCount;
    private final int hotTime; // 獨立 HOT 秒數
    private final int grantItemId; // 選擇技能後發放的啟動道具

    public SilianAstrologyData(int buttonOrder, String note, int needQuestId, int questId, int cards, int skillId,
                               int incompleteGfxId, int completeGfxId, String needItemId, String needItemCount,
                               int addHp, int stunResist, int addWeightLimit,
                               int tripleArrowReduction, int rangedDmgReductionPercent, int stunDmgReduction, int hpr, int mpr,
                               int gfxid1, int gfxid2, int castItemId, int castItemCount, int hotTime,
                               int grantItemId, int grantItemCount) {
        this.buttonOrder = buttonOrder;
        this.note = note;
        this.needQuestId = needQuestId;
        this.questId = questId;
        this.cards = cards;
        this.skillId = skillId;
        this.incompleteGfxId = incompleteGfxId;
        this.completeGfxId = completeGfxId;
        this.needItemId = needItemId;
        this.needItemCount = needItemCount;
        this.addHp = addHp;
        this.stunResist = stunResist;
        this.addWeightLimit = addWeightLimit;
        this.tripleArrowReduction = tripleArrowReduction;
        this.rangedDmgReductionPercent = rangedDmgReductionPercent;
        this.stunDmgReduction = stunDmgReduction;
        this.hpr = hpr;
        this.mpr = mpr;
        this.gfxid1 = gfxid1;
        this.gfxid2 = gfxid2;
        this.castItemId = castItemId;
        this.castItemCount = castItemCount;
        this.hotTime = hotTime;
        this.grantItemId = grantItemId;
    }

    public int getButtonOrder() { return buttonOrder; }
    public String getNote() { return note; }
    public int getNeedQuestId() { return needQuestId; }
    public int getQuestId() { return questId; }
    public int get_cards() { return cards; }
    public int getSkillId() { return skillId; }
    public int getIncompleteGfxId() { return incompleteGfxId; }
    public int getCompleteGfxId() { return completeGfxId; }
    public String getNeedItemId() { return needItemId; }
    public String getNeedItemCount() { return needItemCount; }

    public int getAddHp() { return addHp; }
    public int getStunResist() { return stunResist; }
    public int getAddWeightLimit() { return addWeightLimit; }
    public int getTripleArrowReduction() { return tripleArrowReduction; }
    public int getRangedDmgReductionPercent() { return rangedDmgReductionPercent; }
    public int getStunDmgReduction() { return stunDmgReduction; }
    public int getHpr() { return hpr; }
    public int getMpr() { return mpr; }
    public int getGfxid1() { return gfxid1; }
    public int getGfxid2() { return gfxid2; }
    public int getCastItemId() { return castItemId; }
    public int getCastItemCount() { return castItemCount; }
    public int getHotTime() { return hotTime; }
    public int getGrantItemId() { return grantItemId; }
}


