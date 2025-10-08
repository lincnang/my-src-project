package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.IpTable;
import com.lineage.server.datatables.storage.IpStorage;

import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IpReading {
    private static class Holder {
        private static final IpReading INSTANCE = new IpReading();
    }
    private final Lock _lock;
    private final IpStorage _storage;

    private IpReading() {
        _lock = new ReentrantLock(true);
        _storage = new IpTable();
    }

    public static IpReading get() {
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

    public void setUnbanTime(Timestamp time) {
        _lock.lock();
        try {
            _storage.setUnbanTime(time);
        } finally {
            _lock.unlock();
        }
    }

    public void add(String ip, String info) {
        _lock.lock();
        try {
            _storage.add(ip, info);
        } finally {
            _lock.unlock();
        }
    }

    public void remove(String ip) {
        _lock.lock();
        try {
            _storage.remove(ip);
        } finally {
            _lock.unlock();
        }
    }

    public void checktime(String key) {
        _lock.lock();
        try {
            _storage.checktime(key);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.IpReading JD-Core Version: 0.6.2
 */