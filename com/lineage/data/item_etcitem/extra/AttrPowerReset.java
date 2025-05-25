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

public class AttrPowerReset extends ItemExecutor {
    public static ItemExecutor get() {
        return new AttrPowerReset();
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
        boolean itemuse = false;
        switch (tgItem.getItem().getUseType()) {
            case 1:
            case 2:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 25:
                itemuse = true;
        }
        if (!itemuse) {
            pc.sendPackets(new S_SystemMessage("這個物品無法使用強化卷軸。"));
        }
        if (tgItem.get_power_bless() == null) {
            pc.sendPackets(new S_SystemMessage("這個物品沒有強化過。"));
            return;
        }
        power = tgItem.get_power_bless();
        if ((power.get_hole_str() >= 5) && (power.get_hole_dex() >= 5) && (power.get_hole_int() >= 5) && (power.get_hole_dmg() >= 5) && (power.get_hole_bowdmg() >= 5) && (power.get_hole_mcdmg() >= 5)) {
            pc.sendPackets(new S_SystemMessage("您的強化能力值已達最大還要重置？"));
            return;
        }
        if ((power.get_hole_str() == 0) && (power.get_hole_dex() == 0) && (power.get_hole_int() == 0) && (power.get_hole_dmg() == 0) && (power.get_hole_bowdmg() == 0) && (power.get_hole_mcdmg() == 0)) {
            pc.sendPackets(new S_SystemMessage("您的強化能力值皆為0，不需要重置。"));
            return;
        }
        if (power != null) {
            power.set_hole_str(0);
            power.set_hole_dex(0);
            power.set_hole_int(0);
            power.set_hole_dmg(0);
            power.set_hole_bowdmg(0);
            power.set_hole_mcdmg(0);
            tgItem.setBless(1);
            pc.getInventory().updateItem(tgItem, L1PcInventory.COL_IS_ID);
            pc.getInventory().removeItem(item, 1L);
            CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
            pc.sendPackets(new S_ItemStatus(tgItem));
            pc.sendPackets(new S_SystemMessage("您的裝備強化能力值重置。"));
        }
    }
}
