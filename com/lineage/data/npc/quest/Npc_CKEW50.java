package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_CKEW50 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_CKEW50.class);

    public static NpcExecutor get() {
        return new Npc_CKEW50();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (!pc.isInParty()) {
                return;
            }
            int i = 0;
            for (final L1PcInstance otherPc : pc.getParty().getMemberList()) {
                if (otherPc.isCrown()) {
                    i++;
                } else if (otherPc.isKnight()) {
                    i += 2;
                } else if (otherPc.isElf()) {
                    i += 4;
                } else if (otherPc.isWizard()) {
                    i += 8;
                }
            }
            if (i != 15) {
                return;
            }
            if (pc.isCrown()) {
                if ((pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) && (npc.getNpcId() == 80135)) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "50quest_p"));
                } else {
                    QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
                }
            } else if (pc.isKnight()) {
                if ((pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) && (npc.getNpcId() == 80136)) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "50quest_k"));
                } else {
                    QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
                }
            } else if (pc.isElf()) {
                if ((pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) && (npc.getNpcId() == 80133)) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "50quest_e"));
                } else {
                    QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
                }
            } else if (pc.isWizard()) {
                if ((pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) && (npc.getNpcId() == 80134)) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "50quest_w"));
                } else {
                    QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
                }
            }
            // else if (!pc.isDarkelf())
            // {
            // if (!pc.isDragonKnight())
            // {
            // pc.isIllusionist();
            // }
            // }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isCrown()) {
            if ((pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) && (npc.getNpcId() == 80135) && (cmd.equalsIgnoreCase("ent"))) {
                staraQuest(pc);
            }
            isCloseList = true;
        } else if (pc.isKnight()) {
            if ((pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) && (npc.getNpcId() == 80136) && (cmd.equalsIgnoreCase("ent"))) {
                staraQuest(pc);
            }
            isCloseList = true;
        } else if (pc.isElf()) {
            if ((pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) && (npc.getNpcId() == 80133) && (cmd.equalsIgnoreCase("ent"))) {
                staraQuest(pc);
            }
            isCloseList = true;
        } else if (pc.isWizard()) {
            if ((pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) && (npc.getNpcId() == 80134) && (cmd.equalsIgnoreCase("ent"))) {
                staraQuest(pc);
            }
            isCloseList = true;
        } else if (pc.isDarkelf()) {
            isCloseList = true;
        } else if (pc.isDragonKnight()) {
            isCloseList = true;
        } else if (pc.isIllusionist()) {
            isCloseList = true;
        } else {
            isCloseList = true;
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    private void staraQuest(L1PcInstance pc) {
        try {
            if (!pc.isInParty()) {
                return;
            }
            int questid = CKEWLv50_1.QUEST.get_id();
            int showId = -1;
            int users = QuestMapTable.get().getTemplate(2000);
            if (users == -1) {
                users = 127;
            }
            int i = 0;
            for (final L1PcInstance otherPc : pc.getParty().getMemberList()) {
                if (otherPc.get_showId() != -1) {
                    showId = otherPc.get_showId();
                }
                if (otherPc.isCrown()) {
                    i++;
                } else if (otherPc.isKnight()) {
                    i += 2;
                } else if (otherPc.isElf()) {
                    i += 4;
                } else if (otherPc.isWizard()) {
                    i += 8;
                }
            }
            if (i != 15) {
                return;
            }
            if (showId == -1) {
                showId = WorldQuest.get().nextId();
            }
            L1QuestUser quest = WorldQuest.get().put(showId, 2000, questid, pc);
            if (quest == null) {
                _log.error("副本設置過程發生異常!!");
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            quest.set_info(false);
            quest.set_outStop(true);
            Integer time = QuestMapTable.get().getTime(2000);
            if (time != null) {
                quest.set_time(time.intValue());
            }
            if (pc.isCrown()) {
                L1Teleport.teleport(pc, 32720, 32900, (short) 2000, 2, true);
            } else if (pc.isKnight()) {
                L1Teleport.teleport(pc, 32721, 32853, (short) 2000, 3, true);
            } else if (pc.isElf()) {
                L1Teleport.teleport(pc, 32725, 32940, (short) 2000, 1, true);
            } else if (pc.isWizard()) {
                L1Teleport.teleport(pc, 32810, 32941, (short) 2000, 7, true);
            }
            pc.getQuest().set_step(CKEWLv50_1.QUEST.get_id(), 2);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_CKEW50 JD-Core Version: 0.6.2
 */