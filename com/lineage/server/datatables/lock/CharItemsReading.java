package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharItemsTable;
import com.lineage.server.datatables.storage.CharItemsStorage;
import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharItemsReading {
    private static class Holder {
        private static final CharItemsReading INSTANCE = new CharItemsReading();
    }
    private final Lock _lock;
    private final CharItemsStorage _storage;

    private CharItemsReading() {
        _lock = new ReentrantLock(true);
        _storage = new CharItemsTable();
    }

    public static CharItemsReading get() {
        return Holder.INSTANCE;
    }

    public void load() {
        _lock.lock();
        try {
            _storage.load();
        } finally {
            _lock.unlock();
        }
    }

    public CopyOnWriteArrayList<L1ItemInstance> loadItems(Integer objid) {
        _lock.lock();
        CopyOnWriteArrayList<L1ItemInstance> tmp = null;
        try {
            tmp = _storage.loadItems(objid);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void delUserItems(Integer objid) {
        _lock.lock();
        try {
            _storage.delUserItems(objid);
        } finally {
            _lock.unlock();
        }
    }

    public void del_item(int itemid) {
        _lock.lock();
        try {
            _storage.del_item(itemid);
        } finally {
            _lock.unlock();
        }
    }

    public void storeItem(int objId, L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.storeItem(objId, item);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteItem(int objid, L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.deleteItem(objid, item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemId_Name(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemId_Name(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemId(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemId(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemCount(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemCount(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemDurability(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemDurability(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemChargeCount(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemChargeCount(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemRemainingTime(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemRemainingTime(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemEnchantLevel(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemEnchantLevel(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemEquipped(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemEquipped(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemIdentified(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemIdentified(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemBless(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemBless(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemAttrEnchantKind(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemAttrEnchantKind(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemAttrEnchantLevel(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemAttrEnchantLevel(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemDelayEffect(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemDelayEffect(item);
        } finally {
            _lock.unlock();
        }
    }

    public int getItemCount(int objId) throws Exception {
        _lock.lock();
        int tmp = 0;
        try {
            tmp = _storage.getItemCount(objId);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void getAdenaCount(int objId, long count) throws Exception {
        _lock.lock();
        try {
            _storage.getAdenaCount(objId, count);
        } finally {
            _lock.unlock();
        }
    }

    public boolean getUserItems(int pcObjId, int objid, long count) {
        _lock.lock();
        boolean tmp = false;
        try {
            tmp = _storage.getUserItems(pcObjId, objid, count);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public boolean getUserItem(int objid) {
        _lock.lock();
        boolean tmp = false;
        try {
            tmp = _storage.getUserItem(objid);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public Map<Integer, L1ItemInstance> getUserItems(int itemid) {
        _lock.lock();
        Map<Integer, L1ItemInstance> tmp = null;
        try {
            tmp = _storage.getUserItems(itemid);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public int checkItemId(int itemId) {
        _lock.lock();
        int tmp = 0;
        try {
            tmp = _storage.checkItemId(itemId);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void updateItemAttack(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemAttack(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemBowAttack(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemBowAttack(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemReductionDmg(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemReductionDmg(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemSp(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemSp(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemprobability(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemprobability(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemStr(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemStr(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemDex(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemDex(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemInt(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemInt(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemHp(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemHp(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemMp(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemMp(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemCon(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemCon(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemWis(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemWis(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemCha(L1ItemInstance item) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemCha(item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItemCharTrade(L1ItemInstance tradeItem) throws Exception {
        _lock.lock();
        try {
            _storage.updateItemCharTrade(tradeItem);
        } finally {
            _lock.unlock();
        }
    }
}
