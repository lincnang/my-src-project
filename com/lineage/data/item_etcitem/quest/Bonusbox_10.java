package com.lineage.data.item_etcitem.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.Random;

public class Bonusbox_10 extends ItemExecutor {
    private final Random _random = new Random();
    private final int[] bonus_list = {56216, 56217, 56218, 56219, 56220, 56221, 56222, 56223, 56224, 56225, 56226, 56227, 56228, 56229, 56230, 56231, 56232, 56233, 49334, 49333, 49332, 49331, 49330, 49329, 49328, 49327, 56235, 49336};

    public static ItemExecutor get() {
        return new Bonusbox_10();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        pc.getInventory().removeItem(item, 1L);
        int value = _random.nextInt(2) + 1;
        for (int i = 0; i < value; i++) {
            CreateNewItem.createNewItem(pc, bonus_list[_random.nextInt(bonus_list.length)], 1L);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.quest.Bonusbox_10 JD-Core Version: 0.6.2
 */