package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Jimu extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Jimu.class);

    public static NpcExecutor get() {
        return new Npc_Jimu();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isKnight()) {
                if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));
                    return;
                }
                if (pc.getLevel() >= KnightLv45_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(KnightLv45_1.QUEST.get_id())) {
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk1"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isKnight()) {
            if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
                return;
            }
            if (cmd.equalsIgnoreCase("quest 21 jimuk2")) {
                switch (pc.getQuest().get_step(KnightLv45_1.QUEST.get_id())) {
                    case 1:
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk2"));
                        pc.getQuest().set_step(KnightLv45_1.QUEST.get_id(), 2);
                        break;
                    default:
                        isCloseList = true;
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
 * com.lineage.data.npc.quest.Npc_Jimu JD-Core Version: 0.6.2
 */