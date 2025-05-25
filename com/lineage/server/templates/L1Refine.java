package com.lineage.server.templates;

/**
 * 火神精煉
 *
 */
public class L1Refine {
    private int itemId;
    private int enchantLevel;
    private int newItemId;
    private int newItemCount;
    private int plusItemId;
    private int plusItemCount;
    private int plusItemChance;

    public L1Refine(int itemId, int enchantLevel, int newItemId, int newItemCount, int plusItemId, int plusItemCount, int plusItemChance) {
        this.itemId = itemId;
        this.enchantLevel = enchantLevel;
        this.newItemId = newItemId;
        this.newItemCount = newItemCount;
        this.plusItemId = plusItemId;
        this.plusItemCount = plusItemCount;
        this.plusItemChance = plusItemChance;
    }

    public int getItemId() {
        return this.itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getEnchantLevel() {
        return this.enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
    }

    public int getNewItemId() {
        return this.newItemId;
    }

    public void setNewItemId(int newItemId) {
        this.newItemId = newItemId;
    }

    public int getNewItemCount() {
        return this.newItemCount;
    }

    public void setNewItemCount(int newItemCount) {
        this.newItemCount = newItemCount;
    }

    public int getPlusItemId() {
        return this.plusItemId;
    }

    public void setPlusItemId(int plusItemId) {
        this.plusItemId = plusItemId;
    }

    public int getPlusItemCount() {
        return this.plusItemCount;
    }

    public void setPlusItemCount(int plusItemCount) {
        this.plusItemCount = plusItemCount;
    }

    public int getPlusItemChance() {
        return this.plusItemChance;
    }

    public void setPlusItemChance(int plusItemChance) {
        this.plusItemChance = plusItemChance;
    }
}
