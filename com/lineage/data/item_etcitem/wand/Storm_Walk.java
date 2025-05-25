package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1Trade;

public class Storm_Walk extends ItemExecutor {
    public static ItemExecutor get() {
        return new Storm_Walk();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int spellsc_x = data[1];
        int spellsc_y = data[2];
        if (pc.getTradeID() != 0) {
            L1Trade trade = new L1Trade();
            trade.tradeCancel(pc);
        }
        L1Teleport.teleport(pc, spellsc_x, spellsc_y, pc.getMapId(), 5, false);
    }
}
