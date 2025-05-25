package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.CharItemPowerTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.RandomArrayList;
import william.AbilityOrginal;
import william.L1WilliamAbilityOrginal;

/**
 * 強化擴充能力
 */
public class Item_Ability extends ItemExecutor {
    public static ItemExecutor get() {
        return new Item_Ability();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int targObjId = data[0];
        if (!pc.getInventory().checkItem(item.getItem().getItemId(), 1)) {
            return;
        }
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生
            return;
        }
        final int itemId = item.getItem().getItemId();
        final L1WilliamAbilityOrginal abilityOrginal = AbilityOrginal.getInstance().getTemplate(itemId);
        if (abilityOrginal == null) {
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("\\fR你必須先解除物品裝備!"));
            return;
        }
        if (abilityOrginal.get_itemidok() != 0) {
            //if (abilityOrginal.get_itemid().length != 0) {
            int[] m = abilityOrginal.get_itemid();
            boolean itemok = false;
            final StringBuilder name = new StringBuilder();
            for (int i = 0; i < m.length; i++) {
                L1Item abilityitemid = ItemTable.get().getTemplate(m[i]);
                if (abilityitemid != null) {
                    if (abilityitemid.getItemId() == tgItem.getItemId()) {
                        itemok = true;
                    }
                }
                if (name.length() != 0) {
                    name.append("\n\r");
                }
                name.append(abilityitemid.getName());
            }
            if (!itemok) {
                pc.sendPackets(new S_ServerMessage("只能對（\n\r" + name.toString() + "\n\r）使用！", 11));
                return;
            }
        }
        if (abilityOrginal.getItemType() != -1) {
            if (tgItem.getItem().getType2() != abilityOrginal.getItemType()) {
                if (abilityOrginal.getItemType() == 1) {
                    pc.sendPackets(new S_ServerMessage("只能強化在武器上。"));
                } else if (abilityOrginal.getItemType() == 2) {
                    pc.sendPackets(new S_ServerMessage("只能強化在裝備上。"));
                } else if (abilityOrginal.getItemType() == 0) {
                    pc.sendPackets(new S_ServerMessage("只能強化在道具上。"));
                }
                return;
            }
            boolean ok = true;
            if (abilityOrginal.getItemType_Aromr() != 0 && abilityOrginal.getItemType() == 2) {
                if (abilityOrginal.getItemType_Aromr() == 23) {
                    switch (tgItem.getItem().getType()) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 12:
                        case 13:
                        case 20: {
                            ok = false;
                            break;
                        }
                    }
                } else if (abilityOrginal.getItemType_Aromr() == 22) {
                    switch (tgItem.getItem().getType()) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20: {
                            ok = false;
                            break;
                        }
                    }
                } else if (abilityOrginal.getItemType_Aromr() == 21) {
                    switch (tgItem.getItem().getType()) {
                        case 8:
                        case 9:
                        case 10:
                        case 12:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19: {
                            ok = false;
                            break;
                        }
                    }
                } else if (tgItem.getItem().getType() != abilityOrginal.getItemType_Aromr()) {
                    if (abilityOrginal.getItemType_Aromr() == 1) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 2) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 3) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 4) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 5) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 6) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 7) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 8) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 9) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 10) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 12) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 13) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 14) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 15) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 16) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 17) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 18) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 19) {
                        ok = false;
                    } else if (abilityOrginal.getItemType_Aromr() == 20) {
                        ok = false;
                    }
                }
            }
            if (!ok) {
                pc.sendPackets(new S_ServerMessage("無法強化在該裝備。"));
                return;
            }
        }
        if (abilityOrginal.getHaveAbility() != 0) {
            if (abilityOrginal.getAddStr() != 0 && tgItem.getUpdateStr() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddDex() != 0 && tgItem.getUpdateDex() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddInt() != 0 && tgItem.getUpdateInt() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddCon() != 0 && tgItem.getUpdateCon() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddWis() != 0 && tgItem.getUpdateWis() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddCha() != 0 && tgItem.getUpdateCha() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddMaxHp() != 0 && tgItem.getUpdateHp() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddMaxMp() != 0 && tgItem.getUpdateMp() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddMr() != 0 && tgItem.getUpdateMr() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddSp() != 0 && tgItem.getUpdateSp() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddWeaponDmg() != 0 && tgItem.getUpdateDmgModifier() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddWeaponHit() != 0 && tgItem.getUpdateHitModifier() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddWeaponBowDmg() != 0 && tgItem.getUpdateBowDmgModifier() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddWeaponBowHit() != 0 && tgItem.getUpdateBowHitModifier() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddHpr() != 0 && tgItem.getUpdateHpr() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddMpr() != 0 && tgItem.getUpdateMpr() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddAc() != 0 && tgItem.getUpdateAc() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddFire() != 0 && tgItem.getUpdateFire() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddWind() != 0 && tgItem.getUpdateWind() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddEarth() != 0 && tgItem.getUpdateEarth() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getAddWater() != 0 && tgItem.getUpdateWater() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getPVPdmg() != 0 && tgItem.getUpdatePVPdmg() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
            if (abilityOrginal.getPVPdmg_R() != 0 && tgItem.getUpdatePVPdmg_R() == 0) {
                pc.sendPackets(new S_ServerMessage("請洗鍊出該能力，才能進行強化。"));
                return;
            }
        }
        if (abilityOrginal.getMax_capacity() != 0) {
            final int max = abilityOrginal.getMax_capacity();
            if (abilityOrginal.getAddStr() != 0 && max <= tgItem.getUpdateStr()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddDex() != 0 && max <= tgItem.getUpdateDex()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddInt() != 0 && max <= tgItem.getUpdateInt()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddCon() != 0 && max <= tgItem.getUpdateCon()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddWis() != 0 && max <= tgItem.getUpdateWis()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddCha() != 0 && max <= tgItem.getUpdateCha()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddMaxHp() != 0 && max <= tgItem.getUpdateHp()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddMaxMp() != 0 && max <= tgItem.getUpdateMp()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddMr() != 0 && max <= tgItem.getUpdateMr()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddSp() != 0 && max <= tgItem.getUpdateSp()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddWeaponDmg() != 0 && max <= tgItem.getUpdateDmgModifier()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddWeaponHit() != 0 && max <= tgItem.getUpdateHitModifier()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddWeaponBowDmg() != 0 && max <= tgItem.getUpdateBowDmgModifier()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddWeaponBowHit() != 0 && max <= tgItem.getUpdateBowHitModifier()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddHpr() != 0 && max <= tgItem.getUpdateHpr()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddMpr() != 0 && max <= tgItem.getUpdateMpr()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddAc() != 0 && max <= tgItem.getUpdateAc()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddFire() != 0 && max <= tgItem.getUpdateFire()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddWind() != 0 && max <= tgItem.getUpdateWind()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddEarth() != 0 && max <= tgItem.getUpdateEarth()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getAddWater() != 0 && max <= tgItem.getUpdateWater()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getPVPdmg() != 0 && max <= tgItem.getUpdatePVPdmg()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
            if (abilityOrginal.getPVPdmg_R() != 0 && max <= tgItem.getUpdatePVPdmg_R()) {
                pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg1()));
                return;
            }
        }
        if (abilityOrginal.getCleaning_ability() != 0) {
            if (abilityOrginal.getCleaning_ability() == 1) {
                tgItem.setUpdateStr(0);
                tgItem.setUpdateDex(0);
                tgItem.setUpdateInt(0);
                tgItem.setUpdateCon(0);
                tgItem.setUpdateWis(0);
                tgItem.setUpdateCha(0);
                tgItem.setUpdateHp(0);
                tgItem.setUpdateMp(0);
                tgItem.setUpdateSp(0);
                tgItem.setUpdateDmgModifier(0);
                tgItem.setUpdateHitModifier(0);
                tgItem.setUpdateBowDmgModifier(0);
                tgItem.setUpdateBowHitModifier(0);
                tgItem.setUpdateHpr(0);
                tgItem.setUpdateMpr(0);
                tgItem.setUpdateAc(0);
                tgItem.setUpdateMr(0);
                tgItem.setUpdateFire(0);
                tgItem.setUpdateWind(0);
                tgItem.setUpdateEarth(0);
                tgItem.setUpdateWater(0);
                tgItem.setUpdatePVPdmg(0);
                tgItem.setUpdatePVPdmg_R(0);
            } else if (abilityOrginal.getCleaning_ability() == 2) {
                if (abilityOrginal.getAddStr() != 0) {
                    tgItem.setUpdateStr(0);
                }
                if (abilityOrginal.getAddDex() != 0) {
                    tgItem.setUpdateDex(0);
                }
                if (abilityOrginal.getAddInt() != 0) {
                    tgItem.setUpdateInt(0);
                }
                if (abilityOrginal.getAddCon() != 0) {
                    tgItem.setUpdateCon(0);
                }
                if (abilityOrginal.getAddWis() != 0) {
                    tgItem.setUpdateWis(0);
                }
                if (abilityOrginal.getAddCha() != 0) {
                    tgItem.setUpdateCha(0);
                }
                if (abilityOrginal.getAddMaxHp() != 0) {
                    tgItem.setUpdateHp(0);
                }
                if (abilityOrginal.getAddMaxMp() != 0) {
                    tgItem.setUpdateMp(0);
                }
                if (abilityOrginal.getAddSp() != 0) {
                    tgItem.setUpdateSp(0);
                }
                if (abilityOrginal.getAddMr() != 0) {
                    tgItem.setUpdateMr(0);
                }
                if (abilityOrginal.getAddWeaponDmg() != 0) {
                    tgItem.setUpdateDmgModifier(0);
                }
                if (abilityOrginal.getAddWeaponHit() != 0) {
                    tgItem.setUpdateHitModifier(0);
                }
                if (abilityOrginal.getAddWeaponBowDmg() != 0) {
                    tgItem.setUpdateBowDmgModifier(0);
                }
                if (abilityOrginal.getAddWeaponBowHit() != 0) {
                    tgItem.setUpdateBowHitModifier(0);
                }
                if (abilityOrginal.getAddHpr() != 0) {
                    tgItem.setUpdateHpr(0);
                }
                if (abilityOrginal.getAddMpr() != 0) {
                    tgItem.setUpdateMpr(0);
                }
                if (abilityOrginal.getAddAc() != 0) {
                    tgItem.setUpdateAc(0);
                }
                if (abilityOrginal.getAddFire() != 0) {
                    tgItem.setUpdateFire(0);
                }
                if (abilityOrginal.getAddWind() != 0) {
                    tgItem.setUpdateWind(0);
                }
                if (abilityOrginal.getAddEarth() != 0) {
                    tgItem.setUpdateEarth(0);
                }
                if (abilityOrginal.getAddWater() != 0) {
                    tgItem.setUpdateWater(0);
                }
                if (abilityOrginal.getPVPdmg() != 0) {
                    tgItem.setUpdatePVPdmg(0);
                }
                if (abilityOrginal.getPVPdmg_R() != 0) {
                    tgItem.setUpdatePVPdmg_R(0);
                }
            }
            if (CharItemPowerTable.get().getPower(tgItem) == null) {
                CharItemPowerTable.get().storeItem(tgItem);
            } else {
                CharItemPowerTable.get().updateItem(tgItem);
            }
            pc.sendPackets(new S_ItemStatus(tgItem));
        }
        if (abilityOrginal.get_evel() != 0 && tgItem.getEnchantLevel() < abilityOrginal.get_evel()) {
            pc.sendPackets(new S_SystemMessage("裝備的強化值不足 +" + abilityOrginal.get_evel() + " 無法強化。"));
            return;
        }
        boolean isSameAttr = false;
        if (abilityOrginal.getAddStr() != 0) {
            if (abilityOrginal.getAddStr() == -1) {
                isSameAttr = true;
                tgItem.setUpdateStr(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddStr()) + 1;
                    tgItem.setUpdateStr(tgItem.getUpdateStr() + r);
                } else {
                    tgItem.setUpdateStr(tgItem.getUpdateStr() + abilityOrginal.getAddStr());
                }
            }
        }
        if (abilityOrginal.getAddDex() != 0) {
            if (abilityOrginal.getAddDex() == -1) {
                isSameAttr = true;
                tgItem.setUpdateDex(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddDex()) + 1;
                    tgItem.setUpdateDex(tgItem.getUpdateDex() + r);
                } else {
                    tgItem.setUpdateDex(tgItem.getUpdateDex() + abilityOrginal.getAddDex());
                }
            }
        }
        if (abilityOrginal.getAddInt() != 0) {
            if (abilityOrginal.getAddInt() == -1) {
                isSameAttr = true;
                tgItem.setUpdateInt(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddInt()) + 1;
                    tgItem.setUpdateInt(tgItem.getUpdateInt() + r);
                } else {
                    tgItem.setUpdateInt(tgItem.getUpdateInt() + abilityOrginal.getAddInt());
                }
            }
        }
        if (abilityOrginal.getAddCon() != 0) {
            if (abilityOrginal.getAddCon() == -1) {
                isSameAttr = true;
                tgItem.setUpdateCon(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddCon()) + 1;
                    tgItem.setUpdateCon(tgItem.getUpdateCon() + r);
                } else {
                    tgItem.setUpdateCon(tgItem.getUpdateCon() + abilityOrginal.getAddCon());
                }
            }
        }
        if (abilityOrginal.getAddWis() != 0) {
            if (abilityOrginal.getAddWis() == -1) {
                isSameAttr = true;
                tgItem.setUpdateWis(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddWis()) + 1;
                    tgItem.setUpdateWis(tgItem.getUpdateWis() + r);
                } else {
                    tgItem.setUpdateWis(tgItem.getUpdateWis() + abilityOrginal.getAddWis());
                }
            }
        }
        if (abilityOrginal.getAddCha() != 0) {
            if (abilityOrginal.getAddCha() == -1) {
                isSameAttr = true;
                tgItem.setUpdateCha(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddCha()) + 1;
                    tgItem.setUpdateCha(tgItem.getUpdateCha() + r);
                } else {
                    tgItem.setUpdateCha(tgItem.getUpdateCha() + abilityOrginal.getAddCha());
                }
            }
        }
        if (abilityOrginal.getAddMaxHp() != 0) {
            if (abilityOrginal.getAddMaxHp() == -1) {
                isSameAttr = true;
                tgItem.setUpdateHp(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddMaxHp()) + 1;
                    tgItem.setUpdateHp(tgItem.getUpdateHp() + r);
                } else {
                    tgItem.setUpdateHp(tgItem.getUpdateHp() + abilityOrginal.getAddMaxHp());
                }
            }
        }
        if (abilityOrginal.getAddMaxMp() != 0) {
            if (abilityOrginal.getAddMaxMp() == -1) {
                isSameAttr = true;
                tgItem.setUpdateMp(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddMaxMp()) + 1;
                    tgItem.setUpdateMp(tgItem.getUpdateMp() + r);
                } else {
                    tgItem.setUpdateMp(tgItem.getUpdateMp() + abilityOrginal.getAddMaxMp());
                }
            }
        }
        if (abilityOrginal.getAddMr() != 0) {
            if (abilityOrginal.getAddMr() == -1) {
                isSameAttr = true;
                tgItem.setUpdateMr(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddMr()) + 1;
                    tgItem.setUpdateMr(tgItem.getUpdateMr() + r);
                } else {
                    tgItem.setUpdateMr(tgItem.getUpdateMr() + abilityOrginal.getAddMr());
                }
            }
        }
        if (abilityOrginal.getAddSp() != 0) {
            if (abilityOrginal.getAddSp() == -1) {
                isSameAttr = true;
                tgItem.setUpdateSp(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddSp()) + 1;
                    tgItem.setUpdateSp(tgItem.getUpdateSp() + r);
                } else {
                    tgItem.setUpdateSp(tgItem.getUpdateSp() + abilityOrginal.getAddSp());
                }
            }
        }
        if (abilityOrginal.getAddWeaponDmg() != 0) {
            if (abilityOrginal.getAddWeaponDmg() == -1) {
                isSameAttr = true;
                tgItem.setUpdateDmgModifier(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddWeaponDmg()) + 1;
                    tgItem.setUpdateDmgModifier(tgItem.getUpdateDmgModifier() + r);
                } else {
                    tgItem.setUpdateDmgModifier(tgItem.getUpdateDmgModifier() + abilityOrginal.getAddWeaponDmg());
                }
            }
        }
        if (abilityOrginal.getAddWeaponHit() != 0) {
            if (abilityOrginal.getAddWeaponHit() == -1) {
                isSameAttr = true;
                tgItem.setUpdateHitModifier(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddWeaponHit()) + 1;
                    tgItem.setUpdateHitModifier(tgItem.getUpdateHitModifier() + r);
                } else {
                    tgItem.setUpdateHitModifier(tgItem.getUpdateHitModifier() + abilityOrginal.getAddWeaponHit());
                }
            }
        }
        if (abilityOrginal.getAddWeaponBowDmg() != 0) {
            if (abilityOrginal.getAddWeaponBowDmg() == -1) {
                isSameAttr = true;
                tgItem.setUpdateBowDmgModifier(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddWeaponBowDmg()) + 1;
                    tgItem.setUpdateBowDmgModifier(tgItem.getUpdateBowDmgModifier() + r);
                } else {
                    tgItem.setUpdateBowDmgModifier(tgItem.getUpdateBowDmgModifier() + abilityOrginal.getAddWeaponBowDmg());
                }
            }
        }
        if (abilityOrginal.getAddWeaponBowHit() != 0) {
            if (abilityOrginal.getAddWeaponBowHit() == -1) {
                isSameAttr = true;
                tgItem.setUpdateBowHitModifier(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddWeaponBowHit()) + 1;
                    tgItem.setUpdateBowHitModifier(tgItem.getUpdateBowHitModifier() + r);
                } else {
                    tgItem.setUpdateBowHitModifier(tgItem.getUpdateBowHitModifier() + abilityOrginal.getAddWeaponBowHit());
                }
            }
        }
        if (abilityOrginal.getAddHpr() != 0) {
            if (abilityOrginal.getAddHpr() == -1) {
                isSameAttr = true;
                tgItem.setUpdateHpr(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddHpr()) + 1;
                    tgItem.setUpdateHpr(tgItem.getUpdateHpr() + r);
                } else {
                    tgItem.setUpdateHpr(tgItem.getUpdateHpr() + abilityOrginal.getAddHpr());
                }
            }
        }
        if (abilityOrginal.getAddMpr() != 0) {
            if (abilityOrginal.getAddMpr() == -1) {
                isSameAttr = true;
                tgItem.setUpdateMpr(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddMpr()) + 1;
                    tgItem.setUpdateMpr(tgItem.getUpdateMpr() + r);
                } else {
                    tgItem.setUpdateMpr(tgItem.getUpdateMpr() + abilityOrginal.getAddMpr());
                }
            }
        }
        if (abilityOrginal.getAddAc() != 0) {
            if (abilityOrginal.getAddAc() == -1) {
                isSameAttr = true;
                tgItem.setUpdateAc(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddAc()) + 1;
                    tgItem.setUpdateAc(tgItem.getUpdateAc() + r);
                } else {
                    tgItem.setUpdateAc(tgItem.getUpdateAc() + abilityOrginal.getAddAc());
                }
            }
        }
        if (abilityOrginal.getAddFire() != 0) {
            if (abilityOrginal.getAddFire() == -1) {
                isSameAttr = true;
                tgItem.setUpdateFire(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddFire()) + 1;
                    tgItem.setUpdateFire(tgItem.getUpdateFire() + r);
                } else {
                    tgItem.setUpdateFire(tgItem.getUpdateFire() + abilityOrginal.getAddFire());
                }
            }
        }
        if (abilityOrginal.getAddWind() != 0) {
            if (abilityOrginal.getAddWind() == -1) {
                isSameAttr = true;
                tgItem.setUpdateWind(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddWind()) + 1;
                    tgItem.setUpdateWind(tgItem.getUpdateWind() + r);
                } else {
                    tgItem.setUpdateWind(tgItem.getUpdateWind() + abilityOrginal.getAddWind());
                }
            }
        }
        if (abilityOrginal.getAddEarth() != 0) {
            if (abilityOrginal.getAddEarth() == -1) {
                isSameAttr = true;
                tgItem.setUpdateEarth(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddEarth()) + 1;
                    tgItem.setUpdateEarth(tgItem.getUpdateEarth() + r);
                } else {
                    tgItem.setUpdateEarth(tgItem.getUpdateEarth() + abilityOrginal.getAddEarth());
                }
            }
        }
        if (abilityOrginal.getAddWater() != 0) {
            if (abilityOrginal.getAddWater() == -1) {
                isSameAttr = true;
                tgItem.setUpdateWater(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getAddWater()) + 1;
                    tgItem.setUpdateWater(tgItem.getUpdateWater() + r);
                } else {
                    tgItem.setUpdateWater(tgItem.getUpdateWater() + abilityOrginal.getAddWater());
                }
            }
        }
        if (abilityOrginal.getPVPdmg() != 0) {
            if (abilityOrginal.getPVPdmg() == -1) {
                isSameAttr = true;
                tgItem.setUpdatePVPdmg(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getPVPdmg()) + 1;
                    tgItem.setUpdatePVPdmg(tgItem.getUpdatePVPdmg() + r);
                } else {
                    tgItem.setUpdatePVPdmg(tgItem.getUpdatePVPdmg() + abilityOrginal.getPVPdmg());
                }
            }
        }
        if (abilityOrginal.getPVPdmg_R() != 0) {
            if (abilityOrginal.getPVPdmg_R() == -1) {
                isSameAttr = true;
                tgItem.setUpdatePVPdmg_R(0);
            } else if (RandomArrayList.getInt(100) + 1 > 100 - abilityOrginal.getRandom()) {
                isSameAttr = true;
                if (abilityOrginal.getRixed_Or_Random() != 0) {
                    final int r = RandomArrayList.getInt(abilityOrginal.getPVPdmg_R()) + 1;
                    tgItem.setUpdatePVPdmg_R(tgItem.getUpdatePVPdmg_R() + r);
                } else {
                    tgItem.setUpdatePVPdmg_R(tgItem.getUpdatePVPdmg_R() + abilityOrginal.getPVPdmg_R());
                }
            }
        }
        if (isSameAttr) {
            if (CharItemPowerTable.get().getPower(tgItem) == null) {
                CharItemPowerTable.get().storeItem(tgItem);
            } else {
                CharItemPowerTable.get().updateItem(tgItem);
            }
            pc.sendPackets(new S_ItemStatus(tgItem));
            pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg2()));
        } else {
            pc.sendPackets(new S_SystemMessage(abilityOrginal.get_msg3()));
        }
        pc.getInventory().removeItem(item, 1);
    }
}
