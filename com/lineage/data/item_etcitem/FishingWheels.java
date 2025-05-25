package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 卷線輪
 */
public class FishingWheels extends ItemExecutor {
    private int _itemid; // 可以被使用的道具
    private int _itemChargeCount; // 給予的次數
    private int _itemMaxChargeCount; // 次數上限

    /**
     *
     */
    private FishingWheels() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new FishingWheels();
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
        final L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
        if (item1 == null) {
            return;
        }
        final int poleId = item1.getItem().getItemId();
        if (poleId != 83001 // 高彈力釣竿
                && poleId != _itemid) {
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
            return;
        }
        if (item1.getChargeCount() >= _itemMaxChargeCount) { // 次數上限
            pc.sendPackets(new S_ServerMessage(3457)); // 無法繼續使用卷線輪
            return;
        }
        if (poleId == 83001) { // 如果是 高彈力釣竿 給設置的被使用的道具
            pc.getInventory().removeItem(item1, 1); // 刪除 高彈力釣竿
            CreateNewItem.createNewItem(pc, _itemid, 1);
        } else {
            item1.setChargeCount(item1.getChargeCount() + _itemChargeCount); // 更新次數
            pc.getInventory().updateItem(item1, L1PcInventory.COL_CHARGE_COUNT); // 更新次數
        }
        pc.getInventory().removeItem(item, 1); // 刪除卷線輪
    }

    @Override
    public void set_set(final String[] set) {
        try {
            _itemid = Integer.parseInt(set[1]);
            _itemChargeCount = Integer.parseInt(set[2]);
            _itemMaxChargeCount = Integer.parseInt(set[3]);
        } catch (final Exception e) {
        }
    }
}
