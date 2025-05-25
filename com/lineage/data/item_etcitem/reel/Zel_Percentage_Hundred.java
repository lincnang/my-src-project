package com.lineage.data.item_etcitem.reel;

import com.lineage.config.ConfigWeaponArmor;
import com.lineage.data.cmd.EnchantArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import william.server_lv;

public class Zel_Percentage_Hundred extends ItemExecutor {
    public static ItemExecutor get() {
        return new Zel_Percentage_Hundred();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("(預防誤點機制啟動)裝備中無法強化"));// 沒有任何事發生
            return;
        }
        boolean isErr = false;
        int safe_enchant = tgItem.getItem().get_safeenchant();
        int use_type = tgItem.getItem().getUseType();
        switch (use_type) {
            case 2:// 盔甲
            case 18:// T恤
            case 19:// 斗篷
            case 20:// 手套
            case 21:// 靴
            case 22:// 頭盔
            case 25:// 盾牌
            case 70:// 脛甲
                if (safe_enchant < 0) {
                    isErr = true;
                }
                break;
            default:
                isErr = true;
        }
        if (tgItem.getBless() >= 128) {
            isErr = true;
        }
        if (isErr) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (tgItem.getEnchantLevel() < ConfigWeaponArmor.ARMOR100) {
            pc.getInventory().removeItem(item, 1L);
            EnchantExecutor enchantExecutor = new EnchantArmor();
            int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
            enchantExecutor.successEnchant(pc, tgItem, randomELevel);
            server_lv.forIntensifyArmor(pc, tgItem);//terry770106
        } else {
            pc.sendPackets(new S_ServerMessage("已達防具最大強化!"));
        }
    }
}
