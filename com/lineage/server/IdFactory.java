package com.lineage.server;

import com.lineage.DatabaseFactory;
import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.datatables.lock.ServerReading;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IdFactory - 修復版：安全的 ID 生成器
 * 
 * 修復內容：
 * 1. 使用 AtomicInteger 確保線程安全
 * 2. 定期回寫 current 到 DB
 * 3. 新增日誌表到掃描清單
 * 4. 移除危險的「填洞模式」
 * 5. 改為純成長模式，確保 ID 永不重複
 * 
 * @author Fixed by GitHub Copilot CLI
 * @date 2025-12-06
 */
public final class IdFactory {

    private static final Log _log = LogFactory.getLog(IdFactory.class);

    /** 最小發號下限 */
    private static final int MIN_ID = 10000;
    
    /** 回寫間隔：每生成多少個 ID 就回寫一次到 DB */
    private static final int SAVE_INTERVAL = 1000;

    // ---------- Singleton ----------
    private IdFactory() {}
    private static class Holder { 
        private static final IdFactory I = new IdFactory(); 
    }
    
    public static IdFactory get() { return Holder.I; }
    public static IdFactory getId() { return get(); }

    // ---------- 主序列狀態 ----------
    /** 當前 ID（線程安全）*/
    private final AtomicInteger current = new AtomicInteger(MIN_ID);
    
    /** 上次回寫到 DB 的 ID */
    private volatile int lastSavedId = MIN_ID;
    
    /** 統計：已發出的 ID 數量 */
    private final AtomicInteger issuedCount = new AtomicInteger(0);

    /** 初始化標記 */
    private volatile boolean _initialized = false;

    // ---------- 舊 API 相容：怪物對戰 / 大樂透 小序列 ----------
    private final Set<Integer> _MobblingSet = ConcurrentHashMap.newKeySet();
    private final Set<Integer> _BigHotblingSet = ConcurrentHashMap.newKeySet();
    private volatile int _MobId = 0;
    private volatile int _BigHotId = 0;

    // ============== 對外 API：主序列 ==============

    /**
     * 啟動時呼叫：從 DB 讀取最大 ID，設定起始值
     */
    public void load() {
        PerformanceTimer t = new PerformanceTimer();
        
        // 從所有表中找出最大 ID
        int maxIdFromDb = loadMaxIdFromAllTables();
        
        // 從 server_info 讀取上次保存的 maxId
        int maxIdFromConfig = readServerBound();
        
        // 取兩者中的較大值，並加上安全邊界（+1000）
        int startId = Math.max(maxIdFromDb, maxIdFromConfig) + 1000;
        
        // 確保不低於 MIN_ID
        if (startId < MIN_ID) {
            startId = MIN_ID;
        }
        
        current.set(startId);
        lastSavedId = startId;
        
        // 立即回寫到 DB
        saveCurrentIdToDb();
        
        _initialized = true;

        _log.info(String.format(
            "IdFactory 啟動：起始ID=%d (DB最大=%d, Config最大=%d) (%d ms)",
            startId, maxIdFromDb, maxIdFromConfig, t.get()
        ));
    }

    /**
     * 取得下一個可用 ID（純成長模式，保證不重複）
     */
    public int nextId() {
        if (!_initialized) {
            throw new IllegalStateException("IdFactory is not initialized. Call load() first.");
        }
        int id = current.incrementAndGet();
        int issued = issuedCount.incrementAndGet();
        
        // 每發出 SAVE_INTERVAL 個 ID，回寫一次到 DB
        if (issued % SAVE_INTERVAL == 0) {
            GeneralThreadPool.get().execute(this::saveCurrentIdToDb);
        }
        
        // 安全警告：接近 IdFactoryNpc 的範圍 (20億)
        if (id >= 1900000000) {
            _log.warn("CRITICAL WARNING: IdFactory ID is approaching 2 Billion! Current: " + id + ". Collision with IdFactoryNpc is imminent!");
        }
        
        return id;
    }

    /**
     * 回報下一個將要發的 ID
     */
    public int maxId() {
        return current.get() + 1;
    }
    
    /**
     * 手動保存當前 ID（伺服器關閉時呼叫）
     */
    public void shutdown() {
        saveCurrentIdToDb();
        _log.info("IdFactory 關閉：最終ID=" + current.get() + ", 累計發出=" + issuedCount.get());
    }

