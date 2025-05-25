package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import william.server_lv;

public class WeaponEnchant extends ItemExecutor {
    private int _safeEnchant;
    private int _enchantLevel;

    private WeaponEnchant() {
    }

    public static ItemExecutor get() {
        return new WeaponEnchant();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        System.out.println(item.getId());
        if (tgItem.getItem().getType2() != 1) {
            pc.sendPackets(new S_SystemMessage("只能對武器類道具附加魔法。"));
            return;
        }
        if (tgItem.getItem().get_safeenchant() < 0) {
            pc.sendPackets(new S_SystemMessage("此武器為不可強化之類型。"));
            return;
        }
        if (tgItem.getItem().get_safeenchant() != _safeEnchant) {
            pc.sendPackets(new S_SystemMessage("您的武器安定值不符合。"));
            return;
        }
        if (tgItem.getEnchantLevel() > _enchantLevel) {
            pc.sendPackets(new S_SystemMessage("您的武器大於強化卷軸賦予的強化值。"));
            return;
        }
        pc.getInventory().removeItem(item, 1);
        tgItem.setEnchantLevel(_enchantLevel);
        pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
        pc.sendPackets(new S_ItemStatus(tgItem));
        pc.sendPackets(new S_SystemMessage("武器強化值附魔成功。"));
        //server_lv.forIntensifyArmor(pc, item);//terry770106
        server_lv.forIntensifyArmor(pc, tgItem); //src1106
        try {
            pc.save();
        } catch (Exception e) {
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _safeEnchant = Integer.parseInt(set[1]);
            _enchantLevel = Integer.parseInt(set[2]);
        } catch (final Exception e) {
        }
    }
}
