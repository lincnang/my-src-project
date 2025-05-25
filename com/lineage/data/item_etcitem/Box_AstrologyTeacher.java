package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.sql.Timestamp;

public class Box_AstrologyTeacher extends ItemExecutor {
    public static ItemExecutor get() {
        return new Box_AstrologyTeacher();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        // pc.getInventory().removeItem(item, 1L);
        CreateNewItem.createNewItem(pc, 41313, 1L);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        item.setLastUsed(ts);
        pc.getInventory().updateItem(item, 32);
        pc.getInventory().saveItem(item, 32);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Box_AstrologyTeacher JD-Core Version: 0.6.2
 */