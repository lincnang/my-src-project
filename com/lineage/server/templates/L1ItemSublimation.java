package com.lineage.server.templates;

/**
 * 昇華資料模板
 */
public class L1ItemSublimation {

    private int _itemId;
    private int _sublimationType;
    private int _sublimationLv;
    private int _checkType;
    private int _checkType2;
    private String _title;
    private int _random;
    private int _failureMod;
    private int _successGifid;
    private int _triggerChance;

    private double _damage;
    private double _magicDmg;

    private int _dmgChanceHp;
    private int _dmgChanceMp;

    // 成員變數（若尚未宣告，請加上）
    private boolean _withstandDmg;
    private boolean _withstandMagic;
    private boolean _returnDmg;
    private boolean _returnMagic;
    private boolean _returnSkills;

    private int _returnChanceHp;
    private int _returnChanceMp;

    private String _message;
    private int _messageType;

    private int _gfxid;
    private int _gfxidType;

    // ========== Getters / Setters ==========

    public int getItemId() {
        return _itemId;
    }

    public void setItemId(int itemId) {
        this._itemId = itemId;
    }

    public int getSublimationType() {
        return _sublimationType;
    }

    public void setSublimationType(int type) {
        this._sublimationType = type;
    }

    public int getSublimationLv() {
        return _sublimationLv;
    }

    public void setSublimationLv(int lv) {
        this._sublimationLv = lv;
    }

    public int getCheckType() {
        return _checkType;
    }

    public void setCheckType(int checkType) {
        this._checkType = checkType;
    }

    public int getCheckType2() {
        return _checkType2;
    }

    public void setCheckType2(int checkType2) {
        this._checkType2 = checkType2;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public int getRandom() {
        return _random;
    }

    public void setRandom(int random) {
        this._random = random;
    }

    public int getFailureMod() {
        return _failureMod;
    }

    public void setFailureMod(int failureMod) {
        this._failureMod = failureMod;
    }

    public int getSuccessGifid() {
        return _successGifid;
    }

    public void setSuccessGifid(int successGifid) {
        this._successGifid = successGifid;
    }

    public int getTriggerChance() {
        return _triggerChance;
    }

    public void setTriggerChance(int triggerChance) {
        this._triggerChance = triggerChance;
    }

    public double getDamage() {
        return _damage;
    }

    public void setDamage(double damage) {
        this._damage = damage;
    }

    public double getMagicDmg() {
        return _magicDmg;
    }

    public void setMagicDmg(double magicDmg) {
        this._magicDmg = magicDmg;
    }

    public int getDmgChanceHp() {
        return _dmgChanceHp;
    }

    public void setDmgChanceHp(int dmgChanceHp) {
        this._dmgChanceHp = dmgChanceHp;
    }

    public int getDmgChanceMp() {
        return _dmgChanceMp;
    }

    public void setDmgChanceMp(int dmgChanceMp) {
        this._dmgChanceMp = dmgChanceMp;
    }

    public boolean isWithstandDmg() {
        return _withstandDmg;
    }

    public void setWithstandDmg(boolean withstandDmg) {
        this._withstandDmg = withstandDmg;
    }

    public boolean isWithstandMagic() {
        return _withstandMagic;
    }

    public void setWithstandMagic(boolean withstandMagic) {
        this._withstandMagic = withstandMagic;
    }

    public boolean isReturnDmg() {
        return _returnDmg;
    }

    public void setReturnDmg(boolean returnDmg) {
        this._returnDmg = returnDmg;
    }

    public boolean isReturnMagic() {
        return _returnMagic;
    }

    public void setReturnMagic(boolean returnMagic) {
        this._returnMagic = returnMagic;
    }

    public boolean isReturnSkills() {
        return _returnSkills;
    }

    public void setReturnSkills(boolean returnSkills) {
        this._returnSkills = returnSkills;
    }

    public int getReturnChanceHp() {
        return _returnChanceHp;
    }

    public void setReturnChanceHp(int returnChanceHp) {
        this._returnChanceHp = returnChanceHp;
    }

    public int getReturnChanceMp() {
        return _returnChanceMp;
    }

    public void setReturnChanceMp(int returnChanceMp) {
        this._returnChanceMp = returnChanceMp;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        this._message = message;
    }

    public int getMessageType() {
        return _messageType;
    }

    public void setMessageType(int messageType) {
        this._messageType = messageType;
    }

    public int getGfxid() {
        return _gfxid;
    }

    public void setGfxid(int gfxid) {
        this._gfxid = gfxid;
    }

    public int getGfxidType() {
        return _gfxidType;
    }

    public void setGfxidType(int gfxidType) {
        this._gfxidType = gfxidType;
    }

    public boolean getWithstandDmg() {
        return _withstandDmg;
    }
    public boolean getWithstandMagic() {
        return _withstandMagic;
    }

    public boolean getReturnDmg() {
        return _returnDmg;
    }

    public boolean getReturnMagic() {
        return _returnMagic;
    }

    public boolean getReturnSkills() {
        return _returnSkills;
    }
}
