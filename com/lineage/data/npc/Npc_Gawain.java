package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

public class Npc_Gawain extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_Gawain();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gawain01"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        int[] oldweapon = {272, 273};
        int newweapon = 410174;
        boolean success = false;
        if (cmd.equalsIgnoreCase("A")) {
            for (int j : oldweapon) {
                if (pc.getInventory().checkEnchantItem(j, 8, 1L) && (pc.getInventory().checkItem(40308, 5000000L))) {
                    pc.getInventory().consumeItem(j, 1L);
                    pc.getInventory().consumeItem(40308, 5000000L);
                    final L1ItemInstance item = ItemTable.get().createItem(newweapon);
                    item.setEnchantLevel(7);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    success = true;
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gawain05"));
                    break;
                }
            }
        } else if (cmd.equalsIgnoreCase("B")) {
            for (int j : oldweapon) {
                if (pc.getInventory().checkEnchantItem(j, 9, 1L) && (pc.getInventory().checkItem(40308, 10000000L))) {
                    pc.getInventory().consumeItem(j, 1L);
                    pc.getInventory().consumeItem(40308, 10000000L);
                    final L1ItemInstance item = ItemTable.get().createItem(newweapon);
                    item.setEnchantLevel(8);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    success = true;
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gawain05"));
                    break;
                }
            }
        }
        if (!success) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gawain04"));
        }
    }
}
