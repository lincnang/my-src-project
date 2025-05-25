package com.lineage.data.item_etcitem.doll;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Doll;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimeRecharge extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(TimeRecharge.class);

    public static ItemExecutor get() {
        return new TimeRecharge();
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
            L1Doll doll = DollPowerTable.get().get_type(tgItem.getItemId());
            if (doll == null) {
                // 2,477：只有魔法娃娃可以選擇。
                pc.sendPackets(new S_ServerMessage(2477));
                return;
            }
            if (tgItem.getRemainingTime() > 1800) {// 還有30分鐘以上時間
                pc.sendPackets(new S_ServerMessage(3331));// 只能對剩於30分之內的道具進行充電。
                return;
            }
            if (tgItem.getItem().getMaxUseTime() <= 0) {// DB沒有設定秒數限制
                pc.sendPackets(new S_ServerMessage(3329));// 該道具不是充電類型的道具。
                return;
            }
            pc.getInventory().removeItem(item, 1L);
            int time = 0;
            switch (item.getItemId()) {
                case 44215:
                    time = 18000;// 5小時
                    break;
                case 44216:
                    time = 36000;// 10小時
                    break;
                case 44217:
                    time = 86400;// 24小時
                    break;
                case 44218:
                    time = 360000;// 100小時
                    break;
            }
            tgItem.setRemainingTime(time);
            pc.getInventory().updateItem(tgItem, 256);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.shop.TimeUpdate1 JD-Core Version: 0.6.2
 */