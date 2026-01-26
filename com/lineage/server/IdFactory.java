package com.lineage.server;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IdFactory - ID 生成器 (BitSet 回收模式)
 *
 * 設計內容：
 * 1. 使用 BitSet 記錄已使用的 ID (需約 250MB 記憶體)
 * 2. 啟動時讀取所有資料表，標記已使用 ID
 * 3. 發號時尋找 BitSet 中的空缺 (nextClearBit)
 * 4. 支援 ID 回收 (伺服器重啟後生效)
 *
 * @author Fixed by GitHub Copilot CLI
 * @date 2026-01-20
 */
public final class IdFactory {

    private static final Log _log = LogFactory.getLog(IdFactory.class);

    /** 最小發號下限 */
    private static final int MIN_ID = 10000;
    
    /** 最大發號上限 (避免與 IdFactoryNpc 20億 衝突) */
    private static final int MAX_ID = 2000000000;

    // ---------- Singleton ----------
    private IdFactory() {}
    private static class Holder {
        private static final IdFactory I = new IdFactory();
    }

    public static IdFactory get() { return Holder.I; }
    public static IdFactory getId() { return get(); }

    // ---------- 主序列狀態 ----------
    /** BitSet 追蹤 ID 佔用狀態 (true=已使用) */
    private BitSet _usedIds;
    
    /** 下次搜尋起點 (優化搜尋速度) */
    private int _nextIdStartPoint = MIN_ID;

    /** 同步鎖 */
    private final Object _monitor = new Object();

    /** 初始化標記 */
    private volatile boolean _initialized = false;

    // ---------- 舊 API 相容：怪物對戰 / 大樂透 小序列 ----------
    private final Set<Integer> _MobblingSet = ConcurrentHashMap.newKeySet();
    private final Set<Integer> _BigHotblingSet = ConcurrentHashMap.newKeySet();
    private volatile int _MobId = 0;
    private volatile int _BigHotId = 0;

    // ============== 對外 API：主序列 ==============

    /**
     * 啟動時呼叫：從 DB 讀取所有已使用 ID，建立 BitSet
     */
    public void load() {
        PerformanceTimer t = new PerformanceTimer();

        synchronized (_monitor) {
            // 初始化 BitSet (20億 bit ~= 238MB)
            _usedIds = new BitSet(MAX_ID);
            
            // 標記保留區段 (0 ~ MIN_ID) 為已使用
            _usedIds.set(0, MIN_ID);

            // 從所有表中找出已使用的 ID
            int count = loadAllIdsFromDb();

            // 設定搜尋起點：從 MIN_ID 開始找第一個空缺
            _nextIdStartPoint = _usedIds.nextClearBit(MIN_ID);

            _initialized = true;

            _log.info(String.format(
                    "IdFactory 啟動(BitSet模式)：已佔用ID數=%d, 下次發號=%d (%d ms)",
                    count, _nextIdStartPoint, t.get()
            ));
        }
    }

    /**
     * 取得下一個可用 ID（回收模式，填補空缺）
     */
    public int nextId() {
        if (!_initialized) {
            throw new IllegalStateException("IdFactory is not initialized. Call load() first.");
        }

        synchronized (_monitor) {
            // 尋找下一個 false (未使用) 的 bit
            int id = _usedIds.nextClearBit(_nextIdStartPoint);

            // 安全警告：接近 IdFactoryNpc 的範圍
            if (id >= 1900000000) {
                 _log.warn("CRITICAL WARNING: IdFactory ID is approaching 2 Billion! Current: " + id);
            }
            
            // 檢查是否溢出 (雖然 BitSet 會自動擴展，但我們邏輯上限是 MAX_ID)
            if (id >= MAX_ID) {
                // 嘗試從頭找起 (防止 _nextIdStartPoint 之後全滿但前面有洞的情況)
                id = _usedIds.nextClearBit(MIN_ID);
                if (id >= MAX_ID) {
                    _log.fatal("CRITICAL ERROR: IdFactory has run out of IDs! (Max: " + MAX_ID + ")");
                    throw new RuntimeException("All IDs are exhausted.");
                }
            }

            // 標記為使用
            _usedIds.set(id);
            
            // 更新下次搜尋起點 (線性往後，優化下次搜尋)
            _nextIdStartPoint = id + 1;

            return id;
        }
    }

    /**
     * 回報下一個將要發的 ID
     */
    public int maxId() {
        synchronized (_monitor) {
            return _nextIdStartPoint;
        }
    }

    /**
     * 手動保存當前 ID（伺服器關閉時呼叫）
     * BitSet 模式下不需要保存狀態，下次啟動會重新掃描 DB
     */
    public void shutdown() {
        synchronized (_monitor) {
            _log.info("IdFactory 關閉：當前序列位置=" + _nextIdStartPoint);
        }
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
     * 載入所有資料表中的 ID，標記到 BitSet
     */
    private int loadAllIdsFromDb() {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            cn = DatabaseFactory.get().getConnection();

            // 使用 UNION ALL 查詢所有 ID
            // 注意：這裡 SELECT `id` (或對應欄位) 而不是 MAX(id)
            ps = cn.prepareStatement(
                    "SELECT `id` FROM (" +
                            "  SELECT `id` AS id FROM `character_items`" +
                            "  UNION ALL SELECT `id` FROM `character_warehouse`" +
                            "  UNION ALL SELECT `id` FROM `character_elf_warehouse`" +
                            "  UNION ALL SELECT `id` FROM `clan_warehouse`" +
                            "  UNION ALL SELECT `id` FROM `character_shopinfo`" +
                            "  UNION ALL SELECT `objid` FROM `characters`" +
                            "  UNION ALL SELECT `clan_id` FROM `clan_data`" +
                            "  UNION ALL SELECT `id` FROM `character_teleport`" +
                            "  UNION ALL SELECT `id` FROM `character_mail`" +
                            "  UNION ALL SELECT `objid` FROM `character_pets`" +
                            "  UNION ALL SELECT `id` FROM `日誌_衝裝紀錄`" +
                            "  UNION ALL SELECT `id` FROM `日誌_商店購買紀錄`" +
                            "  UNION ALL SELECT `id` FROM `日誌_金幣買賣系統紀錄`" +
                            "  UNION ALL SELECT `index_id` FROM `clan_members`" +
                            "  UNION ALL SELECT `id` FROM `dummy_fishing`" +
                            "  UNION ALL SELECT `item_obj_id` FROM `spawnlist_furniture`" +
                            "  UNION ALL SELECT `item_object_id` FROM `character_letter`" +
                            ") t"
            );

            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                if (id >= 0 && id < MAX_ID) {
                    _usedIds.set(id);
                    count++;
                }
            }

        } catch (SQLException e) {
            _log.error("載入 ID 失敗", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }

        return count;
    }
}
