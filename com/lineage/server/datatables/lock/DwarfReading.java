package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.DwarfTable;
import com.lineage.server.datatables.storage.DwarfStorage;
import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DwarfReading {
    private static class Holder {
        private static final DwarfReading INSTANCE = new DwarfReading();
    }
    private final Lock _lock;
    private final DwarfStorage _storage;

    private DwarfReading() {
        _lock = new ReentrantLock(true);
        _storage = new DwarfTable();
    }

    public static DwarfReading get() {
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

    public Map<String, CopyOnWriteArrayList<L1ItemInstance>> allItems() {
        _lock.lock();
        Map<String, CopyOnWriteArrayList<L1ItemInstance>> tmp = null;
        try {
            tmp = _storage.allItems();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public CopyOnWriteArrayList<L1ItemInstance> loadItems(String account_name) {
        _lock.lock();
        CopyOnWriteArrayList<L1ItemInstance> tmp = null;
        try {
            tmp = _storage.loadItems(account_name);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void delUserItems(String account_name) {
        _lock.lock();
        try {
            _storage.delUserItems(account_name);
        } finally {
            _lock.unlock();
        }
    }

    public void insertItem(String account_name, L1ItemInstance item) {
        _lock.lock();
        try {
            _storage.insertItem(account_name, item);
        } finally {
            _lock.unlock();
        }
    }

    public void updateItem(L1ItemInstance item) {
        _lock.lock();
        try {
            _storage.updateItem(item);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteItem(String account_name, L1ItemInstance item) {
        _lock.lock();
        try {
            _storage.deleteItem(account_name, item);
        } finally {
            _lock.unlock();
        }
    }

    public boolean getUserItems(String account_name, int objid, int count) {
        _lock.lock();
        boolean tmp = false;
        try {
            tmp = _storage.getUserItems(account_name, objid, count);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }
}
/*
 * Location: C:\Users\kenny\Desktop\ Qualified Name:
 * com.lineage.server.datatables.lock.DwarfReading JD-Core Version: 0.6.2
 */