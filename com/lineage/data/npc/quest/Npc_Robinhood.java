package com.lineage.data.npc.quest;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv45_2;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Robinhood extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Robinhood.class);

    public static NpcExecutor get() {
        return new Npc_Robinhood();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isElf()) {
                if (pc.getQuest().isEnd(ElfLv45_2.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood12"));
                    return;
                }
                if (pc.getLevel() >= ElfLv45_2.QUEST.get_questlevel()) {
                    switch (pc.getQuest().get_step(ElfLv45_2.QUEST.get_id())) {
                        case 0:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood1"));
                            break;
                        case 1:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood5"));
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood13"));
                            break;
                        case 6:
                            if (pc.getInventory().checkItem(41351, 1L)) {
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood9"));
                            } else {
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood13"));
                            }
                            break;
                        case 7:
                            if (pc.getInventory().checkItem(41350, 1L)) {
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood11"));
                            } else {
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood18"));
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood19"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood2"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isElf()) {
            if (pc.getQuest().isEnd(ElfLv45_2.QUEST.get_id())) {
                return;
            }
            switch (pc.getQuest().get_step(ElfLv45_2.QUEST.get_id())) {
                case 0:
                    if (!cmd.equals("A")) {
                        break;
                    }
                    L1ItemInstance item = pc.getInventory().findItemId(40068);
                    if (item != null) {
                        pc.getInventory().removeItem(item, 1L);
                        QuestClass.get().startQuest(pc, ElfLv45_2.QUEST.get_id());
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood4"));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood19"));
                    }
                    break;
                case 1:
                    if (!cmd.equals("B")) {
                        break;
                    }
                    pc.getQuest().set_step(ElfLv45_2.QUEST.get_id(), 2);
                    CreateNewItem.getQuestItem(pc, npc, 41348, 1L);
                    CreateNewItem.getQuestItem(pc, npc, 41346, 1L);
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood13"));
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    break;
                case 6:
                    if (!cmd.equals("C")) {
                        break;
                    }
                    if (CreateNewItem.checkNewItem(pc, new int[]{41352, 40618, 40643, 40645, 40651, 40676, 40514, 41351, 41346}, new int[]{4, 30, 30, 30, 30, 30, 20, 1, 1}) < 1L) {
                        if (CreateNewItem.checkNewItem(pc, new int[]{41352, 41351}, new int[]{4, 1}) < 1L) {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood15"));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood14"));
                        }
                    } else {
                        CreateNewItem.createNewItem(pc, new int[]{41352, 40618, 40643, 40645, 40651, 40676, 40514, 41351, 41346}, new int[]{4, 30, 30, 30, 30, 30, 20, 1, 1}, new int[]{41347, 41350}, 1L, new int[]{1, 1});
                        pc.getQuest().set_step(ElfLv45_2.QUEST.get_id(), 7);
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood10"));
                    }
                    break;
                case 7:
                    if (!cmd.equals("E")) {
                        break;
                    }
                    if (CreateNewItem.checkNewItem(pc, new int[]{40491, 40495, 100, 40509, 40052, 40053, 40054, 40055, 41347, 41350}, new int[]{30, 40, 1, 12, 1, 1, 1, 1, 1, 1}) < 1L) {
                        if (CreateNewItem.checkNewItem(pc, new int[]{40052, 40053, 40054, 40055}, new int[]{1, 1, 1, 1}) < 1L) {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood16"));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood17"));
                        }
                    } else {
                        CreateNewItem.createNewItem(pc, new int[]{40491, 40495, 100, 40509, 40052, 40053, 40054, 40055, 41347, 41350}, new int[]{30, 40, 1, 12, 1, 1, 1, 1, 1, 1}, new int[]{205}, 1L, new int[]{1});
                        QuestClass.get().endQuest(pc, ElfLv45_2.QUEST.get_id());
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood12"));
                    }
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
 * com.lineage.data.npc.quest.Npc_Robinhood JD-Core Version: 0.6.2
 */