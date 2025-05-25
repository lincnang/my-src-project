package com.lineage.server.model;

import com.lineage.server.datatables.lock.DwarfForChaReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.CopyOnWriteArrayList;

public class L1DwarfForChaInventory extends L1Inventory {
    public static final Log _log = LogFactory.getLog(L1DwarfForChaInventory.class);
    private static final long serialVersionUID = 1L;
    private final L1PcInstance _owner;

    public L1DwarfForChaInventory(L1PcInstance owner) {
        _owner = owner;
    }

    public void loadItems() {
        try {
            CopyOnWriteArrayList<L1ItemInstance> items = DwarfForChaReading.get().loadItems(_owner.getName());
            if (items != null) {
                _items = items;
                // System.out.println("items.size() ==" + items.size());
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void insertItem(L1ItemInstance item) {
        if (item.getCount() <= 0L) {
            return;
        }
        try {
            DwarfForChaReading.get().insertItem(_owner.getName(), item);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void updateItem(L1ItemInstance item) {
        try {
            DwarfForChaReading.get().updateItem(item);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void deleteItem(L1ItemInstance item) {
        try {
            _items.remove(item);
            DwarfForChaReading.get().deleteItem(_owner.getName(), item);
            World.get().removeObject(item);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1DwarfInventory JD-Core Version: 0.6.2
 */