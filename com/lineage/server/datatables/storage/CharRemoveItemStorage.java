package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 輔助(自動刪物)倉庫物件清單
 *
 * @author juonena
 */
public interface CharRemoveItemStorage {
    /**
     * 預先載入
     */
    public void load();

    /**
     * 傳回全部倉庫數據
     *
     * @return
     */
    public Map<Integer, CopyOnWriteArrayList<L1ItemInstance>> allItems();

    /**
     * 傳回倉庫數據
     *
     * @param char_id
     * @return
     */
    public CopyOnWriteArrayList<L1ItemInstance> loadItems(final int char_id);

    /**
     * 該倉庫是否有指定數據
     *
     * @param char_id
     * @param objid
     * @return
     */
    public boolean getUserItems(final int char_id, final int objid);

    /**
     * 加入倉庫數據
     *
     * @param char_id
     * @param item
     */
    public void insertItem(final int char_id, final L1ItemInstance item);

    /**
     * 倉庫物品資料刪除
     *
     * @param char_id
     * @param item
     */
    public void deleteItem(final int char_id, final L1ItemInstance item);
}
