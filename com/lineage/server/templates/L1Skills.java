package com.lineage.server.templates;

public class L1Skills {
    public static final int ATTR_NONE = 0;
    public static final int ATTR_EARTH = 1;
    public static final int ATTR_FIRE = 2;
    public static final int ATTR_WATER = 4;
    public static final int ATTR_WIND = 8;
    public static final int ATTR_RAY = 16;
    public static final int TYPE_PROBABILITY = 1;
    public static final int TYPE_CHANGE = 2;
    public static final int TYPE_CURSE = 4;
    public static final int TYPE_DEATH = 8;
    public static final int TYPE_HEAL = 16;
    public static final int TYPE_RESTORE = 32;
    public static final int TYPE_ATTACK = 64;
    public static final int TYPE_OTHER = 128;
    /**
     * 技能對自己
     */
    public static final int TARGET_TO_ME = 0;
    /**
     * 技能對人物
     */
    public static final int TARGET_TO_PC = 1;
    /**
     * 技能對NPC
     */
    public static final int TARGET_TO_NPC = 2;
    /**
     * 技能對血盟
     */
    public static final int TARGET_TO_CLAN = 4;
    /**
     * 技能對隊伍
     */
    public static final int TARGET_TO_PARTY = 8;
    /**
     * 技能對寵物
     */
    public static final int TARGET_TO_PET = 16;
    /**
     * 技能對地點
     */
    public static final int TARGET_TO_PLACE = 32;
    boolean _isThrough;
    private int _skillId;
    private String _name;
    private int _skillLevel;
    private int _skillNumber;
    private int _mpConsume;
    private int _hpConsume;
    private int _itmeConsumeId;
    private int _itmeConsumeCount;
    private int _reuseDelay;
    private int _buffDuration;
    private String _target;
    private int _targetTo;
    private int _damageValue;
    private int _damageDice;
    private int _damageDiceCount;
    private int _probabilityValue;
    private int _probabilityDice;
    private int _attr;
    private int _type;
    private int _lawful;
    private int _ranged;
    private int _area;
    private int _id;
    private String _nameId;
    private int _actionId;
    private int _castGfx;
    private int _castGfx2;
    private int _sysmsgIdHappen;
    private int _sysmsgIdStop;
    private int _sysmsgIdFail;
    private int _probability_lv1;
    private int _probability_lv2;
    private int _probability_lv3;
    private int _probability_mr;
    private int _intel_add_probability;
    private int _intel_add_probability_max;
    private int _buff_iconid;
    private int _buff_stringid;

    public L1Skills() {
        this._probability_lv1 = 0;
        this._probability_lv2 = 0;
        this._probability_lv3 = 0;
        this._probability_mr = 0;
        this._intel_add_probability = 0;
        this._intel_add_probability_max = 0;
    }

    public int getSkillId() {
        return _skillId;
    }

    public void setSkillId(int i) {
        _skillId = i;
    }

    public String getName() {
        return _name;
    }

    public void setName(String s) {
        _name = s;
    }

    public int getSkillLevel() {
        return _skillLevel;
    }

    public void setSkillLevel(int i) {
        _skillLevel = i;
    }

    public int getSkillNumber() {
        return _skillNumber;
    }

    public void setSkillNumber(int i) {
        _skillNumber = i;
    }

    public int getMpConsume() {
        return _mpConsume;
    }

    public void setMpConsume(int i) {
        _mpConsume = i;
    }

    public int getHpConsume() {
        return _hpConsume;
    }

    public void setHpConsume(int i) {
        _hpConsume = i;
    }

    public int getItemConsumeId() {
        return _itmeConsumeId;
    }

    public void setItemConsumeId(int i) {
        _itmeConsumeId = i;
    }

    public int getItemConsumeCount() {
        return _itmeConsumeCount;
    }

    public void setItemConsumeCount(int i) {
        _itmeConsumeCount = i;
    }

    public int getReuseDelay() {
        return _reuseDelay;
    }

    public void setReuseDelay(int i) {
        _reuseDelay = i;
    }

    public int getBuffDuration() {
        return _buffDuration;
    }

    public void setBuffDuration(int i) {
        _buffDuration = i;
    }

    public String getTarget() {
        return _target;
    }

    public void setTarget(String s) {
        _target = s;
    }

