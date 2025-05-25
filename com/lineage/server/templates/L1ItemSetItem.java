package com.lineage.server.templates;

public class L1ItemSetItem {
    private final int id;
    private final int amount;
    private final int enchant;

    public L1ItemSetItem(int id, int amount, int enchant) {
        this.id = id;
        this.amount = amount;
        this.enchant = enchant;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public int getEnchant() {
        return enchant;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1ItemSetItem JD-Core Version: 0.6.2
 */