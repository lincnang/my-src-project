package com.lineage.data.npc.quest;

import com.lineage.data.event.centraltempl.L1CentralTemple;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_CentralTemple extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_CentralTemple.class);

    public static NpcExecutor get() {
        return new Npc_CentralTemple();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ctid"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("enter")) {
            L1CentralTemple.get().centralTemplStart(pc);
        }
    }
}
