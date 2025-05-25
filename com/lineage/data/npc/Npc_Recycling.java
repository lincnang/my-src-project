package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ShopBuyListAll;

public class Npc_Recycling extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_Recycling();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tzmerchant"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("sell")) {
            pc.sendPackets(new S_ShopBuyListAll(pc, npc));
        }
    }
}
