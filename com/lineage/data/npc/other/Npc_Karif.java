package com.lineage.data.npc.other;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv50_2;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Karif extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Karif.class);

    public static NpcExecutor get() {
        return new Npc_Karif();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.isDarkelf()) {
                if (pc.getQuest().isEnd(DarkElfLv50_2.QUEST.get_id())) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));
                    return;
                }
                if (pc.getLevel() >= DarkElfLv50_2.QUEST.get_questlevel()) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif1"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (pc.isDarkelf()) {
            if (cmd.equalsIgnoreCase("quest 32 karif4")) {
                isCloseList = getItem1(pc);
            } else if (cmd.equalsIgnoreCase("quest 32 karif5")) {
                isCloseList = getItem2(pc);
            } else if (cmd.equalsIgnoreCase("quest 32 karif6")) {
                isCloseList = getItem3(pc);
            } else if (cmd.equalsIgnoreCase("request darkness dualblade")) {
                isCloseList = getItem1(pc);
            } else if (cmd.equalsIgnoreCase("request darkness claw")) {
                isCloseList = getItem2(pc);
            } else if (cmd.equalsIgnoreCase("request darkness crossbow")) {
                isCloseList = getItem3(pc);
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "karif9"));
            return;
        }
        if (cmd.equalsIgnoreCase("request karif bag1")) {
            int[] items = {40044};
            int[] counts = {1};
            int[] gitems = {49005};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a1");
        } else if (cmd.equalsIgnoreCase("a1")) {
            int[] items = {40044};
            int[] counts = {1};
            int[] gitems = {49005};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag2")) {
            int[] items = {40047};
            int[] counts = {1};
            int[] gitems = {49008};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a2");
        } else if (cmd.equalsIgnoreCase("a2")) {
            int[] items = {40047};
            int[] counts = {1};
            int[] gitems = {49008};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag3")) {
            int[] items = {40045};
            int[] counts = {1};
            int[] gitems = {49006};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a3");
        } else if (cmd.equalsIgnoreCase("a3")) {
            int[] items = {40045};
            int[] counts = {1};
            int[] gitems = {49006};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag4")) {
            int[] items = {40046};
            int[] counts = {1};
            int[] gitems = {49007};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a4");
        } else if (cmd.equalsIgnoreCase("a4")) {
            int[] items = {40046};
            int[] counts = {1};
            int[] gitems = {49007};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag5")) {
            int[] items = {40048};
            int[] counts = {1};
            int[] gitems = {49009};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a5");
        } else if (cmd.equalsIgnoreCase("a5")) {
            int[] items = {40048};
            int[] counts = {1};
            int[] gitems = {49009};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag6")) {
            int[] items = {40051};
            int[] counts = {1};
            int[] gitems = {49010};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a6");
        } else if (cmd.equalsIgnoreCase("a6")) {
            int[] items = {40051};
            int[] counts = {1};
            int[] gitems = {49010};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag7")) {
            int[] items = {40049};
            int[] counts = {1};
            int[] gitems = {49011};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a7");
        } else if (cmd.equalsIgnoreCase("a7")) {
            int[] items = {40049};
            int[] counts = {1};
            int[] gitems = {49011};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag8")) {
            int[] items = {40050};
            int[] counts = {1};
            int[] gitems = {49012};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a8");
        } else if (cmd.equalsIgnoreCase("a8")) {
            int[] items = {40050};
            int[] counts = {1};
            int[] gitems = {49012};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag9")) {
            int[] items = {40052};
            int[] counts = {1};
            int[] gitems = {180100};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a9");
        } else if (cmd.equalsIgnoreCase("a9")) {
            int[] items = {40052};
            int[] counts = {1};
            int[] gitems = {180100};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag10")) {
            int[] items = {40055};
            int[] counts = {1};
            int[] gitems = {180101};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a10");
        } else if (cmd.equalsIgnoreCase("a10")) {
            int[] items = {40055};
            int[] counts = {1};
            int[] gitems = {180101};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag11")) {
            int[] items = {40053};
            int[] counts = {1};
            int[] gitems = {180102};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a11");
        } else if (cmd.equalsIgnoreCase("a11")) {
            int[] items = {40053};
            int[] counts = {1};
            int[] gitems = {180102};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        } else if (cmd.equalsIgnoreCase("request karif bag12")) {
            int[] items = {40054};
            int[] counts = {1};
            int[] gitems = {180103};
            int[] gcounts = {1};
            isCloseList = requestKarifBag(pc, npc, items, counts, gitems, gcounts, "a12");
        } else if (cmd.equalsIgnoreCase("a12")) {
            int[] items = {40054};
            int[] counts = {1};
            int[] gitems = {180103};
            int[] gcounts = {1};
            isCloseList = getKarifBag(pc, items, counts, gitems, gcounts, amount);
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    private boolean getItem3(L1PcInstance pc) {
        if (pc.getQuest().isEnd(DarkElfLv50_2.QUEST.get_id())) {
            return true;
        }
        if (pc.getLevel() < DarkElfLv50_2.QUEST.get_questlevel()) {
            return true;
        }
        int[] items = {40466, 40054, 40308, 40403, 40413, 40525, 177};
        int[] counts = {1, 3, 100000, 10, 9, 3, 1};
        int[] gitems = {189};
        int[] gcounts = {1};
        return getItem(pc, items, counts, gitems, gcounts, 1L);
    }

    private boolean getItem2(L1PcInstance pc) {
        if (pc.getQuest().isEnd(DarkElfLv50_2.QUEST.get_id())) {
            return true;
        }
        if (pc.getLevel() < DarkElfLv50_2.QUEST.get_questlevel()) {
            return true;
        }
        int[] items = {40466, 40055, 40308, 40404, 40413, 40525, 162};
        int[] counts = {1, 3, 100000, 10, 9, 3, 1};
        int[] gitems = {164};
        int[] gcounts = {1};
        return getItem(pc, items, counts, gitems, gcounts, 1L);
    }

    private boolean getItem1(L1PcInstance pc) {
        if (pc.getQuest().isEnd(DarkElfLv50_2.QUEST.get_id())) {
            return true;
        }
        if (pc.getLevel() < DarkElfLv50_2.QUEST.get_questlevel()) {
            return true;
        }
        int[] items = {40466, 40053, 40308, 40402, 40413, 40525, 81};
        int[] counts = {1, 3, 100000, 10, 9, 3, 1};
        int[] gitems = {84};
        int[] gcounts = {1};
        return getItem(pc, items, counts, gitems, gcounts, 1L);
    }

    private boolean getItem(L1PcInstance pc, int[] items, int[] counts, int[] gitems, int[] gcounts, long amount) {
        if (CreateNewItem.checkNewItem(pc, items, counts) < 1L) {
            return true;
        }
        CreateNewItem.createNewItem(pc, items, counts, gitems, amount, gcounts);
        QuestClass.get().startQuest(pc, DarkElfLv50_2.QUEST.get_id());
        QuestClass.get().endQuest(pc, DarkElfLv50_2.QUEST.get_id());
        return true;
    }

    private boolean getKarifBag(L1PcInstance pc, int[] items, int[] counts, int[] gitems, int[] gcounts, long amount) {
        long xcount = CreateNewItem.checkNewItem(pc, items, counts);
        if (xcount >= amount) {
            CreateNewItem.createNewItem(pc, items, counts, gitems, amount, gcounts);
        }
        return true;
    }

    private boolean requestKarifBag(L1PcInstance pc, L1NpcInstance npc, int[] items, int[] counts, int[] gitems, int[] gcounts, String string) {
        boolean isCloseList = false;
        long xcount = CreateNewItem.checkNewItem(pc, items, counts);
        if (xcount == 1L) {
            CreateNewItem.createNewItem(pc, items, counts, gitems, 1L, gcounts);
            isCloseList = true;
        } else if (xcount > 1L) {
            pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, string));
        } else if (xcount < 1L) {
            isCloseList = true;
        }
        return isCloseList;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Karif JD-Core Version: 0.6.2
 */