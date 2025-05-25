package com.lineage.server.model;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.DwarfForClanReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1DwarfForClanInventory extends L1Inventory {
    public static final Log _log = LogFactory.getLog(L1DwarfForClanInventory.class);
    private static final long serialVersionUID = 1L;
    private final L1Clan _clan;

    public L1DwarfForClanInventory(L1Clan clan) {
        _clan = clan;
    }

    /**
     * 載入血盟倉庫資料
     */
    @Override
    public synchronized void loadItems() {
        // System.out.println("加入血盟倉庫數據");
        try {
            CopyOnWriteArrayList<L1ItemInstance> items = DwarfForClanReading.get().loadItems(this._clan.getClanName());
            if (items != null) {
                // System.out.println("加入血盟倉庫數據:"+items.size());
                _items = items;
                /*
                 * for (final L1ItemInstance item : _items) {
                 * System.out.println("加入血盟倉庫數據:"+item.getName()); }
                 */
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public synchronized void insertItem(L1ItemInstance item) {
        try {
            DwarfForClanReading.get().insertItem(_clan.getClanName(), item);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public synchronized void updateItem(L1ItemInstance item) {
        try {
            DwarfForClanReading.get().updateItem(item);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public synchronized void deleteItem(L1ItemInstance item) {
        try {
            _items.remove(item);
            DwarfForClanReading.get().deleteItem(_clan.getClanName(), item);
            World.get().removeObject(item);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public synchronized void deleteAllItems() {
        try {
            DwarfForClanReading.get().delUserItems(_clan.getClanName());
            _items.clear();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 倉庫使用履歷記錄
     *
     * @param pc    倉庫使用者
     * @param item  取引
     * @param count 數量
     * @param type  受領區分(0:預入, 1:受取)
     */
    public void writeHistory(L1PcInstance pc, L1ItemInstance item, int count, int type) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO clan_warehouse_history SET clan_id=?, char_name=?, type=?, item_name=?, item_count=?, record_time=?");
            pstm.setInt(1, _clan.getClanId());
            pstm.setString(2, pc.getName());
            pstm.setInt(3, type);
            pstm.setString(4, item.getWarehouseHistoryName());
            pstm.setInt(5, count);
            pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1DwarfForClanInventory JD-Core Version: 0.6.2
 */