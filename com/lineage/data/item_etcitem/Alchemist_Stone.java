package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.sql.Timestamp;

public class Alchemist_Stone extends ItemExecutor {
    public static ItemExecutor get() {
        return new Alchemist_Stone();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int item_id = 0;
        int count = 2;
        int k = (int) (Math.random() * 7.0D);
        switch (k) {
            case 1:
                item_id = 40024;
                break;
            case 2:
                item_id = 40023;
                break;
            case 3:
                item_id = 40022;
                count = 3;
                break;
            case 4:
                item_id = 40015;
                break;
            case 5:
                item_id = 40016;
                break;
            case 6:
                item_id = 40042;
                count = 1;
                break;
            default:
                item_id = 40068;
        }
        CreateNewItem.createNewItem(pc, item_id, count);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        item.setLastUsed(ts);
        pc.getInventory().updateItem(item, 32);
        pc.getInventory().saveItem(item, 32);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Alchemist_Stone JD-Core Version: 0.6.2
 */