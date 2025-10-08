package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharacterQuestTable;
import com.lineage.server.datatables.storage.CharacterQuestStorage;
import com.lineage.server.templates.CharQuest;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharacterQuestReading {
    private static class Holder {
        private static final CharacterQuestReading INSTANCE = new CharacterQuestReading();
    }
    private final Lock _lock;
    private final CharacterQuestStorage _storage;

    private CharacterQuestReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new CharacterQuestTable();
    }

    public static CharacterQuestReading get() {
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

    public Map<Integer, CharQuest> get(final int char_id) {
        this._lock.lock();
        Map<Integer, CharQuest> tmp = null;
        try {
            tmp = this._storage.get(char_id);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    public void storeQuest(final int char_id, final int key, final CharQuest value) {
        this._lock.lock();
        try {
            this._storage.storeQuest(char_id, key, value);
        } finally {
            this._lock.unlock();
        }
    }

    public void storeQuest2(final int char_id, final int key, final int value) {
        this._lock.lock();
        try {
            this._storage.storeQuest2(char_id, key, value);
        } finally {
            this._lock.unlock();
        }
    }

    public void updateQuest(final int char_id, final int key, final CharQuest value) {
        this._lock.lock();
        try {
            this._storage.updateQuest(char_id, key, value);
        } finally {
            this._lock.unlock();
        }
    }

    public void delQuest(final int char_id, final int key) {
        this._lock.lock();
        try {
            this._storage.delQuest(char_id, key);
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

    public void storeQuest(final int char_id, final int key, final CharQuest value, final int clear) {
        this._lock.lock();
        try {
            this._storage.storeQuest(char_id, key, value, clear);
        } finally {
            this._lock.unlock();
        }
    }
}
