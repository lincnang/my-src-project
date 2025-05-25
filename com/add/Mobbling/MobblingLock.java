package com.add.Mobbling;

import java.util.concurrent.locks.ReentrantLock;

public class MobblingLock {
    private static MobblingLock _instance;
    private final MobblingStorage _MobSt;
    private final ReentrantLock _lock;

    public MobblingLock() {
        this._MobSt = new MySqlMobblingStorage();
        this._lock = new ReentrantLock(true);
    }

    public static MobblingLock create() {
        if (_instance == null) {
            _instance = new MobblingLock();
        }
        return _instance;
    }

    public void load() {
        this._MobSt.load();
    }

    public void create(int id, int npcid, double rate, int totalPrice) {
        this._lock.lock();
        try {
            this._MobSt.create(id, npcid, rate, totalPrice);
        } finally {
            this._lock.unlock();
        }
    }

    public L1Mobbling[] getMobblingList() {
        return this._MobSt.getMobblingList();
    }

    public L1Mobbling getMobbling(int id) {
        return this._MobSt.getMobbling(id);
    }
}