package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Npc_Bunny extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_Bunny();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nt_14chu"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        int[] olditem = {41413, 41414};
        int newWeapon = 0;
        boolean success = false;
        if (cmd.equalsIgnoreCase("a")) {
            newWeapon = 410176;
            for (int j : olditem) {
                if (pc.getInventory().checkItem(j, 1000L)) {
                    pc.getInventory().consumeItem(j, 1000L);
                    final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getNameId()));
                    success = true;
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            }
        } else if (cmd.equalsIgnoreCase("b")) {
            newWeapon = 410177;
            for (int j : olditem) {
                if (pc.getInventory().checkItem(j, 1000L)) {
                    pc.getInventory().consumeItem(j, 1000L);
                    final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getNameId()));
                    success = true;
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            }
        } else if (cmd.equalsIgnoreCase("c")) {
            newWeapon = 410175;
            for (int j : olditem) {
                if (pc.getInventory().checkItem(j, 1000L)) {
                    pc.getInventory().consumeItem(j, 1000L);
                    final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getNameId()));
                    success = true;
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            }
        } else if (cmd.equalsIgnoreCase("d")) {
            newWeapon = 410178;
            for (int j : olditem) {
                if (pc.getInventory().checkItem(j, 1000L)) {
                    pc.getInventory().consumeItem(j, 1000L);
                    final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getNameId()));
                    success = true;
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            }
        } else if (cmd.equalsIgnoreCase("e")) {
            newWeapon = 410179;
            for (int j : olditem) {
                if (pc.getInventory().checkItem(j, 1000L)) {
                    pc.getInventory().consumeItem(j, 1000L);
                    final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getNameId()));
                    success = true;
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            }
        } else if (cmd.equalsIgnoreCase("f")) {
            newWeapon = 410180;
            for (int j : olditem) {
                if (pc.getInventory().checkItem(j, 1000L)) {
                    pc.getInventory().consumeItem(j, 1000L);
                    final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getNameId()));
                    success = true;
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            }
        }
        if (!success) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nt_14chu2"));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.Npc_Recycling JD-Core Version: 0.6.2
 */