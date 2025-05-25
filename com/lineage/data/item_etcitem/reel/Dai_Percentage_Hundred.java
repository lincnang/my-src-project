package com.lineage.data.item_etcitem.reel;

import com.lineage.config.ConfigWeaponArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.cmd.EnchantWeapon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import william.server_lv;

public class Dai_Percentage_Hundred extends ItemExecutor {
    public static ItemExecutor get() {
        return new Dai_Percentage_Hundred();
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
        int safe_enchant = tgItem.getItem().get_safeenchant();
        boolean isErr = false;
        int use_type = tgItem.getItem().getUseType();
        switch (use_type) {
            case 1:
                if (safe_enchant < 0) {
                    isErr = true;
                }
                break;
            default:
                isErr = true;
        }
        int weaponId = tgItem.getItem().getItemId();
        if ((weaponId >= 246) && (weaponId <= 255)) {
            isErr = true;
        }
        if (tgItem.getBless() >= 128) {
            isErr = true;
        }
        if (isErr) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (tgItem.getEnchantLevel() < ConfigWeaponArmor.WEAPON100) {
            pc.getInventory().removeItem(item, 1L);
            EnchantExecutor enchantExecutor = new EnchantWeapon();
            int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
            enchantExecutor.successEnchant(pc, tgItem, randomELevel);
            server_lv.forIntensifyArmor(pc, tgItem);//terry770106
            ;
        } else {
            pc.sendPackets(new S_ServerMessage("已達武器最大強化!"));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.reel.Dai_Percentage_Hundred JD-Core Version:
 * 0.6.2
 */