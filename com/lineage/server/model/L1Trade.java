package com.lineage.server.model;

import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.OtherUserTitleReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_TradeAddItem;
import com.lineage.server.serverpackets.S_TradeStatus;
import com.lineage.server.world.World;

import java.util.List;

/**
 * 玩家相互交易判斷類
 *
 * @author L1jJP
 */
public class L1Trade {
    /**
     * 加入交易物品到視窗
     *
     * @param pc
     * @param itemid
     * @param itemcount
     */
    public void tradeAddItem(final L1PcInstance pc, final int itemObjid, final long itemcount) {
        // 取回交易對象
        final L1PcInstance tradeTarget = (L1PcInstance) World.get().findObject(pc.getTradeID());
        if (tradeTarget == null) {
            return;
        }
        // 取回要加入交易的物品
        final L1ItemInstance item = pc.getInventory().getItem(itemObjid);
        if (item == null) {
            return;
        }
        if (item.isEquipped()) {
            return;
        }
        // 數據合法性
        // 身上物品數量不足封包數據中的數量或者封包數據中的數量不足1件
        if ((item.getCount() < itemcount) || (itemcount < 1)) {
            pc.sendPackets(new S_TradeStatus(1));
            tradeTarget.sendPackets(new S_TradeStatus(1));
            pc.setTradeOk(false);
            tradeTarget.setTradeOk(false);
            pc.setTradeID(0);
            tradeTarget.setTradeID(0);
            return;
        }
        pc.getInventory().tradeItem(item, itemcount, pc.getTradeWindowInventory()); // 交易到視窗
        pc.sendPackets(new S_TradeAddItem(item, itemcount, 0)); // 視窗顯示封包
        tradeTarget.sendPackets(new S_TradeAddItem(item, itemcount, 1)); // 視窗顯示封包
    }

    /**
     * 執行交易
     *
     * @param pc
     */
    public void tradeOK(final L1PcInstance pc) {
        // 取回交易對象
        final L1PcInstance tradeTarget = (L1PcInstance) World.get().findObject(pc.getTradeID());
        if (tradeTarget != null) {
            // 取回自己的交易物品
            final List<L1ItemInstance> ownTradeList = pc.getTradeWindowInventory().getItems();
            // 取回對象交易物品
            final List<L1ItemInstance> tarTradeList = tradeTarget.getTradeWindowInventory().getItems();
            if (!ownTradeList.isEmpty()) {
                for (L1ItemInstance pcItem : ownTradeList) {
                    // 物品轉移
                    pc.getTradeWindowInventory().tradeItem(pcItem, pcItem.getCount(), tradeTarget.getInventory());
                    final L1ItemInstance Item1 = ItemTable.get().createItem(pcItem.getItemId());
                    if (pcItem.getItemId() == 40312 // 旅館鑰匙
                            || pcItem.getItemId() == 82503 // 訓練所鑰匙
                            || pcItem.getItemId() == 82504) {// 龍門憑證
                        Item1.setInnNpcId(pcItem.getInnNpcId());
                        Item1.setKeyId(pcItem.getKeyId());
                        Item1.setHall(pcItem.checkRoomOrHall());
                        Item1.setDueTime(pcItem.getDueTime());
                    }
                    // (自己)個人交易物品紀錄
                    OtherUserTitleReading.get().add(pcItem.getItem().getName() + "(" + pcItem.getItemId() + ")", pcItem.getId(), 0, pcItem.getCount(), tradeTarget.getId(), tradeTarget.getName(), pc.getId(), pc.getName());
                    // 交易記錄
                    WriteLogTxt.Recording("交易記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】交易物品【" + pcItem.getRecordName(pcItem.getCount()) + ", (ObjectId: " + pcItem.getId() + ")】 給 IP(" + tradeTarget.getNetConnection().getIp() + ")玩家:【 " + tradeTarget.getName() + " 】.");
                }
            }
            if (!tarTradeList.isEmpty()) {
                for (L1ItemInstance tgPcItem : tarTradeList) {
                    // 交易對象物品轉移
                    tradeTarget.getTradeWindowInventory().tradeItem(tgPcItem, tgPcItem.getCount(), pc.getInventory());
                    final L1ItemInstance Item2 = ItemTable.get().createItem(tgPcItem.getItemId());
                    if (tgPcItem.getItemId() == 40312 // 旅館鑰匙
                            || tgPcItem.getItemId() == 82503 // 訓練所鑰匙
                            || tgPcItem.getItemId() == 82504) {// 龍門憑證
                        Item2.setInnNpcId(tgPcItem.getInnNpcId());
                        Item2.setKeyId(tgPcItem.getKeyId());
                        Item2.setHall(tgPcItem.checkRoomOrHall());
                        Item2.setDueTime(tgPcItem.getDueTime());
                    }
                    // (交易對象)個人交易物品紀錄
                    OtherUserTitleReading.get().add(tgPcItem.getItem().getName() + "(" + tgPcItem.getItemId() + ")", tgPcItem.getId(), 0, tgPcItem.getCount(), pc.getId(), pc.getName(), tradeTarget.getId(), tradeTarget.getName());
                    // 交易記錄
                    WriteLogTxt.Recording("交易記錄", "IP(" + tradeTarget.getNetConnection().getIp() + ")玩家:【 " + tradeTarget.getName() + " 】交易物品【" + tgPcItem.getRecordName(tgPcItem.getCount()) + ", (ObjectId: " + tgPcItem.getId() + ")】 給 IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】.");
                }
            }
            pc.sendPackets(new S_TradeStatus(0));
            tradeTarget.sendPackets(new S_TradeStatus(0));
            pc.setTradeOk(false);
            tradeTarget.setTradeOk(false);
            pc.setTradeID(0);
            tradeTarget.setTradeID(0);
            pc.turnOnOffLight();
            tradeTarget.turnOnOffLight();
        }
    }

    /**
     * 交易取消
     *
     * @param pc
     */
    public void tradeCancel(final L1PcInstance pc) {
        final L1PcInstance tradeTarget = (L1PcInstance) World.get().findObject(pc.getTradeID());
        if (tradeTarget != null) {
            // 取回自己的交易物品
            final List<L1ItemInstance> ownTradeList = pc.getTradeWindowInventory().getItems();
            // 取回對象交易物品
            final List<L1ItemInstance> tarTradeList = tradeTarget.getTradeWindowInventory().getItems();
            if (!ownTradeList.isEmpty()) {
                for (L1ItemInstance pcItem : ownTradeList) {
                    // 物品還回
                    pc.getTradeWindowInventory().tradeItem(pcItem, pcItem.getCount(), pc.getInventory());
                }
            }
            if (!tarTradeList.isEmpty()) {
                for (L1ItemInstance tgPcItem : tarTradeList) {
                    // 物品還回
                    tradeTarget.getTradeWindowInventory().tradeItem(tgPcItem, tgPcItem.getCount(), tradeTarget.getInventory());
                }
            }
            pc.sendPackets(new S_TradeStatus(1));
            tradeTarget.sendPackets(new S_TradeStatus(1));
            pc.setTradeOk(false);
            tradeTarget.setTradeOk(false);
            pc.setTradeID(0);
            tradeTarget.setTradeID(0);
        }
    }
}
