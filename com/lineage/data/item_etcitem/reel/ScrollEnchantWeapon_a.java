package com.lineage.data.item_etcitem.reel;

import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.cmd.EnchantWeapon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1ItemUpdata;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.util.Random;

public class ScrollEnchantWeapon_a extends ItemExecutor {
    public static ItemExecutor get() {
        return new ScrollEnchantWeapon_a();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("\\fU你必須先解除物品裝備。"));
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
        if ((weaponId >= 301) && (weaponId <= 305)) {
            isErr = false;
        } else {
            isErr = true;
        }
        if (isErr) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (tgItem.getEnchantLevel() == 20) {
            pc.sendPackets(new S_ServerMessage("您的武器已達上限值。"));
            return;
        }
        int enchant_level = tgItem.getEnchantLevel();
        EnchantExecutor enchantExecutor = new EnchantWeapon();
        int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
        pc.getInventory().removeItem(item, 1L);
        boolean isEnchant = true;
        if (enchant_level < -6) {
            isEnchant = false;
        } else if (enchant_level < safe_enchant) {
            isEnchant = true;
        } else {
            Random random = new Random();
            int rnd2 = random.nextInt(100) + 1;
            int enchant_level_tmp;
            if (safe_enchant == 0) {
                enchant_level_tmp = enchant_level + 6;
            } else {
                enchant_level_tmp = enchant_level;
            }
            int enchant_chance_wepon;
            if (enchant_level >= 9) {
                enchant_chance_wepon = (int) L1ItemUpdata.enchant_wepon_up9(enchant_level_tmp);
            } else {
                enchant_chance_wepon = (int) L1ItemUpdata.enchant_wepon_dn9(enchant_level_tmp);
            }
            if (item.getItemId() == 44066) {
                enchant_chance_wepon *= 2;
            }
            if (rnd2 < enchant_chance_wepon) {
                isEnchant = true;
            } else if ((enchant_level >= 9) && (rnd2 < enchant_chance_wepon * 2)) {
                randomELevel = 0;
            } else {
                isEnchant = false;
            }
        }
        if ((randomELevel <= 0) && (enchant_level > -6)) {
            isEnchant = true;
        }
        if (isEnchant) {
            enchantExecutor.successEnchant(pc, tgItem, randomELevel);
            tgItem.setproctect(false);
            pc.sendPackets(new S_ItemStatus(tgItem));
            pc.getInventory().saveItem(tgItem, 4);
            if ((pc.getAccessLevel() == 0) && (tgItem.getEnchantLevel() >= tgItem.getItem().get_safeenchant() + 3)) {
                World.get().broadcastPacketToAll(new S_BlueMessage(166, "\\f=【" + pc.getName() + "】的+" + enchant_level + " " + tgItem.getName() + "強化成功"));
            }
        } else {
            enchantExecutor.failureEnchant(pc, tgItem);
        }
    }
}