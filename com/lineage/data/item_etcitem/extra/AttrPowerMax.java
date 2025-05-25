package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemBlessReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemPower_bless;

public class AttrPowerMax extends ItemExecutor {
    public static ItemExecutor get() {
        return new AttrPowerMax();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("\\fR你必須先解除物品裝備!"));
            return;
        }
        L1ItemPower_bless power = null;
        boolean update = false;
        switch (tgItem.getItem().getUseType()) {
            case 1:
            case 2:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 25:
                if (tgItem.get_power_bless() != null) {
                    power = tgItem.get_power_bless();
                    update = true;
                } else {
                    power = new L1ItemPower_bless();
                }
                break;
            default:
                pc.sendPackets(new S_SystemMessage("這個物品無法使用強化卷軸。"));
                return;
        }
        if ((power.get_hole_str() >= 5) && (power.get_hole_dex() >= 5) && (power.get_hole_int() >= 5) && (power.get_hole_dmg() >= 5) && (power.get_hole_bowdmg() >= 5) && (power.get_hole_mcdmg() >= 5)) {
            pc.sendPackets(new S_SystemMessage("您的強化能力值已達最大。"));
            return;
        }
        if (power != null) {
            power.set_hole_str(5);
            power.set_hole_int(5);
            power.set_hole_dex(5);
            power.set_hole_dmg(5);
            power.set_hole_bowdmg(5);
            power.set_hole_mcdmg(5);
            tgItem.setBless(1);
            pc.getInventory().updateItem(tgItem, L1PcInventory.COL_IS_ID);
        }
        power.set_item_obj_id(tgItem.getId());
        tgItem.set_power_bless(power);
        pc.getInventory().removeItem(item, 1L);
        if (update) {
            CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
        } else {
            CharItemBlessReading.get().storeItem(tgItem.getId(), tgItem.get_power_bless());
        }
        pc.sendPackets(new S_ItemStatus(tgItem));
    }
}
