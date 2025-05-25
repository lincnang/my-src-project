package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ExtraAttrWeaponTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1AttrWeapon;

public class AttrWeaponScroll4 extends ItemExecutor {
    private int _attr_id;
    private int _attrlv;

    public static ItemExecutor get() {
        return new AttrWeaponScroll4();
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
            pc.sendPackets(new S_SystemMessage("已達該屬性最高階級。"));
            return;
        }
        pc.getInventory().removeItem(item, 1L);
        pc.sendPackets(new S_ServerMessage(1410, tgItem.getLogName()));
        tgItem.setAttrEnchantKind(this._attr_id);
        tgItem.setAttrEnchantLevel(_attrlv);
        pc.getInventory().updateItem(tgItem, 3072);
        pc.getInventory().saveItem(tgItem, 3072);
    }

    @Override
    public void set_set(String[] set) {
        try {
            _attr_id = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
        try {
            _attrlv = Integer.parseInt(set[2]);
        } catch (Exception e) {
        }
    }
}