package com.lineage.data.npc.other;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv30_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Pual extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Pual.class);

    public static NpcExecutor get() {
        return new Npc_Pual();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pual1"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (cmd.equalsIgnoreCase("request halpas symbol")) {
            if (pc.getQuest().isStart(DragonKnightLv30_1.QUEST.get_id())) {
                L1ItemInstance item = pc.getInventory().checkItemX(49215, 1L);
                if (item != null) {
                    pc.getInventory().removeItem(item, 1L);
                    CreateNewItem.getQuestItem(pc, npc, 49223, 1L);
                } else {
                    pc.sendPackets(new S_ServerMessage(337, "$5731(1)"));
                }
            }
            isCloseList = true;
        } else if (cmd.equalsIgnoreCase("request chainsword extinctioner")) {
            L1ItemInstance item1 = pc.getInventory().checkItemX(49230, 1L);
            if (item1 != null) {
                pc.getInventory().removeItem(item1);
            }
            int[] items = {49228, 40494, 40779, 40052};
            int[] counts = {1, 100, 10, 3};
            int[] gitems = {273};
            int[] gcounts = {1};
            isCloseList = getItem(pc, items, counts, gitems, gcounts);
        } else if (cmd.equalsIgnoreCase("request lump of steel")) {
            int[] items = {40899, 40408, 40308};
            int[] counts = {5, 5, 500};
            int[] gitems = {40779};
            int[] gcounts = {1};
            long xcount = CreateNewItem.checkNewItem(pc, items, counts);
            if (xcount == 1L) {
                CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
                isCloseList = true;
            } else if (xcount > 1L) {
                pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "a1"));
            } else if (xcount < 1L) {
                isCloseList = true;
            }
        } else if (cmd.equalsIgnoreCase("a1")) {
            int[] items = {40899, 40408, 40308};
            int[] counts = {5, 5, 500};
            int[] gitems = {40779};
            int[] gcounts = {1};
            long xcount = CreateNewItem.checkNewItem(pc, items, counts);
            if (xcount >= amount) {
                CreateNewItem.createNewItem(pc, items, counts, gitems, amount, gcounts);
            }
            isCloseList = true;
        } else if (cmd.equalsIgnoreCase("request chainsword destroyer")) {
            int[] items = {17, 40406, 40503, 40393, 40308};
            int[] counts = {1, 20, 20, 1, 1000000};
            int[] gitems = {273};
            int[] gcounts = {1};
            isCloseList = getItem(pc, items, counts, gitems, gcounts);
        } else if (cmd.equalsIgnoreCase("request guarder of ancient archer")) {
            int[] items = {20140, 40445, 40504, 40505, 40521, 40495, 40308};
            int[] counts = {1, 3, 20, 50, 20, 50, 1000000};
            int[] gitems = {21105};
            int[] gcounts = {1};
            isCloseList = getItem(pc, items, counts, gitems, gcounts);
        } else if (cmd.equalsIgnoreCase("request guarder of ancient champion")) {
            int[] items = {20143, 40445, 40308};
            int[] counts = {1, 5, 1000000};
            int[] gitems = {21106};
            int[] gcounts = {1};
            isCloseList = getItem(pc, items, counts, gitems, gcounts);
        } else if (cmd.equalsIgnoreCase("request cold of chainsword")) {
            int[] items = {80036, 49037, 41246};
            int[] counts = {1, 2, 50000};
            int[] gitems = {410130};
            int[] gcounts = {1};
            isCloseList = getItem(pc, items, counts, gitems, gcounts);
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    private boolean getItem(L1PcInstance pc, int[] items, int[] counts, int[] gitems, int[] gcounts) {
        if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
            return true;
        }
        CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
        return true;
    }
}
