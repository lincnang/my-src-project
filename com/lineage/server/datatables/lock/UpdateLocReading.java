package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.UpdateLocTable;
import com.lineage.server.datatables.storage.UpdateLocStorage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UpdateLocReading {
    private static UpdateLocReading _instance;
    private final Lock _lock;
    private final UpdateLocStorage _storage;

    private UpdateLocReading() {
        _lock = new ReentrantLock(true);
        _storage = new UpdateLocTable();
    }

    public static UpdateLocReading get() {
        if (_instance == null) {
            _instance = new UpdateLocReading();
        }
        return _instance;
    }

    public void setPcLoc(String accName) {
        _lock.lock();
        try {
            _storage.setPcLoc(accName);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.UpdateLocReading JD-Core Version: 0.6.2
 */