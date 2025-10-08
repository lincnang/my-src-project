package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharBuffTable;
import com.lineage.server.datatables.storage.CharBuffStorage;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharBuffReading {
    private static class Holder {
        private static final CharBuffReading INSTANCE = new CharBuffReading();
    }
    private final Lock _lock;
    private final CharBuffStorage _storage;

    private CharBuffReading() {
        _lock = new ReentrantLock(true);
        _storage = new CharBuffTable();
    }

    public static CharBuffReading get() {
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

    public void saveBuff(L1PcInstance pc) {
        _lock.lock();
        try {
            _storage.saveBuff(pc);
        } finally {
            _lock.unlock();
        }
    }

    public void buff(L1PcInstance pc) {
        _lock.lock();
        try {
            _storage.buff(pc);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteBuff(L1PcInstance pc) {
        _lock.lock();
        try {
            _storage.deleteBuff(pc);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteBuff(int objid) {
        _lock.lock();
        try {
            _storage.deleteBuff(objid);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteBuff_skill(int skillid) {
        _lock.lock();
        try {
            _storage.deleteBuff_skill(skillid);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteBuff_skill_buff(int objid) {
        _lock.lock();
        try {
            _storage.deleteBuff_skill_buff(objid);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.CharBuffReading JD-Core Version: 0.6.2
 */