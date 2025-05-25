package com.lineage.data.item_etcitem.poweritem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemBlessReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemPower_bless;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class StoneWeapon extends ItemExecutor {  //src039
    private static final Log _log = LogFactory.getLog(StoneWeapon.class);
    private static final Random _random = new Random();
    private int _chance = 100;

    private StoneWeapon() {
    }

    public static ItemExecutor get() {
        return new StoneWeapon();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        try {
            final int targObjId = data[0];
            final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
            if (tgItem == null) {
                return;
            }
            if (tgItem.get_power_bless() == null) {
                pc.sendPackets(new S_ServerMessage("\\fT這個物品沒有凹槽!"));
                return;
            }
            if (tgItem.getItem().getType2() != 1) {
                pc.sendPackets(new S_ServerMessage("\\fT這只能使用在武器上"));
                return;
            }
            if (tgItem.isEquipped()) {
                pc.sendPackets(new S_ServerMessage("\\fR你必須先解除物品裝備!"));
                return;
            }
            if (pc.isGm()) {
            }
            final L1ItemPower_bless power = tgItem.get_power_bless();
            if (power.get_hole_1() == 0 && power.get_hole_count() >= 1) {
                pc.getInventory().removeItem(item, 1);
                if (_random.nextInt(1000) > _chance) {
                    pc.sendPackets(new S_ServerMessage("\\fT凹槽置入魔法物品失敗!"));
                    return;
                }
                power.set_hole_1(item.getItemId());
                pc.sendPackets(new S_ItemStatus(tgItem));
                pc.sendPackets(new S_ItemName(tgItem));
                CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
            } else if (power.get_hole_2() == 0 && power.get_hole_count() >= 2) {
                pc.getInventory().removeItem(item, 1);
                if (_random.nextInt(1000) > _chance) {
                    pc.sendPackets(new S_ServerMessage("\\fT凹槽置入魔法物品失敗!"));
                    return;
                }
                power.set_hole_2(item.getItemId());
                pc.sendPackets(new S_ItemStatus(tgItem));
                pc.sendPackets(new S_ItemName(tgItem));
                CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
            } else if (power.get_hole_3() == 0 && power.get_hole_count() >= 3) {
                pc.getInventory().removeItem(item, 1);
                if (_random.nextInt(1000) > _chance) {
                    pc.sendPackets(new S_ServerMessage("\\fT凹槽置入魔法物品失敗!"));
                    return;
                }
                power.set_hole_3(item.getItemId());
                pc.sendPackets(new S_ItemStatus(tgItem));
                pc.sendPackets(new S_ItemName(tgItem));
                CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
            } else if (power.get_hole_4() == 0 && power.get_hole_count() >= 4) {
                pc.getInventory().removeItem(item, 1);
                if (_random.nextInt(1000) > _chance) {
                    pc.sendPackets(new S_ServerMessage("\\fT凹槽置入魔法物品失敗!"));
                    return;
                }
                power.set_hole_4(item.getItemId());
                pc.sendPackets(new S_ItemStatus(tgItem));
                pc.sendPackets(new S_ItemName(tgItem));
                CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
            } else if (power.get_hole_5() == 0 && power.get_hole_count() >= 5) {
                pc.getInventory().removeItem(item, 1);
                if (_random.nextInt(1000) > _chance) {
                    pc.sendPackets(new S_ServerMessage("\\fT凹槽置入魔法物品失敗!"));
                    return;
                }
                power.set_hole_5(item.getItemId());
                pc.sendPackets(new S_ItemStatus(tgItem));
                pc.sendPackets(new S_ItemName(tgItem));
                CharItemBlessReading.get().updateItem(tgItem.getId(), tgItem.get_power_bless());
            } else {
                pc.sendPackets(new S_ServerMessage("\\fT這個物品沒有足夠凹槽!"));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _chance = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
