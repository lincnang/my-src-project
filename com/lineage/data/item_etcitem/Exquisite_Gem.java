package com.lineage.data.item_etcitem;

import com.lineage.config.ConfigRate;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.Random;

public class Exquisite_Gem extends ItemExecutor {
    public static ItemExecutor get() {
        return new Exquisite_Gem();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        int itemobj = data[0];
        L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
        if (item1 == null) {
            return;
        }
        Random _random = new Random();
        int earingId = item1.getItem().getItemId();
        int earinglevel = 0;
        if ((earingId >= 41161) && (41172 >= earingId)) {
            if (earingId == itemId + 230) {
                if (_random.nextInt(99) + 1 < ConfigRate.CREATE_CHANCE_PROCESSING) {
                    if (earingId == 41161) {
                        earinglevel = 21014;
                    } else if (earingId == 41162) {
                        earinglevel = 21006;
                    } else if (earingId == 41163) {
                        earinglevel = 21007;
                    } else if (earingId == 41164) {
                        earinglevel = 21015;
                    } else if (earingId == 41165) {
                        earinglevel = 21009;
                    } else if (earingId == 41166) {
                        earinglevel = 21008;
                    } else if (earingId == 41167) {
                        earinglevel = 21016;
                    } else if (earingId == 41168) {
                        earinglevel = 21012;
                    } else if (earingId == 41169) {
                        earinglevel = 21010;
                    } else if (earingId == 41170) {
                        earinglevel = 21017;
                    } else if (earingId == 41171) {
                        earinglevel = 21013;
                    } else if (earingId == 41172) {
                        earinglevel = 21011;
                    }
                    CreateNewItem.createNewItem(pc, earinglevel, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(158, item1.getName()));
                }
                pc.getInventory().removeItem(item1, 1L);
                pc.getInventory().removeItem(item, 1L);
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Exquisite_Gem JD-Core Version: 0.6.2
 */