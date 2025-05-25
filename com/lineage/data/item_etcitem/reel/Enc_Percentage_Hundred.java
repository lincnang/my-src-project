package com.lineage.data.item_etcitem.reel;

import com.lineage.data.cmd.EnchantArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Enc_Percentage_Hundred extends ItemExecutor {
    public static ItemExecutor get() {
        return new Enc_Percentage_Hundred();
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
            case 23:// 戒指
            case 24:// 項鏈
            case 37:// 腰帶
            case 40:// 耳環
                if (tgItem.getItem().get_greater() == 3) {
                    isErr = true;
                }
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
        int enchant_level = tgItem.getEnchantLevel();
        // 限定所有 耳環 項鏈 戒指 腰帶 最高加到9 //2017/04/21
        if (enchant_level >= 9) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        EnchantExecutor enchantExecutor = new EnchantArmor();
        pc.getInventory().removeItem(item, 1L);
        enchantExecutor.successEnchant(pc, tgItem, 1);
    }
}
/*
 * Location: C:\Users\kenny\Desktop\ Qualified Name:
 * com.lineage.data.item_etcitem.reel.Enc_Percentage_Hundred JD-Core Version:
 * 0.6.2
 */