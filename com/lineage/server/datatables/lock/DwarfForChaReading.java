package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.DwarfForChaTable;
import com.lineage.server.datatables.storage.DwarfForChaStorage;
import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DwarfForChaReading {
    private static DwarfForChaReading _instance;
    private final Lock _lock;
    private final DwarfForChaStorage _storage;

    private DwarfForChaReading() {
        _lock = new ReentrantLock(true);
        _storage = new DwarfForChaTable();
    }

    public static DwarfForChaReading get() {
        if (_instance == null) {
            _instance = new DwarfForChaReading();
        }
        return _instance;
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

    public CopyOnWriteArrayList<L1ItemInstance> loadItems(String owner_name) {
        _lock.lock();
        CopyOnWriteArrayList<L1ItemInstance> tmp = null;
        try {
            tmp = _storage.loadItems(owner_name);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void delUserItems(String owner_name) {
        _lock.lock();
        try {
            _storage.delUserItems(owner_name);
        } finally {
            _lock.unlock();
        }
    }

    public void insertItem(String owner_name, L1ItemInstance item) {
        _lock.lock();
        try {
            _storage.insertItem(owner_name, item);
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

    public void deleteItem(String owner_name, L1ItemInstance item) {
        _lock.lock();
        try {
            _storage.deleteItem(owner_name, item);
        } finally {
            _lock.unlock();
        }
    }

    public boolean getUserItems(String owner_name, int objid, int count) {
        _lock.lock();
        boolean tmp = false;
        try {
            tmp = _storage.getUserItems(owner_name, objid, count);
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