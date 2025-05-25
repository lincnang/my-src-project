package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class Lavor_Pouch extends ItemExecutor {
    public static ItemExecutor get() {
        return new Lavor_Pouch();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int item_id = 0;
        int k = (int) (Math.random() * 5.0D);
        switch (k) {
            case 0:
                item_id = 40090;
                break;
            case 1:
                item_id = 40091;
                break;
            case 2:
                item_id = 40092;
                break;
            case 3:
                item_id = 40093;
                break;
            default:
                item_id = 40094;
        }
        pc.getInventory().removeItem(item, 1L);
        CreateNewItem.createNewItem(pc, item_id, 1L);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Lavor_Pouch JD-Core Version: 0.6.2
 */