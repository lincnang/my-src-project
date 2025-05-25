package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class RuneEracePotion extends ItemExecutor {
    public static ItemExecutor get() {
        return new RuneEracePotion();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemobj = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(itemobj);
        if (tgItem == null) {
            return;
        }
        if (tgItem.getItemId() >= 600000 && tgItem.getItemId() <= 600039) {
            pc.getInventory().removeItem(item, 1);
            pc.getInventory().removeItem(tgItem, 1);
            CreateNewItem.createNewItem(pc, 49928, 1);
        } else if (tgItem.getItemId() >= 600041 && tgItem.getItemId() <= 600080) {
            pc.getInventory().removeItem(item, 1);
            pc.getInventory().removeItem(tgItem, 1);
            CreateNewItem.createNewItem(pc, 149928, 1);
        } else {
            pc.sendPackets(new S_ServerMessage(79));
            // \f1何起。
        }
    }
}