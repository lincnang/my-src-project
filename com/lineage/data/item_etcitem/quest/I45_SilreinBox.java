package com.lineage.data.item_etcitem.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.IllusionistLv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class I45_SilreinBox extends ItemExecutor {
    public static ItemExecutor get() {
        return new I45_SilreinBox();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        pc.getInventory().removeItem(item, 1L);
        if (pc.getQuest().isStart(IllusionistLv45_1.QUEST.get_id())) {
            CreateNewItem.createNewItem(pc, 49184, 1L);
            CreateNewItem.createNewItem(pc, 49192, 1L);
            CreateNewItem.createNewItem(pc, 49192, 1L);
            CreateNewItem.createNewItem(pc, 49192, 1L);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.quest.I45_SilreinBox JD-Core Version: 0.6.2
 */