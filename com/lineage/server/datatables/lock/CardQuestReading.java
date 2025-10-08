package com.lineage.server.datatables.lock;

import com.add.Tsai.CardQuest;
import com.add.Tsai.CardQuestTable;
import com.lineage.server.datatables.storage.CardQuestStorage;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CardQuestReading {
    private static class Holder {
        private static final CardQuestReading INSTANCE = new CardQuestReading();
    }
    private final Lock _lock;
    private final CardQuestStorage _storage;

    private CardQuestReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new CardQuestTable();
    }

    public static CardQuestReading get() {
        return Holder.INSTANCE;
    }

    public void load() {
        this._lock.lock();
        try {
            this._storage.load();
        } finally {
            this._lock.unlock();
        }
    }

    public List<CardQuest> get(final String account) {
        this._lock.lock();
        List<CardQuest> tmp = null;
        try {
            tmp = this._storage.get(account);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    public void storeQuest(final String account, final int key, final CardQuest value) {
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

    public void updateQuest(final String account, final int key, final CardQuest value) {
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

    public void storeQuest(final String account, final int key, final CardQuest value, final int clear) {
        this._lock.lock();
        try {
            this._storage.storeQuest(account, key, value, clear);
        } finally {
            this._lock.unlock();
        }
    }
}
