package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.IllusionistLv50_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_BlueSoul extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_BlueSoul.class);

    public static NpcExecutor get() {
        return new Npc_BlueSoul();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (!pc.isCrown()) {
                if (pc.isIllusionist()) {
                    if (pc.getQuest().isStart(IllusionistLv50_1.QUEST.get_id())) {
                        switch (pc.getQuest().get_step(IllusionistLv50_1.QUEST.get_id())) {
                            case 2:
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f1"));
                                break;
                            case 3:
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f2"));
                                break;
                            default:
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));
                                break;
                        }
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (pc.isIllusionist()) {
            if (pc.getQuest().isStart(IllusionistLv50_1.QUEST.get_id())) {
                switch (pc.getQuest().get_step(IllusionistLv50_1.QUEST.get_id())) {
                    case 2:
                        if (!cmd.equalsIgnoreCase("a")) {
                            break;
                        }
                        if (CreateNewItem.checkNewItem(pc, new int[]{49203, 49204}, new int[]{5, 5}) < 1L) {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f3"));
                        } else {
                            CreateNewItem.createNewItem(pc, new int[]{49203, 49204}, new int[]{5, 5}, new int[]{49207, 49208}, 1L, new int[]{1, 1});
                            pc.getQuest().set_step(IllusionistLv50_1.QUEST.get_id(), 3);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f2"));
                        }
                        break;
                    default:
                        pc.sendPackets(new S_CloseList(pc.getId()));
                        break;
                }
            } else {
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_BlueSoul JD-Core Version: 0.6.2
 */