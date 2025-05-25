package com.lineage.server.model.Instance;

/**
 * 怪物強化系統
 *
 * <p>
 * 怪物強化公式 - (current_dc / dc_enhance) * 屬性 + 原始屬性 = 最後屬性
 * <p>
 * 屬性 - monster_enhance裡面的level、hp、mp、ac、str、dex、con、wis、int、mr、Hpr 原始屬性 - npc裡面的level、hp、mp、ac、str、dex、con、wis、int、mr、Hpr
 * 最後屬性 - 怪物重生後的level、hp、mp、ac、str、dex、con、wis、int、mr、Hpr
 * </p>
 */
public class L1MonsterEnhanceInstance {
    private int npcid;
    private int currentdc;
    private int dcEnhance;
    private int level;
    private int hp;
    private int mp;
    private int ac;
    private int str;
    private int dex;
    private int con;
    private int wis;
    private int intelligence;
    private int mr;
    private int hpr;

    // Getter and Setter methods
    public int getNpcId() {
        return npcid;
    }

    public void setNpcId(int npcid) {
        this.npcid = npcid;
    }

    public int getCurrentDc() {
        return currentdc;
    }

    public void setCurrentDc(int currentdc) {
        this.currentdc = currentdc;
    }

    public int getDcEnhance() {
        return dcEnhance;
    }

    public void setDcEnhance(int dcEnhance) {
        this.dcEnhance = dcEnhance;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    public int getWis() {
        return wis;
    }

    public void setWis(int wis) {
        this.wis = wis;
    }


    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getMr() {
        return mr;
    }

    public void setMr(int mr) {
        this.mr = mr;
    }

    public int getHpr() {
        return hpr;
    }

    public void setHpr(int hpr) {
        this.hpr = hpr;
    }
}
