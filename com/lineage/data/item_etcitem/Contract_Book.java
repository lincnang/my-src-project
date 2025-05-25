package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Contract_Book extends ItemExecutor {
    public static ItemExecutor get() {
        return new Contract_Book();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        if (itemId == 41130) {
            if ((pc.getQuest().get_step(36) == 255) || (pc.getInventory().checkItem(41131, 1L))) {
                pc.sendPackets(new S_ServerMessage(79));
            } else {
                CreateNewItem.createNewItem(pc, 41131, 1L);
            }
        } else if ((pc.getQuest().get_step(37) == 255) || (pc.getInventory().checkItem(41122, 1L))) {
            pc.sendPackets(new S_ServerMessage(79));
        } else {
            CreateNewItem.createNewItem(pc, 41122, 1L);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Contract_Book JD-Core Version: 0.6.2
 */