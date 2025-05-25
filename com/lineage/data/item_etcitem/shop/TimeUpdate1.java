package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimeUpdate1 extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(TimeUpdate1.class);

    public static ItemExecutor get() {
        return new TimeUpdate1();
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
            if (tgItem.getItem().getType2() == 0) {// 道具類別
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            if (tgItem.getRemainingTime() > 0) {// 還有剩餘時間
                pc.sendPackets(new S_SystemMessage("目前還不需要解除封印。"));
                return;
            }
            if (tgItem.getItem().getMaxUseTime() <= 0) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            pc.getInventory().removeItem(item, 1L);
            int time = tgItem.getItem().getMaxUseTime();
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