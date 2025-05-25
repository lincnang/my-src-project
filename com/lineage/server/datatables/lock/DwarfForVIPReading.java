//package com.lineage.server.datatables.lock;
//
////import com.lineage.server.datatables.sql.DwarfForVIPTable;
//import com.lineage.server.datatables.storage.DwarfForVIPStorage;
//import com.lineage.server.model.Instance.L1ItemInstance;
//
//import java.util.Map;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * 專屬倉庫數據
// *
// * @author juonena
// */
//public class DwarfForVIPReading {
//    private static DwarfForVIPReading _instance;
//    private final Lock _lock;
//    private final DwarfForVIPStorage _storage;
//
//    private DwarfForVIPReading() {
//        this._lock = new ReentrantLock(true);
//        this._storage = new DwarfForVIPTable();
//    }
//
//    public static DwarfForVIPReading get() {
//        if (_instance == null) {
//            _instance = new DwarfForVIPReading();
//        }
//        return _instance;
//    }
//
//    /**
//     * 資料預先載入
//     */
//    public void load() {
//        this._lock.lock();
//        try {
//            this._storage.load();
//        } finally {
//            this._lock.unlock();
//        }
//    }
//
//    /**
//     * 傳回全部專屬倉庫數據
//     *
//     * @return
//     */
//    public Map<String, CopyOnWriteArrayList<L1ItemInstance>> allItems() {
//        this._lock.lock();
//        Map<String, CopyOnWriteArrayList<L1ItemInstance>> tmp = null;
//        try {
//            tmp = this._storage.allItems();
//        } finally {
//            this._lock.unlock();
//        }
//        return tmp;
//    }
//
//    /**
//     * 傳回專屬倉庫數據
//     *
//     * @return
//     */
//    public CopyOnWriteArrayList<L1ItemInstance> loadItems(final String char_name) {
//        this._lock.lock();
//        CopyOnWriteArrayList<L1ItemInstance> tmp = null;
//        try {
//            tmp = this._storage.loadItems(char_name);
//        } finally {
//            this._lock.unlock();
//        }
//        return tmp;
//    }
//
//    /**
//     * 刪除專屬倉庫資料(完整)
//     *
//     * @param char_name
//     */
//    public void delUserItems(final String char_name) {
//        this._lock.lock();
//        try {
//            this._storage.delUserItems(char_name);
//        } finally {
//            this._lock.unlock();
//        }
//    }
//
//    /**
//     * 加入專屬倉庫數據
//     */
//    public void insertItem(final String char_name, final L1ItemInstance item) {
//        this._lock.lock();
//        try {
//            this._storage.insertItem(char_name, item);
//        } finally {
//            this._lock.unlock();
//        }
//    }
//
//    /**
//     * 專屬倉庫資料更新(物品數量)
//     *
//     * @param item
//     */
//    public void updateItem(final L1ItemInstance item) {
//        this._lock.lock();
//        try {
//            this._storage.updateItem(item);
//        } finally {
//            this._lock.unlock();
//        }
//    }
//
//    /**
//     * 專屬倉庫物品資料刪除
//     *
//     * @param account_name
//     * @param item
//     */
//    public void deleteItem(final String char_name, final L1ItemInstance item) {
//        this._lock.lock();
//        try {
//            this._storage.deleteItem(char_name, item);
//        } finally {
//            this._lock.unlock();
//        }
//    }
//
//    /**
//     * 專屬倉庫是否有指定數據
//     *
//     * @param account_name
//     * @param objid
//     * @param count
//     * @return
//     */
//    public boolean getUserItems(final String char_name, final int objid, int count) {
//        this._lock.lock();
//        boolean tmp = false;
//        try {
//            tmp = this._storage.getUserItems(char_name, objid, count);
//        } finally {
//            this._lock.unlock();
//        }
//        return tmp;
//    }
//}
