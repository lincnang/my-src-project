package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharItemSublimationTable;
import com.lineage.server.datatables.storage.CharItemSublimationStorage;
import com.lineage.server.templates.CharItemSublimation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 全部角色昇華物品
 */
public class CharItemSublimationReading {

    private static class Holder {
        private static final CharItemSublimationReading INSTANCE = new CharItemSublimationReading();
    }
    private final Lock _lock;
    private final CharItemSublimationStorage _storage;

    private CharItemSublimationReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new CharItemSublimationTable();
    }

    public static CharItemSublimationReading get() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化載入
     */
    public void load(final boolean local) {
        this._lock.lock();
        try {
            this._storage.load(local);
        } finally {
            this._lock.unlock();
        }
    }

    public void storeItem(final CharItemSublimation sublimation) {
        this._lock.lock();
        try {
            this._storage.storeItem(sublimation);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            this._lock.unlock();
        }
    }

    public void updateItem(final CharItemSublimation sublimation) {
        this._lock.lock();
        try {
            this._storage.updateItem(sublimation);
        } finally {
            this._lock.unlock();
        }
    }

    public void deleteItem(final int item_obj_id) {
        this._lock.lock();
        try {
            this._storage.deleteItem(item_obj_id);
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 根據物品ID載入昇華資料
     */
    public CharItemSublimation loadItem(final int item_obj_id) {
        return CharItemSublimationTable.get().loadItem(item_obj_id);
    }
}
