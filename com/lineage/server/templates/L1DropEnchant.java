package com.lineage.server.templates;

/**
 * 掉落道具強化值虛擬接口
 *
 * @author kenny
 */
public class L1DropEnchant {
    int _itemId;
    int[] _enchants;

    public L1DropEnchant(int itemId, int[] enchants) {
        _enchants = enchants;
        _itemId = itemId;
    }

    /**
     * 取回物品ID
     *
     */
    public int getItemid() {
        return _itemId;
    }

    /**
     * 取回強化值陣列
     *
     */
    public int[] getEnchants() {
        return _enchants;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1DropMap JD-Core Version: 0.6.2
 */