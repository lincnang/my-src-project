package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv15_1;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Mark extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Mark.class);

    public static NpcExecutor get() {
        return new Npc_Mark();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isKnight()) {
                if (!pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));
                    return;
                }
                if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "markcg"));
                    return;
                }
                if (pc.getLevel() >= KnightLv30_1.QUEST.get_questlevel()) {
                    if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark1"));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark2"));
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isKnight()) {
            if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
                return;
            }
            if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
                if (cmd.equalsIgnoreCase("quest 13 mark2")) {
                    QuestClass.get().startQuest(pc, KnightLv30_1.QUEST.get_id());
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark2"));
                }
            } else {
                isCloseList = true;
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Mark JD-Core Version: 0.6.2
 */