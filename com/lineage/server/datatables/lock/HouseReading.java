package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.HouseTable;
import com.lineage.server.datatables.storage.HouseStorage;
import com.lineage.server.templates.L1House;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HouseReading {
    private static class Holder {
        private static final HouseReading INSTANCE = new HouseReading();
    }
    private final Lock _lock;
    private final HouseStorage _storage;

    private HouseReading() {
        _lock = new ReentrantLock(true);
        _storage = new HouseTable();
    }

    public static HouseReading get() {
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

    public Map<Integer, L1House> getHouseTableList() {
        _lock.lock();
        Map<Integer, L1House> tmp;
        try {
            tmp = _storage.getHouseTableList();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1House getHouseTable(int houseId) {
        _lock.lock();
        L1House tmp;
        try {
            tmp = _storage.getHouseTable(houseId);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void updateHouse(L1House house) {
        _lock.lock();
        try {
            _storage.updateHouse(house);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.HouseReading JD-Core Version: 0.6.2
 */