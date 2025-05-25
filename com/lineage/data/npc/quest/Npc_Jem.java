package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv15_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Jem extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Jem.class);

    public static NpcExecutor get() {
        return new Npc_Jem();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isWizard()) {
                if (pc.getQuest().isEnd(WizardLv15_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));
                } else if (pc.getLevel() >= WizardLv15_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(WizardLv15_1.QUEST.get_id())) {
                        case 0:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem1"));
                            QuestClass.get().startQuest(pc, WizardLv15_1.QUEST.get_id());
                            break;
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem1"));
                            break;
                        case 2:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem4"));
                        default:
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem2"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem7"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isWizard()) {
            if (pc.getQuest().isEnd(WizardLv15_1.QUEST.get_id())) {
                return;
            }
            if (cmd.equalsIgnoreCase("request cursed spellbook")) {
                if (pc.getQuest().isStart(WizardLv15_1.QUEST.get_id())) {
                    if (CreateNewItem.checkNewItem(pc, new int[]{40539, 40538}, new int[]{1, 1}) < 1L) {
                        pc.sendPackets(new S_CloseList(pc.getId()));
                    } else {
                        CreateNewItem.createNewItem(pc, new int[]{40539, 40538}, new int[]{1, 1}, new int[]{40591}, 1L, new int[]{1});
                        pc.getQuest().set_step(WizardLv15_1.QUEST.get_id(), 2);
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem4"));
                    }
                }
            } else if (cmd.equalsIgnoreCase("request book of magical powers")) {
                if (CreateNewItem.checkNewItem(pc, new int[]{40605, 40591}, new int[]{1, 1}) < 1L) {
                    pc.sendPackets(new S_CloseList(pc.getId()));
                } else {
                    CreateNewItem.createNewItem(pc, new int[]{40605, 40591}, new int[]{1, 1}, new int[]{20226}, 1L, new int[]{1});
                    QuestClass.get().endQuest(pc, WizardLv15_1.QUEST.get_id());
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));
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
 * com.lineage.data.npc.quest.Npc_Jem JD-Core Version: 0.6.2
 */