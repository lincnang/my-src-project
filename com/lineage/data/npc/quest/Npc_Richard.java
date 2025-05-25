package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Richard extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Richard.class);

    public static NpcExecutor get() {
        return new Npc_Richard();
    }

    public int type() {
        return 1;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isCrown()) {
                if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));
                    return;
                }
                if (pc.getLevel() >= CrownLv45_1.QUEST.get_questlevel()) {
                    if (!pc.getQuest().isStart(CrownLv45_1.QUEST.get_id())) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));
                    } else if (pc.getInventory().checkItem(40586)) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard4"));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard1"));
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "richard3"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Richard JD-Core Version: 0.6.2
 */