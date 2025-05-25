package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemBlessReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemPower_bless;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class AttrPowerStrDexInt extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(AttrPowerStrDexInt.class);
    private static Random _random = new Random();
    private int _ramdon = -1;
    private int _chance = -1;

    public static ItemExecutor get() {
        return new AttrPowerStrDexInt();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("\\fR你必須先解除物品裝備!"));
            return;
        }
        if ((this._ramdon == -1) || (this._chance == -1)) {
            this._ramdon = 50;
            this._chance = 33;
            int itemId = item.getItemId();
            _log.error("自訂強化卷軸錯誤: " + itemId + " 沒有設定機率!(使用預設50%)");
            return;
        }
        L1ItemPower_bless power = null;
        boolean update = false;
        switch (tgItem.getItem().getUseType()) {
            case 1:
                if (tgItem.get_power_bless() != null) {
                    power = tgItem.get_power_bless();
                    update = true;
                } else {
                    power = new L1ItemPower_bless();
                }
                break;
            default:
                pc.sendPackets(new S_SystemMessage("這個物品無法使用強化卷軸。"));
                return;
        }
        if ((power.get_hole_str() >= 5) && (power.get_hole_dex() >= 5) && (power.get_hole_int() >= 5) && (power.get_hole_dmg() >= 5) && (power.get_hole_bowdmg() >= 5) && (power.get_hole_mcdmg() >= 5)) {
            pc.sendPackets(new S_SystemMessage("您的強化能力值已達最大。"));
            return;
        }
        if (power != null) {
            boolean unSuccessful = false;
            int k = _random.nextInt(6);
            if (this._ramdon >= _random.nextInt(100) + 1) {
                switch (k) {
                    case 0:
                        if (power.get_hole_str() >= 5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_str(power.get_hole_str() + 1);
                            pc.sendPackets(new S_SystemMessage("強化成功，力量 +1。"));
                        }
                        break;
                    case 1:
                        if (power.get_hole_dex() >= 5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_dex(power.get_hole_dex() + 1);
                            pc.sendPackets(new S_SystemMessage("強化成功，敏捷 +1。"));
                        }
                        break;
                    case 2:
                        if (power.get_hole_int() >= 5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_int(power.get_hole_int() + 1);
                            pc.sendPackets(new S_SystemMessage("強化成功，智力 +1。"));
                        }
                        break;
                    case 3:
                        if (power.get_hole_dmg() >= 5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_dmg(power.get_hole_dmg() + 1);
                            pc.sendPackets(new S_SystemMessage("強化成功，近戰傷害 +1。"));
                        }
                        break;
                    case 4:
                        if (power.get_hole_bowdmg() >= 5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_bowdmg(power.get_hole_bowdmg() + 1);
                            pc.sendPackets(new S_SystemMessage("強化成功，遠弓傷害 +1。"));
                        }
                        break;
                    case 5:
                        if (power.get_hole_mcdmg() >= 5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_mcdmg(power.get_hole_mcdmg() + 1);
                            pc.sendPackets(new S_SystemMessage("強化成功，魔法傷害 +1。"));
                        }
                        break;
                }
            } else if (_random.nextInt(100) + 1 >= this._chance) {
                switch (k) {
                    case 0:
                        if (power.get_hole_str() <= -5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_str(power.get_hole_str() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，力量 -1。"));
                        }
                        break;
                    case 1:
                        if (power.get_hole_dex() <= -5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_dex(power.get_hole_dex() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，敏捷 -1。"));
                        }
                        break;
                    case 2:
                        if (power.get_hole_int() <= -5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_int(power.get_hole_int() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，智力 -1。"));
                        }
                        break;
                    case 3:
                        if (power.get_hole_dmg() <= -5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_dmg(power.get_hole_dmg() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，近戰傷害 -1。"));
                        }
                        break;
                    case 4:
                        if (power.get_hole_bowdmg() <= -5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_bowdmg(power.get_hole_bowdmg() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，遠弓傷害-1。"));
                        }
                        break;
                    case 5:
                        if (power.get_hole_mcdmg() <= -5) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_mcdmg(power.get_hole_mcdmg() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，魔法傷害 -1。"));
                        }
                        break;
                }
            } else {
                unSuccessful = true;
            }
            if (unSuccessful) {
                pc.sendPackets(new S_SystemMessage("強化失敗，但沒有事情發生。"));
            }
            if (power.get_hole_str() > 5) {
                power.set_hole_str(5);
            }
            if (power.get_hole_dex() > 5) {
                power.set_hole_dex(5);
            }
            if (power.get_hole_int() > 5) {
                power.set_hole_int(5);
            }
            if (power.get_hole_dmg() > 5) {
                power.set_hole_dmg(5);
            }
            if (power.get_hole_bowdmg() > 5) {
                power.set_hole_bowdmg(5);
            }
            if (power.get_hole_mcdmg() > 5) {
                power.set_hole_mcdmg(5);
            }
            if (power.get_hole_str() < -5) {
                power.set_hole_str(-5);
            }
            if (power.get_hole_dex() < -5) {
                power.set_hole_dex(-5);
            }
            if (power.get_hole_int() < -5) {
                power.set_hole_int(-5);
            }
            if (power.get_hole_dmg() < -5) {
                power.set_hole_dmg(-5);
            }
            if (power.get_hole_bowdmg() < -5) {
                power.set_hole_bowdmg(-5);
            }
            if (power.get_hole_mcdmg() < -5) {
                power.set_hole_mcdmg(-5);
            }
            power.set_item_obj_id(tgItem.getId());
            tgItem.set_power_bless(power);
            tgItem.setBless(1);
            pc.getInventory().updateItem(tgItem, L1PcInventory.COL_IS_ID);
            pc.getInventory().removeItem(item, 1L);
            if (update) {
                CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
            } else {
                CharItemBlessReading.get().storeItem(tgItem.getId(), tgItem.get_power_bless());
            }
            pc.sendPackets(new S_ItemStatus(tgItem));
        }
    }

    public void set_set(String[] set) {
        try {
            this._ramdon = Integer.parseInt(set[1]);
            this._chance = Integer.parseInt(set[2]);
        } catch (Exception localException) {
        }
    }
}
