package com.lineage.data.npc.quest2;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.Chapter02;
import com.lineage.data.quest.Chapter02R;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Chapter_2 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Chapter_2.class);

    public static NpcExecutor get() {
        return new Chapter_2();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id1"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("enter")) {
            if (isError(pc, npc)) {
                return;
            }
            startQuest(pc);
        }
        pc.sendPackets(new S_CloseList(pc.getId()));
    }

    private void startQuest(L1PcInstance pc) {
        try {
            int questid = Chapter02.QUEST.get_id();
            int showId = WorldQuest.get().nextId();
            int users = QuestMapTable.get().getTemplate(9101);
            if (users == -1) {
                users = 127;
            }
            L1Location loc = new L1Location(32798, 32803, 9101);
            L1Party party = pc.getParty();
            if (party != null) {
                int i = 0;
                for (final L1PcInstance otherPc : party.getMemberList()) {
                    if ((i <= users - 1) && (otherPc.getId() != party.getLeaderID()) && (otherPc.getMapId() == pc.getMapId())) {
                        WorldQuest.get().put(showId, 9101, questid, otherPc);
                        L1BuffUtil.cancelAbsoluteBarrier(otherPc);
                        L1Location new_loc = loc.randomLocation(5, true);
                        L1Teleport.teleport(otherPc, new_loc.getX(), new_loc.getY(), (short) 9101, pc.getHeading(), true);
                        L1PolyMorph.undoPoly(otherPc);
                        QuestClass.get().startQuest(otherPc, Chapter02.QUEST.get_id());
                    }
                    i++;
                }
            } else {
                return;
            }
            L1QuestUser quest = WorldQuest.get().put(showId, 9101, questid, pc);
            if (quest == null) {
                _log.error("");
                return;
            }
            quest.set_info(false);
            Integer time = QuestMapTable.get().getTime(9101);
            if (time != null) {
                quest.set_time(time.intValue());
            }
            L1NpcInstance door = L1SpawnUtil.spawn(97108, new L1Location(32799, 32806, 9101), 4, showId);
            door.setStatus(32);
            L1NpcInstance npc1 = L1SpawnUtil.spawn(97102, new L1Location(32792, 32809, 9101), 4, showId);
            L1NpcInstance npc2 = L1SpawnUtil.spawn(97102, new L1Location(32803, 32809, 9101), 4, showId);
            L1BuffUtil.cancelAbsoluteBarrier(pc);
            L1Location new_loc = loc.randomLocation(5, true);
            L1Teleport.teleport(pc, new_loc.getX(), new_loc.getY(), (short) 9101, pc.getHeading(), true);
            L1PolyMorph.undoPoly(pc);
            Chapter02R chapter02R = new Chapter02R(quest, party, door, npc1, npc2);
            quest.set_orimR(chapter02R);
            chapter02R.startR();
            QuestClass.get().startQuest(pc, Chapter02.QUEST.get_id());
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private final boolean isError(L1PcInstance pc, L1NpcInstance npc) {
        if (pc.isGm()) {
            return false;
        }
        L1Party party = pc.getParty();
        if (party == null) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id1_3"));
            return true;
        }
        if (!party.isLeader(pc)) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id1_2"));
            return true;
        }
        if ((party.getNumOfMembers() < 3) || (party.getNumOfMembers() > 5)) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id1_1"));
            return true;
        }
        return false;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest2.Chapter_2 JD-Core Version: 0.6.2
 */