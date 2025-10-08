package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharItemsPowerTable;
import com.lineage.server.datatables.storage.CharItemsPowerStorage;
import com.lineage.server.templates.L1ItemPower_name;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 人物古文字物品資料
 *
 * @author dexc
 */
public class CharItemPowerReading {
    private static class Holder {
        private static final CharItemPowerReading INSTANCE = new CharItemPowerReading();
    }
    private final Lock _lock;
    private final CharItemsPowerStorage _storage;

    private CharItemPowerReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new CharItemsPowerTable();
    }

    public static CharItemPowerReading get() {
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
     * 增加古文字物品資料
     *
     */
    public void storeItem(final int objId, final L1ItemPower_name power) {
        this._lock.lock();
        try {
            this._storage.storeItem(objId, power);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this._lock.unlock();
        }
    }

    public void delItem(final int objId) {
        this._lock.lock();
        try {
            this._storage.delItem(objId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this._lock.unlock();
        }
    }

    /**
     * 更新凹槽資料
     *
     */
    public void updateItem(final int item_obj_id, final L1ItemPower_name power) {
        this._lock.lock();
        try {
            this._storage.updateItem(item_obj_id, power);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this._lock.unlock();
        }
    }
}