    /**
     * 施展對像<br>
     * 0:自己 1:玩家 2:NPC 4:血盟 8:隊伍 16:寵物 32:位置
     */
    public int getTargetTo() {
        return _targetTo;
    }

    /**
     * 施展對像
     *
     * @param i 0:自己 1:玩家 2:NPC 4:血盟 8:隊伍 16:寵物 32:位置
     */
    public void setTargetTo(int i) {
        _targetTo = i;
    }

    public int getDamageValue() {
        return _damageValue;
    }

    public void setDamageValue(int i) {
        _damageValue = i;
    }

    public int getDamageDice() {
        return _damageDice;
    }

    public void setDamageDice(int i) {
        _damageDice = i;
    }

    public int getDamageDiceCount() {
        return _damageDiceCount;
    }

    public void setDamageDiceCount(int i) {
        _damageDiceCount = i;
    }

    public int getProbabilityValue() {
        return _probabilityValue;
    }

    public void setProbabilityValue(int i) {
        _probabilityValue = i;
    }

    public int getProbabilityDice() {
        return _probabilityDice;
    }

    public void setProbabilityDice(int i) {
        _probabilityDice = i;
    }

    public int getAttr() {
        return _attr;
    }

    public void setAttr(int i) {
        _attr = i;
    }

    public int getType() {
        return _type;
    }

    public void setType(int i) {
        _type = i;
    }

    public int getLawful() {
        return _lawful;
    }

    public void setLawful(int i) {
        _lawful = i;
    }

    public int getRanged() {
        return _ranged;
    }

    public void setRanged(int i) {
        _ranged = i;
    }

    public int getArea() {
        return _area;
    }

    public void setArea(int i) {
        _area = i;
    }

    /**
     * 是否無視障礙物
     *
     */
    public boolean isThrough() {
        return _isThrough;
    }

    public void setThrough(boolean flag) {
        _isThrough = flag;
    }

    public int getId() {
        return _id;
    }

    public void setId(int i) {
        _id = i;
    }

    public String getNameId() {
        return _nameId;
    }

    public void setNameId(String s) {
        _nameId = s;
    }

    public int getActionId() {
        return _actionId;
    }

    public void setActionId(int i) {
        _actionId = i;
    }

    public int getCastGfx() {
        return _castGfx;
    }

    public void setCastGfx(int i) {
        _castGfx = i;
    }

    public int getCastGfx2() {
        return _castGfx2;
    }

    public void setCastGfx2(int i) {
        _castGfx2 = i;
    }

    public int getSysmsgIdHappen() {
        return _sysmsgIdHappen;
    }

    public void setSysmsgIdHappen(int i) {
        _sysmsgIdHappen = i;
    }

    public int getSysmsgIdStop() {
        return _sysmsgIdStop;
    }

    public void setSysmsgIdStop(int i) {
        _sysmsgIdStop = i;
    }

    public int getSysmsgIdFail() {
        return _sysmsgIdFail;
    }

    public void setSysmsgIdFail(int i) {
        _sysmsgIdFail = i;
    }

    public int getProbability_Lv1() {
        return _probability_lv1;
    }

    public void setProbability_Lv1(final int i) {
        _probability_lv1 = i;
    }

    public int getProbability_Lv2() {
        return _probability_lv2;
    }

    public void setProbability_Lv2(final int i) {
        _probability_lv2 = i;
    }

    public int getProbability_Lv3() {
        return _probability_lv3;
    }

    public void setProbability_Lv3(final int i) {
        _probability_lv3 = i;
    }

    public int getProbability_Mr() {
        return _probability_mr;
    }

    public void setProbability_Mr(final int i) {
        _probability_mr = i;
    }

    public int get_intel_add_probability() {
        return this._intel_add_probability;
    }

    public void set_intel_add_probability(final int i) {
        this._intel_add_probability = i;
    }

    public int get_intel_add_probability_max() {
        return this._intel_add_probability_max;
    }

    public void set_intel_add_probability_max(final int i) {
        this._intel_add_probability_max = i;
    }

    public int get_buff_iconid() {
        return this._buff_iconid;
    }   //*圖檔tbl編號

    public void set_buff_iconid(final int i) {
        this._buff_iconid = i;
    } //*圖檔tbl編號

    public int get_buff_stringid() {
        return this._buff_stringid;
    } //*偵測圖檔string編號

    public void set_buff_stringid(final int i) {
        this._buff_stringid = i;
    } //*偵測圖檔string編號
}
