package com.lineage.data.item_etcitem;

import com.lineage.config.ConfigRate;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.Random;

public class Summoning_Center extends ItemExecutor {
    public static ItemExecutor get() {
        return new Summoning_Center();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        int itemobj = data[0];
        L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
        if (item1 == null) {
            return;
        }
        Random _random = new Random();
        if (itemId == 41029) {
            int dantesId = item1.getItem().getItemId();
            if ((dantesId >= 41030) && (41034 >= dantesId)) {
                if (_random.nextInt(99) + 1 < ConfigRate.CREATE_CHANCE_DANTES) {
                    CreateNewItem.createNewItem(pc, dantesId + 1, 1L);
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
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Summoning_Center JD-Core Version: 0.6.2
 */