package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ItemHtml;
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
 * 自訂道具對話系統
 */
public class ItemHtmlTable {
    public static final Log _log = LogFactory.getLog(ItemHtmlTable.class);
    private static final Map<Integer, L1ItemHtml> _htmlMap = new HashMap<>();
    private static ItemHtmlTable _instance;

    private ItemHtmlTable() {
        load();
    }

    public static ItemHtmlTable get() {
        if (_instance == null) {
            _instance = new ItemHtmlTable();
        }
        return _instance;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `etcitem_對話系統`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int itemid = rs.getInt("道具編號");
                int quest_id = rs.getInt("任務編號");
                int quest_step = rs.getInt("任務階段");
                String html = rs.getString("對話檔名");
                if (ItemTable.get().getTemplate(itemid) == null) {
                    _log.error("自定對話道具資料錯誤: 沒有這個編號的道具:" + itemid);
                } else {
                    L1ItemHtml value = new L1ItemHtml();
                    value.setItemId(itemid);
                    value.setQuestId(quest_id);
                    value.setQuestStep(quest_step);
                    value.setHtml(html);
                    _htmlMap.put(itemid, value);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->etcitem_對話系統： " + _htmlMap.size() + " (" + timer.get() + "ms)");
    }

    public L1ItemHtml getHtml(int itemid) {
        try {
            if (_htmlMap.containsKey(itemid)) {
                return (L1ItemHtml) _htmlMap.get(itemid);
            }
        } catch (Exception localException) {
        }
        return null;
    }
}