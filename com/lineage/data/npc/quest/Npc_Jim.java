package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv15_1;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Jim extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Jim.class);

    public static NpcExecutor get() {
        return new Npc_Jim();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getTempCharGfx() != 2374) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim1"));
                return;
            }
            if (pc.isKnight()) {
                if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimcg"));
                    return;
                }
                if (pc.getInventory().checkItem(40529)) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimcg"));
                    return;
                }
                if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));
                } else {
                    if (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id()) < 2) {
                        pc.getQuest().set_step(KnightLv30_1.QUEST.get_id(), 2);
                    }
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim2"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jim4"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isKnight()) {
            if (!pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
                return;
            }
            if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
                return;
            }
            if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
                isCloseList = true;
            } else if (cmd.equalsIgnoreCase("request letter of gratitude")) {
                if (CreateNewItem.checkNewItem(pc, new int[]{40607}, new int[]{1}) < 1L) {
                    isCloseList = true;
                } else {
                    CreateNewItem.createNewItem(pc, new int[]{40607}, new int[]{1}, new int[]{40529}, 1L, new int[]{1});
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimcg"));
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
 * com.lineage.data.npc.quest.Npc_Jim JD-Core Version: 0.6.2
 */