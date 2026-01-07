package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.StrSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 從資料表「能力力量設置」載入 STR→加成 的對應表
 * 熱重載：呼叫 reload() 即可
 */
public class StrSettingTable {
    private static final Log _log = LogFactory.getLog(StrSettingTable.class);
    private static volatile StrSettingTable _instance;

    private volatile Map<Integer, StrSetting> _byStr = Collections.emptyMap();

    public static StrSettingTable getInstance() {
        if (_instance == null) {
            synchronized (StrSettingTable.class) {
                if (_instance == null) {
                    _instance = new StrSettingTable();
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
        final String sql =
                "SELECT 力量, PVP攻擊, PVP命中, PVE攻擊, PVE命中, 爆擊機率, 爆擊百分比倍率, 爆擊特效 " +
                        "FROM `系統_強化力量設置` WHERE 力量 IS NOT NULL";

        Map<Integer, StrSetting> map = new HashMap<>();
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int str = rs.getInt("力量");
                StrSetting s = new StrSetting(
                        str,
                        rs.getInt("PVP攻擊"),
                        rs.getInt("PVP命中"),
                        rs.getInt("PVE攻擊"),
                        rs.getInt("PVE命中"),
                        rs.getInt("爆擊機率"),
                        rs.getInt("爆擊百分比倍率"),
                        rs.getInt("爆擊特效")
                );
                map.put(str, s);
            }
            _byStr = Collections.unmodifiableMap(map);
        } catch (SQLException e) {
            _log.error("Load 系統_強化力量設置 failed", e);
        }
    }

    /** 取得指定力量的設定；若沒有就回傳 null */
    public StrSetting findByStr(int str) {
        return _byStr.get(str);
    }

    /** 取得目前完整表（唯讀） */
    public Map<Integer, StrSetting> viewAll() {
        return _byStr;
    }
}
