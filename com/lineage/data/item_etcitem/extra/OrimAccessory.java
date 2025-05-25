package com.lineage.data.item_etcitem.extra;

import com.lineage.config.ConfigWeaponArmor;
import com.lineage.data.cmd.EnchantArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.Random;

public class OrimAccessory extends ItemExecutor {
    public static ItemExecutor get() {
        return new OrimAccessory();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        // 安定值
        final int safe_enchant = tgItem.getItem().get_safeenchant();
        boolean isErr = false;
		/*if (!tgItem.getName().startsWith("星星") && !tgItem.getName().startsWith("月亮")
				&& !tgItem.getName().startsWith("底比斯賀洛斯戒指")
				&& !tgItem.getName().startsWith("底比斯阿努比斯戒指")
				) {
			pc.sendPackets(new S_ServerMessage("此物品無法強化"));// 沒有任何事發生
			return;
		}*/
        // 取得物件觸發事件
        final int use_type = tgItem.getItem().getUseType();
        switch (use_type) {
            case 23:// 戒指
            case 24:// 項鏈
            case 37:// 腰帶
            case 40:// 耳環
                if (tgItem.getItem().get_greater() == 3) {
                    isErr = true;
                }
                if (tgItem.isEquipped()) {
                    pc.sendPackets(new S_ServerMessage("請先將飾品脫下!!"));// 沒有任何事發生
                    return;
                }
                if (safe_enchant < 0) { // 物品不可強化
                    isErr = true;
                }
                break;
            default:
                isErr = true;
                break;
        }
        if (tgItem.getBless() >= 128) {// 封印的裝備
            isErr = true;
        }
        if (isErr) {
            pc.sendPackets(new S_ServerMessage(79));// 沒有任何事發生
            return;
        }
        // 物品已追加值
        final int enchant_level = tgItem.getEnchantLevel();
        // 限定所有 耳環 項鏈 戒指 腰帶 最高加到8
        if (enchant_level >= 8) {
            pc.sendPackets(new S_ServerMessage(79));// 沒有任何事發生
            return;
        }
        pc.getInventory().removeItem(item, 1L);
        Random random = new Random();
        final EnchantExecutor enchantExecutor = new EnchantArmor();
        //	int r=280;
		/*if (tgItem.getName().startsWith("底比斯賀洛斯戒指") 
				|| tgItem.getName().startsWith("底比斯阿努比斯戒指")) {
			r=220+(enchant_level*2);
		}
		if(tgItem.getName().startsWith("蕾雅戒指") ){
			r=400+(enchant_level*2);
		}*/
        int orimrandom = 0;
        if (item.getBless() == 0) {
            orimrandom = ConfigWeaponArmor.Orim_ENCHANT_SUCCESS_BLESS;
        } else {
            orimrandom = ConfigWeaponArmor.Orim_ENCHANT_SUCCESS;
        }
        //if (random.nextInt(100)+1 <= ConfigAlt.Orim_ENCHANT_SUCCESS) {// 強化成功
        if (random.nextInt(100) + 1 <= orimrandom) {// 強化成功
            enchantExecutor.successEnchant(pc, tgItem, 1);
        } else if (item.getBless() == 0) {// 強化失敗 但是使用受祝福歐林飾品強化卷軸
            enchantExecutor.successEnchant(pc, tgItem, 0);
        } else {// 非祝福卷軸 而且強化失敗
            if (enchant_level <= 0) {
                enchantExecutor.successEnchant(pc, tgItem, 0);
            } else {
                enchantExecutor.successEnchant(pc, tgItem, -1);
            }
        }
        if (enchant_level < -6) {// 飾品將會消失,最大可追加到-7
            enchantExecutor.failureEnchant(pc, tgItem);
        }
    }
}
