package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * 損壞武器名單
 *
 * @author dexc
 */
public class S_FixWeaponList extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 損壞武器名單
     *
     * @param weaponList 清單
     */
    public S_FixWeaponList(List<L1ItemInstance> weaponList) {
        this.writeC(S_FIXABLE_ITEM_LIST);
        this.writeD(0x000000c8); // Price
        this.writeH(weaponList.size()); // Weapon Amount
        for (final L1ItemInstance weapon : weaponList) {
            this.writeD(weapon.getId()); // Item ID
            this.writeC(weapon.get_durability()); // Fix Level
        }
    }

    /**
     * 損壞武器名單
     *
     * @param pc 執行人物
     */
    public S_FixWeaponList(final L1PcInstance pc) {
        this.buildPacket(pc);
    }

    /**
     * 損壞武器名單 - 測試
     */
    public S_FixWeaponList(final L1ItemInstance weapon) {
        this.writeC(S_FIXABLE_ITEM_LIST);
        this.writeD(0x000000c8); // Price
        this.writeH(1); // Weapon Amount
        this.writeD(weapon.getId()); // Item ID
        this.writeC(weapon.get_durability()); // Fix Level
    }

    private void buildPacket(final L1PcInstance pc) {
        this.writeC(S_FIXABLE_ITEM_LIST);
        this.writeD(0x000000c8); // Price
        final List<L1ItemInstance> weaponList = new ArrayList<L1ItemInstance>();
        final List<L1ItemInstance> itemList = pc.getInventory().getItems();
        for (final L1ItemInstance item : itemList) {
            // Find Weapon
            switch (item.getItem().getType2()) {
                case 1:
                    if (item.get_durability() > 0) {
                        weaponList.add(item);
                    }
                    break;
            }
        }
        this.writeH(weaponList.size()); // Weapon Amount
        for (final L1ItemInstance weapon : weaponList) {
            this.writeD(weapon.getId()); // Item ID
            this.writeC(weapon.get_durability()); // Fix Level
        }
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}