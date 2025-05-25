package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Npc_Snip extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_Snip();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if ((pc.getQuest().get_step(58001) == 1) && (pc.getQuest().get_step(58002) == 1) && (pc.getQuest().get_step(58003) == 1)) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "slot5"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "slot7"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("A")) {
            if (pc.getQuest().get_step(58003) == 1) {
                pc.sendPackets(new S_ServerMessage(3254));
                return;
            }
            if (pc.getLevel() < 76) {
                pc.sendPackets(new S_ServerMessage("您的條件不足。"));
                pc.sendPackets(new S_NPCTalkReturn(pc, "slot11"));
                return;
            }
            if (!pc.getInventory().checkItem(40308, 10000000L)) {
                pc.sendPackets(new S_ServerMessage("您的條件不足。"));
                pc.sendPackets(new S_NPCTalkReturn(pc, "slot11"));
                return;
            }
            if (pc.getQuest().get_step(58003) == 1) {
                pc.sendPackets(new S_ServerMessage(3254));
            } else {
                pc.setSlot(76);
                pc.getInventory().consumeItem(40308, 10000000L);
                pc.getQuest().set_step(58003, 1);
                pc.sendPackets(new S_NPCTalkReturn(pc, "slot9"));
            }
        } else if (cmd.equalsIgnoreCase("B")) {
            if (pc.getQuest().get_step(58003) != 1) {
                pc.sendPackets(new S_ServerMessage("尚未開通Lv76戒指欄位。"));
                return;
            }
            if (pc.getQuest().get_step(58002) == 1) {
                pc.sendPackets(new S_ServerMessage(3254));
                return;
            }
            if (pc.getLevel() < 81) {
                pc.sendPackets(new S_ServerMessage("您的條件不足。"));
                pc.sendPackets(new S_NPCTalkReturn(pc, "slot11"));
                return;
            }
            if (!pc.getInventory().checkItem(40308, 30000000L)) {
                pc.sendPackets(new S_ServerMessage("您的條件不足。"));
                pc.sendPackets(new S_NPCTalkReturn(pc, "slot11"));
                return;
            }
            if (pc.getQuest().get_step(58002) == 1) {
                pc.sendPackets(new S_ServerMessage(3254));
            } else {
                pc.setSlot(81);
                pc.getInventory().consumeItem(40308, 30000000L);
                pc.getQuest().set_step(58002, 1);
                pc.sendPackets(new S_NPCTalkReturn(pc, "slot9"));
            }
        } else if (cmd.equalsIgnoreCase("D")) {
            if (pc.getQuest().get_step(58001) == 1) {
                pc.sendPackets(new S_ServerMessage(3254));
                return;
            }
            if (pc.getLevel() < 59) {
                pc.sendPackets(new S_ServerMessage(3253));
                pc.sendPackets(new S_NPCTalkReturn(pc, "slot11"));
                return;
            }
            if (!pc.getInventory().checkItem(44070, 800L)) {
                pc.sendPackets(new S_ServerMessage("您的條件不足。"));
                pc.sendPackets(new S_NPCTalkReturn(pc, "slot11"));
                return;
            }
            if (pc.getQuest().get_step(58001) == 1) {
                pc.sendPackets(new S_ServerMessage(3254));
            } else {
                pc.setSlot(59);
                pc.getInventory().consumeItem(44070, 800L);
                pc.getQuest().set_step(58001, 1);
                pc.sendPackets(new S_NPCTalkReturn(pc, "slot9"));
            }
        }
    }
}