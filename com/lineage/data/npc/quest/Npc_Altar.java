package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.IllusionistLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Altar extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Altar.class);

    public static NpcExecutor get() {
        return new Npc_Altar();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isIllusionist()) {
                if (pc.getQuest().isStart(IllusionistLv30_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar1"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        int[] items = (int[]) null;
        int[] counts = (int[]) null;
        int[] gitems = (int[]) null;
        int[] gcounts = (int[]) null;
        if (cmd.equalsIgnoreCase("A")) {
            items = new int[]{41327, 41319};
            counts = new int[]{20, 1};
            gitems = new int[]{41325};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("B")) {
            items = new int[]{41327, 41320};
            counts = new int[]{20, 1};
            gitems = new int[]{41325};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("C")) {
            items = new int[]{41327, 41321};
            counts = new int[]{20, 1};
            gitems = new int[]{41325};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("D")) {
            items = new int[]{41327, 41322};
            counts = new int[]{20, 1};
            gitems = new int[]{41325};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("E")) {
            items = new int[]{41327, 41323};
            counts = new int[]{20, 1};
            gitems = new int[]{41325};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("F")) {
            items = new int[]{41327, 41324};
            counts = new int[]{20, 1};
            gitems = new int[]{41325};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("G")) {
            items = new int[]{41328, 41319};
            counts = new int[]{1, 1};
            gitems = new int[]{41326};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("H")) {
            items = new int[]{41328, 41320};
            counts = new int[]{1, 1};
            gitems = new int[]{41326};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("I")) {
            items = new int[]{41328, 41321};
            counts = new int[]{1, 1};
            gitems = new int[]{41326};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("J")) {
            items = new int[]{41328, 41322};
            counts = new int[]{1, 1};
            gitems = new int[]{41326};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("K")) {
            items = new int[]{41328, 41323};
            counts = new int[]{1, 1};
            gitems = new int[]{41326};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("L")) {
            items = new int[]{41328, 41324};
            counts = new int[]{1, 1};
            gitems = new int[]{41326};
            gcounts = new int[]{1};
        } else if (cmd.equalsIgnoreCase("M")) {
            if (pc.getQuest().isStart(IllusionistLv30_1.QUEST.get_id())) {
                if (CreateNewItem.checkNewItem(pc, new int[]{49187, 41319}, new int[]{1, 1}) < 1L) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar8"));
                } else {
                    CreateNewItem.createNewItem(pc, new int[]{49187, 41319}, new int[]{1, 1}, new int[]{49188}, 1L, new int[]{1});
                    if (!pc.getInventory().checkItem(274)) {
                        CreateNewItem.getQuestItem(pc, npc, 274, 1L);
                    }
                    pc.getQuest().set_step(IllusionistLv30_1.QUEST.get_id(), 2);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar9"));
                }
            } else {
                isCloseList = true;
            }
        } else if (cmd.equalsIgnoreCase("N")) {
            if (pc.getQuest().isStart(IllusionistLv30_1.QUEST.get_id())) {
                if (CreateNewItem.checkNewItem(pc, new int[]{49190, 274, 41322}, new int[]{1, 1, 1}) < 1L) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar8"));
                } else {
                    CreateNewItem.createNewItem(pc, new int[]{49190, 274, 41322}, new int[]{1, 1, 1}, new int[]{49191}, 1L, new int[]{1});
                    pc.getQuest().set_step(IllusionistLv30_1.QUEST.get_id(), 3);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar10"));
                }
            } else {
                isCloseList = true;
            }
        } else {
            isCloseList = true;
        }
        if (items != null) {
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar8"));
            } else {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar3"));
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Altar JD-Core Version: 0.6.2
 */