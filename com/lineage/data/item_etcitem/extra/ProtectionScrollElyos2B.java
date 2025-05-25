package com.lineage.data.item_etcitem.extra;

import com.lineage.config.ConfigWeaponArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.cmd.EnchantWeapon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import william.server_lv;

import java.util.Random;

/**
 * 魔族武器保護卷軸
 *
 * @author terry0412
 */
public class ProtectionScrollElyos2B extends ItemExecutor {
    private int _chance;

    /**
     *
     */
    private ProtectionScrollElyos2B() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new ProtectionScrollElyos2B();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        // 對像OBJID
        final int targObjId = data[0];
        // 目標物品
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        // 目標對像必須是武器
        if (tgItem.getItem().getType2() != 2) {
            pc.sendPackets(new S_SystemMessage("使用對像錯誤，請確認清楚。"));
            return;
        }
        if (tgItem.getItem().get_safeenchant() < 0 // 安定值小於0
                || tgItem.getBless() >= 128) { // 封印的裝備
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        // 飾品無法使用
        final int use_type = tgItem.getItem().getUseType();
        if (use_type == 23 // 戒指
                || use_type == 24 // 項鏈
                || use_type == 37 // 腰帶
                || use_type == 40) { // 耳環
            pc.sendPackets(new S_SystemMessage("飾品無法使用。"));
            return;
        }
        // 最高只能強化到 +20
        if (tgItem.getEnchantLevel() >= ConfigWeaponArmor.ELYOS2_ENCHANT) {
            pc.sendPackets(new S_SystemMessage("已經超過最高強化上限。"));
            return;
        }
        pc.getInventory().removeItem(item, 1);
        final Random random = new Random();
        final EnchantExecutor enchantExecutor = new EnchantWeapon();
        // 強化成功機率 (強化值增加1)
        if (this._chance > random.nextInt(100) + 1) {
            enchantExecutor.successEnchant(pc, tgItem, 1);
            server_lv.forIntensifyArmor(pc, tgItem);//terry770106
            // 強化失敗機率 (強化值歸零)
        } else {
            enchantExecutor.successEnchant(pc, tgItem, 0);
        }
    }

    public void set_set(String[] set) {
        try {
            this._chance = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
    }
}
