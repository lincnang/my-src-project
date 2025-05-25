package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTimeTable;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

public class TimeUpdate2 extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(TimeUpdate2.class);

    public static ItemExecutor get() {
        return new TimeUpdate2();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        try {
            int targObjId = data[0];
            L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
            if (tgItem == null) {
                return;
            }
            if (tgItem.get_time() == null) {
                pc.sendPackets(new S_ServerMessage(79));
            } else {
                final L1ItemTime itemTime = ItemTimeTable.TIME.get(tgItem.getItemId());
                //Integer date = (Integer) ItemTimeTable.TIME.get(Integer.valueOf(tgItem.getItemId()));
                if (itemTime.get_remain_time() != 0) {
                    pc.getInventory().removeItem(item, 1L);
                    long time = System.currentTimeMillis();
                    long x1 = itemTime.get_remain_time() * 60L * 60L;
                    long x2 = x1 * 1000L;
                    long upTime = x2 + time;
                    Timestamp ts = new Timestamp(upTime);
                    tgItem.set_time(ts);
                    CharItemsTimeReading.get().updateTime(tgItem.getId(), ts);
                    pc.sendPackets(new S_ItemName(tgItem));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
