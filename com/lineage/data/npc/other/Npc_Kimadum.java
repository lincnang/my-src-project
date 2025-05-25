package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Kimadum extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Kimadum.class);

    public static NpcExecutor get() {
        return new Npc_Kimadum();
    }

    public int type() {
        return 1;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getQuest().isStart(DarkElfLv50_1.QUEST.get_id())) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimadums"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimadum"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Kimadum JD-Core Version: 0.6.2
 */