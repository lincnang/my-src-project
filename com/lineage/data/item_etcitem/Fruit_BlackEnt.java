package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Fruit_BlackEnt extends ItemExecutor {
    public static ItemExecutor get() {
        return new Fruit_BlackEnt();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        String itenName = item.getLogName();
        if (pc.castleWarResult()) {
            pc.sendPackets(new S_ServerMessage(403, itenName));
        } else if (pc.getMapId() == 303) {
            pc.sendPackets(new S_ServerMessage(403, itenName));
        } else {
            pc.getInventory().removeItem(item, 1L);
            pc.death(null);
            CreateNewItem.createNewItem(pc, 20299, 1L);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Fruit_BlackEnt JD-Core Version: 0.6.2
 */