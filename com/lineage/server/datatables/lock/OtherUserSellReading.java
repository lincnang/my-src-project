package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.OtherUserSellTable;
import com.lineage.server.datatables.storage.OtherUserSellStorage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OtherUserSellReading {
    private static OtherUserSellReading _instance;
    private final Lock _lock;
    private final OtherUserSellStorage _storage;

    private OtherUserSellReading() {
        _lock = new ReentrantLock(true);
        _storage = new OtherUserSellTable();
    }

    public static OtherUserSellReading get() {
        if (_instance == null) {
            _instance = new OtherUserSellReading();
        }
        return _instance;
    }

    public void add(String itemname, int itemobjid, int itemadena, long itemcount, int pcobjid, String pcname, int srcpcobjid, String srcpcname) {
        _lock.lock();
        try {
            _storage.add(itemname, itemobjid, itemadena, itemcount, pcobjid, pcname, srcpcobjid, srcpcname);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.OtherUserSellReading JD-Core Version:
 * 0.6.2
 */