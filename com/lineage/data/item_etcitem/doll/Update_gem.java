package com.lineage.data.item_etcitem.doll;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.utils.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Update_gem extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Update_gem.class);

    public static ItemExecutor get() {
        return new Update_gem();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            int targObjId = data[0];
            L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
            if (tgItem == null) {
                return;
            }
            final L1Doll doll = DollPowerTable.get().get_type(tgItem.getItemId());
            if (doll == null) {
                // 2,477：只有魔法娃娃可以選擇。
                pc.sendPackets(new S_ServerMessage(2477));
                return;
            }
            if (pc.getDoll(tgItem.getId()) != null) {
                // 1,181：這個魔法娃娃目前正在使用中。
                pc.sendPackets(new S_ServerMessage(1181));
                return;
            }
            // 附魔效果1~10
            int[] firegirls = {80290, 80291, 80292, 80293, 80294, 80295, 80296, 80297, 80298, 80299};
            int givegirl = firegirls[Random.getInt(firegirls.length)];
            // 附魔效果11~15
            int[] firegirls2 = {80300, 80301, 80302, 80303, 80304};
            int givegirl2 = firegirls2[Random.getInt(firegirls2.length)];
            // 附魔效果1~10
            int[] fireboys = {80305, 80306, 80307, 80308, 80309, 80310, 80311, 80312, 80313, 80314};
            int giveboy = fireboys[Random.getInt(fireboys.length)];
            // 附魔效果11~15
            int[] fireboys2 = {80315, 80316, 80317, 80318, 80319};
            int giveboy2 = fireboys2[Random.getInt(fireboys2.length)];
            if ((tgItem.getItemId() == 80283) || // 魔法娃娃：魔導師帕歐
                    (tgItem.getItemId() >= 80290 && tgItem.getItemId() <= 80299)) {
                pc.getInventory().removeItem(item, 1L);// 刪除寶石
                pc.getInventory().removeItem(tgItem, 1L);// 刪除原有娃娃
                if (Random.nextInt(100) + 1 <= 94) {// 95%機率 附魔1~10
                    L1ItemInstance giveitem = ItemTable.get().createItem(givegirl);
                    giveitem.setRemainingTime(90000);// 充電9萬秒
                    giveitem.setIdentified(true);
                    giveitem.setCount(1L);
                    pc.getInventory().storeItem(giveitem);
                    return;
                } else {// 5%機率 爆擊火焰衝擊 附魔11~15
                    L1ItemInstance giveitem = ItemTable.get().createItem(givegirl2);
                    giveitem.setRemainingTime(90000);// 充電9萬秒
                    giveitem.setIdentified(true);
                    giveitem.setCount(1L);
                    pc.getInventory().storeItem(giveitem);
                    return;
                }
            } else if ((tgItem.getItemId() == 80288) || // 魔法娃娃：魔騎士帕克
                    (tgItem.getItemId() >= 80305 && tgItem.getItemId() <= 80314)) {
                pc.getInventory().removeItem(item, 1L);// 刪除寶石
                pc.getInventory().removeItem(tgItem, 1L);// 刪除原有娃娃
                if (Random.nextInt(100) + 1 <= 94) {// 95%機率 附魔1~10
                    L1ItemInstance giveitem = ItemTable.get().createItem(giveboy);
                    giveitem.setRemainingTime(90000);// 充電9萬秒
                    giveitem.setIdentified(true);
                    giveitem.setCount(1L);
                    pc.getInventory().storeItem(giveitem);
                    return;
                } else {// 5%機率 爆擊火焰衝擊 附魔11~15
                    L1ItemInstance giveitem = ItemTable.get().createItem(giveboy2);
                    giveitem.setRemainingTime(90000);// 充電9萬秒
                    giveitem.setIdentified(true);
                    giveitem.setCount(1L);
                    pc.getInventory().storeItem(giveitem);
                    return;
                }
            } else {// 沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage("精靈的寶石只可使用於火焰衝擊系列"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}