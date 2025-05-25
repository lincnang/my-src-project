package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class IncantationBag extends ItemExecutor {
    public static ItemExecutor get() {
        return new IncantationBag();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        pc.getInventory().removeItem(item, 1L);
        int k = (int) (Math.random() * 7.0D);
        int item_id = 0;
        int count = 1;
        switch (k) {
            case 0:
                item_id = 40020;
                count = 5;
                break;
            case 1:
                item_id = 40021;
                count = 3;
                break;
            case 2:
                item_id = 40014;
                count = 3;
                break;
            case 3:
                item_id = 40016;
                count = 3;
                break;
            case 4:
                item_id = 40015;
                count = 3;
                break;
            case 5:
                item_id = 40008;
                count = 1;
                break;
            case 6:
                item_id = 40007;
                count = 1;
        }
        if (item_id != 0) {
            CreateNewItem.createNewItem(pc, item_id, count);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Box_BravePumpkin JD-Core Version: 0.6.2
 */