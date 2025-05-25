package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.poison.L1DamagePoison;

public class Acid_Liquor extends ItemExecutor {
    public static ItemExecutor get() {
        return new Acid_Liquor();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        L1DamagePoison.doInfection(pc, pc, 3000, 5);
        pc.getInventory().removeItem(item, 1L);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Acid_Liquor JD-Core Version: 0.6.2
 */