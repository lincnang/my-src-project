package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Marbin extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Marbin.class);

    public static NpcExecutor get() {
        return new Npc_Marbin();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getLevel() < 52) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest8"));
                return;
            }
            if (pc.getInventory().checkItem(41784)) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest3"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest1"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("a")) {
            L1PcInventory inv = pc.getInventory();
            if (inv != null) {
                if ((inv.checkItem(41785) == true) && (inv.checkItem(41788, 100) == true) && (inv.checkItem(41787) == true)) {
                    inv.consumeItem(41785, 1);
                    inv.consumeItem(41787, 1);
                    L1ItemInstance pcItem = pc.getInventory().findItemId(41788);
                    inv.removeItem(pcItem);
                    L1ItemInstance item = ItemTable.get().createItem(41789);
                    if (item != null) {
                        item.setCount(5);
                        item.setIdentified(true);
                        if (pc.getInventory().checkAddItem(item, 5) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest2"));
                        }
                        double addExp = 360000 * 2;
                        double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
                        pc.addExp((long) (addExp * exppenalty));
                    }
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest4"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest5"));
                }
            }
        } else if (cmd.equalsIgnoreCase("b")) {
            if (pc.getLevel() >= 52) {
                L1PcInventory inv = pc.getInventory();
                L1PcQuest quest = pc.getQuest();
                int questStep = quest.get_step(L1PcQuest.QUEST_MARBIN);
                if ((inv != null) && (inv.checkItem(41784) != true) && questStep != L1PcQuest.QUEST_END) {
                    L1ItemInstance item = ItemTable.get().createItem(41784);
                    if (item != null) {
                        item.setCount(1);
                        item.setIdentified(true);
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.getQuest().set_step(L1PcQuest.QUEST_MARBIN, 255);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest2"));
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("你已經領取過袋子"));
                    return;
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest7"));
            }
        } else if (cmd.equalsIgnoreCase("c")) {
            L1PcInventory inv = pc.getInventory();
            if (inv != null) {
                if ((inv.checkItem(41786) == true) && (inv.checkItem(41788, 100) == true) && (inv.checkItem(41787) == true)) {
                    inv.consumeItem(41786, 1);
                    inv.consumeItem(41787, 1);
                    L1ItemInstance pcItem = pc.getInventory().findItemId(41788);
                    inv.removeItem(pcItem);
                    L1ItemInstance item = ItemTable.get().createItem(41790);
                    if (item != null) {
                        item.setCount(1);
                        item.setIdentified(true);
                        if (pc.getInventory().checkAddItem(item, 5) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest2"));
                        }
                        double addExp = 360000 * 6;
                        double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
                        pc.addExp((long) (addExp * exppenalty));
                    }
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest4"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "marbinquest5"));
                }
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