    // ============== 相容 API：Mob / BigHot 小序列 ==============

    public void addMobId(int i) {
        _MobblingSet.add(i);
        if (i > _MobId) _MobId = i;
    }

    public void removeMobId(int i) {
        _MobblingSet.remove(i);
    }

    public int nextMobId() {
        if (!_initialized) {
            throw new IllegalStateException("IdFactory is not initialized. Call load() first.");
        }
        int cand = _MobId + 1;
        while (_MobblingSet.contains(cand)) {
            cand++;
        }
        _MobblingSet.add(cand);
        _MobId = cand;
        return cand;
    }

    public void addBigHotId(int i) {
        _BigHotblingSet.add(i);
        if (i > _BigHotId) _BigHotId = i;
    }

    public void removeBigHotId(int i) {
        _BigHotblingSet.remove(i);
    }

    public int nextBigHotId() {
        if (!_initialized) {
            throw new IllegalStateException("IdFactory is not initialized. Call load() first.");
        }
        int cand = _BigHotId + 1;
        while (_BigHotblingSet.contains(cand)) {
            cand++;
        }
        _BigHotblingSet.add(cand);
        _BigHotId = cand;
        return cand;
    }

    // ============== 內部邏輯：主序列 ==============

    /**
     * 從所有相關表中找出最大的 ID
     */
    private int loadMaxIdFromAllTables() {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int maxId = 0;
        
        try {
            cn = DatabaseFactory.get().getConnection();
            
            // 使用 UNION ALL 並取 MAX，效能更好
            ps = cn.prepareStatement(
                "SELECT MAX(id) FROM (" +
                "  SELECT MAX(`id`) AS id FROM `character_items`" +
                "  UNION ALL SELECT MAX(`id`) FROM `character_warehouse`" +
                "  UNION ALL SELECT MAX(`id`) FROM `character_elf_warehouse`" +
                "  UNION ALL SELECT MAX(`id`) FROM `clan_warehouse`" +
                "  UNION ALL SELECT MAX(`id`) FROM `character_shopinfo`" +
                "  UNION ALL SELECT MAX(`objid`) FROM `characters`" +
                "  UNION ALL SELECT MAX(`clan_id`) FROM `clan_data`" +
                "  UNION ALL SELECT MAX(`id`) FROM `character_teleport`" +
                "  UNION ALL SELECT MAX(`id`) FROM `character_mail`" +
                "  UNION ALL SELECT MAX(`objid`) FROM `character_pets`" +
                // ✅ 新增：日誌表
                "  UNION ALL SELECT MAX(`id`) FROM `日誌_衝裝紀錄`" +
                "  UNION ALL SELECT MAX(`id`) FROM `日誌_商店購買紀錄`" +
                "  UNION ALL SELECT MAX(`id`) FROM `日誌_金幣買賣系統紀錄`" +
                "  UNION ALL SELECT MAX(`index_id`) FROM `clan_members`" +
                "  UNION ALL SELECT MAX(`id`) FROM `dummy_fishing`" +
                ") t"
            );
            
            rs = ps.executeQuery();
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            _log.error("載入最大 ID 失敗", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        
        return maxId;
    }

    /**
     * 將當前 ID 回寫到 DB（server_info 表）
     */
    private void saveCurrentIdToDb() {
        int currentId = current.get();
        
        // 如果沒有變化，不需要回寫
        if (currentId == lastSavedId) {
            return;
        }
        
        Connection cn = null;
        PreparedStatement ps = null;
        
        try {
            cn = DatabaseFactoryLogin.get().getConnection();
            
            // 更新 server_info 的 maxId
            ps = cn.prepareStatement(
                "UPDATE `server_info` SET `maxid` = ? WHERE `id` = ?"
            );
            ps.setInt(1, currentId);
            ps.setInt(2, com.lineage.config.Config.SERVERNO);
            ps.executeUpdate();
            
            lastSavedId = currentId;
            
            _log.debug("IdFactory 回寫：maxId=" + currentId);
            
        } catch (SQLException e) {
            _log.error("回寫 ID 到 DB 失敗: " + currentId, e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 讀取 server_info 的 maxId
     */
    private int readServerBound() {
        try {
            int max = ServerReading.get().maxId();
            return Math.max(max, 0);
        } catch (Throwable e) {
            _log.warn("讀取 server_info maxId 失敗", e);
            return 0;
        }
    }
}
