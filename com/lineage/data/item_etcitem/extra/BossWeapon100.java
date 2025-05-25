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

import java.sql.Timestamp;

public class BossWeapon100 extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(BossWeapon100.class);
    private int _setBossPower;

    private BossWeapon100() {
    }

    public static ItemExecutor get() {
        return new BossWeapon100();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 1) {
            pc.sendPackets(new S_SystemMessage("只能對武器類道具附加頭目靈魂。"));
            return;
        }
        L1ItemPower_name power = tgItem.get_power_name();
        L1BossWeapon bossWeapon = ExtraBossWeaponTable.getInstance().get(this._setBossPower, 1);
        if (bossWeapon == null) {
            _log.error("------ 新增頭目水晶資料錯誤 ------");
            return;
        }
        if (power == null) {
            pc.getInventory().removeItem(item, 1L);
            power = new L1ItemPower_name();
            power.set_item_obj_id(tgItem.getId());
            power.set_boss_lv(1);
            tgItem.set_power_name(power);
            power.set_boss_weapon(bossWeapon);
            if (bossWeapon.getMaxUseTime() > 0) {
                final Timestamp ts = new Timestamp(System.currentTimeMillis() + bossWeapon.getMaxUseTime() * 1000L);
                power.set_boss_date_time(ts);
            } else {
                power.set_boss_date_time(null);
            }
            pc.sendPackets(new S_SystemMessage(bossWeapon.getSuccessMsg()));
            CharItemPowerReading.get().storeItem(tgItem.getId(), power);
            WriteLogTxt.Recording("Boss附魔紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家【" + pc.getName() + "】的【" + " +" + tgItem.getEnchantLevel() + " " + tgItem.getName() + "】【" + bossWeapon.getBossName() + "】, (ObjId: " + item.getId() + ")】附魔成功.");
        } else {
            if (power.get_boss_weapon() == null || power.get_boss_lv() <= 0) {
                power.set_boss_lv(1);
            }
            final L1BossWeapon srcBossWeaopn = ExtraBossWeaponTable.getInstance().get(this._setBossPower, power.get_boss_lv());
            if (srcBossWeaopn == null) {
                _log.error("------ 當前頭目武器升級錯誤 ------");
                return;
            }
            if (power.get_boss_weapon() == srcBossWeaopn) {
                final L1BossWeapon nextBossWeaopn = ExtraBossWeaponTable.getInstance().get(this._setBossPower, power.get_boss_lv() + 1);
                if (nextBossWeaopn == null) {
                    pc.sendPackets(new S_SystemMessage("頭目靈魂等級已經是最大值。"));
                    return;
                }
                pc.getInventory().removeItem(item, 1);
                power.set_boss_lv(power.get_boss_lv() + 1);
                power.set_boss_weapon(nextBossWeaopn);
                if (nextBossWeaopn.getMaxUseTime() > 0) {
                    final Timestamp ts = new Timestamp(System.currentTimeMillis() + nextBossWeaopn.getMaxUseTime() * 1000L);
                    power.set_boss_date_time(ts);
                } else {
                    power.set_boss_date_time(null);
                }
                pc.sendPackets(new S_SystemMessage(nextBossWeaopn.getSuccessMsg()));
                WriteLogTxt.Recording("Boss附魔紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家【" + pc.getName() + "】的【" + " +" + tgItem.getEnchantLevel() + " " + tgItem.getName() + "】【" + nextBossWeaopn.getBossName() + "】, (ObjId: " + item.getId() + ")】附魔成功.");
            } else {
                if (ConfigWeaponArmor.Cross_BOSS) {
                    final L1BossWeapon newBossWeapon = ExtraBossWeaponTable.getInstance().get(this._setBossPower, 1);
                    if (newBossWeapon == null) {
                        _log.error("------- 新的一筆頭目靈魂武器錯誤 --------");
                        return;
                    }
                    pc.getInventory().removeItem(item, 1);
                    power.set_boss_lv(1);
                    power.set_boss_weapon(newBossWeapon);
                    if (newBossWeapon.getMaxUseTime() > 0) {
                        final Timestamp ts = new Timestamp(System.currentTimeMillis() + newBossWeapon.getMaxUseTime() * 1000L);
                        power.set_boss_date_time(ts);
                    } else {
                        power.set_boss_date_time(null);
                    }
                    pc.sendPackets(new S_SystemMessage(newBossWeapon.getSuccessMsg()));
                    WriteLogTxt.Recording("Boss附魔紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家【" + pc.getName() + "】的【" + " +" + tgItem.getEnchantLevel() + " " + tgItem.getName() + "】【" + newBossWeapon.getBossName() + "】, (ObjId: " + item.getId() + ")】附魔成功.");
                } else {
                    if (power.get_boss_weapon() != null) {
                        pc.sendPackets(new S_SystemMessage("請先消除其他BOSS靈魂。"));
                        return;
                    } else {
                        final L1BossWeapon newBossWeapon = ExtraBossWeaponTable.getInstance().get(this._setBossPower, 1);
                        if (newBossWeapon == null) {
                            _log.error("------- 新的一筆頭目靈魂武器錯誤 --------");
                            return;
                        }
                        pc.getInventory().removeItem(item, 1);
                        power.set_boss_lv(1);
                        power.set_boss_weapon(newBossWeapon);
                        if (newBossWeapon.getMaxUseTime() > 0) {
                            final Timestamp ts = new Timestamp(System.currentTimeMillis() + newBossWeapon.getMaxUseTime() * 1000L);
                            power.set_boss_date_time(ts);
                        } else {
                            power.set_boss_date_time(null);
                        }
                        pc.sendPackets(new S_SystemMessage(newBossWeapon.getSuccessMsg()));
                        WriteLogTxt.Recording("Boss附魔紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家【" + pc.getName() + "】的【" + " +" + tgItem.getEnchantLevel() + " " + tgItem.getName() + "】【" + newBossWeapon.getBossName() + "】, (ObjId: " + item.getId() + ")】附魔成功.");
                    }
                }
            }
            CharItemPowerReading.get().updateItem(tgItem.getId(), power);
        }
        pc.sendPackets(new S_ItemStatus(tgItem));
    }

    public void set_set(String[] set) {
        try {
            this._setBossPower = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
    }
}
