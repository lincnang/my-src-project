package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemIntegrationTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class ItemIntegration extends ItemExecutor {
    /**
     * 物品升級系統
     *
     * @author
     */
    public static ItemExecutor get() {
        return new ItemIntegration();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgitem = pc.getInventory().getItem(targObjId);
        if (tgitem.isEquipped()) {
            pc.sendPackets(new S_SystemMessage("請先卸除該裝備在升級。"));
            return;
        }
        if (ItemIntegrationTable.getSetList() != null) {
            ItemIntegrationTable.forItemIntegration(pc, item, tgitem);
        }
    }
}