package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.SpawnBossTable;
import com.lineage.server.datatables.storage.SpawnBossStorage;
import com.lineage.server.model.L1Spawn;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BOSS召喚資料
 *
 * @author dexc
 */
public class SpawnBossReading {
    private static SpawnBossReading _instance;
    private final Lock _lock;
    private final SpawnBossStorage _storage;

    private SpawnBossReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new SpawnBossTable();
    }

    public static SpawnBossReading get() {
        if (_instance == null) {
            _instance = new SpawnBossReading();
        }
        return _instance;
    }

    /**
     * 初始化載入
     */
    public void load() {
        this._lock.lock();
        try {
            this._storage.load();
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 更新資料庫 下次召喚時間紀錄
     *
     */
    public void upDateNextSpawnTime(final int id, final Calendar spawnTime) {
        this._lock.lock();
        try {
            this._storage.upDateNextSpawnTime(id, spawnTime);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * BOSS召喚列表中物件
     *
     */
    public L1Spawn getTemplate(final int key) {
        this._lock.lock();
        L1Spawn tmp = null;
        try {
            tmp = this._storage.getTemplate(key);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * BOSS召喚列表中物件(NPCID)
     *
     * @return _bossId
     */
    public List<Integer> bossIds() {
        this._lock.lock();
        List<Integer> tmp = null;
        try {
            tmp = this._storage.bossIds();
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    public Map<Integer, L1Spawn> get_bossSpawnTable() {
        _lock.lock();
        Map<Integer, L1Spawn> tmp = null;
        try {
            tmp = _storage.get_bossSpawnTable();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public int BossSeachSize() {
        _lock.lock();
        int s = 0;
        try {
            s = _storage.BossSeachSize();
        } finally {
            this._lock.unlock();
        }
        return s;
    }

    public L1Spawn getBossSeach(final int key) {
        this._lock.lock();
        L1Spawn tmp = null;
        try {
            tmp = this._storage.getBossSeach(key);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }
}