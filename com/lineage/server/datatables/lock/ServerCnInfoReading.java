package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.ServerCnInfoTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerCnInfoReading {
    private static ServerCnInfoReading _instance;
    private final Lock _lock;
    private final ServerCnInfoTable _storage;

    private ServerCnInfoReading() {
        _lock = new ReentrantLock(true);
        _storage = new ServerCnInfoTable();
    }

    public static ServerCnInfoReading get() {
        if (_instance == null) {
            _instance = new ServerCnInfoReading();
        }
        return _instance;
    }

    public void create(L1PcInstance pc, L1Item itemtmp, long count, boolean mode) {
        _lock.lock();
        try {
            _storage.create(pc, itemtmp, count, mode);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.ServerCnInfoReading JD-Core Version: 0.6.2
 */