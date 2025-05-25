package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import william.server_lv;

public class WeaponEnchantDraw extends ItemExecutor {
    private WeaponEnchantDraw() {
    }

    public static ItemExecutor get() {
        return new WeaponEnchantDraw();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 1) {
            pc.sendPackets(new S_SystemMessage("只能對武器類提取強化值。"));
            return;
        }
        final int enchantLevel = tgItem.getEnchantLevel();
        final int safeEnchant = tgItem.getItem().get_safeenchant();
        if (safeEnchant < 0) {
            pc.sendPackets(new S_SystemMessage("此武器為不可強化之類型。"));
            return;
        }
        if (enchantLevel <= safeEnchant) {
            pc.sendPackets(new S_SystemMessage("您的武器在安定值內，不建議提取強化值。"));
            return;
        }
        if (enchantLevel > 12) {
            pc.sendPackets(new S_SystemMessage("您的武器強化值超過伺服器的範圍。"));
            return;
        }
        int itemEnchant_0 = 61301;
        int itemEnchant_6 = 61313;
        int itemEnchant_8 = 61319;
        int itemEnchant_9 = 61323;
        int getItem = 0;
        switch (safeEnchant) {
            case 0:
                itemEnchant_0 += enchantLevel;
                getItem = itemEnchant_0 - 1;
                break;
            case 6:
                itemEnchant_6 += enchantLevel - safeEnchant;
                getItem = itemEnchant_6 - 1;
                break;
            case 8:
                itemEnchant_8 += enchantLevel - safeEnchant;
                getItem = itemEnchant_8 - 1;
                break;
            case 9:
                itemEnchant_9 += enchantLevel - safeEnchant;
                getItem = itemEnchant_9 - 1;
                break;
            default:
                break;
        }
        if (getItem == 0) {
            return;
        }
        final L1Item itemX = ItemTable.get().getTemplate(getItem);
        if (itemX != null) {
            pc.getInventory().removeItem(item, 1);
            pc.getInventory().storeItem(getItem, 1);
            tgItem.setEnchantLevel(0);
            pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
            pc.sendPackets(new S_ItemStatus(tgItem));
            pc.sendPackets(new S_SystemMessage("武器強化值提取成功。"));
            //server_lv.forIntensifyArmor(pc, item);//src056
            server_lv.forIntensifyArmor(pc, tgItem); //src1106
        }
        try {
            pc.save();
        } catch (Exception e) {
        }
    }
}
