package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv30_1;
import com.lineage.data.quest.DragonKnightLv45_1;
import com.lineage.data.quest.DragonKnightLv50_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Talrion extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Talrion.class);

    public static NpcExecutor get() {
        return new Npc_Talrion();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isDragonKnight()) {
                if ((pc.getQuest().isEnd(DragonKnightLv50_1.QUEST.get_id())) && (pc.getInventory().checkItem(49228)) && (!pc.getInventory().checkItem(49230))) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion10"));
                    return;
                }
                if ((pc.getQuest().isEnd(DragonKnightLv45_1.QUEST.get_id())) && (pc.getInventory().checkItem(49214))) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion9"));
                    return;
                }
                if ((pc.getQuest().isEnd(DragonKnightLv30_1.QUEST.get_id())) && (pc.getInventory().checkItem(49213))) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion1"));
                    return;
                }
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion4"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isDragonKnight()) {
            if (cmd.equalsIgnoreCase("a")) {
                L1ItemInstance item = pc.getInventory().checkItemX(49213, 1L);
                if (item != null) {
                    pc.getInventory().removeItem(item, 1L);
                    CreateNewItem.getQuestItem(pc, npc, 21103, 1L);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion2"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion3"));
                }
            } else if (cmd.equalsIgnoreCase("b")) {
                L1ItemInstance item = pc.getInventory().checkItemX(49214, 1L);
                if (item != null) {
                    pc.getInventory().removeItem(item, 1L);
                    CreateNewItem.getQuestItem(pc, npc, 21102, 1L);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion7"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion8"));
                }
            } else if (cmd.equalsIgnoreCase("c")) {
                L1ItemInstance item = pc.getInventory().checkItemX(49228, 1L);
                if (item != null) {
                    CreateNewItem.getQuestItem(pc, npc, 49230, 1L);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion5"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talrion6"));
                }
            }
        } else {
            isCloseList = true;
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    public void attack(L1PcInstance pc, L1NpcInstance npc) {
    }

    public void work(L1NpcInstance npc) {
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Talrion JD-Core Version: 0.6.2
 */