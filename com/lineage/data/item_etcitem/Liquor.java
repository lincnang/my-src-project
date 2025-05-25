package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Liquor;

public class Liquor extends ItemExecutor {
    public static ItemExecutor get() {
        return new Liquor();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        pc.setDrink(true);
        pc.sendPackets(new S_Liquor(pc.getId()));
        pc.getInventory().removeItem(item, 1L);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Liquor JD-Core Version: 0.6.2
 */