package com.lineage.server.templates;

/**
 * 自選寶箱選項資料
 */
public class L1ChooseBoxOption {
    private final int itemId;
    private final int enchant;
    private final int count;
    private final int bless;

    public L1ChooseBoxOption(int itemId, int enchant, int count, int bless) {
        this.itemId = itemId;
        this.enchant = enchant;
        this.count = count;
        this.bless = bless;
    }

    public int getItemId() {
        return itemId;
    }

    public int getEnchant() {
        return enchant;
    }

    public int getCount() {
        return count;
    }

    public int getBless() {
        return bless;
    }
}


