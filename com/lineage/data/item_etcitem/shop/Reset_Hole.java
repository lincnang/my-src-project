package com.lineage.data.item_etcitem.shop;

import com.lineage.data.event.StonePowerSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemBlessReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemPower_bless;

public class Reset_Hole extends ItemExecutor {  //src039
    private Reset_Hole() {
    }

    public static ItemExecutor get() {
        return new Reset_Hole();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        L1ItemPower_bless power = null;
        boolean update = false;
        int count = 0;
        switch (tgItem.getItem().getUseType()) {
            case 1:
                count = StonePowerSet.WEAPONHOLE;
                if (tgItem.get_power_bless() != null) {
                    power = tgItem.get_power_bless();
                    update = true;
                } else {
                    power = new L1ItemPower_bless();
                }
                break;
            case 2:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 25:
                count = StonePowerSet.ARMORHOLE;
                if (tgItem.get_power_bless() != null) {
                    power = tgItem.get_power_bless();
                    update = true;
                } else {
                    power = new L1ItemPower_bless();
                }
                break;
        }
        if (power != null) {
            if ((tgItem.getItem().getType2() == 1 && power.get_hole_count() != StonePowerSet.WEAPONHOLE) || (tgItem.getItem().getType2() == 2 && power.get_hole_count() != StonePowerSet.ARMORHOLE)) {
                pc.sendPackets(new S_SystemMessage("請洞數達到最大在使用。"));
                return;
            }
            if (power.get_hole_1() == 0 && power.get_hole_2() == 0 && power.get_hole_3() == 0 && power.get_hole_4() == 0 && power.get_hole_5() == 0) {
                pc.sendPackets(new S_SystemMessage("裝備並沒有鑲嵌能力。"));
                return;
            }
            power.set_item_obj_id(tgItem.getId());
            power.set_hole_count(count);
            power.set_hole_1(0);
            power.set_hole_2(0);
            power.set_hole_3(0);
            power.set_hole_4(0);
            power.set_hole_5(0);
            tgItem.set_power_bless(power);
            pc.getInventory().removeItem(item, 1);
            if (update) {
                CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
            } else {
                CharItemBlessReading.get().storeItem(tgItem.getId(), tgItem.get_power_bless());
            }
            pc.sendPackets(new S_SystemMessage("裝備鑲嵌能力重置成功。"));
            pc.sendPackets(new S_ItemStatus(tgItem));
            pc.sendPackets(new S_ItemName(tgItem));
        }
    }
}
