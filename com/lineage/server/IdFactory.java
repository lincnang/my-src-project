package com.lineage.server;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.ServerReading;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 啟動時掃 DB 找出 ID 缺口，先填洞；沒洞再成長 (max+1)。
 * 內含舊 API 相容：getId()/addMobId()/nextMobId()/addBigHotId()/nextBigHotId()
 * 適用於單機/單分流。若多分流，建議改用 DB 回收表方案。
 */
public final class IdFactory {

    private static final Log _log = LogFactory.getLog(IdFactory.class);

    /** 最小發號下限（依你環境調整；要沿用舊值就改掉這裡） */
    private static final int MIN_ID = 10000;

    // ---------- Singleton ----------
    private IdFactory() {}
    private static class Holder { private static final IdFactory I = new IdFactory(); }
    /** 新入口 */
    public static IdFactory get() { return Holder.I; }
    /** 舊入口相容：等同 get() */
    public static IdFactory getId() { return get(); }

    // ---------- 主序列狀態 ----------
    /** 依序排序後的已用 ID（去重） */
    private int[] usedIds = new int[0];
    /** 指向「目前洞」的左端索引（usedIds[i] 與 usedIds[i+1] 形成一個可能有洞的區間） */
    private int gapLeftIdx = -1;
    /** 目前正在發的 ID（上次發出去的號碼）。下一個就是 current+1 */
    private int current = 0;
    /** true=填洞模式；false=成長模式 */
    private boolean fillingGaps = false;
    /** 併發保護 */
    private final Object monitor = new Object();

    // ---------- 舊 API 相容：怪物對戰 / 大樂透 小序列 ----------
    private final Set<Integer> _MobblingSet = ConcurrentHashMap.newKeySet();
    private final Set<Integer> _BigHotblingSet = ConcurrentHashMap.newKeySet();
    private volatile int _MobId = 0;
    private volatile int _BigHotId = 0;

    // ============== 對外 API：主序列 ==============

    /** 啟動時呼叫：掃描 DB，建立缺口索引 */
    public void load() {
        PerformanceTimer t = new PerformanceTimer();
        this.usedIds = loadAllUsedIdsSorted();
        initCursorFromGaps();
        _log.info(String.format("IdFactory 啟動：模式=%s, start=%d, 已用筆數=%d (%d ms)",
                fillingGaps ? "填洞" : "成長",
                current + 1, usedIds.length, t.get()));
    }

    /** 取得下一個可用 ID（先填洞，沒洞才成長） */
    public int nextId() {
        synchronized (monitor) {
            if (fillingGaps) {
                // 目前洞的右界
                final int right = usedIds[gapLeftIdx + 1];
                if (current + 1 < right) {
                    // 洞內還有空位
                    current += 1;
                    return current;
                }
                // 這個洞填完了 → 找下一個洞
                if (advanceToNextGap()) {
                    // 已切到下一個洞，發第一顆
                    current += 1;
                    return current;
                }
                // 沒洞了 → 切換到成長模式（max+1 開始）
                switchToGrowth();
                return ++current;
            } else {
                // 成長模式：一路往上加
                return ++current;
            }
        }
    }

    /** 回報「下一個將要發的 ID」（給停機回寫 maxid 或除錯用） */
    public int maxId() {
        synchronized (monitor) {
            return current + 1;
        }
    }

    // ============== 相容 API：Mob / BigHot 小序列 ==============

    /** 舊 API：記錄已用的 Mob ID（通常在載入既有資料時呼叫） */
    public void addMobId(int i) {
        _MobblingSet.add(i);
        if (i > _MobId) _MobId = i;
    }

    /** 舊 API：發下一個 Mob ID（跳過已用） */
    public int nextMobId() {
        int cand = _MobId + 1;
        while (_MobblingSet.contains(cand)) {
            cand++;
        }
        _MobblingSet.add(cand);
        _MobId = cand;
        return cand;
    }

    /** 舊 API：記錄已用的 BigHot ID */
    public void addBigHotId(int i) {
        _BigHotblingSet.add(i);
        if (i > _BigHotId) _BigHotId = i;
    }

    /** 舊 API：發下一個 BigHot ID（跳過已用） */
    public int nextBigHotId() {
        int cand = _BigHotId + 1;
        while (_BigHotblingSet.contains(cand)) {
            cand++;
        }
        _BigHotblingSet.add(cand);
        _BigHotId = cand;
        return cand;
    }

