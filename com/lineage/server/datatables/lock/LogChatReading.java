package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.LogChatTable;
import com.lineage.server.datatables.storage.LogChatStorage;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogChatReading {
    private static class Holder {
        private static final LogChatReading INSTANCE = new LogChatReading();
    }
    private final Lock _lock;
    private final LogChatStorage _storage;

    private LogChatReading() {
        _lock = new ReentrantLock(true);
        _storage = new LogChatTable();
    }

    public static LogChatReading get() {
        return Holder.INSTANCE;
    }

    public void isTarget(L1PcInstance pc, L1PcInstance target, String text, int type) {
        _lock.lock();
        try {
            _storage.isTarget(pc, target, text, type);
        } finally {
            _lock.unlock();
        }
    }

    public void noTarget(L1PcInstance pc, String text, int type) {
        _lock.lock();
        try {
            _storage.noTarget(pc, text, type);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.LogChatReading JD-Core Version: 0.6.2
 */