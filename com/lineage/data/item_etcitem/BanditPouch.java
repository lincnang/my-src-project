package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class BanditPouch extends ItemExecutor {
    public static ItemExecutor get() {
        return new BanditPouch();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        pc.getInventory().removeItem(item, 1L);
        int k = (int) (Math.random() * 300.0D);
        int count = 300 + k;
        CreateNewItem.createNewItem(pc, 40308, count);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.BanditPouch JD-Core Version: 0.6.2
 */