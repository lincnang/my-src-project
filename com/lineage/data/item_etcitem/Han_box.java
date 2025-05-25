package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.sql.Timestamp;

public class Han_box extends ItemExecutor {
    public static ItemExecutor get() {
        return new Han_box();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int item_id = 80020;
        int count = 1;
        CreateNewItem.createNewItem(pc, item_id, count);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        item.setLastUsed(ts);
        pc.getInventory().updateItem(item, 32);
        pc.getInventory().saveItem(item, 32);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Unlimited_Quiver JD-Core Version: 0.6.2
 */