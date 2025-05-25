package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ShowPolyList;

public class Sosc_Japan extends ItemExecutor {
    public static ItemExecutor get() {
        return new Sosc_Japan();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        pc.sendPackets(new S_ShowPolyList(pc.getId(), "japanpoly"));
        if (!pc.isItemPoly()) {
            pc.setSummonMonster(false);
            pc.setItemPoly(true);
            pc.setPolyScroll(item);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Sosc_PolyReel JD-Core Version: 0.6.2
 */