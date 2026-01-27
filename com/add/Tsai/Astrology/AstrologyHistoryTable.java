package com.add.Tsai.Astrology;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 星盤解鎖紀錄表
 * 記錄玩家已經完成(解鎖)的星盤節點
 */
public class AstrologyHistoryTable {
    private static final Log _log = LogFactory.getLog(AstrologyHistoryTable.class);
    private static AstrologyHistoryTable _instance;
    
    // 儲存結構: Map<ObjId, Set<AstrologyType>>
    private static final Map<Integer, Set<Integer>> _historyMap = new ConcurrentHashMap<>();

    public static AstrologyHistoryTable get() {
        if (_instance == null) {
            synchronized (AstrologyHistoryTable.class) {
                if (_instance == null) {
                    _instance = new AstrologyHistoryTable();
                }
            }
        }
        return _instance;
    }

    /**
     * 載入所有玩家的星盤解鎖紀錄
     */
    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            _historyMap.clear();
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `character_星盤紀錄`");
            rs = ps.executeQuery();
            while (rs.next()) {
                int objId = rs.getInt("objid");
                int type = rs.getInt("type");
                
                // 使用 ConcurrentHashMap 的 KeySet 來保證線程安全
                _historyMap.computeIfAbsent(objId, k -> ConcurrentHashMap.newKeySet()).add(type);
                count++;
            }
        } catch (SQLException e) {
            _log.error("載入 character_星盤紀錄 失敗: " + e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, ps, cn);
        }
        _log.info("讀取->[character_星盤紀錄]: " + count + "(" + timer.get() + "ms)");
    }

    /**
     * 檢查玩家是否已解鎖指定星盤
     * @param objId 玩家ID
     * @param type 星盤編號
     * @return true 已解鎖, false 未解鎖
     */
    public boolean isUnlocked(int objId, int type) {
        Set<Integer> unlocked = _historyMap.get(objId);
        return unlocked != null && unlocked.contains(type);
    }

    /**
     * 新增一筆解鎖紀錄 (寫入DB並更新記憶體)
     * @param objId 玩家ID
     * @param type 星盤編號
     */
    public void add(int objId, int type) {
        Set<Integer> unlocked = _historyMap.computeIfAbsent(objId, k -> ConcurrentHashMap.newKeySet());
        
        // 如果記憶體中已經有紀錄，則不重複寫入資料庫
        if (unlocked.contains(type)) {
            return;
        }
        
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            // 寫入資料庫，包含紀錄時間
            ps = cn.prepareStatement("INSERT INTO `character_星盤紀錄` SET `objid`=?, `type`=?, `update_time`=NOW()");
            ps.setInt(1, objId);
            ps.setInt(2, type);
            ps.execute();
            
            // 更新記憶體
            unlocked.add(type);
            
        } catch (SQLException e) {
            _log.error("新增 character_星盤紀錄 失敗(objId=" + objId + ", type=" + type + "): " + e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps, cn);
        }
    }
}
