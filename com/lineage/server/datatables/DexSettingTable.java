package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.DexSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 從資料表「能力敏捷設置」載入 DEX→加成 的對應表
 * 熱重載：呼叫 reload() 即可
 */
public class DexSettingTable {
    private static final Log _log = LogFactory.getLog(DexSettingTable.class);
    private static volatile DexSettingTable _instance;

    private volatile Map<Integer, DexSetting> _byDex = Collections.emptyMap();

    public static DexSettingTable getInstance() {
        if (_instance == null) {
            synchronized (DexSettingTable.class) {
                if (_instance == null) {
                    _instance = new DexSettingTable();
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
                "SELECT 敏捷, 攻擊, 命中, 爆擊機率, 爆擊百分比倍率, 爆擊特效 " +
                        "FROM `系統_強化敏捷設置` WHERE 敏捷 IS NOT NULL";

        Map<Integer, DexSetting> map = new HashMap<>();
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int dex = rs.getInt("敏捷");
                DexSetting s = new DexSetting(
                        dex,
                        rs.getInt("攻擊"),
                        rs.getInt("命中"),
                        rs.getInt("爆擊機率"),
                        rs.getInt("爆擊百分比倍率"),
                        rs.getInt("爆擊特效")
                );
                map.put(dex, s);
            }
            _byDex = Collections.unmodifiableMap(map);
        } catch (SQLException e) {
            _log.error("Load 系統_強化敏捷設置 failed", e);
        }
    }

    /** 取得指定敏捷的設定；若沒有就回傳 null */
    public DexSetting findByDex(int dex) {
        return _byDex.get(dex);
    }

    /** 取得目前完整表（唯讀） */
    public Map<Integer, DexSetting> viewAll() {
        return _byDex;
    }
}
