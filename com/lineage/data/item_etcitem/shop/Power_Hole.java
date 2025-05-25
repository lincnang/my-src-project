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

import java.util.Random;

public class Power_Hole extends ItemExecutor {  //src039
    private static Random _random = new Random();
    private int _change = -1;

    private Power_Hole() {
    }

    public static ItemExecutor get() {
        return new Power_Hole();
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
                pc.sendPackets(new S_SystemMessage("該物品無法打洞。"));
                break;
        }
        if (power != null) {
            if ((tgItem.getItem().getType2() == 1 && power.get_hole_count() >= StonePowerSet.WEAPONHOLE) || (tgItem.getItem().getType2() == 2 && power.get_hole_count() >= StonePowerSet.ARMORHOLE)) {
                pc.sendPackets(new S_SystemMessage("可打洞的孔數已達上限..."));
                return;
            }
            pc.getInventory().removeItem(item, 1);
            final int random = _random.nextInt(100) + 1;
            if (_change >= random) {
                power.set_item_obj_id(tgItem.getId());
                power.set_hole_count(power.get_hole_count() + 1);
                tgItem.set_power_bless(power);
                if (update) {
                    CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
                } else {
                    CharItemBlessReading.get().storeItem(tgItem.getId(), tgItem.get_power_bless());
                }
                pc.sendPackets(new S_ItemStatus(tgItem));
                pc.sendPackets(new S_ItemName(tgItem));
                pc.sendPackets(new S_SystemMessage("裝備打洞成功。"));
            } else {
                pc.sendPackets(new S_SystemMessage("物品打洞失敗.."));
            }
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _change = Integer.parseInt(set[1]);
            if (_change <= 0) {
                _change = 30;
            }
        } catch (Exception e) {
        }
    }
}
