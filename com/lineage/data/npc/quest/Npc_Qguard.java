package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Qguard extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Qguard.class);

    public static NpcExecutor get() {
        return new Npc_Qguard();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isKnight()) {
                if (pc.getLevel() >= KnightLv30_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id())) {
                        case 5:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguard1"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isKnight()) {
            if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
                return;
            }
            if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
                isCloseList = true;
            } else {
                switch (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id())) {
                    case 5:
                        if (cmd.equalsIgnoreCase("teleportURL")) {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "qguards"));
                        } else if (cmd.equalsIgnoreCase("teleport gerard-dungen")) {
                            staraQuest(pc);
                            isCloseList = true;
                        }
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

    private void staraQuest(L1PcInstance pc) {
        try {
            int questid = KnightLv30_1.QUEST.get_id();
            int showId = WorldQuest.get().nextId();
            L1QuestUser quest = WorldQuest.get().put(showId, 22, questid, pc);
            if (quest == null) {
                _log.error("副本設置過程發生異常!!");
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            Integer time = QuestMapTable.get().getTime(22);
            if (time != null) {
                quest.set_time(time.intValue());
            }
            L1SpawnUtil.spawnDoor(quest, 10004, 89, 32769, 32778, (short) 22, 1);
            L1Location loc = new L1Location(32774, 32778, 22);
            L1NpcInstance mob = L1SpawnUtil.spawn(81107, loc, 5, showId);
            mob.getInventory().storeItem(40544, 1L);
            quest.addNpc(mob);
            pc.getInventory().takeoffEquip(945);
            L1Teleport.teleport(pc, 32769, 32768, (short) 22, 4, true);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Qguard JD-Core Version: 0.6.2
 */