package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharItemsTimeTable;
import com.lineage.server.datatables.storage.CharItemsTimeStorage;

import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharItemsTimeReading {
    private static CharItemsTimeReading _instance;
    private final Lock _lock;
    private final CharItemsTimeStorage _storage;

    private CharItemsTimeReading() {
        _lock = new ReentrantLock(true);
        _storage = new CharItemsTimeTable();
    }

    public static CharItemsTimeReading get() {
        if (_instance == null) {
            _instance = new CharItemsTimeReading();
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

    public void addTime(int itemr_obj_id, Timestamp usertime) {
        _lock.lock();
        try {
            _storage.addTime(itemr_obj_id, usertime);
        } finally {
            _lock.unlock();
        }
    }

    public void updateTime(int itemr_obj_id, Timestamp usertime) {
        _lock.lock();
        try {
            _storage.updateTime(itemr_obj_id, usertime);
        } finally {
            _lock.unlock();
        }
    }

    public boolean isExistTimeData(int itemr_obj_id) {
        _lock.lock();
        boolean exist = false;
        try {
            exist = _storage.isExistTimeData(itemr_obj_id);
        } finally {
            _lock.unlock();
        }
        return exist;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.CharItemsTimeReading JD-Core Version:
 * 0.6.2
 */