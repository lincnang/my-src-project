package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.data.quest.CrownLv50_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Kiholl extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Kiholl.class);

    public static NpcExecutor get() {
        return new Npc_Kiholl();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (!pc.getInventory().checkItem(49159)) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kiholl0"));
                return;
            }
            if (pc.isCrown()) {
                if (pc.getQuest().isEnd(CrownLv50_1.QUEST.get_id())) {
                    if (pc.getTempCharGfx() == 4261) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kiholl2"));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kiholl0"));
                    }
                    return;
                }
                if (pc.getLevel() >= CrownLv50_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(CrownLv50_1.QUEST.get_id())) {
                        case 0:
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kiholl0"));
                            break;
                        case 2:
                            if (pc.getTempCharGfx() == 4261) {
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kiholl1"));
                            } else {
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kiholl0"));
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kiholl0"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kiholl0"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isCrown()) {
            switch (pc.getQuest().get_step(CrownLv50_1.QUEST.get_id())) {
                case 2:
                    if (!cmd.equalsIgnoreCase("a")) {
                        break;
                    }
                    L1ItemInstance item = pc.getInventory().checkItemX(49159, 1L);
                    if (item != null) {
                        pc.getInventory().removeItem(item, 1L);
                    }
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kiholl2"));
                    QuestClass.get().endQuest(pc, CrownLv50_1.QUEST.get_id());
                    QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
                    break;
                default:
                    isCloseList = true;
                    break;
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
 * com.lineage.data.npc.quest.Npc_Kiholl JD-Core Version: 0.6.2
 */