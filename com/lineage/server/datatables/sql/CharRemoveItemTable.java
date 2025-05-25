package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.storage.CharRemoveItemStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 輔助(自動刪物)倉庫物件清單
 *
 * @author juonena
 */
public class CharRemoveItemTable implements CharRemoveItemStorage {
    private static final Log _log = LogFactory.getLog(CharRemoveItemTable.class);
    // 輔助(自動刪物)倉庫物件清單 (角色ObjId) (物品清單)
    private static final Map<Integer, CopyOnWriteArrayList<L1ItemInstance>> _itemList = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<L1ItemInstance>>();

    /**
     * 刪除錯誤物品資料
     *
     * @param item_id
     */
    private static void errorItem(int item_id) {
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("DELETE FROM `character_remove_item` WHERE `item_id`=?");
            ps.setInt(1, item_id);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    /**
     * 建立資料
     *
     * @param char_id
     * @param item
     */
    private static void addItem(final int char_id, final L1ItemInstance item) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(char_id);
        if (list == null) {
            list = new CopyOnWriteArrayList<L1ItemInstance>();
            if (!list.contains(item)) {
                list.add(item);
            }
        } else {
            if (!list.contains(item)) {
                list.add(item);
            }
        }
        _itemList.put(char_id, list);
    }

    /**
     * 預先載入
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        int i = 0;
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `character_remove_item`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int objid = rs.getInt("char_id");
                final int item_id = rs.getInt("item_id");
                final L1ItemInstance item = new L1ItemInstance();
                final L1Item itemTemplate = ItemTable.get().getTemplate(item_id);
                if (itemTemplate == null) {
                    // 無該物品資料 移除
                    errorItem(item_id);
                    continue;
                }
                item.setItem(itemTemplate);
                item.setItemId(item.getItemId());
                addItem(objid, item);
                i++;
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
        _log.info("讀取->輔助(自動刪物)倉庫物件清單資料數量: " + _itemList.size() + "/" + i + "(" + timer.get() + "ms)");
    }

    /**
     * 傳回全部輔助(自動刪物)倉庫數據
     */
    public Map<Integer, CopyOnWriteArrayList<L1ItemInstance>> allItems() {
        return _itemList;
    }

    /**
     * 傳回輔助(自動刪物)倉庫數據
     *
     * @param char_id
     */
    public CopyOnWriteArrayList<L1ItemInstance> loadItems(final int char_id) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(char_id);
        if (list != null) {
            return list;
        }
        return null;
    }

    /**
     * 輔助(自動刪物)倉庫是否有指定數據
     */
    public boolean getUserItems(final int char_id, final int item_id) {
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(char_id);
        if (list != null) {
            if (list.size() <= 0) {
                return false;
            }
            for (L1ItemInstance item : list) {
                if (item.getItemId() == item_id) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 加入輔助(自動刪物)倉庫數據
     */
    public void insertItem(final int char_id, final L1ItemInstance item) {
        addItem(char_id, item);
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("INSERT INTO `character_remove_item` SET `char_id`=?," + "`item_id`= ?,`item_name`=?");
            int i = 0;
            ps.setInt(++i, char_id);
            ps.setInt(++i, item.getItemId());
            ps.setString(++i, item.getItem().getName());
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    /**
     * 輔助(自動刪物)倉庫物品資料刪除
     */
    public void deleteItem(final int char_id, final L1ItemInstance item) {
        // System.out.println("輔助(自動刪物)倉庫物品資料刪除 : SQL");
        CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(char_id);
        if (list != null) {
            list.remove(item);
            Connection co = null;
            PreparedStatement pstm = null;
            try {
                co = DatabaseFactory.get().getConnection();
                pstm = co.prepareStatement("DELETE FROM `character_remove_item` WHERE `item_id`=?");
                pstm.setInt(1, item.getItemId());
                pstm.execute();
            } catch (final SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(pstm);
                SQLUtil.close(co);
            }
        }
    }
}
