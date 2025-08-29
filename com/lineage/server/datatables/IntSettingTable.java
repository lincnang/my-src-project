package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.IntSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 從資料表「系統_強化智力設置」載入 INT→加成 的對應表
 * 熱重載：呼叫 reload() 即可
 */
public class IntSettingTable {
    private static final Log _log = LogFactory.getLog(IntSettingTable.class);
    private static volatile IntSettingTable _instance;

    private volatile Map<Integer, IntSetting> _byInt = Collections.emptyMap();

    public static IntSettingTable getInstance() {
        if (_instance == null) {
            synchronized (IntSettingTable.class) {
                if (_instance == null) {
                    _instance = new IntSettingTable();
                    _instance.load();
                }
            }
        }
        return _instance;
    }

    public void reload() {
        load();
    }

    private void load() {
        com.lineage.server.utils.PerformanceTimer timer = new com.lineage.server.utils.PerformanceTimer();
        final String sql =
                "SELECT 智力, 魔攻, 魔法命中, 魔法穿透率, 忽略魔防, 魔法爆擊特效 " +
                        "FROM `系統_強化智力設置` WHERE 智力 IS NOT NULL";

        Map<Integer, IntSetting> map = new HashMap<>();
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int intelligence = rs.getInt("智力");
                IntSetting s = new IntSetting(
                        intelligence,
                        rs.getInt("魔攻"),
                        rs.getInt("魔法命中"),
                        rs.getInt("魔法穿透率"),
                        rs.getInt("忽略魔防"),
                        rs.getInt("魔法爆擊特效")
                );
                map.put(intelligence, s);
            }
            _byInt = Collections.unmodifiableMap(map);
            _log.info("讀取->[系統]_強化智力設置資料數量: " + map.size() + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error("Load 系統_強化智力設置 failed", e);
        }
    }

    /** 取得指定智力的設定；若沒有就回傳 null */
    public IntSetting findByInt(int intelligence) {
        return _byInt.get(intelligence);
    }

    /** 取得目前完整表（唯讀） */
    public Map<Integer, IntSetting> viewAll() {
        return _byInt;
    }
}
