package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;

public class Transmission_Reel_Seal extends ItemExecutor {
    public static ItemExecutor get() {
        return new Transmission_Reel_Seal();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        int itemId = item.getItemId();
        pc.getInventory().removeItem(item, 1L);
        L1ItemInstance item1 = pc.getInventory().storeItem(itemId + 9, 1L);
        if (item1 != null) {
            pc.sendPackets(new S_ServerMessage(403, item1.getLogName()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.teleport.Transmission_Reel_Seal JD-Core
 * Version: 0.6.2
 */