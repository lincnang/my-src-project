package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.LogEnchantTable;
import com.lineage.server.datatables.storage.LogEnchantStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogEnchantReading {
    private static LogEnchantReading _instance;
    private final Lock _lock;
    private final LogEnchantStorage _storage;

    private LogEnchantReading() {
        _lock = new ReentrantLock(true);
        _storage = new LogEnchantTable();
    }

    public static LogEnchantReading get() {
        if (_instance == null) {
            _instance = new LogEnchantReading();
        }
        return _instance;
    }

    public void failureEnchant(L1PcInstance pc, L1ItemInstance item) {
        _lock.lock();
        try {
            _storage.failureEnchant(pc, item);
        } finally {
            _lock.unlock();
        }
    }

    /**
     * 衝裝贖回系統
     *
     */
    public void resetEnchant(L1PcInstance pc, L1ItemInstance item) {
        _lock.lock();
        try {
            _storage.resetEnchant(pc, item);
        } finally {
            _lock.unlock();
        }
    }
}
