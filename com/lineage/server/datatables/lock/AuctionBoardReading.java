package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.AuctionBoardTable;
import com.lineage.server.datatables.storage.AuctionBoardStorage;
import com.lineage.server.templates.L1AuctionBoardTmp;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionBoardReading {
    private static AuctionBoardReading _instance;
    private final Lock _lock;
    private final AuctionBoardStorage _storage;

    private AuctionBoardReading() {
        _lock = new ReentrantLock(true);
        _storage = new AuctionBoardTable();
    }

    public static AuctionBoardReading get() {
        if (_instance == null) {
            _instance = new AuctionBoardReading();
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

    public Map<Integer, L1AuctionBoardTmp> getAuctionBoardTableList() {
        _lock.lock();
        Map<Integer, L1AuctionBoardTmp> tmp;
        try {
            tmp = _storage.getAuctionBoardTableList();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1AuctionBoardTmp getAuctionBoardTable(int houseId) {
        _lock.lock();
        L1AuctionBoardTmp tmp;
        try {
            tmp = _storage.getAuctionBoardTable(houseId);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void insertAuctionBoard(L1AuctionBoardTmp board) {
        _lock.lock();
        try {
            _storage.insertAuctionBoard(board);
        } finally {
            _lock.unlock();
        }
    }

    public void updateAuctionBoard(L1AuctionBoardTmp board) {
        _lock.lock();
        try {
            _storage.updateAuctionBoard(board);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteAuctionBoard(int houseId) {
        _lock.lock();
        try {
            _storage.deleteAuctionBoard(houseId);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.AuctionBoardReading JD-Core Version: 0.6.2
 */