package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.TownTable;
import com.lineage.server.datatables.storage.TownStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Town;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TownReading {
    private static TownReading _instance;
    private final Lock _lock;
    private final TownStorage _storage;

    private TownReading() {
        _lock = new ReentrantLock(true);
        _storage = new TownTable();
    }

    public static TownReading get() {
        if (_instance == null) {
            _instance = new TownReading();
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

    public L1Town[] getTownTableList() {
        _lock.lock();
        L1Town[] tmp;
        try {
            tmp = _storage.getTownTableList();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1Town getTownTable(int id) {
        _lock.lock();
        L1Town tmp;
        try {
            tmp = _storage.getTownTable(id);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public boolean isLeader(L1PcInstance pc, int town_id) {
        _lock.lock();
        boolean tmp;
        try {
            tmp = _storage.isLeader(pc, town_id);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void addSalesMoney(int town_id, int salesMoney) {
        _lock.lock();
        try {
            _storage.addSalesMoney(town_id, salesMoney);
        } finally {
            _lock.unlock();
        }
    }

    public void updateTaxRate() {
        _lock.lock();
        try {
            _storage.updateTaxRate();
        } finally {
            _lock.unlock();
        }
    }

    public void updateSalesMoneyYesterday() {
        _lock.lock();
        try {
            _storage.updateSalesMoneyYesterday();
        } finally {
            _lock.unlock();
        }
    }

    public String totalContribution(int townId) {
        _lock.lock();
        String tmp;
        try {
            tmp = _storage.totalContribution(townId);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void clearHomeTownID() {
        _lock.lock();
        try {
            _storage.clearHomeTownID();
        } finally {
            _lock.unlock();
        }
    }

    public int getPay(int objid) {
        _lock.lock();
        int tmp = 0;
        try {
            _storage.getPay(objid);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.TownReading JD-Core Version: 0.6.2
 */