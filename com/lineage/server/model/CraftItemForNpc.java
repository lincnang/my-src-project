package com.lineage.server.model;

import com.lineage.server.templates.L1CraftItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 樂天堂火神製作(DB化)
 */
public class CraftItemForNpc {
    private Map<Integer, L1CraftItem> materialList = new HashMap<Integer, L1CraftItem>();
    private Map<Integer, L1CraftItem> aidMaterialList = new HashMap<Integer, L1CraftItem>();
    private Map<Integer, L1CraftItem> itemList = new HashMap<Integer, L1CraftItem>();
    private Map<Integer, L1CraftItem> bigsuccesitemList = new HashMap<Integer, L1CraftItem>(); // 大成功道具
    private Map<Integer, L1CraftItem> failItem = new HashMap<Integer, L1CraftItem>();
    private List<Integer> polyList = new ArrayList<Integer>();
    private List<Integer> bless = new ArrayList<Integer>();
    private int actionid;
    private int maxLevel;
    private int minLevel;
    private int minLawful;
    private int maxLawful;
    private int minKarma;
    private int maxKarma;
    private int maxCount;
    private int craftDelayTime;
    private int npcId;
    private int succeedRandom;
    private int showWorld;
    private int bigsuccessitemrandom;
    private int craft_nameid;

    public Map<Integer, L1CraftItem> getMaterialList() {
        return this.materialList;
    }

    public void setMaterialList(final Map<Integer, L1CraftItem> materialList) {
        this.materialList = materialList;
    }

    public Map<Integer, L1CraftItem> getAidMaterialList() {
        return this.aidMaterialList;
    }

    public void setAidMaterialList(final Map<Integer, L1CraftItem> aidMaterialList) {
        this.aidMaterialList = aidMaterialList;
    }

    public Map<Integer, L1CraftItem> getItemList() {
        return this.itemList;
    }

    public void setItemList(final Map<Integer, L1CraftItem> itemList) {
        this.itemList = itemList;
    }

    /**
     * 大成功道具
     */
    public Map<Integer, L1CraftItem> getBigSuccessItemList() {
        return this.bigsuccesitemList;
    }

    /**
     * 大成功道具
     */
    public void setBigSuccessItemList(final Map<Integer, L1CraftItem> bigsuccesitemList) {
        this.bigsuccesitemList = bigsuccesitemList;
    }

    public Map<Integer, L1CraftItem> getFailItem() {
        return this.failItem;
    }

    public void setFailItem(final Map<Integer, L1CraftItem> failItem) {
        this.failItem = failItem;
    }

    public List<Integer> getPolyList() {
        return this.polyList;
    }

    public void setPolyList(final List<Integer> polyList) {
        this.polyList = polyList;
    }

    public int getActionid() {
        return this.actionid;
    }

    public void setActionid(final int actionid) {
        this.actionid = actionid;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public void setMaxLevel(final int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getMinLevel() {
        return this.minLevel;
    }

    public void setMinLevel(final int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMinLawful() {
        return this.minLawful;
    }

    public void setMinLawful(final int minLawful) {
        this.minLawful = minLawful;
    }

    public int getMaxLawful() {
        return this.maxLawful;
    }

    public void setMaxLawful(final int maxLawful) {
        this.maxLawful = maxLawful;
    }

    public int getMinKarma() {
        return this.minKarma;
    }

    public void setMinKarma(final int minKarma) {
        this.minKarma = minKarma;
    }

    public int getMaxKarma() {
        return this.maxKarma;
    }

    public void setMaxKarma(final int maxKarma) {
        this.maxKarma = maxKarma;
    }

    public int getCraftDelayTime() {
        return this.craftDelayTime;
    }

    public void setCraftDelayTime(final int craftDelayTime) {
        this.craftDelayTime = craftDelayTime;
    }

    public int getNpcId() {
        return this.npcId;
    }

    public void setNpcId(final int npcId) {
        this.npcId = npcId;
    }

    public int getSucceedRandom() {
        return this.succeedRandom;
    }

    public void setSucceedRandom(final int succeedRandom) {
        this.succeedRandom = succeedRandom;
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public void setMaxCount(final int maxCount) {
        this.maxCount = maxCount;
    }

    public int getShowWorld() {
        return this.showWorld;
    }

    public void setShowWorld(final int showworld) {
        this.showWorld = showworld;
    }

    public int getBigSuccessItemRandom() {
        return this.bigsuccessitemrandom;
    }

    public void setBigSuccessItemRandom(final int bigsuccessitemrandom) {
        this.bigsuccessitemrandom = bigsuccessitemrandom;
    }

    public int getCraftNameID() {
        return craft_nameid;
    }

    public void setCraftNameID(final int i) {
        craft_nameid = i;
    }

    public List<Integer> getBless() {
        return this.bless;
    }

    public void setBless(final List<Integer> bless) {
        this.bless = bless;
    }
}
