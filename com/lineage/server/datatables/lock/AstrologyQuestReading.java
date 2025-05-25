package com.lineage.server.datatables.lock;

import com.add.Tsai.Astrology.AstrologyQuest;
import com.add.Tsai.Astrology.AstrologyQuestTable;
import com.lineage.server.datatables.storage.AstrologyQuestStorage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AstrologyQuestReading {
    private static AstrologyQuestReading _instance;
    private final Lock _lock;
    private final AstrologyQuestStorage _storage;

    private AstrologyQuestReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new AstrologyQuestTable();
    }

    public static AstrologyQuestReading get() {
        if (_instance == null) {
            _instance = new AstrologyQuestReading();
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

    /**
     * 获取到玩家指定的星盘记录
     *
     * @param char_id       玩家 objId
     * @param astrologyType 任務編號
     */
    public AstrologyQuest get(final int char_id, final int astrologyType) {
        this._lock.lock();
        AstrologyQuest tmp;
        try {
            tmp = this._storage.get(char_id, astrologyType);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * 保存玩家星盤信息
     *
     * @param char_id 玩家 objId
     * @param key     星盤編號
     * @param num     目前數量
     */
    public void storeQuest(final int char_id, final int key, final int num) {
        this._lock.lock();
        try {
            this._storage.storeQuest(char_id, key, num);
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

    /**
     * 更新玩家星盘信息
     *
     * @param char_id 玩家 objId
     * @param key     星盤編號
     * @param num     目前數量
     */
    public void updateQuest(final int char_id, final int key, final int num) {
        this._lock.lock();
        try {
            this._storage.updateQuest(char_id, key, num);
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
}
