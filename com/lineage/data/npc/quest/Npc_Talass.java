package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv30_1;
import com.lineage.data.quest.WizardLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Talass extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Talass.class);

    public static NpcExecutor get() {
        return new Npc_Talass();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isWizard()) {
                if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
                    return;
                }
                if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
                    if (pc.getLevel() >= WizardLv45_1.QUEST.get_questlevel()) {
                        if (!pc.getQuest().isStart(WizardLv45_1.QUEST.get_id())) {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassmq1"));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassmq2"));
                        }
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
                    }
                    return;
                }
                if (pc.getLevel() >= WizardLv30_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(WizardLv30_1.QUEST.get_id())) {
                        case 4:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassE1"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isWizard()) {
            if (cmd.equalsIgnoreCase("quest 16 talassE2")) {
                if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
                    return;
                }
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassE2"));
            } else if (cmd.equalsIgnoreCase("request crystal staff")) {
                if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
                    return;
                }
                int[] items = {40580, 40569};
                int[] counts = {1, 1};
                int[] gitems = {115};
                int[] gcounts = {1};
                if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                    isCloseList = true;
                } else {
                    CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                    pc.getQuest().set_end(WizardLv30_1.QUEST.get_id());
                    isCloseList = true;
                }
            } else if (cmd.equalsIgnoreCase("quest 18 talassmq2")) {
                if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
                    return;
                }
                QuestClass.get().startQuest(pc, WizardLv45_1.QUEST.get_id());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassmq2"));
            } else if (cmd.equalsIgnoreCase("request magic bag of talass")) {
                if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
                    return;
                }
                int[] items = {40536};
                int[] counts = {1};
                int[] gitems = {40599};
                int[] gcounts = {1};
                if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                    isCloseList = true;
                } else {
                    CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                    pc.getQuest().set_end(WizardLv45_1.QUEST.get_id());
                    isCloseList = true;
                }
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Talass JD-Core Version: 0.6.2
 */