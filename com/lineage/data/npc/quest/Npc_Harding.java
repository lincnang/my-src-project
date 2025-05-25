package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Harding extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Harding.class);

    public static NpcExecutor get() {
        return new Npc_Harding();
    }

    public int type() {
        return 1;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.get_hardinR() != null) {
                if ((pc.get_hardinR().get_time() > 0) && (pc.get_hardinR().get_time() <= 180)) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_ep002"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_ep001"));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Harding JD-Core Version: 0.6.2
 */