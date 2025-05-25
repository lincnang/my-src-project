package com.lineage.data.npc.teleport;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;

public class Npc_KaSiOPL extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_KaSiOPL();
    }

    public int type() {
        return 1;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        L1Teleport.teleport(pc, 32641, 32886, (short) 7783, 5, true);
    }
}