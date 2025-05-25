package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 保羅的快速卷線器
 */
public class FishingPoleRoller extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(FishingPoleRoller.class);
    private int _fishpoleid;

    public static ItemExecutor get() {
        return new FishingPoleRoller();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int itemobj = data[0];
        final L1ItemInstance tgitem = pc.getInventory().getItem(itemobj);
        if (tgitem == null) {
            return;
        }
        this.useRoller(pc, tgitem, item);
    }

    private void useRoller(final L1PcInstance pc, final L1ItemInstance tgitem, final L1ItemInstance item) {
        if ((tgitem == null) || (item == null)) {
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
            return;
        }
        if (tgitem.getItem().getUseType() != 42) { // 釣魚竿
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
            return;
        }
        if (tgitem.getItemId() == 83014 && item.getItemId() != 83004) {// 卷線器類型不符
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
            return;
        }
        if (tgitem.getItemId() == 83024 && item.getItemId() != 83023) {// 卷線器類型不符
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
            return;
        }
        if (tgitem.getItemId() == 83032 && item.getItemId() != 83031) {// 卷線器類型不符
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
            return;
        }
        if (tgitem.getItemId() == 83014 || tgitem.getItemId() == 83024 || tgitem.getItemId() == 83032) {// 裝上卷線器的高彈力釣竿
            int maxchargecount = tgitem.getItem().getMaxChargeCount();// 最大使用次數
            if (tgitem.getChargeCount() == 500) {
                pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
                return;
            }
            if (tgitem.getChargeCount() + 100 >= maxchargecount) {
                tgitem.setChargeCount(maxchargecount);
            } else {
                tgitem.setChargeCount(tgitem.getChargeCount() + 100);// 增加100次使用次數
            }
            pc.getInventory().updateItem(tgitem, L1PcInventory.COL_CHARGE_COUNT);// 更新使用次數
            pc.getInventory().removeItem(item, 1);
        }
        if (tgitem.getItemId() == 83001) {// 高彈力釣竿
            L1ItemInstance newitem = ItemTable.get().createItem(_fishpoleid);// 裝上卷線器的高彈力釣竿
            newitem.setChargeCount(100);
            newitem.setIdentified(true);
            pc.getInventory().storeItem(newitem);
            pc.getInventory().removeItem(tgitem, 1);
            pc.getInventory().removeItem(item, 1);
        }
    }

    public void set_set(String[] set) {
        try {
            _fishpoleid = Integer.parseInt(set[1]);
            if (_fishpoleid <= 0) {
                _fishpoleid = 83014;
                _log.error("FishingPoleRoller 設置錯誤:釣竿編號錯誤! 使用預設83014");
            }
        } catch (Exception localException) {
        }
    }
}
