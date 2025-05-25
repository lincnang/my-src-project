package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.SuperRuneTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemPower_name;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuperRune_3 extends ItemExecutor {
    private SuperRune_3() {
    }

    public static ItemExecutor get() {
        return new SuperRune_3();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (!tgItem.getItem().isSuperRune() && (tgItem.getItem().getUseType() != 43 || tgItem.getItem().getUseType() != 44 || tgItem.getItem().getUseType() != 45 || tgItem.getItem().getUseType() != 47 || tgItem.getItem().getUseType() != 48)) {
            pc.sendPackets(new S_SystemMessage("只有部分輔助欄位道具可以使用超能附魔。"));
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_SystemMessage("必須脫掉裝備才能附加能力。"));
            return;
        }
        L1ItemPower_name power = tgItem.get_power_name();
        final List<Integer> idListSrc = SuperRuneTable.getInstance().getSuperIdList();
        if (idListSrc == null) {
            System.out.println("超能資料載入錯誤。");
            return;
        }
        final List<Integer> idList = new ArrayList<Integer>();
        idList.addAll(idListSrc);
        if (power != null) {
            final Integer rune1 = power.get_super_rune_1();
            final Integer rune2 = power.get_super_rune_2();
            final Integer rune3 = power.get_super_rune_3();
            idList.remove(rune1);
            idList.remove(rune2);
            idList.remove(rune3);
        }
        pc.getInventory().removeItem(item, 1);
        Collections.shuffle(idList);
        final int type = idList.get(0);
        if (power == null) {
            power = new L1ItemPower_name();
            power.set_item_obj_id(tgItem.getId());
            tgItem.set_power_name(power);
            power.set_super_rune_3(type);
            CharItemPowerReading.get().storeItem(tgItem.getId(), power);
        } else {
            power.set_super_rune_3(type);
            CharItemPowerReading.get().updateItem(tgItem.getId(), power);
        }
        pc.sendPackets(new S_ItemStatus(tgItem));
        pc.sendPackets(new S_SystemMessage("超能附魔成功。"));
    }
}
