package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Npc_InHaiSaBelt extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_InHaiSaBelt();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tw_2014belt1"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        int[] oldbelts = {300034, 300035, 300036, 300037};
        int newbelt = 0;
        boolean success = false;
        if (cmd.equalsIgnoreCase("b")) {
            newbelt = 401008;// 光之殷海薩智力腰帶
            for (int oldbelt : oldbelts) {
                if ((pc.getInventory().checkItemNotEquipped(oldbelt, 1L)) && (pc.getInventory().checkItem(80329, 1L))) {// 光之加護
                    pc.getInventory().consumeItem(oldbelt, 1L);
                    pc.getInventory().consumeItem(80329, 1L);
                    final L1ItemInstance item = ItemTable.get().createItem(newbelt);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
                    success = true;
                    break;
                }
            }
        } else if (cmd.equalsIgnoreCase("c")) {
            newbelt = 401009;// 光之殷海薩力量腰帶
            for (int oldbelt : oldbelts) {
                if ((pc.getInventory().checkItemNotEquipped(oldbelt, 1L)) && (pc.getInventory().checkItem(80329, 1L))) {// 光之加護
                    pc.getInventory().consumeItem(oldbelt, 1L);
                    pc.getInventory().consumeItem(80329, 1L);
                    final L1ItemInstance item = ItemTable.get().createItem(newbelt);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
                    success = true;
                    break;
                }
            }
        } else if (cmd.equalsIgnoreCase("d")) {
            newbelt = 401010;// 光之殷海薩敏捷腰帶
            for (int oldbelt : oldbelts) {
                if ((pc.getInventory().checkItemNotEquipped(oldbelt, 1L)) && (pc.getInventory().checkItem(80329, 1L))) {// 光之加護
                    pc.getInventory().consumeItem(oldbelt, 1L);
                    pc.getInventory().consumeItem(80329, 1L);
                    final L1ItemInstance item = ItemTable.get().createItem(newbelt);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
                    success = true;
                    break;
                }
            }
        } else if (cmd.equalsIgnoreCase("e")) {
            newbelt = 401011;// 光之殷海薩魅力腰帶
            for (int oldbelt : oldbelts) {
                if ((pc.getInventory().checkItemNotEquipped(oldbelt, 1L)) && (pc.getInventory().checkItem(80329, 1L))) {// 光之加護
                    pc.getInventory().consumeItem(oldbelt, 1L);
                    pc.getInventory().consumeItem(80329, 1L);
                    final L1ItemInstance item = ItemTable.get().createItem(newbelt);
                    item.setIdentified(true);
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
                    success = true;
                    break;
                }
            }
        }
        if (success) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tw_2014belt2"));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tw_2014belt3"));
        }
    }
}
