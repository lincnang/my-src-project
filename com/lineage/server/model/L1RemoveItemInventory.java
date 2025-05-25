package com.lineage.server.model;

import com.lineage.server.datatables.lock.CharRemoveItemReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 輔助(自動刪物) -> 倉庫資料
 */
public class L1RemoveItemInventory extends L1Inventory {
    public static final Log _log = LogFactory.getLog(L1RemoveItemInventory.class);
    private static final long serialVersionUID = 1L;
    private final L1PcInstance _owner;

    public L1RemoveItemInventory(final L1PcInstance owner) {
        this._owner = owner;
    }

    /**
     * 傳回輔助(自動刪物)倉庫資料
     */
    @Override
    public void loadItems() {
        try {
            final CopyOnWriteArrayList<L1ItemInstance> items = CharRemoveItemReading.get().loadItems(this._owner.getId());
            if (items != null) {
                _items = items;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 加入輔助(自動刪物)倉庫數據
     */
    @Override
    public void insertItem(final L1ItemInstance item) {
        if (item.getCount() <= 0) {
            return;
        }
        try {
            CharRemoveItemReading.get().insertItem(this._owner.getId(), item);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 輔助(自動刪物)倉庫物品資料刪除
     */
    @Override
    public void deleteItem(final L1ItemInstance item) {
        // System.out.println("倉庫物品資料刪除");
        try {
            _items.remove(item);
            CharRemoveItemReading.get().deleteItem(this._owner.getId(), item);
            World.get().removeObject(item);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
