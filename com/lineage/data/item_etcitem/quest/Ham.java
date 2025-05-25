package com.lineage.data.item_etcitem.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;

//漢的袋子
public class Ham extends ItemExecutor {
    public static ItemExecutor get() {
        return new Ham();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        int maxchargeCount = item.getChargeCount();
        if (maxchargeCount - 1 <= 0) {
            item.setChargeCount(0);
            pc.getInventory().removeItem(item, 1);
            CreateNewItem.createNewItem(pc, 80020, 1);
        } else {
            item.setChargeCount(maxchargeCount - 1);
            CreateNewItem.createNewItem(pc, 80020, 1);
        }
        pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);// 更新使用次數
    }
}
