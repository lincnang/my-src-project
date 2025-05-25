package com.lineage.data.item_etcitem;

import com.lineage.data.event.CardSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTimeTable;
import com.lineage.server.datatables.ItemVIPTable;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemTime;
import com.lineage.server.templates.L1ItemVIP;

import java.sql.Timestamp;
import java.util.List;

/**
 * vip 系統
 */
public class VIP extends ItemExecutor {
    public static ItemExecutor get() {
        return new VIP();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int item_id = item.getItemId();
        if (!CardSet.START) {
            pc.sendPackets(new S_ServerMessage("\\fR目前未開放VIP道具使用"));
            return;
        }
        L1ItemVIP vip = ItemVIPTable.get().getVIP(item_id);
        if (vip == null) {
            return;
        }
        if (item.isEquipped()) {
            item.setEquipped(false);
            ItemVIPTable.get().deleItemVIP(pc, item_id);
            pc.getInventory().updateItem(item, 8);
            pc.getInventory().saveItem(item, 8);
        } else {
            boolean isvip = false;
            List<L1ItemInstance> items = pc.getInventory().getItems();
            for (L1ItemInstance tgitem : items) {
                if (!tgitem.isEquipped()) {
                    continue;
                }
                int tgitem_id = tgitem.getItemId();
                L1ItemVIP tgvip = ItemVIPTable.get().getVIP(tgitem_id);
                if (tgvip == null) {
                    continue;
                }
                if (tgvip.get_type() == vip.get_type()) {
                    isvip = true;
                    break;
                }
            }
            if (isvip) {
                pc.sendPackets(new S_ServerMessage(66));
            } else {
                item.setEquipped(true);
                ItemVIPTable.get().addItemVIP(pc, item_id);
                set_time_item(item, pc);
                pc.getInventory().updateItem(item, 8);
                pc.getInventory().saveItem(item, 8);
            }
        }
    }

    /**
     * 給予時間限制物品
     *
     */
    private void set_time_item(final L1ItemInstance item, final L1PcInstance pc) {
        if (item.get_time() == null) {
            final L1ItemTime itemTime = ItemTimeTable.TIME.get(item.getItemId());
            if (itemTime != null && itemTime.is_equipped()) {
                /*
                 * final long upTime = System.currentTimeMillis() +
                 * itemTime.get_remain_time() * 60 * 1000;
                 *
                 * final Timestamp ts = new Timestamp(upTime);
                 * item.set_time(ts);
                 */
                long time = System.currentTimeMillis();// 目前時間豪秒
                long x1 = itemTime.get_remain_time() * 60;// 指定分鐘耗用秒數
                long x2 = x1 * 1000;// 轉為豪秒
                long upTime = x2 + time;// 目前時間 加上指定天數耗用秒數
                // 時間數據
                final Timestamp ts = new Timestamp(upTime);
                item.set_time(ts);
                CharItemsTimeReading.get().addTime(item.getId(), ts);
                pc.sendPackets(new S_ItemName(item));
                pc.sendPackets(new S_ItemStatus(item)); // 更新 X年X月X日 以後自動刪除
            }
        }
    }
}