package com.lineage.server.templates;

import java.util.ArrayList;

/**
 * 樂天堂火神製作(DB化)
 */
public class L1CraftItem {
    private int _itemId;
    private long _count;
    private int enchantLevel;
    private int bless;
    private int random;
    private int windowLattice;
    private ArrayList<L1CraftItem> substituteList;

    public L1CraftItem(final int _itemId, final long _count, final int enchantLevel, final int bless, final int windowLattice) {
        this._itemId = _itemId;
        this._count = _count;
        this.enchantLevel = enchantLevel;
        this.bless = bless;
    }

    public L1CraftItem(final int _itemId, final long _count, final int enchantLevel, final int bless, final int random, final int windowLattice) {
        this._itemId = _itemId;
        this._count = _count;
        this.enchantLevel = enchantLevel;
        this.bless = bless;
        this.random = random;
        this.windowLattice = windowLattice;
    }

    public L1CraftItem(final int _itemId, final long _count) {
        this._itemId = _itemId;
        this._count = _count;
    }

    public int getRandom() {
        return this.random;
    }

    public void setRandom(final int random) {
        this.random = random;
    }

    public int getEnchantLevel() {
        return this.enchantLevel;
    }

    public void setEnchantLevel(final int enchantLevel) {
        this.enchantLevel = enchantLevel;
    }

    public int getBless() {
        return this.bless;
    }

    public void setBless(final int bless) {
        this.bless = bless;
    }

    public int getItemId() {
        return this._itemId;
    }

    public long getCount() {
        return this._count;
    }

    public int getWindowLattice() {
        return this.windowLattice;
    }

    public void setWindowLattice(final int windowLattice) {
        this.windowLattice = windowLattice;
    }

    public ArrayList<L1CraftItem> getSubstituteList() {
        return this.substituteList;
    }

    public void setSubstituteList(final ArrayList<L1CraftItem> substituteList) {
        this.substituteList = substituteList;
    }
}
