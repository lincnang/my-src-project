package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.AccountBankTable;
import com.lineage.server.datatables.storage.AccountBankStorage;
import com.lineage.server.templates.L1Bank;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountBankReading {
    private static AccountBankReading _instance;
    private final Lock _lock;
    private final AccountBankStorage _storage;

    private AccountBankReading() {
        _lock = new ReentrantLock(true);
        _storage = new AccountBankTable();
    }

    public static AccountBankReading get() {
        if (_instance == null) {
            _instance = new AccountBankReading();
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

    public L1Bank get(String account_name) {
        _lock.lock();
        L1Bank tmp = null;
        try {
            tmp = _storage.get(account_name);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public Map<String, L1Bank> map() {
        _lock.lock();
        Map<String, L1Bank> tmp = null;
        try {
            tmp = _storage.map();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void create(String loginName, L1Bank bank) {
        _lock.lock();
        try {
            _storage.create(loginName, bank);
        } finally {
            _lock.unlock();
        }
    }

    public void updatePass(String loginName, String pwd) {
        _lock.lock();
        try {
            _storage.updatePass(loginName, pwd);
        } finally {
            _lock.unlock();
        }
    }

    public void updateAdena(String loginName, long adena) {
        _lock.lock();
        try {
            _storage.updateAdena(loginName, adena);
        } finally {
            _lock.unlock();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.AccountBankReading JD-Core Version: 0.6.2
 */