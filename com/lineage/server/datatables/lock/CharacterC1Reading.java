package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharacterC1Table;
import com.lineage.server.datatables.storage.CharacterC1Storage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1User_Power;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharacterC1Reading {
    private static class Holder {
        private static final CharacterC1Reading INSTANCE = new CharacterC1Reading();
    }
    private final Lock _lock;
    private final CharacterC1Storage _storage;

    private CharacterC1Reading() {
        _lock = new ReentrantLock(true);
        _storage = new CharacterC1Table();
    }

    public static CharacterC1Reading get() {
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

    public L1User_Power get(int objectId) {
        _lock.lock();
        L1User_Power tmp;
        try {
            tmp = _storage.get(objectId);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void storeCharacterC1(L1PcInstance pc) {
        _lock.lock();
        try {
            _storage.storeCharacterC1(pc);
        } finally {
            _lock.unlock();
        }
    }

    public void updateCharacterC1(int object_id, int c1_type, String note) {
        _lock.lock();
        try {
            _storage.updateCharacterC1(object_id, c1_type, note);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.CharacterC1Reading JD-Core Version: 0.6.2
 */