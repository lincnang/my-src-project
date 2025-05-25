package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Npc_Nerva extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_Nerva();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (pc.getLevel() >= 70) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva2"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva1"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("A")) {
            if (pc.getInventory().checkItem(49477, 1) && // 褪色的古書
                    pc.getInventory().checkItem(40087, 70) && // 武卷70張
                    pc.getInventory().checkItem(49928, 1)) {// 失去魔力的符石
                pc.getInventory().consumeItem(49477, 1);
                pc.getInventory().consumeItem(40087, 70);
                pc.getInventory().consumeItem(49928, 1);
                L1ItemInstance item = pc.getInventory().storeItem(49871, 1);
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getName()));
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva3"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva4"));
            }
        } else if (cmd.equalsIgnoreCase("B")) {
            if (pc.getInventory().checkItem(49477, 1) && // 褪色的古書
                    pc.getInventory().checkItem(40087, 70) && // 武卷70張
                    pc.getInventory().checkItem(49928, 1)) {// 失去魔力的符石
                pc.getInventory().consumeItem(49477, 1);
                pc.getInventory().consumeItem(40087, 70);
                pc.getInventory().consumeItem(49928, 1);
                L1ItemInstance item = pc.getInventory().storeItem(49872, 1);
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getName()));
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva3"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva4"));
            }
        } else if (cmd.equalsIgnoreCase("C")) {
            if (pc.getInventory().checkItem(49477, 1) && // 褪色的古書
                    pc.getInventory().checkItem(40087, 70) && // 武卷70張
                    pc.getInventory().checkItem(49928, 1)) {// 失去魔力的符石
                pc.getInventory().consumeItem(49477, 1);
                pc.getInventory().consumeItem(40087, 70);
                pc.getInventory().consumeItem(49928, 1);
                L1ItemInstance item = pc.getInventory().storeItem(49873, 1);
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getName()));
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva3"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva4"));
            }
        } else if (cmd.equalsIgnoreCase("D")) {
            if (pc.getInventory().checkItem(49477, 1) && // 褪色的古書
                    pc.getInventory().checkItem(40087, 70) && // 武卷70張
                    pc.getInventory().checkItem(49928, 1)) {// 失去魔力的符石
                pc.getInventory().consumeItem(49477, 1);
                pc.getInventory().consumeItem(40087, 70);
                pc.getInventory().consumeItem(49928, 1);
                L1ItemInstance item = pc.getInventory().storeItem(49874, 1);
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getName()));
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva3"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva4"));
            }
        } else if (cmd.equalsIgnoreCase("E")) {
            if (pc.getInventory().checkItem(49477, 1) && // 褪色的古書
                    pc.getInventory().checkItem(40087, 70) && // 武卷70張
                    pc.getInventory().checkItem(49928, 1)) {// 失去魔力的符石
                pc.getInventory().consumeItem(49477, 1);
                pc.getInventory().consumeItem(40087, 70);
                pc.getInventory().consumeItem(49928, 1);
                L1ItemInstance item = pc.getInventory().storeItem(49875, 1);
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getItem().getName()));
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva3"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nerva4"));
            }
        }
    }
}