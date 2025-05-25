package com.lineage.data.item_etcitem.shop;

import com.lineage.data.event.CardSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

public class VIP_Card_ extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(VIP_Card_.class);

    public static ItemExecutor get() {
        return new VIP_Card_();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            if (!CardSet.START) {
                pc.sendPackets(new S_ServerMessage("\\fR目前未開放VIP道具使用"));
                return;
            }
            if (item.get_card_use() == 2) {
                pc.sendPackets(new S_ServerMessage("\\fT這張卡片已經到期!"));
                return;
            }
            if (item.get_card_use() == 1) {
                item.set_card_use(0);
                item.setEquipped(false);
                CardSet.remove_card_mode(pc, item);
                pc.sendPackets(new S_ItemName(item));
                return;
            }
            if ((item.get_card_use() == 0) && (item.get_time() != null)) {
                item.set_card_use(1);
                item.setEquipped(true);
                CardSet.set_card_mode(pc, item);
                pc.sendPackets(new S_ItemName(item));
                return;
            }
            for (L1ItemInstance itemcard : pc.getInventory().getItems()) {
                if ((itemcard.getItem().getclassname().startsWith("shop.VIP_Card_")) && (itemcard.get_time() != null)) {
                    pc.sendPackets(new S_ServerMessage("\\fR你已使用的VIP卡：" + itemcard.getName() + "未到期。"));
                    return;
                }
            }
            long time = System.currentTimeMillis();
            long x1 = CardSet.USE_TIME * 60 * 60;
            if (item.getItem().getclassname().startsWith("shop.VIP_Card_ A")) {
                x1 = CardSet.USE_TIME2 * 60 * 60;
            }
            long x2 = x1 * 1000L;
            long upTime = x2 + time;
            Timestamp ts = new Timestamp(upTime);
            item.set_time(ts);
            item.set_card_use(1);
            CharItemsTimeReading.get().addTime(item.getId(), ts);
            pc.sendPackets(new S_ItemName(item));
            CardSet.set_card_mode(pc, item);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
