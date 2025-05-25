package com.add.BigHot;

import java.util.concurrent.locks.ReentrantLock;

public class BigHotblingLock {
    private static BigHotblingLock _instance;
    private final BigHotblingStorage _BigHotSt;
    private final ReentrantLock _lock;

    public BigHotblingLock() {
        this._BigHotSt = new MySqlBigHotblingStorage();
        this._lock = new ReentrantLock(true);
    }

    public static BigHotblingLock create() {
        if (_instance == null) {
            _instance = new BigHotblingLock();
        }
        return _instance;
    }

    public void load() {
        this._BigHotSt.load();
    }

    public void create(int id, String number, int totalPrice, int money1, int count, int money2, int count1, int money3, int count2, int count3) {
        this._lock.lock();
        try {
            this._BigHotSt.create(id, number, totalPrice, money1, count, money2, count1, money3, count2, count3);
        } finally {
            this._lock.unlock();
        }
    }

    public L1BigHotbling[] getBigHotblingList() {
        return this._BigHotSt.getBigHotblingList();
    }

    public L1BigHotbling getBigHotbling(int id) {
        return this._BigHotSt.getBigHotbling(id);
    }
}
