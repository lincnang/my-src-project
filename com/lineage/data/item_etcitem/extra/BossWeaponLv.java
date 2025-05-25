package com.lineage.data.item_etcitem.extra;

import com.lineage.config.ConfigWeaponArmor;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ExtraBossWeaponTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1BossWeapon;
import com.lineage.server.templates.L1ItemPower_name;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BossWeaponLv extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(BossWeaponLv.class);
    private int _setBossPower;
    private int _setBossLv;

    public static ItemExecutor get() {
        return new BossWeaponLv();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 1) {
            pc.sendPackets(new S_SystemMessage("只能對武器類道具附加魔法。"));
            return;
        }
        L1BossWeapon BossWeapon = ExtraBossWeaponTable.getInstance().get(this._setBossPower, this._setBossLv);
        if (BossWeapon == null) {
            _log.error("---------- 魔法石設置錯誤  ----------");
            return;
        }
        L1ItemPower_name power = tgItem.get_power_name();
        if (power != null) {
            if (!ConfigWeaponArmor.Cross_BOSS && power.get_boss_weapon() != null) {
                pc.sendPackets(new S_SystemMessage("請先消除其他BOSS靈魂。"));
                return;
            }
        }
        pc.getInventory().removeItem(item, 1L);
        if (power == null) {
            power = new L1ItemPower_name();
            power.set_item_obj_id(tgItem.getId());
            tgItem.set_power_name(power);
            power.set_boss_lv(this._setBossLv);
            power.set_boss_weapon(BossWeapon);
            // 新建資料
            CharItemPowerReading.get().storeItem(tgItem.getId(), power);
        } else {
            power.set_boss_lv(this._setBossLv);
            power.set_boss_weapon(BossWeapon);
            CharItemPowerReading.get().updateItem(tgItem.getId(), power);
        }
        pc.sendPackets(new S_ItemStatus(tgItem));
        pc.sendPackets(new S_SystemMessage(BossWeapon.getSuccessMsg()));
        WriteLogTxt.Recording("Boss附魔紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家【" + pc.getName() + "】的【" + " +" + tgItem.getEnchantLevel() + " " + tgItem.getName() + "】【" + BossWeapon.getBossName() + "】, (ObjId: " + item.getId() + ")】附魔成功.");
    }

    public void set_set(String[] set) {
        try {
            this._setBossPower = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            this._setBossLv = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
    }
}
