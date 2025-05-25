package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Npc_Fishing_1 extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_Fishing_1();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fk_in_1"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equals("a") && pc.getLevel() >= 35) {
            L1PolyMorph.undoPoly(pc);
            L1Teleport.teleport(pc, 32794, 32795, (short) 5490, 6, true);
        } else {
            pc.sendPackets(new S_ServerMessage("等級35以上才能進入釣魚池"));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Fishing_1 JD-Core Version: 0.6.2
 */