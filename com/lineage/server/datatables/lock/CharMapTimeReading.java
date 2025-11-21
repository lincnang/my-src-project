package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharMapTimeTable;
import com.lineage.server.datatables.storage.CharMapTimeStorage;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 人物記時地圖信息
 *
 * @author sudawei
 */
public class CharMapTimeReading {
    private static class Holder {
        private static final CharMapTimeReading INSTANCE = new CharMapTimeReading();
    }
    private final Lock _lock;
    private final CharMapTimeStorage _storage;

    private CharMapTimeReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new CharMapTimeTable();
    }

    /**
     * 人物記時地圖信息
     *
     * @author sudawei
     */
    public static CharMapTimeReading get() {
        return Holder.INSTANCE;
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
     * 新增地圖入場時間紀錄
     *
     */
    public Map<Integer, Integer> addTime(int objId, int order_id, int used_time) {
        this._lock.lock();
        Map<Integer, Integer> tmp;
        try {
            tmp = this._storage.addTime(objId, order_id, used_time);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    public Map<Integer, Integer> addBonus(int objId, int order_id, int bonus_time) {
        this._lock.lock();
        Map<Integer, Integer> tmp;
        try {
            tmp = this._storage.addBonus(objId, order_id, bonus_time);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * 取回地圖入場時間紀錄
     *
     */
    public void getTime(final L1PcInstance pc) {
        this._lock.lock();
        try {
            this._storage.getTime(pc);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 刪除地圖入場時間紀錄
     *
     */
    public void deleteTime(final int objid) {
        this._lock.lock();
        try {
            this._storage.deleteTime(objid);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 刪除並儲存全部地圖入場時間紀錄
     */
    public void saveAllTime() {
        this._lock.lock();
        try {
            this._storage.saveAllTime();
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 清除全部地圖入場時間紀錄 (重置用)
     */
    public void clearAllTime() {
        this._lock.lock();
        try {
            this._storage.clearAllTime();
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 更新用戶記時地圖信息
     */
    public void update(int objid, int mapid, int time) {
        this._lock.lock();
        try {
            this._storage.update(objid, mapid, time);
        } finally {
            this._lock.unlock();
        }
    }

    public void updateBonus(int objid, int mapid, int bonus_time) {
        this._lock.lock();
        try {
            this._storage.updateBonus(objid, mapid, bonus_time);
        } finally {
            this._lock.unlock();
        }
    }
}