package com.lineage.data.item_etcitem.reel;

import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.cmd.EnchantWeapon;
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
 * 屍魂對武器施法的卷軸240158<br>
 */
public class ScrollEnchantWeapon_D extends ItemExecutor {//20161201

    private ScrollEnchantWeapon_D() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new ScrollEnchantWeapon_D();
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
        final int safe_enchant = tgItem.getItem().get_safeenchant();
        boolean isErr = false;
        // 取得物件觸發事件
        final int use_type = tgItem.getItem().getUseType();
        switch (use_type) {
            case 1:// 武器
                if (safe_enchant < 0) { // 物品不可强化
                    isErr = true;
                }
                break;
            default:
                isErr = true;
                break;
        }
        final int weaponId = tgItem.getItem().getItemId();
        if ((weaponId >= 100232) && (weaponId <= 100234)) { //屍魂武器
            isErr = false;
        } else {
            isErr = true;
        }
        if (isErr) {
            pc.sendPackets(new S_ServerMessage(79));// 没有任何事发生
            return;
        }
        if (tgItem.getEnchantLevel() == 20) {//20161118
            pc.sendPackets(new S_ServerMessage("您的武器已達上限值。"));
            return;
        }
        // 物品已追加值
        final int enchant_level = tgItem.getEnchantLevel();
        final EnchantExecutor enchantExecutor = new EnchantWeapon();
        int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
        pc.getInventory().removeItem(item, 1);
        boolean isEnchant = true;
        if (enchant_level < -6) {// 武器将会消失,最大可追加到-7
            isEnchant = false;
        } else if (enchant_level < safe_enchant) {// 安定值內
            isEnchant = true;
        } else {
            final Random random = new Random();
            final int rnd2 = random.nextInt(100) + 1;
            int enchant_chance_wepon;
            int enchant_level_tmp;
            if (safe_enchant == 0) { // 對武器安定直為0初始計算+6
                enchant_level_tmp = enchant_level + 6;
            } else {
                enchant_level_tmp = enchant_level;
            }
            if (enchant_level >= 9) {
                enchant_chance_wepon = (int) L1ItemUpdata.enchant_wepon_up9(enchant_level_tmp);
            } else {
                enchant_chance_wepon = (int) L1ItemUpdata.enchant_wepon_dn9(enchant_level_tmp);
            }
            if (item.getItemId() == 44066) {//潘朵拉黃金武器魔法卷軸
                enchant_chance_wepon *= 2;//機率加倍
            }
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
            enchantExecutor.successEnchant(pc, tgItem, randomELevel); //20161114
            tgItem.setproctect(false);/**[原碼] 關閉裝備保護 */
            //tgItem.setproctect1(false);/**[原碼] 關閉裝備保護 */
            //tgItem.setproctect2(false);/**[原碼] 關閉裝備保護 */
            pc.sendPackets(new S_ItemStatus(tgItem));
            pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
            if (pc.getAccessLevel() == 0) {    //20161122
                if (tgItem.getEnchantLevel() >= tgItem.getItem().get_safeenchant() + 3) {
                    World.get().broadcastPacketToAll(new S_BlueMessage(166, "\\f=【" + pc.getName() + "】的+" + enchant_level + " " + tgItem.getName() + "強化成功"));
                }
            }
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