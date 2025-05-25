package com.lineage.data.item_etcitem.reel;

import com.lineage.data.cmd.EnchantArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1ItemUpdata;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.util.Random;

/**
 * 屍魂對盔甲施法的卷軸240159<br>
 */
public class ScrollEnchantArmor_b extends ItemExecutor {//20161201

    /**
     *
     */
    private ScrollEnchantArmor_b() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new ScrollEnchantArmor_b();
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
        // 對象OBJID
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        // 安定值
        final int safe_enchant = tgItem.getItem().get_safeenchant();
        boolean isErr = false;
        // 取得物件觸發事件
        final int use_type = tgItem.getItem().getUseType();
        switch (use_type) {
            case 2:// 盔甲
            case 18:// T恤
            case 19:// 斗篷
            case 20:// 手套
            case 21:// 靴
            case 22:// 頭盔
            case 25:// 盾牌
            case 47:// 脛甲
                if (safe_enchant < 0) { // 物品不可强化
                    isErr = true;
                }
                break;
            default:
                isErr = true;
                break;
        }
        final int armorId = tgItem.getItem().getItemId();
        if ((armorId >= 120444) && (armorId <= 120448)) { // 屍魂裝備
            isErr = false;
        } else {
            isErr = true;
        }
        if (tgItem.getBless() >= 128) {// 封印的装備
            isErr = true;
        }
        if (isErr) {
            pc.sendPackets(new S_ServerMessage(79));// 没有任何事发生
            return;
        }
        if (tgItem.getEnchantLevel() == 15) {//20161118
            pc.sendPackets(new S_ServerMessage("您的防具已達上限值。"));
            return;
        }
        // 物品已追加值
        final int enchant_level = tgItem.getEnchantLevel();
        final EnchantExecutor enchantExecutor = new EnchantArmor();
        int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
        pc.getInventory().removeItem(item, 1);
        boolean isEnchant = true;
        if (enchant_level < -6) {// 盔甲将会消失,最大可追加到-7
            isEnchant = false;
        } else if (enchant_level < safe_enchant) {// 安定值內
            isEnchant = true;
        } else {// 超出安定值
            final Random random = new Random();
            final int rnd = random.nextInt(100) + 1;
            int enchant_chance_armor;
            int enchant_level_tmp;
            if (safe_enchant == 0) { // 對防具安定直為0初始計算+2
                enchant_level_tmp = enchant_level + 2;
            } else {
                enchant_level_tmp = enchant_level;
            }
            if (enchant_level >= 9) {
                enchant_chance_armor = (int) L1ItemUpdata.enchant_armor_up9(enchant_level_tmp);
            } else {
                enchant_chance_armor = (int) L1ItemUpdata.enchant_armor_dn9(enchant_level_tmp);
            }
            if (item.getItemId() == 44065) {//潘朵拉黃金盔甲魔法卷軸
                enchant_chance_armor *= 2;//機率加倍
            }
            if (rnd < enchant_chance_armor) {
                isEnchant = true;
            } else {
                if ((enchant_level >= 9) && (rnd < (enchant_chance_armor * 2))) {
                    randomELevel = 0;
                } else {
                    isEnchant = false;
                }
            }
        }
        if ((randomELevel <= 0) && (enchant_level > -6)) {
            isEnchant = true;
        }
        if (isEnchant) {// 成功   //20161114
            enchantExecutor.successEnchant(pc, tgItem, randomELevel);
            tgItem.setproctect(false);/**[原碼] 關閉裝備保護 */
            //tgItem.setproctect1(false);/**[原碼] 關閉裝備保護 */
            //tgItem.setproctect2(false);/**[原碼] 關閉裝備保護 */
            pc.sendPackets(new S_ItemStatus(tgItem));
            pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
            if (pc.getAccessLevel() == 0 && pc.getcheck_lv() == false) {        //20161123
                if (tgItem.getEnchantLevel() >= tgItem.getItem().get_safeenchant() + 3) {
                    World.get().broadcastPacketToAll(new S_BlueMessage(166, "\\f=【" + pc.getName() + "】的+" + enchant_level + " " + tgItem.getName() + "強化成功"));
                }
            }
            pc.setcheck_lv(false);//關閉20161123
        } else {// 失敗
            if (tgItem.getproctect() == true) { //A級-如果裝備受到保護中往下執行
                tgItem.setproctect(false);
                pc.sendPackets(new S_ItemStatus(tgItem));
                pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
                pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
                pc.sendPackets(new S_ServerMessage("受到高級裝備保護卷軸的影響,失敗後物品無變化。"));
            }/*else if (tgItem.getproctect1() == true) { //B級-如果裝備受到保護中往下執行
				tgItem.setproctect1(false);
				tgItem.setEnchantLevel(tgItem.getEnchantLevel() - 1);
				pc.sendPackets(new S_ItemStatus(tgItem));
				pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
				pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
				pc.sendPackets(new S_ServerMessage("受到中級裝備保護卷軸的影響,失敗後物品倒扣1。"));
			}else if (tgItem.getproctect2() == true) { //C級-如果裝備受到保護中往下執行
				tgItem.setproctect2(false);
				tgItem.setEnchantLevel(0);
				pc.sendPackets(new S_ItemStatus(tgItem));
				pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
				pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
				pc.sendPackets(new S_ServerMessage("受到初級裝備保護卷軸的影響,失敗後物品歸0。"));
			}*/ else {
                enchantExecutor.failureEnchant(pc, tgItem);
            }
        }
    }
}