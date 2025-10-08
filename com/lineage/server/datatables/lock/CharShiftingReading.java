package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharShiftingTable;
import com.lineage.server.datatables.storage.CharShiftingStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharShiftingReading {
    private static class Holder {
        private static final CharShiftingReading INSTANCE = new CharShiftingReading();
    }
    private final Lock _lock;
    private final CharShiftingStorage _storage;

    private CharShiftingReading() {
        _lock = new ReentrantLock(true);
        _storage = new CharShiftingTable();
    }

    public static CharShiftingReading get() {
        return Holder.INSTANCE;
    }

    public void newShifting(L1PcInstance pc, int tgId, String tgName, int srcObjid, L1Item srcItem, L1ItemInstance newItem, int mode) {
        _lock.lock();
        try {
            _storage.newShifting(pc, tgId, tgName, srcObjid, srcItem, newItem, mode);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.CharShiftingReading JD-Core Version: 0.6.2
 */