package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharRemoveItemTable;
import com.lineage.server.datatables.storage.CharRemoveItemStorage;
import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 輔助(自動刪物)倉庫數據
 */
public class CharRemoveItemReading {
    private static CharRemoveItemReading _instance;
    private final Lock _lock;
    private final CharRemoveItemStorage _storage;

    private CharRemoveItemReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new CharRemoveItemTable();
    }

    public static CharRemoveItemReading get() {
        if (_instance == null) {
            _instance = new CharRemoveItemReading();
        }
        return _instance;
    }

    /**
     * 資料預先載入
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
     * 傳回全部輔助(自動刪物)倉庫數據
     *
     * @return
     */
    public Map<Integer, CopyOnWriteArrayList<L1ItemInstance>> allItems() {
        this._lock.lock();
        Map<Integer, CopyOnWriteArrayList<L1ItemInstance>> tmp = null;
        try {
            tmp = this._storage.allItems();
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * 傳回輔助(自動刪物)倉庫數據
     *
     * @param char_id
     * @return
     */
    public CopyOnWriteArrayList<L1ItemInstance> loadItems(final int char_id) {
        this._lock.lock();
        CopyOnWriteArrayList<L1ItemInstance> tmp = null;
        try {
            tmp = this._storage.loadItems(char_id);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * 加入輔助(自動刪物)倉庫數據
     *
     * @param char_id
     * @param item
     */
    public void insertItem(final int char_id, final L1ItemInstance item) {
        this._lock.lock();
        try {
            this._storage.insertItem(char_id, item);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 輔助(自動刪物)倉庫物品資料刪除
     *
     * @param char_id
     * @param item
     */
    public void deleteItem(final int char_id, final L1ItemInstance item) {
        this._lock.lock();
        try {
            this._storage.deleteItem(char_id, item);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 輔助(自動刪物)倉庫是否有指定數據
     *
     * @param char_id
     * @param objid
     * @return
     */
    public boolean getUserItems(final int char_id, final int objid) {
        this._lock.lock();
        boolean tmp = false;
        try {
            tmp = this._storage.getUserItems(char_id, objid);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }
}
