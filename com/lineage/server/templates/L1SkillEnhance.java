package com.lineage.server.templates;

/**
 * 對應資料表 skills_技能強化 的資料結構
 */
public class L1SkillEnhance {

    private int _id;               // Id (PK)
    private int _skillId;          // 技能編號
    private String _note;          // 備註說明
    private int _bookLevel;        // 技能吃書等級
    private int _setting1;         // 自定義用
    private int _setting2;
    private int _setting3;
    private double _setting4;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public int getSkillId() {
        return _skillId;
    }

    public void setSkillId(int skillId) {
        this._skillId = skillId;
    }

    public String getNote() {
        return _note;
    }

    public void setNote(String note) {
        this._note = note;
    }

    public int getBookLevel() {
        return _bookLevel;
    }

    public void setBookLevel(int bookLevel) {
        this._bookLevel = bookLevel;
    }

    public int getSetting1() {
        return _setting1;
    }

    public void setSetting1(int setting1) {
        this._setting1 = setting1;
    }

    public int getSetting2() {
        return _setting2;
    }

    public void setSetting2(int setting2) {
        this._setting2 = setting2;
    }

    public int getSetting3() {
        return _setting3;
    }

    public void setSetting3(int setting3) {
        this._setting3 = setting3;
    }

    public double getSetting4() {
        return _setting4;
    }

    public void setSetting4(double setting4) {
        this._setting4 = setting4;
    }
}
