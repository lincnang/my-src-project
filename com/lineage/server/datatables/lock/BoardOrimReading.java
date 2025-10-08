package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.BoardOrimTable;
import com.lineage.server.datatables.storage.BoardOrimStorage;
import com.lineage.server.templates.L1Rank;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoardOrimReading {
    private static class Holder {
        private static final BoardOrimReading INSTANCE = new BoardOrimReading();
    }
    private final Lock _lock;
    private final BoardOrimStorage _storage;

    private BoardOrimReading() {
        _lock = new ReentrantLock(true);
        _storage = new BoardOrimTable();
    }

    public static BoardOrimReading get() {
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

    public List<L1Rank> getTotalList() {
        _lock.lock();
        List<L1Rank> tmp;
        try {
            tmp = _storage.getTotalList();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public int writeTopic(int score, String leader, List<String> partyMember) {
        _lock.lock();
        int tmp = 0;
        try {
            tmp = _storage.writeTopic(score, leader, partyMember);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void renewPcName(String oriName, String newName) {
        _lock.lock();
        try {
            _storage.renewPcName(oriName, newName);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.BoardOrimReading JD-Core Version: 0.6.2
 */