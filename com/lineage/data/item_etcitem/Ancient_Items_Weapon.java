package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.Random;

public class Ancient_Items_Weapon extends ItemExecutor {
    public static ItemExecutor get() {
        return new Ancient_Items_Weapon();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int[] Weaponid = {273, 271, 131, 180, 37, 194};
        int[] goodWeaponid = {410164, 185, 63, 85, 165};
        int Weaponran = Random.nextInt(Weaponid.length);
        int goodWeaponran = Random.nextInt(goodWeaponid.length);
        int[] Enchant = {0, 1, 2, 3, 4, 5, 6};
        int[] HighEnchant = {7, 8, 9};
        int enchantlvl = 0;
        if (Random.nextInt(100) < 97) {// 97%機率安定值內
            enchantlvl = Enchant[Random.nextInt(Enchant.length)];
        } else {
            enchantlvl = HighEnchant[Random.nextInt(HighEnchant.length)];
        }
        pc.getInventory().removeItem(item, 1);
        // CreateNewItem.createNewItem_LV(pc, Weaponid[Weaponran], 1,
        // enchantlvl);
        if (Random.nextInt(100) < 99) {// 99%機率普通物品
            CreateNewItem.createNewItem_LV(pc, Weaponid[Weaponran], 1, enchantlvl);
        } else {
            CreateNewItem.createNewItem_LV(pc, goodWeaponid[goodWeaponran], 1, enchantlvl);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Crystal_PieceSoul JD-Core Version: 0.6.2
 */