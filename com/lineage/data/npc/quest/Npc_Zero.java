package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv15_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Zero extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Zero.class);

    public static NpcExecutor get() {
        return new Npc_Zero();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isCrown()) {
                if (pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero6"));
                } else if (pc.getLevel() >= CrownLv15_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(CrownLv15_1.QUEST.get_id())) {
                        case 0:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero1"));
                            QuestClass.get().startQuest(pc, CrownLv15_1.QUEST.get_id());
                            break;
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero1"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero5"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero6"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isCrown()) {
            if (cmd.equalsIgnoreCase("request cloak of red")) {
                switch (pc.getQuest().get_step(CrownLv15_1.QUEST.get_id())) {
                    case 0:
                        break;
                    case 1:
                        if (CreateNewItem.checkNewItem(pc, 40565, 1) < 1L) {
                            isCloseList = true;
                        } else {
                            CreateNewItem.createNewItem(pc, 40565, 1, 20065, 1);
                            pc.getQuest().set_step(CrownLv15_1.QUEST.get_id(), 2);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero5"));
                        }
                        break;
                    default:
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero5"));
                        break;
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
 * com.lineage.data.npc.quest.Npc_Zero JD-Core Version: 0.6.2
 */