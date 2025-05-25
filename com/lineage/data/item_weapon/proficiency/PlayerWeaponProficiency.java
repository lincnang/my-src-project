package com.lineage.data.item_weapon.proficiency;

/**
 * 玩家武器熟練度
 * @author 聖子默默
 */
public class PlayerWeaponProficiency {
    /**
     * 玩家編號
     */
    private int _charId;
    /**
     * 武器類型
     */
    private int _type;
    /**
     * 武器熟練度等級
     */
    private int _level;
    /**
     * 武器熟練度經驗
     */
    private int _exp;

    protected int getCharId() {
        return _charId;
    }

    protected void setCharId(int charId) {
        _charId = charId;
    }

    protected int getType() {
        return _type;
    }

    protected void setType(int type) {
        _type = type;
    }

    protected int getLevel() {
        return _level;
    }

    protected void setLevel(int level) {
        _level = level;
    }

    protected int getExp() {
        return _exp;
    }

    protected void addExp(int exp) {
        _exp += exp;
    }

    protected void setExp(int exp) {
        _exp = exp;
    }
}
