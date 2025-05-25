package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ExtraMagicWeaponTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemPower_name;
import com.lineage.server.templates.L1MagicWeapon;

import java.sql.Timestamp;
import java.util.Random;

/**
 * 武器魔法DIY系統(DB自製)
 *
 * @author terry0412
 */
public class MagicWeapon extends ItemExecutor {
    private static final Random _random = new Random();

    /**
     *
     */
    private MagicWeapon() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new MagicWeapon();
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
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        // 目標對像不為 L1Weapon
        if (tgItem.getItem().getType2() != 1) {
            pc.sendPackets(new S_SystemMessage("只能對武器類道具附加魔法。"));
            return;
        }
        // 取得 魔法石資料
        final L1MagicWeapon magicWeapon = ExtraMagicWeaponTable.getInstance().get(item.getItemId());
        if (magicWeapon == null) {
            return;
        }
        // 移除一個道具
        pc.getInventory().removeItem(item, 1);
        // 成功機率判定
        if (_random.nextInt(100) >= magicWeapon.getSuccessRandom()) {
            // 附魔失敗的訊息
            pc.sendPackets(new S_SystemMessage(magicWeapon.getFailureMsg()));
            return;
        }
        // 可直接覆蓋武器原有的附魔效果
        L1ItemPower_name power = tgItem.get_power_name();
        if (power == null) {
            power = new L1ItemPower_name();
            tgItem.set_power_name(power);
            // 附魔效果
            power.set_magic_weapon(magicWeapon);
            // 附魔使用期限 (單位:秒)
            if (magicWeapon.getMaxUseTime() > 0) {
                final Timestamp ts = new Timestamp(System.currentTimeMillis() + magicWeapon.getMaxUseTime() * 1000);
                power.set_date_time(ts);
            } else {
                power.set_date_time(null);
            }
            // 新建資料
            CharItemPowerReading.get().storeItem(tgItem.getId(), power);
        } else {
            // 附魔效果
            power.set_magic_weapon(magicWeapon);
            // 附魔使用期限 (單位:秒)
            if (magicWeapon.getMaxUseTime() > 0) {
                final Timestamp ts = new Timestamp(System.currentTimeMillis() + magicWeapon.getMaxUseTime() * 1000);
                power.set_date_time(ts);
            } else {
                power.set_date_time(null);
            }
            // 更新資料
            CharItemPowerReading.get().updateItem(tgItem.getId(), power);
        }
        // 更新道具狀態
        pc.sendPackets(new S_ItemStatus(tgItem));
        // 附魔成功的訊息
        pc.sendPackets(new S_SystemMessage(magicWeapon.getSuccessMsg()));
        new Timestamp(System.currentTimeMillis());
        WriteLogTxt.Recording("附魔紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家【" + pc.getName() + "】的【" + " +" + tgItem.getEnchantLevel() + " " + tgItem.getName() + "】【" + magicWeapon.getSkillName() + "】, (ObjId: " + item.getId() + ")】附魔成功.");
    }
}
