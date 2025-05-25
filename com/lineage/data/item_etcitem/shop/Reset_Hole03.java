package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemBlessReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPower_bless;

import java.util.Random;

public class Reset_Hole03 extends ItemExecutor {//src039
    private static Random _random = new Random();

    private Reset_Hole03() {
    }

    public static ItemExecutor get() {
        return new Reset_Hole03();
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
                if (tgItem.get_power_bless() != null) {
                    power = tgItem.get_power_bless();
                    update = true;
                } else {
                    power = new L1ItemPower_bless();
                }
                break;
        }
        if (power != null) {
            if ((tgItem.getItem().getType2() == 1 && power.get_hole_3() == 0) || (tgItem.getItem().getType2() == 2 && power.get_hole_3() == 0)) {
                pc.sendPackets(new S_SystemMessage("您的凹槽內沒有物品。"));
                return;
            }
            final int chance = _random.nextInt(100) + 1;
            if (50 >= chance) {
                final int itemId = power.get_hole_3();
                final L1Item newItem = ItemTable.get().getTemplate(itemId);
                if (newItem != null) {
                    pc.getInventory().storeItem(newItem.getItemId(), 1);
                    pc.sendPackets(new S_SystemMessage("成功提取寶石。"));
                } else {
                    pc.sendPackets(new S_SystemMessage("出現意外請聯絡遊戲管理員。"));
                }
            } else {
                pc.sendPackets(new S_SystemMessage("提取寶石失敗。"));
            }
            power.set_item_obj_id(tgItem.getId());
            power.set_hole_3(0);
            tgItem.set_power_bless(power);
            pc.getInventory().removeItem(item, 1);
            if (update) {
                CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
            } else {
                CharItemBlessReading.get().storeItem(tgItem.getId(), tgItem.get_power_bless());
            }
            pc.sendPackets(new S_ItemStatus(tgItem));
            pc.sendPackets(new S_ItemName(tgItem));
        }
    }
}
