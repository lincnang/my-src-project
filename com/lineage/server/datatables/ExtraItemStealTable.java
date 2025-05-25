package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ItemSteal;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 類名稱：道具奪取資料<br>
 * 類描述：<br>
 * 修改時間：2017年01月10日 <br>
 * 修改備註:版本升級為7.6C<br>
 *
 * @version<br>
 */
public final class ExtraItemStealTable {
    private static final Log _log = LogFactory.getLog(ExtraItemStealTable.class);
    private static final ArrayList<L1ItemSteal> _stealList = new ArrayList<L1ItemSteal>();
    private static ExtraItemStealTable _instance;

    public static ExtraItemStealTable getInstance() {
        if (_instance == null) {
            _instance = new ExtraItemStealTable();
        }
        return _instance;
    }

    public final void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 功能_死亡奪取物品");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int item_id = rs.getInt("道具編號");
                final int level = rs.getInt("等級以上不會被搶奪");
                final int mete_level = rs.getInt("轉生次數");
                final int steal_chance = rs.getInt("死亡被奪取機率");
                final int min_steal_count = rs.getInt("最低被奪取數量");
                final int max_steal_count = rs.getInt("最高被奪取數量");
                final boolean is_broadcast = rs.getBoolean("被奪取道具是否廣播");
                final boolean drop_on_floor = rs.getBoolean("是否掉落在地面");
                final int anti_steal_item_id = rs.getInt("防止奪取道具編號");
                // 建立儲存資料
                final L1ItemSteal itemSteal = new L1ItemSteal(item_id, level, mete_level, steal_chance, min_steal_count, max_steal_count, is_broadcast, drop_on_floor, anti_steal_item_id);
                // 加到清單
                _stealList.add(itemSteal);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->[系統]_死亡奪取系統資料數量: " + _stealList.size() + "(" + timer.get() + "ms)");
    }

    public final ArrayList<L1ItemSteal> getList() {
        return _stealList;
    }
}
