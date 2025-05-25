package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;

public class Light extends ItemExecutor {
    public static ItemExecutor get() {
        return new Light();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item.isNowLighting()) {
            item.setNowLighting(false);
            pc.turnOnOffLight();
        } else {
            item.setNowLighting(true);
            pc.turnOnOffLight();
        }
        pc.sendPackets(new S_ItemName(item));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Light JD-Core Version: 0.6.2
 */