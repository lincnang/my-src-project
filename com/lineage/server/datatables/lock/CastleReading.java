package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CastleTable;
import com.lineage.server.datatables.storage.CastleStorage;
import com.lineage.server.templates.L1Castle;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CastleReading {
    private static CastleReading _instance;
    private final Lock _lock;
    private final CastleStorage _storage;

    private CastleReading() {
        _lock = new ReentrantLock(true);
        _storage = new CastleTable();
    }

    public static CastleReading get() {
        if (_instance == null) {
            _instance = new CastleReading();
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

    public Map<Integer, L1Castle> getCastleMap() {
        _lock.lock();
        Map<Integer, L1Castle> tmp;
        try {
            tmp = _storage.getCastleMap();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1Castle[] getCastleTableList() {
        _lock.lock();
        L1Castle[] tmp;
        try {
            tmp = _storage.getCastleTableList();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1Castle getCastleTable(int id) {
        _lock.lock();
        L1Castle tmp;
        try {
            tmp = _storage.getCastleTable(id);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void updateCastle(L1Castle castle) {
        _lock.lock();
        try {
            _storage.updateCastle(castle);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.CastleReading JD-Core Version: 0.6.2
 */