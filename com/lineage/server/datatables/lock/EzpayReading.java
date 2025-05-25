package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.EzpayTable;
import com.lineage.server.datatables.storage.EzpayStorage;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 網站購物資料
 *
 * @author dexc
 */
public class EzpayReading {
    private static EzpayReading _instance;
    private final Lock _lock;
    private final EzpayStorage _storage;

    private EzpayReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new EzpayTable();
    }

    public static EzpayReading get() {
        if (_instance == null) {
            _instance = new EzpayReading();
        }
        return _instance;
    }

    /**
     * 傳回指定帳戶匯款資料
     *
     * @param loginName 帳號名稱
     * @return
     */
    public Map<Integer, int[]> ezpayInfo(final String loginName) {
        this._lock.lock();
        Map<Integer, int[]> tmp = null;
        try {
            tmp = this._storage.ezpayInfo(loginName);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * 傳回指定帳戶匯款資料
     *
     * @param loginName 帳號名稱
     * @param id        流水號
     * @return
     */
    public int[] ezpayInfo(final String loginName, final int id) {
        this._lock.lock();
        int[] tmp = null;
        try {
            tmp = this._storage.ezpayInfo(loginName, id);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * 更新資料
     *
     * @param loginName 帳號名稱
     * @param id        ID
     * @param pcname    領取人物
     * @param ip        IP
     */
    public boolean update(final String loginName, final int id, final String pcclan, final String pcname, final String ip) {
        this._lock.lock();
        boolean tmp = false;
        try {
            tmp = this._storage.update(loginName, id, pcclan, pcname, ip);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }
}
