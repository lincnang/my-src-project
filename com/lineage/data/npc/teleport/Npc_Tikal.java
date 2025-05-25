package com.lineage.data.npc.teleport;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;

public class Npc_Tikal extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_Tikal();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tikalgate1"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equals("e")) {
            L1ItemInstance item = pc.getInventory().checkItemX(49273, 1L);
            if (item != null) {
                pc.getInventory().removeItem(item, 1L);
                L1Teleport.teleport(pc, 32732, 32862, (short) 784, 5, true);
            } else {
                L1Item itemtmp = ItemTable.get().getTemplate(49273);
                pc.sendPackets(new S_ServerMessage(337, itemtmp.getNameId()));
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.teleport.Npc_Tikal JD-Core Version: 0.6.2
 */