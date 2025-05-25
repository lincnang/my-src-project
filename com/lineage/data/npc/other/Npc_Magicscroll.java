package com.lineage.data.npc.other;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

public class Npc_Magicscroll extends NpcExecutor {
    private int oldItem = 0;
    private int newItem = 0;
    private int moneyCount = 0;

    public static NpcExecutor get() {
        return new Npc_Magicscroll();
    }

    @Override
    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bs_m5"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        String selectStr = "request magicscroll ";
        //40090	空的魔法卷軸(等級1)
        //40091	空的魔法卷軸(等級2)
        //40092	空的魔法卷軸(等級3)
        //40093	空的魔法卷軸(等級4)
        //40094	空的魔法卷軸(等級5)
        //40859	魔法卷軸 (初級治癒術)
        //40860	魔法卷軸 (日光術)
        //40861	魔法卷軸 (保護罩)
        //40862	魔法卷軸 (光箭)
        //40866	魔法卷軸 (神聖武器)
        if (cmd.equalsIgnoreCase(selectStr + "01")) {
            oldItem = 40090;
            newItem = 40860;
            moneyCount = 500;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "02")) {
            oldItem = 40090;
            newItem = 40861;
            moneyCount = 500;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "03")) {
            oldItem = 40090;
            newItem = 40862;
            moneyCount = 500;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "04")) {
            oldItem = 40090;
            newItem = 40866;
            moneyCount = 500;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "05")) {
            oldItem = 40090;
            newItem = 40859;
            moneyCount = 500;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "06")) {
            oldItem = 40091;
            newItem = 40872;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "07")) {
            oldItem = 40091;
            newItem = 40871;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "08")) {
            oldItem = 40091;
            newItem = 40870;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "09")) {
            oldItem = 40091;
            newItem = 40867;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "10")) {
            oldItem = 40091;
            newItem = 40873;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "11")) {
            oldItem = 40092;
            newItem = 40875;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "12")) {
            oldItem = 40092;
            newItem = 40879;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "13")) {
            oldItem = 40092;
            newItem = 40877;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "14")) {
            oldItem = 40092;
            newItem = 40880;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "15")) {
            oldItem = 40092;
            newItem = 40876;
            moneyCount = 1000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "16")) {
            oldItem = 40093;
            newItem = 40890;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "17")) {
            oldItem = 40093;
            newItem = 40883;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "18")) {
            oldItem = 40093;
            newItem = 40884;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "19")) {
            oldItem = 40093;
            newItem = 40889;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "20")) {
            oldItem = 40093;
            newItem = 40887;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "21")) {
            oldItem = 40094;
            newItem = 40893;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "22")) {
            oldItem = 40094;
            newItem = 40895;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "23")) {
            oldItem = 40094;
            newItem = 40897;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "24")) {
            oldItem = 40094;
            newItem = 40896;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase(selectStr + "25")) {
            oldItem = 40094;
            newItem = 40892;
            moneyCount = 2000;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase("a1")) {
            int[] items = {oldItem, 40308};
            int[] counts = {1, moneyCount};
            int[] gitems = {newItem};
            int[] gcounts = {1};
            long xcount = CreateNewItem.checkNewItem(pc, items, counts);
            if (xcount >= amount) {
                CreateNewItem.createNewItem(pc, items, counts, gitems, amount, gcounts);
            }
            isCloseList = true;
        } else {
            return;
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    public void showItemCount(L1PcInstance pc, L1NpcInstance npc, int oldItem, int newItem, int moneyCount) {
        int[] items = {oldItem, 40308};
        int[] count = {1, moneyCount};
        int[] gitems = {newItem};
        int[] gcounts = {1};
        long xcount = CreateNewItem.checkNewItem(pc, items, count);
        if (xcount == 1L) {
            CreateNewItem.createNewItem(pc, items, count, gitems, 1L, gcounts);
            pc.sendPackets(new S_CloseList(pc.getId()));
        } else if (xcount > 1L) {
            pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "a1"));
        } else if (xcount < 1L) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Hector JD-Core Version: 0.6.2
 */