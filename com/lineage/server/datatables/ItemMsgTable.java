package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
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
 * 類名稱：要公告的打寶物品記錄<br>
 * 類描述：<br>
 * 修改時間：2017年01月10日 <br>
 * 修改備註:版本升級為7.6C<br>
 *
 * @version<br>
 */
public class ItemMsgTable {
    private static final Log _log = LogFactory.getLog(ItemMsgTable.class);
    private static final ArrayList<Integer> _idList = new ArrayList<Integer>();
    private static ItemMsgTable _instance;

    public static ItemMsgTable get() {
        if (_instance == null) {
            _instance = new ItemMsgTable();
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
            pstm = con.prepareStatement("SELECT * FROM `功能_掉落物上廣設定`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int item_id = rs.getInt("物品編號");
                if (!_idList.contains(Integer.valueOf(item_id))) {
                    _idList.add(Integer.valueOf(item_id));
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->打寶公告物品編號數量: " + _idList.size() + "(" + timer.get() + "ms)");
    }

    public boolean contains(int item_id) {
        return _idList.contains(Integer.valueOf(item_id));
    }
}
