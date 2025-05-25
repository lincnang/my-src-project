package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.drop.SetDrop;
import com.lineage.server.model.drop.SetDropExecutor;
import com.lineage.server.templates.L1DropMob;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全怪物掉落物品
 */
public class DropMobTable {
    private static final Log _log = LogFactory.getLog(DropMobTable.class);
    private static DropMobTable _instance;

    private DropMobTable() {
        final Map<Integer, L1DropMob> droplists = this.allMobDropList();
        final SetDropExecutor setDropExecutor = new SetDrop();
        setDropExecutor.addDropMob(droplists);
    }

    public static DropMobTable get() {
        if (_instance == null) {
            _instance = new DropMobTable();
        }
        return _instance;
    }

    /**
     * 刪除錯誤物品資料
     *
     */
    private static void errorItem(int itemid) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM `droplist_mob` WHERE `掉落指定道具編號`=?");
            pstm.setInt(1, itemid);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private Map<Integer, L1DropMob> allMobDropList() {
        final Map<Integer, L1DropMob> _droplistMob = new HashMap<Integer, L1DropMob>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `系統_全地圖物品掉落`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int itemId = rs.getInt("掉落指定道具編號");
                final int min = rs.getInt("最小數量");
                final int max = rs.getInt("最大數量");
                final int chance = rs.getInt("機率");
                if (check_item(itemId)) {
                    final L1DropMob drop = new L1DropMob();
                    drop.setMin(min);
                    drop.setMax(max);
                    drop.setChance(chance);
                    _droplistMob.put(itemId, drop);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->全地圖物品掉落資料數量: " + _droplistMob.size());
        return _droplistMob;
    }

    private boolean check_item(int itemId) {
        final L1Item itemTemplate = ItemTable.get().getTemplate(itemId);
        if (itemTemplate == null) {
            // 無該物品資料 移除
            errorItem(itemId);
            _log.info("删除全部怪物掉落錯誤物品->" + itemId);
            return false;
        }
        return true;
    }
}