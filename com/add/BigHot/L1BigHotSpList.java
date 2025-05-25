package com.add.BigHot;

import com.add.L1Config;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;

import java.util.ArrayList;

public class L1BigHotSpList {
    private final L1PcInstance _pc;
    private final ArrayList<int[]> _BigHotList = new ArrayList<int[]>();
    private final ArrayList<L1ItemInstance> _BigHotSellList = new ArrayList<L1ItemInstance>();

    public L1BigHotSpList(L1PcInstance pc) {
        this._pc = pc;
    }

    public void clear() {
        this._BigHotList.clear();
        this._BigHotSellList.clear();
    }

    public void set_copyBigHot(ArrayList<int[]> invList) {
        clear();
        this._BigHotList.addAll(invList);
    }

    /*
     * public void addBigHotItem(int order, int count) { int[] BigHotItem =
     * (int[])this._BigHotList.get(order);
     *
     * int price = L1Config._2163 * count;
     *
     * checkBigHotShopItem(BigHotItem, count, price); }
     */
    /*
     * private void checkBigHotShopItem(int[] BigHotItem, int amount, int
     * totalPrice) { int price = 0; L1ItemInstance priceItem = null;
     *
     * priceItem = this._pc.getInventory().findItemId(L1Config._2167);
     *
     * if (priceItem != null) { price = (int) priceItem.getCount(); } if
     * (totalPrice > price) { this._pc.sendPackets(new
     * S_SystemMessage("商城金幣不足，無法押注。")); } else {
     * this._pc.getInventory().removeItem(priceItem, totalPrice);
     *
     * L1PcInventory inv = this._pc.getInventory();
     *
     * int itemId = L1Config._2170; int BigHotNo = BigHotItem[1]; int
     * BigHotNpcId = BigHotItem[0];
     *
     * L1ItemInstance item = ItemTable.get().createItem(itemId);
     *
     * item.setCount(amount); item.setIdentified(true); item.setGamNo(BigHotNo);
     * item.setGamNpcId(BigHotNpcId);
     *
     * inv.storeItem(item); } }
     */
    public ArrayList<int[]> get_BigHotList() {
        return this._BigHotList;
    }

    public void checkBigHotShop() {
        clear();
    }

    public void set_copySellBigHot(ArrayList<L1ItemInstance> invList) {
        clear();
        this._BigHotSellList.addAll(invList);
    }

    public ArrayList<L1ItemInstance> get_BigHotSellList() {
        return this._BigHotSellList;
    }

    public void addSellBigHotItem(int objid, int count) {
        boolean isOk = false;
        L1ItemInstance BigHotItem = this._pc.getInventory().getItem(objid);
        for (L1ItemInstance chItem : this._BigHotSellList) {
            if (chItem == BigHotItem) {
                isOk = true;
            }
        }
        if (isOk) {
            int BigHotId = BigHotItem.getGamNo();
            int price = 0;
            L1BigHotbling BigHotInfo = BigHotblingLock.create().getBigHotbling(BigHotId);
            if (BigHotInfo != null) {
                String A = BigHotInfo.get_number();
                String B = BigHotItem.getStarNpcId();
                int AB = BigHotInfo.get_money1();
                int BC = BigHotInfo.get_count();
                if (BC != 0) {
                    AB /= BC;
                }
                int CD = BigHotInfo.get_money2();
                int DE = BigHotInfo.get_count1();
                if (DE != 0) {
                    CD /= DE;
                }
                int EF = BigHotInfo.get_money3();
                int FG = BigHotInfo.get_count2();
                if (FG != 0) {
                    EF /= FG;
                }
                int ch = 0;
                for (int a = 0; a < A.split(",").length; a++) {
                    String[] pk = B.split(",");
                    if (("," + A).indexOf("," + pk[a] + ",") >= 0) {
                        ch++;
                    }
                }
                if (ch >= 3) {
                    switch (ch) {
                        case 3:
                            price = 50 * count;
                            break;
                        case 4:
                            price = EF * count;
                            break;
                        case 5:
                            price = CD * count;
                            break;
                        case 6:
                            price = AB * count;
                    }
                }
            }
            checkBigHotSellItem(BigHotItem, count, price);
        }
    }

    private void checkBigHotSellItem(L1ItemInstance BigHotItem, int count, int amount) {
        if (BigHotItem == null) {
            return;
        }
        this._pc.getInventory().removeItem(BigHotItem, count);
        L1PcInventory inv = this._pc.getInventory();
        L1ItemInstance item = null;
        item = ItemTable.get().createItem(L1Config._2167);
        item.setCount(amount);
        inv.storeItem(item);
		/*Bingo("IP(" + this._pc.getNetConnection().getIp() + ")" + "玩家【 " + this._pc.getName() + " 】 " + "中了第【 "
				+ BigHotItem.getGamNo() + " 】 場，彩票號碼：【 " + BigHotItem.getStarNpcId() + " 】，" + "取得彩金：【 " + amount
				+ " 】，" + "時間：(" + new Timestamp(System.currentTimeMillis()) + ")。");*/
        WriteLogTxt.Recording("大樂透中獎紀錄", "IP(" + this._pc.getNetConnection().getIp() + ")" + "玩家【 " + this._pc.getName() + " 】 " + "中了第【 " + BigHotItem.getGamNo() + " 】 場，彩票號碼：【 " + BigHotItem.getStarNpcId() + " 】，" + "取得彩金：【 " + amount + " 】，");
    }

    public void checkBigHotSell() {
        clear();
    }
	/*public static void Bingo(String info) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("record/yuanbao/大樂透中獎紀錄.txt", true));
			out.write(info + "\r\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
