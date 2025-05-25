package com.lineage.data.item_etcitem.quest;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;

public class DK45_Teleport extends ItemExecutor {
    public static ItemExecutor get() {
        return new DK45_Teleport();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.isDragonKnight()) {
            pc.getInventory().removeItem(item, 1L);
            L1Teleport.teleport(pc, 32839, 32860, (short) 1000, 2, true);
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.quest.DK45_Teleport JD-Core Version: 0.6.2
 */