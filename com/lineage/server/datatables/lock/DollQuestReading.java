package com.lineage.server.datatables.lock;

import com.add.Tsai.dollQuest;
import com.add.Tsai.dollQuestTable;
import com.lineage.server.datatables.storage.DollQuestStorage;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DollQuestReading {
    private static DollQuestReading _instance;
    private final Lock _lock;
    private final DollQuestStorage _storage;

    private DollQuestReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new dollQuestTable();
    }

    public static DollQuestReading get() {
        if (_instance == null) {
            _instance = new DollQuestReading();
        }
        return _instance;
    }

    public void load() {
        this._lock.lock();
        try {
            this._storage.load();
        } finally {
            this._lock.unlock();
        }
    }

    public List<dollQuest> get(final String account) {
        this._lock.lock();
        List<dollQuest> tmp = null;
        try {
            tmp = this._storage.get(account);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    public void storeQuest(final String account, final int key, final dollQuest value) {
        this._lock.lock();
        try {
            this._storage.storeQuest(account, key, value);
        } finally {
            this._lock.unlock();
        }
    }

    public void storeQuest2(final String account, final int key, final int value) {
        this._lock.lock();
        try {
            this._storage.storeQuest2(account, key, value);
        } finally {
            this._lock.unlock();
        }
    }

    public void updateQuest(final String account, final int key, final dollQuest value) {
        this._lock.lock();
        try {
            this._storage.updateQuest(account, key, value);
        } finally {
            this._lock.unlock();
        }
    }

    public void delQuest(final String account, final int key) {
        this._lock.lock();
        try {
            this._storage.delQuest(account, key);
        } finally {
            this._lock.unlock();
        }
    }

    public void delQuest2(final int key) {
        this._lock.lock();
        try {
            this._storage.delQuest2(key);
        } finally {
            this._lock.unlock();
        }
    }

    public void storeQuest(final String account, final int key, final dollQuest value, final int clear) {
        this._lock.lock();
        try {
            this._storage.storeQuest(account, key, value, clear);
        } finally {
            this._lock.unlock();
        }
    }
}
