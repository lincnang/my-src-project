package com.lineage.data.item_etcitem.shop;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class SealArtisan extends ItemExecutor {
    public static ItemExecutor get() {
        return new SealArtisan();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.getInventory().checkItem(241115)) {
            pc.sendPackets(new S_SystemMessage("已經持有工匠之魂，無法再獲得。"));
            return;
        }
        CreateNewItem.createNewItem(pc, 241115, 1L);
        pc.getInventory().removeItem(item, 1L);
    }
}