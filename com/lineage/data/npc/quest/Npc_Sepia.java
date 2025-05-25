package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv45_1;
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

public class Npc_Sepia extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Sepia.class);

    public static NpcExecutor get() {
        return new Npc_Sepia();
    }

    public static void staraQuest(L1PcInstance pc, int mapid) {
        try {
            int questid = ElfLv45_1.QUEST.get_id();
            int showId = WorldQuest.get().nextId();
            L1QuestUser quest = WorldQuest.get().put(showId, mapid, questid, pc);
            if (quest == null) {
                _log.error("副本設置過程發生異常!!");
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            Integer time = QuestMapTable.get().getTime(mapid);
            if (time != null) {
                quest.set_time(time);
            }
            L1Teleport.teleport(pc, 32745, 32872, (short) mapid, 0, true);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isElf()) {
                if (pc.getQuest().isEnd(ElfLv45_1.QUEST.get_id())) {
                    return;
                }
                if (pc.getLevel() >= ElfLv45_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(ElfLv45_1.QUEST.get_id())) {
                        case 0:
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "sepia2"));
                            break;
                        case 2:
                        case 3:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "sepia1"));
                            break;
                        default:
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "sepia2"));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isElf()) {
            if (pc.getQuest().isEnd(ElfLv45_1.QUEST.get_id())) {
                return;
            }
            if (!pc.getQuest().isStart(ElfLv45_1.QUEST.get_id())) {
                isCloseList = true;
            } else {
                switch (pc.getQuest().get_step(ElfLv45_1.QUEST.get_id())) {
                    case 2:
                    case 3:
                        if (!cmd.equalsIgnoreCase("teleport sepia-dungen")) {
                            break;
                        }
                        pc.getQuest().set_step(ElfLv45_1.QUEST.get_id(), 3);
                        staraQuest(pc, 302);
                        isCloseList = true;
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
 * com.lineage.data.npc.quest.Npc_Sepia JD-Core Version: 0.6.2
 */