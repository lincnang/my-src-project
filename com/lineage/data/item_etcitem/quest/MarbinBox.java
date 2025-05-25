package com.lineage.data.item_etcitem.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;

public class MarbinBox extends ItemExecutor {
    //	41785	不完整的魔法晶球碎片
    //	41786	不完整的魔法晶球
    public static ItemExecutor get() {
        return new MarbinBox();
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
            CreateNewItem.createNewItem(pc, 41786, 1);
        } else {
            item.setChargeCount(maxchargeCount - 1);
            CreateNewItem.createNewItem(pc, 41785, 1);
        }
        pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);// 更新使用次數
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.quest.Bonusbox_1 JD-Core Version: 0.6.2
 */