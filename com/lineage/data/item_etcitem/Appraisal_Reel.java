package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_IdentifyDesc;

public class Appraisal_Reel extends ItemExecutor {
    public static ItemExecutor get() {
        return new Appraisal_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemobj = data[0];
        L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
        if (item1 == null) {
            return;
        }
        if (!item1.isIdentified()) {
            item1.setIdentified(true);
            pc.getInventory().updateItem(item1, 2);
        }
        pc.sendPackets(new S_IdentifyDesc(item1));
        pc.getInventory().removeItem(item, 1L);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Appraisal_Reel JD-Core Version: 0.6.2
 */