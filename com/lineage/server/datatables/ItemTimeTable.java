package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ItemTime;
import com.lineage.server.utils.PerformanceTimer;
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
 * 物品可用時間
 *
 */
public class ItemTimeTable {
    public static final Map<Integer, L1ItemTime> TIME = new HashMap<>();
    private static final Log _log = LogFactory.getLog(ItemTimeTable.class);
    private static ItemTimeTable _instance;

    public static ItemTimeTable get() {
        if (_instance == null) {
            _instance = new ItemTimeTable();
        }
        return _instance;
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `server_item_time`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int key = rs.getInt("道具編號");
                int value = rs.getInt("分鐘");
                final boolean equip = rs.getBoolean("穿著後開始計算");
                final L1ItemTime itemTime = new L1ItemTime(value, equip);
                TIME.put(key, itemTime);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->掉落時間限制道具: " + TIME.size() + "(" + timer.get() + "ms)");
    }
}
