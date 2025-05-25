package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Sbin extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Sbin.class);

    public static NpcExecutor get() {
        return new Npc_Sbin();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "newsbin"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("a")) {
            L1ItemInstance item1 = ItemTable.get().createItem(49031);
            L1ItemInstance item2 = ItemTable.get().createItem(21081);
            //49031	冰之結晶
            //21081	冰之女王的耳環LV.0
            if (item1 != null) {
                item1.setCount(1);
                item1.setIdentified(true);
                if (pc.getInventory().checkAddItem(item1, 1) == 0) {
                    pc.getInventory().storeItem(item1);
                    pc.sendPackets(new S_ServerMessage(403, item1.getLogName()));
                }
            }
            if (item2 != null) {
                item2.setCount(1);
                item2.setIdentified(true);
                if (pc.getInventory().checkAddItem(item2, 1) == 0) {
                    pc.getInventory().storeItem(item2);
                    pc.sendPackets(new S_ServerMessage(403, item2.getLogName()));
                }
            }
            L1Teleport.teleport(pc, 32843, 32886, (short) 800, 5, true);
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