package com.lineage.data.item_etcitem;

import com.lineage.config.ConfigRate;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.Random;

public class Purification_Potion extends ItemExecutor {
    public static ItemExecutor get() {
        return new Purification_Potion();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemobj = data[0];
        L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
        if (item1 == null) {
            return;
        }
        Random random = new Random();
        int earingId = item1.getItem().getItemId();
        if ((earingId >= 40987) && (40989 >= earingId)) {
            if (random.nextInt(100) < ConfigRate.CREATE_CHANCE_RECOLLECTION) {
                CreateNewItem.createNewItem(pc, earingId + 186, 1L);
            } else {
                pc.sendPackets(new S_ServerMessage(158, item1.getName()));
            }
            pc.getInventory().removeItem(item1, 1L);
            pc.getInventory().removeItem(item, 1L);
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Purification_Potion JD-Core Version: 0.6.2
 */