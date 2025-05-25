package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Kima extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Kima.class);

    public static NpcExecutor get() {
        return new Npc_Kima();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isDarkelf()) {
                if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));
                    return;
                }
                if (pc.getLevel() >= DarkElfLv50_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id())) {
                        case 0:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));
                            break;
                        case 1:
                        case 2:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq1"));
                            break;
                        case 3:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq3"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq4"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isDarkelf()) {
            if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
                isCloseList = true;
            } else {
                switch (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id())) {
                    case 1:
                    case 2:
                        if (!cmd.equalsIgnoreCase("request mask of true")) {
                            break;
                        }
                        L1ItemInstance item = pc.getInventory().checkItemX(40583, 1L);
                        if (item == null) {
                            pc.sendPackets(new S_ServerMessage(337, "$2654 (1)"));
                            isCloseList = true;
                        } else {
                            pc.getInventory().removeItem(item, 1L);
                            pc.getQuest().set_step(DarkElfLv50_1.QUEST.get_id(), 3);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq3"));
                        }
                        break;
                    case 3:
                        if (!cmd.equalsIgnoreCase("quest 26 kimaq4")) {
                            break;
                        }
                        CreateNewItem.getQuestItem(pc, npc, 20037, 1L);
                        pc.getQuest().set_step(DarkElfLv50_1.QUEST.get_id(), 4);
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq4"));
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
 * com.lineage.data.npc.quest.Npc_Kima JD-Core Version: 0.6.2
 */