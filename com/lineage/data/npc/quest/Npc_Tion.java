package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ALv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Tion extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Tion.class);

    public static NpcExecutor get() {
        return new Npc_Tion();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getQuest().isEnd(ALv45_1.QUEST.get_id())) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion8"));
            } else if (pc.getLevel() >= ALv45_1.QUEST.get_questlevel()) {
                switch (pc.getQuest().get_step(ALv45_1.QUEST.get_id())) {
                    case 0:
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion1"));
                        QuestClass.get().startQuest(pc, ALv45_1.QUEST.get_id());
                        break;
                    case 1:
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion1"));
                        break;
                    case 2:
                    case 3:
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion5"));
                        break;
                    case 4:
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion6"));
                        break;
                    case 5:
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion7"));
                        break;
                    default:
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion8"));
                        break;
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion20"));
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
            items = new int[]{41339};
            counts = new int[]{5};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion9"));
                return;
            }
            gitems = new int[]{41340};
            gcounts = new int[]{1};
            CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion4"));
            pc.getQuest().set_step(ALv45_1.QUEST.get_id(), 2);
        } else if (cmd.equalsIgnoreCase("B")) {
            items = new int[]{41341};
            counts = new int[]{1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion10"));
                return;
            }
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion5"));
        } else if (cmd.equalsIgnoreCase("C")) {
            items = new int[]{41343, 41341};
            counts = new int[]{1, 1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion12"));
                return;
            }
            gitems = new int[]{21057};
            gcounts = new int[]{1};
            CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion6"));
            pc.getQuest().set_step(ALv45_1.QUEST.get_id(), 4);
        } else if (cmd.equalsIgnoreCase("D")) {
            items = new int[]{41344, 21057};
            counts = new int[]{1, 1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion17"));
                return;
            }
            gitems = new int[]{21058};
            gcounts = new int[]{1};
            CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion7"));
            pc.getQuest().set_step(ALv45_1.QUEST.get_id(), 5);
        } else if (cmd.equalsIgnoreCase("E")) {
            items = new int[]{41345, 21058};
            counts = new int[]{1, 1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion18"));
                return;
            }
            gitems = new int[]{21059};
            gcounts = new int[]{1};
            CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tion8"));
            pc.getQuest().set_end(ALv45_1.QUEST.get_id());
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
 * com.lineage.data.npc.quest.Npc_Tion JD-Core Version: 0.6.2
 */