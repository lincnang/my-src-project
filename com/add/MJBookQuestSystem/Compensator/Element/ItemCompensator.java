package com.add.MJBookQuestSystem.Compensator.Element;

import com.add.MJBookQuestSystem.Loader.UserMonsterBookLoader;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;

public class ItemCompensator implements CompensatorElement {
    private L1Item _item;
    private int _count;
    private int _compLevel;

    public ItemCompensator(int itemId, int count, int compLevel) {
        this(ItemTable.get().getTemplate(itemId), count, compLevel);
    }

    public ItemCompensator(L1Item item, int count, int compLevel) {
        _item = item;
        _count = count;
        _compLevel = compLevel;
    }

    @Override
    public void compensate(L1PcInstance pc) {
        if (_item == null || _count <= 0) {
            return;
        }
        pc.getInventory().storeItem(_item.getItemId(), _count, _compLevel);
        if (_compLevel == 0) {
            pc.sendPackets(new S_SystemMessage("恭喜獲得【 " + _item.getName() + " " + _count + "個 】。"));
        } else {
            pc.sendPackets(new S_SystemMessage("恭喜獲得【 +" + _compLevel + " " + _item.getName() + " " + _count + "個 】。"));
        }
        UserMonsterBookLoader.store(pc);
        // UserWeekQuestLoader.store(pc);
    }

    public int getDescId() {
        return _item.getItemDescId();
    }

    public int getCount() {
        return _count;
    }
}
