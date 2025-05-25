package com.lineage.data.npc.quest;

import com.lineage.data.event.ValakasRoom.ValakasRoomSystem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Eldnas extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Eldnas.class);

    public static NpcExecutor get() {
        return new Npc_Eldnas();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "eldnas2"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("a")) {
            if (pc.getLevel() >= 60) {
                if (pc.getInventory().consumeItem(80020, 1)) {
                    ValakasRoomSystem.getInstance().startReady(pc);
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "eldnas1"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "eldnas3"));
            }
        }
    }
}
