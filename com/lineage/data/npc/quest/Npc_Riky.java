package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv15_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Riky extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Riky.class);

    public static NpcExecutor get() {
        return new Npc_Riky();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isKnight()) {
                if (pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "rikycg"));
                    return;
                }
                if (pc.getLevel() >= KnightLv15_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(KnightLv15_1.QUEST.get_id())) {
                        case 0:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky1"));
                            QuestClass.get().startQuest(pc, KnightLv15_1.QUEST.get_id());
                            break;
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky3"));
                            break;
                        case 2:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky5"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "rikycg"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky6"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isKnight()) {
            if (pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
                return;
            }
            if (cmd.equalsIgnoreCase("request hood of knight")) {
                if (pc.getQuest().isStart(KnightLv15_1.QUEST.get_id())) {
                    if (CreateNewItem.checkNewItem(pc, 40608, 1) < 1L) {
                        isCloseList = true;
                    } else {
                        CreateNewItem.createNewItem(pc, 40608, 1, 20005, 1);
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky4"));
                        pc.getQuest().set_step(KnightLv15_1.QUEST.get_id(), 2);
                    }
                } else {
                    isCloseList = true;
                }
            }
        } else {
            isCloseList = true;
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Riky JD-Core Version: 0.6.2
 */