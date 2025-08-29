package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.ConSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 從資料表「系統_強化體質設置」載入 CON→加成 的對應表
 * 熱重載：呼叫 reload() 即可
 */
public class ConSettingTable {
    private static final Log _log = LogFactory.getLog(ConSettingTable.class);
    private static volatile ConSettingTable _instance;

    private volatile Map<Integer, ConSetting> _byCon = Collections.emptyMap();

    public static ConSettingTable getInstance() {
        if (_instance == null) {
            synchronized (ConSettingTable.class) {
                if (_instance == null) {
                    _instance = new ConSettingTable();
                    _instance.load();
                }
            }
        }
        return _instance;
    }

    public void reload() { load(); }

    private void load() {
        final String sql =
                "SELECT 體質, 受到傷害減少, PVP傷害, PVP減免, 抗魔, 魔攻 FROM `系統_強化體質設置` WHERE 體質 IS NOT NULL";

        Map<Integer, ConSetting> map = new HashMap<>();
        try (Connection con = DatabaseFactory.get().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int conVal = rs.getInt("體質");
                ConSetting s = new ConSetting(
                        conVal,
                        rs.getInt("受到傷害減少"),
                        rs.getInt("PVP傷害"),
                        rs.getInt("PVP減免"),
                        rs.getInt("抗魔"),
                        rs.getInt("魔攻")
                );
                map.put(conVal, s);
            }
            _byCon = Collections.unmodifiableMap(map);
        } catch (SQLException e) {
            _log.error("Load 系統_強化體質設置 failed", e);
        }
    }

    /** 取得指定體質的設定；若沒有就回傳 null */
    public ConSetting findByCon(int con) { return _byCon.get(con); }

    /** 取得目前完整表（唯讀） */
    public Map<Integer, ConSetting> viewAll() { return _byCon; }
}


