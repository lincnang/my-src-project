package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.FurnitureSpawnTable;
import com.lineage.server.datatables.storage.FurnitureSpawnStorage;
import com.lineage.server.model.Instance.L1FurnitureInstance;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FurnitureSpawnReading {
    private static class Holder {
        private static final FurnitureSpawnReading INSTANCE = new FurnitureSpawnReading();
    }
    private final Lock _lock;
    private final FurnitureSpawnStorage _storage;

    private FurnitureSpawnReading() {
        _lock = new ReentrantLock(true);
        _storage = new FurnitureSpawnTable();
    }

    public static FurnitureSpawnReading get() {
        return Holder.INSTANCE;
    }

    public void load() {
        _lock.lock();
        try {
            _storage.load();
        } finally {
            _lock.unlock();
        }
    }

    public void insertFurniture(L1FurnitureInstance furniture) {
        _lock.lock();
        try {
            _storage.insertFurniture(furniture);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteFurniture(L1FurnitureInstance furniture) {
        _lock.lock();
        try {
            _storage.deleteFurniture(furniture);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.FurnitureSpawnReading JD-Core Version:
 * 0.6.2
 */