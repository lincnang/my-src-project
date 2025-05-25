package com.lineage.data.npc;

import com.add.system.L1BlendTable;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

import java.util.Map;

public class Npc_FireDis extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_FireDis();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "firedis1"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("request craft")) {
            //System.out.println("test");
            ShowCraftList(pc, npc);
        }
    }

    /**
     * 顯示道具製造清單
     *
     */
    private void ShowCraftList(L1PcInstance pc, L1NpcInstance npc) {
        String msg0 = "";
        String msg1 = "";
        String msg2 = "";
        String msg3 = "";
        String msg4 = "";
        String msg5 = "";
        String msg6 = "";
        String msg7 = "";
        String msg8 = "";
        String msg9 = "";
        String msg10 = "";
        String msg11 = "";
        String msg12 = "";
        String msg13 = "";
        String msg14 = "";
        String msg15 = "";
        String msg16 = "";
        String msg17 = "";
        String msg18 = "";
        String msg19 = "";
        String msg20 = "";
        String msg21 = "";
        String msg22 = "";
        String msg23 = "";
        String msg24 = "";
        String msg25 = "";
        int npcid = npc.getNpcId();
        Map<String, String> craftlist = L1BlendTable.getInstance().get_craftlist();
        if (!craftlist.isEmpty()) {
            msg0 = craftlist.get(npcid + "A");
            msg1 = craftlist.get(npcid + "B");
            msg2 = craftlist.get(npcid + "C");
            msg3 = craftlist.get(npcid + "D");
            msg4 = craftlist.get(npcid + "E");
            msg5 = craftlist.get(npcid + "F");
            msg6 = craftlist.get(npcid + "G");
            msg7 = craftlist.get(npcid + "H");
            msg8 = craftlist.get(npcid + "I");
            msg9 = craftlist.get(npcid + "J");
            msg10 = craftlist.get(npcid + "K");
            msg11 = craftlist.get(npcid + "L");
            msg12 = craftlist.get(npcid + "M");
            msg13 = craftlist.get(npcid + "N");
            msg14 = craftlist.get(npcid + "O");
            msg15 = craftlist.get(npcid + "P");
            msg16 = craftlist.get(npcid + "Q");
            msg17 = craftlist.get(npcid + "R");
            msg18 = craftlist.get(npcid + "S");
            msg19 = craftlist.get(npcid + "T");
            msg20 = craftlist.get(npcid + "U");
            msg21 = craftlist.get(npcid + "V");
            msg22 = craftlist.get(npcid + "W");
            msg23 = craftlist.get(npcid + "X");
            msg24 = craftlist.get(npcid + "Y");
            msg25 = craftlist.get(npcid + "Z");
        }
        String msgs[] = {msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11, msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21, msg22, msg23, msg24, msg25};
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "smithitem1", msgs));
    }
}