    // ============== 內部邏輯：主序列 ==============

    /** 從 DB 撈出所有已用 id，去重、排序 */
    private int[] loadAllUsedIdsSorted() {
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        final List<Integer> list = new ArrayList<>(1 << 15);
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement(
                    "SELECT id FROM (" +
                            "  SELECT `id` FROM `character_items`" +
                            "  UNION ALL SELECT `id` FROM `character_warehouse`" +
                            "  UNION ALL SELECT `id` FROM `character_elf_warehouse`" +
                            "  UNION ALL SELECT `id` FROM `clan_warehouse`" +
                            "  UNION ALL SELECT `id` FROM `character_shopinfo`" +
                            "  UNION ALL SELECT `objid` AS `id` FROM `characters`" +
                            "  UNION ALL SELECT `clan_id` AS `id` FROM `clan_data`" +
                            "  UNION ALL SELECT `id` FROM `character_teleport`" +
                            "  UNION ALL SELECT `id` FROM `character_mail`" +
                            "  UNION ALL SELECT `objid` AS `id` FROM `character_pets`" +
                            ") t ORDER BY id"
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                if (id > 0) list.add(id);
            }
        } catch (SQLException e) {
            _log.error("載入已用 ID 失敗", e);
        } finally {
            SQLUtil.close(rs); SQLUtil.close(ps); SQLUtil.close(cn);
        }
        if (list.isEmpty()) {
            return new int[0];
        }
        // 去重 + 排序（輸入已排序，這裡線性去重）
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        int w = 0;
        for (int i = 0; i < arr.length; i++) {
            if (w == 0 || arr[i] != arr[w - 1]) {
                arr[w++] = arr[i];
            }
        }
        return Arrays.copyOf(arr, w);
    }

    /** 依據 usedIds 初始化游標：找第一個 >= MIN_ID 的缺口；沒有就成長模式 */
    private void initCursorFromGaps() {
        synchronized (monitor) {
            if (usedIds.length == 0) {
                // 沒資料 → 從 MIN_ID 與 server_info 安全下限的較大者開始成長
                current = Math.max(MIN_ID, readServerBound()) - 1;
                fillingGaps = false;
                return;
            }

            // 先確保成長起點至少 >= MIN_ID 與 server_info 的 min/max
            int growthStart = Math.max(usedIds[usedIds.length - 1] + 1, readServerBound());
            if (growthStart < MIN_ID) growthStart = MIN_ID;

            // 從陣列中掃第一個缺口，且缺口右界 > MIN_ID
            gapLeftIdx = -1;
            for (int i = 0; i < usedIds.length - 1; i++) {
                int left = usedIds[i];
                int right = usedIds[i + 1];
                if (right - left > 1) {
                    // 缺口 [left+1, right-1]
                    int first = Math.max(left + 1, MIN_ID);
                    if (first < right) {
                        gapLeftIdx = i;
                        current = first - 1;     // 讓下一次 nextId() 直接回 first
                        fillingGaps = true;
                        return;
                    }
                }
            }

            // 沒有任何可用缺口 → 成長模式
            fillingGaps = false;
            current = growthStart - 1;
        }
    }

    /** 進到下一個可用缺口；回傳是否找到 */
    private boolean advanceToNextGap() {
        for (int i = gapLeftIdx + 1; i < usedIds.length - 1; i++) {
            int left = usedIds[i];
            int right = usedIds[i + 1];
            if (right - left > 1) {
                int first = Math.max(left + 1, MIN_ID);
                if (first < right) {
                    gapLeftIdx = i;
                    current = first - 1;
                    return true;
                }
            }
        }
        return false;
    }

    /** 切換為成長模式（從 max+1 與 server_info 界限中擇大） */
    private void switchToGrowth() {
        int growthStart = Math.max(
                (usedIds.length == 0 ? MIN_ID : usedIds[usedIds.length - 1] + 1),
                readServerBound()
        );
        if (growthStart < MIN_ID) growthStart = MIN_ID;
        fillingGaps = false;
        current = growthStart;
    }

    /** 讀取 server_info 的 minId/maxId 作為安全下限（若讀不到回 0） */
    private int readServerBound() {
        try {
            int min = ServerReading.get().minId();
            int max = ServerReading.get().maxId();
            return Math.max(min, max);
        } catch (Throwable ignore) {
            return 0;
        }
    }
}
