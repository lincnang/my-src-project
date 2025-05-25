package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.Chapter01;
import com.lineage.data.quest.Chapter01R;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class Npc_Yuki extends NpcExecutor {
    public static final Random _random = new Random();
    private static final Log _log = LogFactory.getLog(Npc_Yuki.class);

    public static NpcExecutor get() {
        return new Npc_Yuki();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id0"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        try {
            boolean isCloseList = false;
            if (cmd.equalsIgnoreCase("enter")) {
                if (!pc.isInParty()) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id0_1"));
                    return;
                }
                int count = 5;
                if (pc.isGm()) {
                    count = 2;
                }
                if (pc.getParty().getNumOfMembers() < count) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id0_1"));
                    return;
                }
                if (!pc.getParty().isLeader(pc)) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id0_2"));
                    return;
                }
                teltport_all(pc);
                isCloseList = true;
            }
            if (isCloseList) {
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void teltport_all(L1PcInstance pc) {
        try {
            int questid = Chapter01.QUEST.get_id();
            int mapid = 9000;
            int showId = WorldQuest.get().nextId();
            L1Party party = pc.getParty();
            for (final L1PcInstance otherPc : party.getMemberList()) {
                if (otherPc.getId() != party.getLeaderID()) {
                    L1PolyMorph.undoPoly(otherPc);
                    WorldQuest.get().put(showId, mapid, questid, otherPc);
                    L1BuffUtil.cancelAbsoluteBarrier(otherPc);
                    L1Teleport.teleport(otherPc, 32729 + (_random.nextInt(5) - 2), 32725 + (_random.nextInt(5) - 2), (short) 9000, 0, true);
                    QuestClass.get().startQuest(otherPc, Chapter01.QUEST.get_id());
                }
            }
            L1QuestUser quest = WorldQuest.get().put(showId, 9000, questid, pc);
            if (quest == null) {
                _log.error("副本設置過程發生異常!!");
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            quest.set_info(false);
            Integer time = QuestMapTable.get().getTime(9000);
            if (time != null) {
                quest.set_time(time);
            }
            L1PolyMorph.undoPoly(pc);
            L1BuffUtil.cancelAbsoluteBarrier(pc);
            L1Teleport.teleport(pc, 32729, 32725, (short) 9000, 2, true);
            QuestClass.get().startQuest(pc, Chapter01.QUEST.get_id());
            Chapter01R chapter01R = new Chapter01R(party, showId, Chapter01.QUEST.get_id());
            chapter01R.startR();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Yuki JD-Core Version: 0.6.2
 */