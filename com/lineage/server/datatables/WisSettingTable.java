package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.WisSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 從資料表「系統_強化精神設置」載入 WIS→加成 的對應表
 * 熱重載：呼叫 reload() 即可
 */
public class WisSettingTable {
    private static final Log _log = LogFactory.getLog(WisSettingTable.class);
    private static volatile WisSettingTable _instance;

    private volatile Map<Integer, WisSetting> _byWis = Collections.emptyMap();

    public static WisSettingTable getInstance() {
        if (_instance == null) {
            synchronized (WisSettingTable.class) {
                if (_instance == null) {
                    _instance = new WisSettingTable();
                    _instance.load();
                }
            }
        }
        return _instance;
    }

    public void reload() { load(); }

    private void load() {
        final String sql =
                "SELECT 精神, 抗魔, 回魔 FROM `系統_強化精神設置` WHERE 精神 IS NOT NULL";

        Map<Integer, WisSetting> map = new HashMap<>();
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int wis = rs.getInt("精神");
                WisSetting s = new WisSetting(
                        wis,
                        rs.getInt("抗魔"),
                        rs.getInt("回魔")
                );
                map.put(wis, s);
            }
            _byWis = Collections.unmodifiableMap(map);
        } catch (SQLException e) {
            _log.error("Load 系統_強化精神設置 failed", e);
        }
    }

    /** 取得指定精神的設定；若沒有就回傳 null */
    public WisSetting findByWis(int wis) { return _byWis.get(wis); }

    /** 取得目前完整表（唯讀） */
    public Map<Integer, WisSetting> viewAll() { return _byWis; }
}




