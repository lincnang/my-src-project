package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.sql.Timestamp;

public class Cursed_Blood extends ItemExecutor {
    public static ItemExecutor get() {
        return new Cursed_Blood();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int item_id = 0;
        short k = (short) (int) (Math.random() * 6.0D);
        switch (k) {
            case 1:
                item_id = 40031;
                break;
            case 2:
                item_id = 40006;
                break;
            case 3:
                item_id = 40008;
                break;
            case 4:
                item_id = 40009;
                break;
            case 5:
                item_id = 40524;
                break;
            default:
                item_id = 40007;
        }
        CreateNewItem.createNewItem(pc, item_id, 1L);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        item.setLastUsed(ts);
        pc.getInventory().updateItem(item, 32);
        pc.getInventory().saveItem(item, 32);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Cursed_Blood JD-Core Version: 0.6.2
 */