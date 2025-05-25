package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.BoardTable;
import com.lineage.server.datatables.storage.BoardStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Board;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoardReading {
    private static BoardReading _instance;
    private final Lock _lock;
    private final BoardStorage _storage;

    private BoardReading() {
        _lock = new ReentrantLock(true);
        _storage = new BoardTable();
    }

    public static BoardReading get() {
        if (_instance == null) {
            _instance = new BoardReading();
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

    public Map<Integer, L1Board> getBoardMap() {
        _lock.lock();
        Map<Integer, L1Board> tmp;
        try {
            tmp = _storage.getBoardMap();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1Board[] getBoardTableList() {
        _lock.lock();
        L1Board[] tmp;
        try {
            tmp = _storage.getBoardTableList();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1Board getBoardTable(int houseId) {
        _lock.lock();
        L1Board tmp;
        try {
            tmp = _storage.getBoardTable(houseId);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public int getMaxId() {
        _lock.lock();
        int tmp = 0;
        try {
            tmp = _storage.getMaxId();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void writeTopic(L1PcInstance pc, String date, String title, String content) {
        _lock.lock();
        try {
            _storage.writeTopic(pc, date, title, content);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteTopic(int number) {
        _lock.lock();
        try {
            _storage.deleteTopic(number);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.BoardReading JD-Core Version: 0.6.2
 */