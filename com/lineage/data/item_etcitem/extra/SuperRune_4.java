package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemPower_name;

public class SuperRune_4 extends ItemExecutor {
    private int _type = 0;

    private SuperRune_4() {
    }

    public static ItemExecutor get() {
        return new SuperRune_4();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (_type == 0) {
            System.out.println("還擊石設定有問題....");
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
        if (power == null) {
            pc.sendPackets(new S_SystemMessage("前三個欄位必須付魔才能使用。"));
            return;
        } else {
            if (power.get_super_rune_1() == 0 || power.get_super_rune_2() == 0 || power.get_super_rune_3() == 0) {
                pc.sendPackets(new S_SystemMessage("前三個欄位必須付魔才能使用。"));
                return;
            }
            power.set_super_rune_4(_type);
            CharItemPowerReading.get().updateItem(tgItem.getId(), power);
        }
        pc.getInventory().removeItem(item, 1);
        pc.sendPackets(new S_ItemStatus(tgItem));
        pc.sendPackets(new S_SystemMessage("還擊石附魔成功。"));
        if (_type == 1) {
            pc.sendPackets(new S_SystemMessage("受到近戰攻擊時15%機率還擊80點傷害，裝備多個傷害會累加。"));
        } else if (_type == 2) {
            pc.sendPackets(new S_SystemMessage("受到遠攻攻擊時15%機率還擊80點傷害，裝備多個傷害會累加。"));
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _type = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
