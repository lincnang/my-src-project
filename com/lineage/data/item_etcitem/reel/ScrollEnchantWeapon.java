package com.lineage.data.item_etcitem.reel;

import com.lineage.config.ConfigWeaponArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.cmd.EnchantWeapon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1ItemUpdata;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import william.server_lv;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <font color=#00800>對武器施法的卷軸</font><BR>
 */
public class ScrollEnchantWeapon extends ItemExecutor {
    /**
     *
     */
    private ScrollEnchantWeapon() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new ScrollEnchantWeapon();
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
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.get_time() != null) {
            pc.sendPackets(new S_ServerMessage(210, tgItem.getItem().getNameId()));
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("(預防誤點機制啟動)裝備中無法強化"));// 沒有任何事發生
            return;
        }
        if (tgItem.getEnchantLevel() >= ConfigWeaponArmor.enchantweapon) {
            pc.sendPackets(new S_ServerMessage("\\fR武器強化值已達上限，無法再進行強化。"));
            return;
        }
        final int safe_enchant = tgItem.getItem().get_safeenchant();
        boolean isErr = false;
        // 取得物件觸發事件
        final int use_type = tgItem.getItem().getUseType();
        switch (use_type) {
            case 1:// 武器
                if (safe_enchant < 0) { // 物品不可強化
                    isErr = true;
                }
                break;
            default:
                isErr = true;
                break;
        }
        final int weaponId = tgItem.getItem().getItemId();
        if ((weaponId >= 246) && (weaponId <= 255)) { // 物品不可強化
            isErr = true;
        }
        if (weaponId == 500012 && item.getItemId() != 83102) {
            isErr = true;
        }
        if (weaponId != 500012 && item.getItemId() == 83102) {
            isErr = true;
        }
        if (weaponId >= 410214 && weaponId <= 410221) {
            isErr = true;
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
        final EnchantExecutor enchantExecutor = new EnchantWeapon();
        int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
        pc.getInventory().removeItem(item, 1);
        boolean isEnchant = true;
        if (enchant_level < -6) {// 武器將會消失,最大可追加到-7
            isEnchant = false;
        } else if (enchant_level < safe_enchant) {// 安定值內
            isEnchant = true;
        } else {
            final Random random = new Random();
            final int rnd2 = ThreadLocalRandom.current().nextInt(100) + 1;
            int enchant_chance_wepon;
            int enchant_level_tmp;
            if (safe_enchant == 0) { // 對武器安定直為0初始計算+8
                enchant_level_tmp = enchant_level + 6;
                if (weaponId == 217 || weaponId == 61 || weaponId == 86 || weaponId == 134 || weaponId == 12) {
                    enchant_level_tmp = enchant_level + 12;
                }
                if (weaponId >= 410199 && weaponId <= 410206) {//圓月系列
                    enchant_level_tmp = enchant_level;
                }
            } else {
                enchant_level_tmp = enchant_level;
            }
            if (enchant_level >= 9) {
                enchant_chance_wepon = (int) L1ItemUpdata.enchant_wepon_up9(enchant_level_tmp);
            } else {
                enchant_chance_wepon = (int) L1ItemUpdata.enchant_wepon_dn9(enchant_level_tmp);
            }
            if (item.getItemId() == 44066) {// 潘朵拉黃金武器魔法卷軸
                enchant_chance_wepon *= 2;// 機率加倍
            }
            if (pc.getAccessLevel() >= 200) {
                pc.sendPackets((ServerBasePacket) new S_SystemMessage("[普通卷] bless=" + item.getBless() + " 成功率=" + enchant_chance_wepon));
            }
            //			if(weaponId==410176 || weaponId==500012){
            //				enchant_chance_wepon *= 3;// 機率加倍
            //			}
            if (rnd2 < enchant_chance_wepon) {
                isEnchant = true;
            } else {
                if ((enchant_level >= 9) && (rnd2 < (enchant_chance_wepon * 2))) {
                    randomELevel = 0;
                } else {
                    isEnchant = false;
                }
            }
        }
        if ((randomELevel <= 0) && (enchant_level > -6)) {
            isEnchant = true;
        }
        if (isEnchant) {// 成功
            enchantExecutor.successEnchant(pc, tgItem, randomELevel);
            server_lv.forIntensifyArmor(pc, tgItem);//terry770106
        } else {// 失敗
            enchantExecutor.failureEnchant(pc, tgItem);
        }
    }
}