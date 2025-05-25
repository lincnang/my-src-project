package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ExtraAttrWeaponTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1AttrWeapon;

import java.util.Random;

public class AttrWeaponScroll3 extends ItemExecutor {
    private static final Random _random = new Random();
    private int _attr_id;

    public static ItemExecutor get() {
        return new AttrWeaponScroll3();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 1) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (tgItem.getBless() >= 128) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        int attrEnchantKind = tgItem.getAttrEnchantKind();
        int attrEnchantLevel = tgItem.getAttrEnchantLevel();
        if (attrEnchantKind != this._attr_id) {
            attrEnchantLevel = 0;
        }
        L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(this._attr_id, attrEnchantLevel + 1);
        if ((attrWeapon == null) || (attrWeapon.getChance() <= 0)) {
            pc.sendPackets(new S_SystemMessage("已達該屬性最高階級"));
            return;
        }
        pc.getInventory().removeItem(item, 1L);
        if (_random.nextInt(100) < attrWeapon.getChance()) {
            pc.sendPackets(new S_ServerMessage(1410, tgItem.getLogName()));
            tgItem.setAttrEnchantKind(this._attr_id);
            tgItem.setAttrEnchantLevel(attrEnchantLevel + 1);
        } else {
            pc.sendPackets(new S_ServerMessage(1411, tgItem.getLogName()));
        }
        pc.getInventory().updateItem(tgItem, 3072);
        pc.getInventory().saveItem(tgItem, 3072);
    }

    public void set_set(String[] set) {
        try {
            this._attr_id = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
    }
}
