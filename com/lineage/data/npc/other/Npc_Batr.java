package com.lineage.data.npc.other;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Batr extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Batr.class);

    public static NpcExecutor get() {
        return new Npc_Batr();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "batr1"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("request sapphire kiringku")) {
            int[] items = {40054, 49205, 49181};
            int[] counts = {3, 1, 1};
            int[] gitems = {270};
            int[] gcounts = {1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "batr4"));
            } else {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            }
        } else if (cmd.equalsIgnoreCase("request obsidian kiringku")) {
            int[] items = {40052, 40053, 40054, 40055, 40520, 49092, 40308};
            int[] counts = {10, 10, 10, 10, 30, 2, 1000000};
            int[] gitems = {271};
            int[] gcounts = {1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                isCloseList = true;
            } else {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            }
        } else if (cmd.equalsIgnoreCase("request cold of kiringku")) {
            int[] items = {80037, 49037, 41246};
            int[] counts = {1, 2, 50000};
            int[] gitems = {410129};
            int[] gcounts = {1};
            if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
                isCloseList = true;
            } else {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Batr JD-Core Version: 0.6.2
 */