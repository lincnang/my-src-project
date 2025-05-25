package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Cooking;

public class Food3 extends ItemExecutor {
    public static ItemExecutor get() {
        return new Food3();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        L1Cooking.useCookingItem(pc, item);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Food3 JD-Core Version: 0.6.2
 */