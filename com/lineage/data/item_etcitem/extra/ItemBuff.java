package com.lineage.data.item_etcitem.extra;

import com.lineage.data.event.ItemBuffSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemBuffTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 道具狀態系統
 */
public class ItemBuff extends ItemExecutor {
    /**
     *
     */
    private ItemBuff() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new ItemBuff();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (!ItemBuffSet.START) {
            pc.sendPackets(new S_ServerMessage("道具狀態系統未開啟。", 11));
            return;
        }
        if (ItemBuffTable.get().add(pc, item.getItemId(), 0)) {
            pc.getInventory().removeItem(item, 1L);
        }
    }
}
