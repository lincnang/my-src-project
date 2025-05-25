package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Meg extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Meg.class);

    public static NpcExecutor get() {
        return new Npc_Meg();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isCrown()) {
                if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg3"));
                    return;
                }
                if (pc.getLevel() >= CrownLv45_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(CrownLv45_1.QUEST.get_id())) {
                        case 0:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));
                            break;
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg1"));
                            break;
                        case 2:
                        case 3:
                        case 4:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg2"));
                            break;
                        default:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg3"));
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isCrown()) {
            if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
                return;
            }
            if (cmd.equalsIgnoreCase("quest 17 meg2")) {
                pc.getQuest().set_step(CrownLv45_1.QUEST.get_id(), 2);
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg2"));
            } else if (cmd.equalsIgnoreCase("request royal family piece b")) {
                if (CreateNewItem.checkNewItem(pc, new int[]{40573, 40574, 40575}, new int[]{1, 1, 1}) < 1L) {
                    pc.sendPackets(new S_CloseList(pc.getId()));
                } else {
                    CreateNewItem.createNewItem(pc, new int[]{40573, 40574, 40575}, new int[]{1, 1, 1}, new int[]{40587}, 1L, new int[]{1});
                    pc.getQuest().set_step(CrownLv45_1.QUEST.get_id(), 5);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg3"));
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
 * com.lineage.data.npc.quest.Npc_Meg JD-Core Version: 0.6.2
 */