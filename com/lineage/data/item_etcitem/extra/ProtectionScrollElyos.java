package com.lineage.data.item_etcitem.extra;

import com.lineage.config.ConfigWeaponArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.cmd.EnchantWeapon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.Random;

public class ProtectionScrollElyos extends ItemExecutor {
    public static ItemExecutor get() {
        return new ProtectionScrollElyos();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if ((tgItem.getItem().getType2() != 1) || (tgItem.getItem().get_safeenchant() <= -1)) {
            pc.sendPackets(new S_SystemMessage("使用對像錯誤，請確認清楚。"));
            return;
        }
        if ((tgItem.getItem().getItemId() >= 339) && (tgItem.getItem().getItemId() <= 344)) {
            if (tgItem.getEnchantLevel() >= 12) {
                pc.sendPackets(new S_SystemMessage("已經超過最高強化上限。"));
                return;
            }
            pc.getInventory().removeItem(item, 1L);
            Random random = new Random();
            EnchantExecutor enchantExecutor = new EnchantWeapon();
            if (random.nextInt(100) <= ConfigWeaponArmor.ELYOS_ENCHANT_SUCCESS) {
                enchantExecutor.successEnchant(pc, tgItem, 1);
            } else {
                enchantExecutor.successEnchant(pc, tgItem, -tgItem.getEnchantLevel());
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.extra.ProtectionScrollElyos JD-Core Version:
 * 0.6.2
 */