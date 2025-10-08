package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.CharaterTradeTable;
import com.lineage.server.datatables.storage.CharaterTradeStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1CharaterTrade;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharaterTradeReading {
    private static class Holder {
        private static final CharaterTradeReading INSTANCE = new CharaterTradeReading();
    }
    private final Lock _lock;
    private final CharaterTradeStorage _storage;

    private CharaterTradeReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new CharaterTradeTable();
    }

    public static CharaterTradeReading get() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化載入
     */
    public void load() {
        this._lock.lock();
        try {
            this._storage.load();
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 獲取所有角色交易
     */
    public Collection<L1CharaterTrade> getAllCharaterTradeValues() {
        return this._storage.getAllCharaterTradeValues();
    }

    /**
     * 獲取編號
     */
    public int get_nextId() {
        this._lock.lock();
        int tmp = -1;
        try {
            tmp = this._storage.get_nextId();
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * 增加角色交易
     */
    public boolean addCharaterTrade(final L1CharaterTrade charaterTrade) {
        return this._storage.addCharaterTrade(charaterTrade);
    }

    /**
     * 更新角色交易
     * <br>
     * state 0:普通 1:已交易未領取 2:已交易已領取 3:已撤銷
     */
    public void updateCharaterTrade(final L1CharaterTrade charaterTrade, final int state) {
        this._lock.lock();
        try {
            this._storage.updateCharaterTrade(charaterTrade, state);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 加載當前賬號內的所有角色不包括自己
     */
    public void loadCharacterName(final L1PcInstance pc) {
        this._storage.loadCharacterName(pc);
    }

    /**
     * 更新人物的綁定狀態
     *
     */
    public boolean updateBindChar(final int objId, final int state) {
        return this._storage.updateBindChar(objId, state);
    }

    /**
     * 獲取交易的簡易人物
     *
     */
    public L1PcInstance getPcInstance(final int objId) {
        this._lock.lock();
        L1PcInstance tmp = null;
        try {
            tmp = this._storage.getPcInstance(objId);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    public L1CharaterTrade getCharaterTrade(final int id) {
        return this._storage.getCharaterTrade(id);
    }

    public void updateCharAccountName(final int objId, final String accountName) {
        this._storage.updateCharAccountName(objId, accountName);
    }
}
