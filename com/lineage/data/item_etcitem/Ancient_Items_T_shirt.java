package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.Random;

public class Ancient_Items_T_shirt extends ItemExecutor {
    public static ItemExecutor get() {
        return new Ancient_Items_T_shirt();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int[] Armorid = {20085};
        int Armorran = Random.nextInt(Armorid.length);
        int[] Enchant = {0, 1, 2, 3, 4};
        int[] HighEnchant = {5, 6, 7};
        int enchantlvl = 0;
        if (Random.nextInt(100) < 97) {// 97%機率安定值內
            enchantlvl = Enchant[Random.nextInt(Enchant.length)];
        } else {
            enchantlvl = HighEnchant[Random.nextInt(HighEnchant.length)];
        }
        pc.getInventory().removeItem(item, 1);
        CreateNewItem.createNewItem_LV(pc, Armorid[Armorran], 1, enchantlvl);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Crystal_PieceSoul JD-Core Version: 0.6.2
 */