package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Assassin extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Assassin.class);

    public static NpcExecutor get() {
        return new Npc_Assassin();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            boolean isTak = false;
            if (pc.getTempCharGfx() == 3634) {
                isTak = true;
            }
            if (!isTak) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin4"));
                return;
            }
            if (pc.isDarkelf()) {
                if (pc.getQuest().isEnd(DarkElfLv45_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));
                    return;
                }
                if (pc.getLevel() >= DarkElfLv45_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(DarkElfLv45_1.QUEST.get_id())) {
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin1"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isDarkelf()) {
            if (pc.getQuest().isEnd(DarkElfLv45_1.QUEST.get_id())) {
                return;
            }
            switch (pc.getQuest().get_step(DarkElfLv45_1.QUEST.get_id())) {
                case 1:
                    if (!cmd.equalsIgnoreCase("quest 18 assassin2")) {
                        break;
                    }
                    pc.getQuest().set_step(DarkElfLv45_1.QUEST.get_id(), 2);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin2"));
                    break;
                default:
                    isCloseList = true;
                    break;
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
 * com.lineage.data.npc.quest.Npc_Assassin JD-Core Version: 0.6.2
 */