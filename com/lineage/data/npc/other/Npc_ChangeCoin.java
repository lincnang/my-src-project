package com.lineage.data.npc.other;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.sql.ServerGmCommandTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Npc_ChangeCoin extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_ChangeCoin.class);
    private int oldItem = 0;
    private int newItem = 0;
    private int moneyCount = 0;

    public static NpcExecutor get() {
        return new Npc_ChangeCoin();
    }

    /**
     * 兌換商城
     *
     */
    public static void changelog(L1PcInstance player, long xcount) {
        try {
            File DeleteLog = new File("./物品操作記錄/log/※以下是玩家[兌換商城幣]的所有紀錄※.txt");
            BufferedWriter out;
            if (DeleteLog.createNewFile()) {
                out = new BufferedWriter(new FileWriter("./物品操作記錄/log/※以下是玩家[兌換商城幣]的所有紀錄※.txt", false));
                out.write("※以下是玩家[兌換商城幣]的所有紀錄※" + "\r\n");
                out.close();
            }
            out = new BufferedWriter(new FileWriter("./物品操作記錄/log/※以下是玩家[兌換商城幣]的所有紀錄※.txt", true));
            out.write("\r\n");// 每次填寫資料都控一行
            out.write(" 帳號: " + player.getAccountName() + " 玩家: " + player.getName() + " 兌換: " + xcount + " 時間: " + new Timestamp(System.currentTimeMillis()) + ">" + "\r\n");
            out.close();
        } catch (IOException e) {
            System.out.println("以下是錯誤訊息: " + e.getMessage());
        }
    }

    @Override
    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        final String[] info = new String[]{String.valueOf(ConfigOtherSet2.CHANGE_COUNT)};// 目前比值
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "shopcoin", info));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        int count = ConfigOtherSet2.CHANGE_COUNT;
        L1ItemInstance item = pc.getInventory().findItemId(44070);
        if (item != null && ServerGmCommandTable.tradeControl.contains(item.getId())) {// 限制轉移物品
            pc.sendPackets(new S_ServerMessage("身上有不能轉移的獎勵商幣,請先使用完畢"));
            _log.error("角色購買商幣異常: " + pc.getName() + ":" + item.getId());
            return;
        }
        if (cmd.equalsIgnoreCase("shopcoin")) {
            oldItem = 40308;
            newItem = 80033;
            moneyCount = count;
            showItemCount(pc, npc, oldItem, newItem, moneyCount);
        } else if (cmd.equalsIgnoreCase("a1")) {
            int[] items = {oldItem};
            int[] counts = {moneyCount};
            int[] gitems = {newItem};
            int[] gcounts = {1};
            long xcount = CreateNewItem.checkNewItem(pc, items, counts);
            if (xcount >= amount) {
                CreateNewItem.createNewItem(pc, items, counts, gitems, amount, gcounts);
            }
            changelog(pc, amount);
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