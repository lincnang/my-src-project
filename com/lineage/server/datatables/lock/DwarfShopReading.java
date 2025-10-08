package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.DwarfShopTable;
import com.lineage.server.datatables.storage.DwarfShopStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1ShopS;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DwarfShopReading {
    private static class Holder {
        private static final DwarfShopReading INSTANCE = new DwarfShopReading();
    }
    private final Lock _lock;
    private final DwarfShopStorage _storage;

    private DwarfShopReading() {
        _lock = new ReentrantLock(true);
        _storage = new DwarfShopTable();
    }

    public static DwarfShopReading get() {
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

    public int nextId() {
        _lock.lock();
        int tmp = 1;
        try {
            tmp += _storage.get_id();
            _storage.set_id(tmp);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public HashMap<Integer, L1ShopS> allShopS() {
        _lock.lock();
        HashMap<Integer, L1ShopS> tmp = null;
        try {
            tmp = _storage.allShopS();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public Map<Integer, L1ItemInstance> allItems() {
        _lock.lock();
        Map<Integer, L1ItemInstance> tmp = null;
        try {
            tmp = _storage.allItems();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1ShopS getShopS(int objid) {
        _lock.lock();
        L1ShopS tmp = null;
        try {
            tmp = _storage.getShopS(objid);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public HashMap<Integer, L1ShopS> getShopSMap(int pcobjid) {
        _lock.lock();
        HashMap<Integer, L1ShopS> tmp = null;
        try {
            tmp = _storage.getShopSMap(pcobjid);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void insertItem(int key, L1ItemInstance item, L1ShopS shopS) {
        _lock.lock();
        try {
            _storage.insertItem(key, item, shopS);
        } finally {
            _lock.unlock();
        }
    }

    public void updateShopS(L1ShopS shopS) {
        _lock.lock();
        try {
            _storage.updateShopS(shopS);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteItem(int key) {
        _lock.lock();
        try {
            _storage.deleteItem(key);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.DwarfShopReading JD-Core Version: 0.6.2
 */