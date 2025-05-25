package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.VIPTable;
import com.lineage.server.datatables.storage.VIPStorage;
import com.lineage.server.model.Instance.L1PcInstance;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * VIP系統紀錄資料
 *
 * @author dexc
 */
public class VIPReading {
    private static VIPReading _instance;
    private final Lock _lock;
    private final VIPStorage _storage;

    private VIPReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new VIPTable();
    }

    public static VIPReading get() {
        if (_instance == null) {
            _instance = new VIPReading();
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
     * 全部VIP紀錄
     *
     * @return
     */
    public Map<Integer, Timestamp> map() {
        this._lock.lock();
        Map<Integer, Timestamp> tmp;
        try {
            tmp = this._storage.map();
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * VIP系統紀錄
     *
     * @param pc
     */
    public Timestamp getOther(final L1PcInstance pc) {
        this._lock.lock();
        Timestamp tmp;
        try {
            tmp = this._storage.getOther(pc);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * 增加/更新 VIP系統紀錄
     *
     * @param key
     * @param value
     */
    public void storeOther(final int key, final Timestamp value) {
        this._lock.lock();
        try {
            this._storage.storeOther(key, value);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 刪除VIP系統紀錄
     *
     * @param key PC OBJID
     */
    public void delOther(final int key) {
        this._lock.lock();
        try {
            this._storage.delOther(key);
        } finally {
            this._lock.unlock();
        }
    }
}
