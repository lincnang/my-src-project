package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.OtherUserTradeTable;
import com.lineage.server.datatables.storage.OtherUserTradeStorage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OtherUserTitleReading {
    private static class Holder {
        private static final OtherUserTitleReading INSTANCE = new OtherUserTitleReading();
    }
    private final Lock _lock;
    private final OtherUserTradeStorage _storage;

    private OtherUserTitleReading() {
        _lock = new ReentrantLock(true);
        _storage = new OtherUserTradeTable();
    }

    public static OtherUserTitleReading get() {
        return Holder.INSTANCE;
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
 * com.lineage.server.datatables.lock.OtherUserTitleReading JD-Core Version:
 * 0.6.2
 */