package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

public class Letter_Firecrackers extends ItemExecutor {
    public static ItemExecutor get() {
        return new Letter_Firecrackers();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        int soundid = itemId - 34946;
        pc.sendPacketsAll(new S_SkillSound(pc.getId(), soundid));
        pc.getInventory().removeItem(item, 1L);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Letter_Firecrackers JD-Core Version: 0.6.2
 */