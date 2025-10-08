package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.MaryTable;
import com.lineage.server.datatables.storage.MaryStorage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MaryReading {
    private static class Holder {
        private static final MaryReading INSTANCE = new MaryReading();
    }
    private final Lock _lock;
    private final MaryStorage _storage;

    private MaryReading() {
        _lock = new ReentrantLock(true);
        _storage = new MaryTable();
    }

    public static MaryReading get() {
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

    public void update(long all_stake, long all_user_prize, int count) {
        _lock.lock();
        try {
            _storage.update(all_stake, all_user_prize, count);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.MaryReading JD-Core Version: 0.6.2
 */