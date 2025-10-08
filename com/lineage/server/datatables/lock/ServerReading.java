package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.ServerTable;
import com.lineage.server.datatables.storage.ServerStorage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerReading {
    private static class Holder {
        private static final ServerReading INSTANCE = new ServerReading();
    }
    private final Lock _lock;
    private final ServerStorage _storage;

    private ServerReading() {
        _lock = new ReentrantLock(true);
        _storage = new ServerTable();
    }

    public static ServerReading get() {
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

    public int minId() {
        _lock.lock();
        int temp = 0;
        try {
            temp = _storage.minId();
        } finally {
            _lock.unlock();
        }
        return temp;
    }

    public int maxId() {
        _lock.lock();
        int temp = 0;
        try {
            temp = _storage.maxId();
        } finally {
            _lock.unlock();
        }
        return temp;
    }

    public void isStop() {
        _lock.lock();
        try {
            _storage.isStop();
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.ServerReading JD-Core Version: 0.6.2
 */