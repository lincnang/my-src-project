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

/**
 * 洗鍊卷軸(防具) 不會負數
 */
public class AttrArmorStrDexInt_1 extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(AttrArmorStrDexInt_1.class);
    private static Random _random = new Random();
    private int _ramdon = -1;
    private int _chance = -1;
    private int _ok = 0;

    public static ItemExecutor get() {
        return new AttrArmorStrDexInt_1();
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
            _log.error("自訂洗煉卷軸錯誤: " + itemId + " 沒有設定機率!(使用預設50%)");
            return;
        }
        L1ItemPower_bless power = null;
        boolean update = false;
        int ItemType = tgItem.getItem().getUseType();
        boolean isOK = false;
        StringBuilder info = new StringBuilder();
        switch (this._ok) {
            case 1:
                switch (ItemType) {
                    case 2:
                        isOK = true;
                }
                break;
            case 2:
                switch (ItemType) {
                    case 2:
                    case 18:
                        isOK = true;
                }
                break;
            case 3:
                switch (ItemType) {
                    case 2:
                    case 18:
                    case 19:
                        isOK = true;
                }
                break;
            case 4:
                switch (ItemType) {
                    case 2:
                    case 18:
                    case 19:
                    case 20:
                        isOK = true;
                }
                break;
            case 5:
                switch (ItemType) {
                    case 2:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                        isOK = true;
                }
                break;
            case 6:
                switch (ItemType) {
                    case 2:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                        isOK = true;
                }
                break;
            case 7:
                switch (ItemType) {
                    case 2:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 25:
                        isOK = true;
                }
                break;
            default:
                pc.sendPackets(new S_SystemMessage("強化卷軸尚未開放。"));
        }
        if (isOK) {
            if (tgItem.get_power_bless() != null) {
                power = tgItem.get_power_bless();
                update = true;
            } else {
                power = new L1ItemPower_bless();
            }
        } else {
            switch (this._ok) {
                case 1:
                    info.append("盔甲");
                    break;
                case 2:
                    info.append("盔甲,T恤");
                    break;
                case 3:
                    info.append("盔甲,T恤,斗篷");
                    break;
                case 4:
                    info.append("盔甲,T恤,斗篷,手套");
                    break;
                case 5:
                    info.append("盔甲,T恤,斗篷,手套,靴子");
                    break;
                case 6:
                    info.append("盔甲,T恤,斗篷,手套,靴子,頭盔");
                    break;
                case 7:
                    info.append("盔甲,T恤,斗篷,手套,靴子,頭盔,盾牌(臂甲)");
            }
            pc.sendPackets(new S_SystemMessage("目前只有(" + info + ")開放強化，依玩家進度開放其他裝備使用，避免過於變態以及不平衡。"));
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
                        if (power.get_hole_str() <= 0) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_str(power.get_hole_str() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，力量 -1。"));
                        }
                        break;
                    case 1:
                        if (power.get_hole_dex() <= 0) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_dex(power.get_hole_dex() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，敏捷 -1。"));
                        }
                        break;
                    case 2:
                        if (power.get_hole_int() <= 0) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_int(power.get_hole_int() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，智力 -1。"));
                        }
                        break;
                    case 3:
                        if (power.get_hole_dmg() <= 0) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_dmg(power.get_hole_dmg() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，近戰傷害 -1。"));
                        }
                        break;
                    case 4:
                        if (power.get_hole_bowdmg() <= 0) {
                            unSuccessful = true;
                        } else {
                            power.set_hole_bowdmg(power.get_hole_bowdmg() - 1);
                            pc.sendPackets(new S_SystemMessage("強化失敗，遠弓傷害-1。"));
                        }
                        break;
                    case 5:
                        if (power.get_hole_mcdmg() <= 0) {
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
            // pc.getInventory().updateItem(tgItem,L1PcInventory.COL_BLESS);
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
            this._ok = Integer.parseInt(set[3]);
        } catch (Exception localException) {
        }
    }
}
