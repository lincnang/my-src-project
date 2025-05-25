package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv50_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_RedSoul extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_RedSoul.class);

    public static NpcExecutor get() {
        return new Npc_RedSoul();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (!pc.isCrown()) {
                if (pc.isDragonKnight()) {
                    if (pc.getQuest().isStart(DragonKnightLv50_1.QUEST.get_id())) {
                        switch (pc.getQuest().get_step(DragonKnightLv50_1.QUEST.get_id())) {
                            case 2:
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f1"));
                                break;
                            case 3:
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f2"));
                                break;
                            default:
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));
                                break;
                        }
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (pc.isDragonKnight()) {
            if (pc.getQuest().isStart(DragonKnightLv50_1.QUEST.get_id())) {
                switch (pc.getQuest().get_step(DragonKnightLv50_1.QUEST.get_id())) {
                    case 2:
                    case 3:
                        if (!cmd.equalsIgnoreCase("a")) {
                            break;
                        }
                        L1ItemInstance item = pc.getInventory().checkItemX(49229, 10L);
                        if (item != null) {
                            pc.getInventory().removeItem(item, 10L);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f2"));
                            pc.getQuest().set_step(DragonKnightLv50_1.QUEST.get_id(), 3);
                            CreateNewItem.getQuestItem(pc, npc, 49207, 1L);
                            CreateNewItem.getQuestItem(pc, npc, 49227, 1L);
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f3"));
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
 * com.lineage.data.npc.quest.Npc_RedSoul JD-Core Version: 0.6.2
 */