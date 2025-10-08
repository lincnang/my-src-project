package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.AccountTable;
import com.lineage.server.datatables.storage.AccountStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Account;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountReading {
    private static class Holder {
        private static final AccountReading INSTANCE = new AccountReading();
    }
    private final Lock _lock;
    private final AccountStorage _storage;

    private AccountReading() {
        _lock = new ReentrantLock(true);
        _storage = new AccountTable();
    }

    public static AccountReading get() {
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

    public boolean isAccountUT(String loginName) {
        _lock.lock();
        boolean tmp = false;
        try {
            tmp = _storage.isAccountUT(loginName);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1Account create(String loginName, String pwd, String ip, String host, String spwd) {
        _lock.lock();
        L1Account tmp = null;
        try {
            tmp = _storage.create(loginName, pwd, ip, host, spwd);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public boolean isAccount(String loginName) {
        _lock.lock();
        boolean tmp = false;
        try {
            tmp = _storage.isAccount(loginName);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public L1Account getAccount(String loginName) {
        _lock.lock();
        L1Account tmp = null;
        try {
            tmp = _storage.getAccount(loginName);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void updateWarehouse(String loginName, int pwd) {
        _lock.lock();
        try {
            _storage.updateWarehouse(loginName, pwd);
        } finally {
            _lock.unlock();
        }
    }

    public void updateLastActive(L1Account account) {
        _lock.lock();
        try {
            _storage.updateLastActive(account);
        } finally {
            _lock.unlock();
        }
    }

    public void updateCharacterSlot(String loginName, int count) {
        _lock.lock();
        try {
            _storage.updateCharacterSlot(loginName, count);
        } finally {
            _lock.unlock();
        }
    }

    public void updatePwd(String loginName, String newpwd) {
        _lock.lock();
        try {
            _storage.updatePwd(loginName, newpwd);
        } finally {
            _lock.unlock();
        }
    }

    public void updateLan(String loginName, boolean islan) {
        _lock.lock();
        try {
            _storage.updateLan(loginName, islan);
        } finally {
            _lock.unlock();
        }
    }

    public void updateLan() {
        _lock.lock();
        try {
            _storage.updateLan();
        } finally {
            _lock.unlock();
        }
    }

    public void updateAccessLevel(String loginName) {
        _lock.lock();
        try {
            _storage.updateAccessLevel(loginName);
        } finally {
            _lock.unlock();
        }
    }

    /**
     * 是否首儲
     */
    public void updatefp(final String loginName, final int fp) { //SRC0701
        this._lock.lock();
        try {
            this._storage.updatefp(loginName, 1);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 更新TAM幣點數
     */
    public void updatetam(final String loginName, final int tam_point) { // 成長果實系統(Tam幣)
        this._lock.lock();
        try {
            this._storage.updatetam(loginName, tam_point);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 更新帳號首儲狀態
     *
     * @param loginName 帳號
     * @param count     擴充數量
     */
    public void updateFirstPay(final String loginName, final int count) {
        this._lock.lock();
        try {
            this._storage.updateFirstPay(loginName, count);
        } finally {
            this._lock.unlock();
        }
    }

    public boolean updaterecharBind(int get_char_objId) {
        this._lock.lock();
        try {
            this._storage.updaterecharBind(get_char_objId);
        } finally {
            this._lock.unlock();
        }
        return false;
    }

    public ArrayList<String> loadCharacterItems(int get_char_objId) {
        this._lock.lock();
        try {
            this._storage.loadCharacterItems(get_char_objId);
        } finally {
            this._lock.unlock();
        }
        return null;
    }

    /**
     * vip 等級、時限、最終時間
     *
     */
    public void updateVip(final L1PcInstance pc) {
        this._lock.lock();
        try {
            this._storage.updateVip(pc);
        } finally {
            this._lock.unlock();
        }
    }
}
