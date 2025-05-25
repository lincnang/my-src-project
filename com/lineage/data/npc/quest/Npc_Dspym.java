package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv50_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Dspym extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Dspym.class);

    public static NpcExecutor get() {
        return new Npc_Dspym();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            npc.onTalkAction(pc);
            if (pc.isWizard()) {
                if (pc.getQuest().isEnd(WizardLv50_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym4"));
                    return;
                }
                if (pc.getLevel() >= WizardLv50_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(WizardLv50_1.QUEST.get_id())) {
                        case 0:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));
                            break;
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym1"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym3"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (npc.getMaster() != null) {
            return;
        }
        if (pc.isWizard()) {
            if (pc.getQuest().isEnd(WizardLv50_1.QUEST.get_id())) {
                return;
            }
            if (cmd.equalsIgnoreCase("quest 27 dspym2")) {
                pc.getQuest().set_step(WizardLv50_1.QUEST.get_id(), 2);
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym2"));
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
 * com.lineage.data.npc.quest.Npc_Dspym JD-Core Version: 0.6.2
 */