package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.OtherUserBuyTable;
import com.lineage.server.datatables.storage.OtherUserBuyStorage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OtherUserBuyReading {
    private static class Holder {
        private static final OtherUserBuyReading INSTANCE = new OtherUserBuyReading();
    }
    private final Lock _lock;
    private final OtherUserBuyStorage _storage;

    private OtherUserBuyReading() {
        _lock = new ReentrantLock(true);
        _storage = new OtherUserBuyTable();
    }

    public static OtherUserBuyReading get() {
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
 * com.lineage.server.datatables.lock.OtherUserBuyReading JD-Core Version: 0.6.2
 */