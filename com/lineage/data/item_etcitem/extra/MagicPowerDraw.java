package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPower_name;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MagicPowerDraw extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(MagicPowerDraw.class);

    public static ItemExecutor get() {
        return new MagicPowerDraw();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 1) {
            pc.sendPackets(new S_SystemMessage("只能對武器類道具提取魔法石。"));
            return;
        }
        L1ItemPower_name power = tgItem.get_power_name();
        if ((power == null) || (power.get_boss_weapon() == null) || (power.get_boss_lv() <= 0) || (power.get_boss_weapon().getMaxUseTime() > 0)) {
            pc.sendPackets(new S_SystemMessage("您的武器沒符合提取規定。"));
            return;
        }
        int weaponMagicPower = power.get_boss_weapon().getItemId();
        int[] magicItemId = {640730, 640731, 640732, 640733, 640734, 640735,  //src005
                640736, 640737, 640738, 640739, 640740, 640741, 640742, 640743, 640744, //src005
                640745, 640746, 640747, 6407348, 640749}; //src005
        for (int i = 0; i < magicItemId.length; i++) {
            if (weaponMagicPower == magicItemId[i]) {
                int addItem = 8 * i;  //src007 #最高階數-1
                weaponMagicPower = magicItemId[i] + 50 + addItem +   //src005
                        power.get_boss_lv();
            }
        }
        int giveItemId = weaponMagicPower;
        L1Item tmp = ItemTable.get().getTemplate(giveItemId);
        if (tmp == null) {
            _log.error("---------- 頭目水晶設置錯誤  ----------");
            return;
        }
        pc.getInventory().storeItem(giveItemId, 1L);
        pc.getInventory().removeItem(item, 1L);
        power.set_boss_lv(0);
        power.set_boss_weapon(null);
        CharItemPowerReading.get().updateItem(tgItem.getId(), power);
        pc.sendPackets(new S_ItemStatus(tgItem));
        pc.sendPackets(new S_SystemMessage("提取頭目水晶成功。"));
        try {
            pc.save();
        } catch (Exception localException) {
        }
    }
}