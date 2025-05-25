package com.lineage.data.item_etcitem.quest;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;

public class OrcEmissaryPoly extends ItemExecutor {
    public static ItemExecutor get() {
        return new OrcEmissaryPoly();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        pc.getInventory().removeItem(item);
        L1PolyMorph.doPoly(pc, 6984, 1800, 1);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.quest.OrcEmissaryPoly JD-Core Version: 0.6.2
 */