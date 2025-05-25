package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv15_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Marba extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Marba.class);

    public static NpcExecutor get() {
        return new Npc_Marba();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getLawful() < -500) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba1"));
            } else if (pc.isElf()) {
                if (pc.getQuest().isEnd(ElfLv15_1.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba15"));
                } else if (pc.getLevel() >= ElfLv15_1.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(ElfLv15_1.QUEST.get_id())) {
                        case 0:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba3"));
                            break;
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba6"));
                            break;
                        case 2:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba19"));
                            break;
                        case 3:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba19"));
                            break;
                        case 4:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba17"));
                            break;
                        case 5:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba22"));
                        default:
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isElf()) {
            switch (pc.getQuest().get_step(ElfLv15_1.QUEST.get_id())) {
                case 0:
                    if (!cmd.equals("A")) {
                        break;
                    }
                    CreateNewItem.createNewItem(pc, 40637, 1L);
                    QuestClass.get().startQuest(pc, ElfLv15_1.QUEST.get_id());
                    isCloseList = true;
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    if (!cmd.equals("B")) {
                        break;
                    }
                    L1ItemInstance item = pc.getInventory().checkItemX(40665, 1L);
                    if (item != null) {
                        pc.getInventory().removeItem(item, 1L);
                    }
                    pc.getQuest().set_step(ElfLv15_1.QUEST.get_id(), 5);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba7"));
                    break;
                case 5:
                    if (cmd.equalsIgnoreCase("1")) {
                        int[] srcid = {40699, 40518, 40516, 40517, 40512, 40495};
                        int[] srccount = {1, 3, 3, 3, 5, 10};
                        getItem(pc, npc, srcid, srccount, 214);
                    } else if (cmd.equalsIgnoreCase("2")) {
                        int[] srcid = {40698, 40501, 40492, 40523, 40510, 40495};
                        int[] srccount = {1, 3, 3, 3, 5, 10};
                        getItem(pc, npc, srcid, srccount, 20389);
                    } else if (cmd.equalsIgnoreCase("3")) {
                        int[] srcid = {40693, 40518, 40516, 40517, 40510, 40508};
                        int[] srccount = {1, 5, 5, 5, 6, 100};
                        getItem(pc, npc, srcid, srccount, 20393);
                    } else if (cmd.equalsIgnoreCase("4")) {
                        int[] srcid = {40697, 40522, 40523, 40500, 40510, 40495};
                        int[] srccount = {1, 5, 5, 5, 3, 10};
                        getItem(pc, npc, srcid, srccount, 20409);
                    } else if (cmd.equalsIgnoreCase("5")) {
                        int[] srcid = {40695, 40522, 40523, 40500, 40510, 40508};
                        int[] srccount = {1, 5, 5, 5, 3, 50};
                        getItem(pc, npc, srcid, srccount, 20406);
                    } else if (cmd.equalsIgnoreCase("6")) {
                        int[] srcid = {40694, 40492, 40500, 40501, 40495, 40520};
                        int[] srccount = {1, 1, 1, 1, 10, 35};
                        getItem(pc, npc, srcid, srccount, 20401);
                    }
                    break;
                default:
                    break;
            }
        } else {
            isCloseList = true;
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    private void getItem(L1PcInstance pc, L1NpcInstance npc, int[] srcid, int[] srccount, int itemid) {
        if (CreateNewItem.checkNewItem(pc, srcid, srccount) < 1L) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba16"));
        } else {
            CreateNewItem.createNewItem(pc, srcid, srccount, new int[]{itemid}, 1L, new int[]{1});
            if (checkItem(pc)) {
                QuestClass.get().endQuest(pc, ElfLv15_1.QUEST.get_id());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marba21"));
            }
        }
    }

    private boolean checkItem(L1PcInstance pc) {
        int i = 0;
        int[] itemids = {214, 20401, 20409, 20393, 20406, 20389};
        for (int itemid : itemids) {
            L1ItemInstance item = pc.getInventory().checkItemX(itemid, 1L);
            if (item != null) {
                i++;
            }
        }
        if (i >= 6) {
            return true;
        }
        return false;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest.Npc_Marba JD-Core Version: 0.6.2
 */