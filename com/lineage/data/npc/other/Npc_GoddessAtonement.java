package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Npc_GoddessAtonement extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_GoddessAtonement();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "restore1pk"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("pk")) {
            if (pc.getLawful() < 30000) {
                pc.sendPackets(new S_ServerMessage(559));
            } else if (pc.get_PKcount() < 5) {
                pc.sendPackets(new S_ServerMessage(560));
            } else if (pc.getInventory().consumeItem(40308, 700000L)) {
                pc.set_PKcount(pc.get_PKcount() - 5);
                pc.sendPackets(new S_ServerMessage(561, String.valueOf(pc.get_PKcount())));
            } else {
                pc.sendPackets(new S_ServerMessage(189));
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_GoddessAtonement JD-Core Version: 0.6.2
 */