package com.lineage.server.serverpackets;

import com.lineage.config.ConfigAutoAll;
import com.lineage.server.datatables.lock.CharRemoveItemReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 特殊商店<BR>
 * <p>
 * 添加刪除物品
 */
public class S_RemoveItemShopSellList extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 添加刪除物品
     *
     * @param pc
     * @param objid
     */
    public S_RemoveItemShopSellList(final L1PcInstance pc, final int objid) {
        writeC(S_SELL_LIST);
        writeD(objid);
        final Map<Integer, Integer> assessedItems = new HashMap<Integer, Integer>();
        for (final L1ItemInstance item : pc.getInventory().getItems()) {
            switch (item.getItem().getItemId()) {
                case 40308: // 金幣
                case 41246: // 魔法結晶體
                case 44070: // 商城幣
                case 40314: // 項圈
                case 40316: // 高等寵物項圈
                case 83000: // 貝利
                case 83022: // 黃金貝利
                case 80033: // 推廣銀幣
                case 83048: // 龍之幣
                    continue;
            }
            if (item.get_time() != null) {// 有時間性的
                continue;
            }
            if (item.getItem().isCantDelete()) { // 不能刪除的
                continue;
            }
            if (item.isEquipped()) {// 使用中
                continue;
            }
            if (item.getBless() >= 128) {// 封印狀態
                continue;
            }
            String classname = item.getItem().getclassname();
            if (classname.startsWith("doll.Magic_Doll") || classname.startsWith("doll.Magic_Doll2") || classname.startsWith("doll.Magic_Doll_Power")) { // 娃娃
                continue;
            }
            // 添加過的不再顯示到窗口
            if (CharRemoveItemReading.get().getUserItems(pc.getId(), item.getItem().getItemId())) {
                continue;
            }
            // 刪物上限
            if (pc.getRemoveItemInventory().getSize() >= ConfigAutoAll.Remove_Item_Max) {
                pc.sendPackets(new S_SystemMessage("\\aE超過添加刪物上限" + ConfigAutoAll.Remove_Item_Max + "個"));
                writeH(0x0000);
                return;
            }
            pc.get_otherList().add_removeItemSellList(item, item.getId());
            assessedItems.put(item.getId(), 1);
        }
        writeH(assessedItems.size());
        for (final Entry<Integer, Integer> entrySet : assessedItems.entrySet()) {
            writeD(entrySet.getKey());
            writeD(entrySet.getValue());
        }
        writeH(18101);
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
