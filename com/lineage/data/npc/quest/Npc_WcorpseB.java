package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.IllusionistLv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_WcorpseB extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_WcorpseB.class);

    public static NpcExecutor get() {
        return new Npc_WcorpseB();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isIllusionist()) {
                if (pc.getQuest().isStart(IllusionistLv45_1.QUEST.get_id())) {
                    if (!pc.getInventory().checkItem(49195)) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse5"));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse4"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("a")) {
            if (pc.getQuest().isStart(IllusionistLv45_1.QUEST.get_id())) {
                if (!pc.getInventory().checkItem(49195)) {
                    L1ItemInstance item = pc.getInventory().checkItemX(49192, 1L);
                    if (item != null) {
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wcorpse6"));
                        CreateNewItem.createNewItem(pc, 49195, 1L);
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                    isCloseList = true;
                }
            } else {
                isCloseList = true;
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
 * com.lineage.data.npc.quest.Npc_WcorpseB JD-Core Version: 0.6.2
 */