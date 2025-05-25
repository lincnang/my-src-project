package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv15_1;
import com.lineage.data.quest.CrownLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Ant extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Ant.class);

    public static NpcExecutor get() {
        return new Npc_Ant();
    }

    public int type() {
        return 1;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            boolean isTak = false;
            if (pc.getTempCharGfx() == 1037) {
                isTak = true;
            }
            if (pc.getTempCharGfx() == 1039) {
                isTak = true;
            }
            if (!isTak) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant2"));
                return;
            }
            if (pc.isCrown()) {
                if (!pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));
                } else if (pc.getQuest().isEnd(CrownLv30_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));
                } else if (pc.getLevel() >= CrownLv30_1.QUEST.get_questlevel()) {
                    if (!pc.getQuest().isStart(CrownLv30_1.QUEST.get_id())) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant1"));
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ant3"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Ant JD-Core Version: 0.6.2
 */