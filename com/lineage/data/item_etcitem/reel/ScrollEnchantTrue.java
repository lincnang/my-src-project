package com.lineage.data.item_etcitem.reel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.world.World;

public class ScrollEnchantTrue extends ItemExecutor {
    public static ItemExecutor get() {
        return new ScrollEnchantTrue();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(data[0]);
        int itemId = item.getItem().getItemId();
        int useType = l1iteminstance1.getItem().getType2();
        if (itemId == 44069) {
            if (useType == 2) {
                scrollOfEnchantArmor100(pc, item, l1iteminstance1);
            } else {
                pc.sendPackets(new S_SystemMessage("請選擇一種防具"));
            }
        } else if (itemId == 44068) {
            if (useType == 1) {
                scrollOfEnchantWeapon100(pc, item, l1iteminstance1);
            } else {
                pc.sendPackets(new S_SystemMessage("請選擇一種武器"));
            }
        }
    }

    public void scrollOfEnchantArmor100(L1PcInstance pc, L1ItemInstance l1iteminstance, L1ItemInstance l1iteminstance1) {
        int itemId = l1iteminstance.getItem().getItemId();
        int safe_enchant = l1iteminstance1.getItem().get_safeenchant();
        int armorId = l1iteminstance1.getItem().getItemId();
        if (l1iteminstance1.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("\\fU你必須先解除物品裝備。"));
            return;
        }
        if ((l1iteminstance1 == null) || (l1iteminstance1.getItem().getType2() != 2) || (safe_enchant < 0) || (l1iteminstance1.getEnchantLevel() >= 15)) {
            pc.sendPackets(new S_ServerMessage("您的防具已達上限值。"));
            return;
        }
        if ((l1iteminstance1.getItem().getItemId() >= 120477) && (l1iteminstance1.getItem().getItemId() <= 120479) && (l1iteminstance1.getEnchantLevel() >= 9)) {
            pc.sendPackets(new S_ServerMessage("您的防具已達上限值。"));
            return;
        }
        if ((armorId == 20028) || (armorId == 20082) || (armorId == 20126) || (armorId == 20173) || (armorId == 20206) || (armorId == 20232) || (armorId == 21138) || (armorId == 30009) || (armorId == 21051) || (armorId == 21052) || (armorId == 21053) || (armorId == 30012) || (armorId == 21054) || (armorId == 21055) || (armorId == 21056) || (armorId == 21140) || (armorId == 21141)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((armorId >= 401004) && (armorId <= 401007)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((armorId >= 70376) && (armorId <= 70380)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((armorId >= 70125) && (armorId <= 70132)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((armorId >= 120477) && (armorId <= 120479)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (((armorId == 20161) || ((armorId >= 21035) && (armorId <= 21038))) && (itemId != 40127)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((itemId == 40127) && (armorId != 20161) && ((armorId < 21035) || (armorId > 21038))) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        pc.getInventory().removeItem(l1iteminstance, 1L);
        SuccessEnchant(pc, l1iteminstance1, pc.getNetConnection(), 1);
        pc.sendPackets(new S_ItemStatus(l1iteminstance1));
        pc.getInventory().updateItem(l1iteminstance1, 4);
        pc.getInventory().saveItem(l1iteminstance1, 4);
        if ((pc.getAccessLevel() == 0) && (l1iteminstance1.getEnchantLevel() >= l1iteminstance1.getItem().get_safeenchant() + 3)) {
            World.get().broadcastPacketToAll(new S_BlueMessage(166, "\\f=【" + pc.getName() + "】的+" + (l1iteminstance1.getEnchantLevel() - 1) + " " + l1iteminstance1.getName() + "強化成功"));
        }
        try {
            pc.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pc.sendPackets(new S_ItemStatus(l1iteminstance1));
    }

    public void scrollOfEnchantWeapon100(L1PcInstance pc, L1ItemInstance l1iteminstance, L1ItemInstance l1iteminstance1) {
        int itemId = l1iteminstance.getItem().getItemId();
        int safe_enchant = l1iteminstance1.getItem().get_safeenchant();
        int weaponId = l1iteminstance1.getItem().getItemId();
        if (l1iteminstance1.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("\\fU你必須先解除物品裝備。"));
            return;
        }
        if ((l1iteminstance1 == null) || (l1iteminstance1.getItem().getType2() != 1) || (safe_enchant < 0) || (l1iteminstance1.getEnchantLevel() == 20)) {
            pc.sendPackets(new S_ServerMessage("您的武器已達上限值。"));
            return;
        }
        if ((weaponId >= 301) && (weaponId <= 305)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((weaponId >= 852) && (weaponId <= 856)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((weaponId >= 857) && (weaponId <= 860)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((weaponId == 2) || (weaponId == 7) || (weaponId == 35) || (weaponId == 48) || (weaponId == 73) || (weaponId == 105) || (weaponId == 120) || (weaponId == 147) || (weaponId == 156) || (weaponId == 174) || (weaponId == 175) || (weaponId == 224)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((weaponId >= 246) && (weaponId <= 249) && (itemId != 40660)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((itemId == 40660) && ((weaponId < 246) || (weaponId > 249))) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (((weaponId == 36) || (weaponId == 183) || ((weaponId >= 250) && (weaponId <= 255))) && (itemId != 40128)) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if ((itemId == 40128) && (weaponId != 36) && (weaponId != 183) && ((weaponId < 250) || (weaponId > 255))) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        pc.getInventory().removeItem(l1iteminstance, 1L);
        SuccessEnchant(pc, l1iteminstance1, pc.getNetConnection(), 1);
        pc.sendPackets(new S_ItemStatus(l1iteminstance1));
        if ((pc.getAccessLevel() == 0) && (l1iteminstance1.getEnchantLevel() >= l1iteminstance1.getItem().get_safeenchant() + 3)) {
            World.get().broadcastPacketToAll(new S_BlueMessage(166, "\\f=【" + pc.getName() + "】的+" + (l1iteminstance1.getEnchantLevel() - 1) + " " + l1iteminstance1.getName() + "強化成功"));
        }
    }

    public void SuccessEnchant(L1PcInstance pc, L1ItemInstance item, ClientExecutor client, int i) {
        int itemType2 = item.getItem().getType2();
        String[][] sa = {{"", "", "", "", ""}, {"$246", "", "$245", "$245", "$245"}, {"$246", "", "$252", "$252", "$252"}};
        String[][] sb = {{"", "", "", "", ""}, {"$247", "", "$247", "$248", "$248"}, {"$247", "", "$247", "$248", "$248"}};
        String sa_temp = sa[itemType2][(i + 1)];
        String sb_temp = sb[itemType2][(i + 1)];
        pc.sendPackets(new S_ServerMessage(161, item.getLogName(), sa_temp, sb_temp));
        int oldEnchantLvl = item.getEnchantLevel();
        int newEnchantLvl = oldEnchantLvl + i;
        int safe_enchant = item.getItem().get_safeenchant();
        item.setEnchantLevel(newEnchantLvl);
        client.getActiveChar().getInventory().updateItem(item, 4);
        if (newEnchantLvl > safe_enchant) {
            client.getActiveChar().getInventory().saveItem(item, 4);
        }
        if (item.getItem().getType2() == 2) {
            if (item.isEquipped()) {
                if ((item.getItem().getType() < 8) || (item.getItem().getType() > 12)) {
                    pc.addAc(-i);
                }
                int armorId = item.getItem().getItemId();
                int[] i1 = {20011, 20110, 21123, 21124, 21125, 21126, 120011};
                for (int j = 0; j < i1.length; j++) {
                    if (armorId == i1[j]) {
                        pc.addMr(i);
                        pc.sendPackets(new S_SPMR(pc));
                        break;
                    }
                }
                int[] i2 = {20056, 120056, 220056};
                for (int j = 0; j < i2.length; j++) {
                    if (armorId == i2[j]) {
                        pc.addMr(i * 2);
                        pc.sendPackets(new S_SPMR(pc));
                        break;
                    }
                }
                if (armorId == 21535) {
                    pc.addMr(i * 3);
                    pc.sendPackets(new S_SPMR(pc));
                }
                if (armorId == 71100) {
                    pc.addMr(i * 4);
                    pc.sendPackets(new S_SPMR(pc));
                }
            }
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
    }
}