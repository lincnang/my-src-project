package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.DwarfForClanTable;
import com.lineage.server.datatables.storage.DwarfForClanStorage;
import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DwarfForClanReading {
    private static DwarfForClanReading _instance;
    private final Lock _lock;
    private final DwarfForClanStorage _storage;

    private DwarfForClanReading() {
        _lock = new ReentrantLock(true);
        _storage = new DwarfForClanTable();
    }

    public static DwarfForClanReading get() {
        if (_instance == null) {
            _instance = new DwarfForClanReading();
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

    public CopyOnWriteArrayList<L1ItemInstance> loadItems(String clan_name) {
        _lock.lock();
        CopyOnWriteArrayList<L1ItemInstance> tmp = null;
        try {
            tmp = _storage.loadItems(clan_name);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void delUserItems(String clan_name) {
        _lock.lock();
        try {
            _storage.delUserItems(clan_name);
        } finally {
            _lock.unlock();
        }
    }

    public void insertItem(String clan_name, L1ItemInstance item) {
        _lock.lock();
        try {
            _storage.insertItem(clan_name, item);
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

    public void deleteItem(String clan_name, L1ItemInstance item) {
        _lock.lock();
        try {
            _storage.deleteItem(clan_name, item);
        } finally {
            _lock.unlock();
        }
    }

    public boolean getUserItems(String clan_name, int objid, int count) {
        _lock.lock();
        boolean tmp = false;
        try {
            tmp = _storage.getUserItems(clan_name, objid, count);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.DwarfForClanReading JD-Core Version: 0.6.2
 */