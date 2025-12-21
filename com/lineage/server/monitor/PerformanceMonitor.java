package com.lineage.server.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能監控器 - 追蹤特定操作的詳細性能
 * 用於定位性能瓶頸的根源
 */
public class PerformanceMonitor {
    private static final Log _log = LogFactory.getLog(PerformanceMonitor.class);

    // 追蹤各種操作的性能指標
    private static final ConcurrentHashMap<String, OperationMetrics> _metrics =
        new ConcurrentHashMap<>();

    // 操作性能指標
    private static class OperationMetrics {
        final String operation;
        final AtomicLong totalCalls = new AtomicLong(0);
        final AtomicLong totalTime = new AtomicLong(0);
        final AtomicLong slowCalls = new AtomicLong(0); // 超過閾值的調用
        volatile long lastSlowCallTime = 0;
        volatile String lastSlowCallContext = "";

        OperationMetrics(String op) {
            this.operation = op;
        }

        void record(long duration, String context) {
            totalCalls.incrementAndGet();
            totalTime.addAndGet(duration);

            if (duration > 100) { // 超過100ms視為慢操作
                slowCalls.incrementAndGet();
                lastSlowCallTime = System.currentTimeMillis();
                lastSlowCallContext = context;

                // 記錄詳細信息
                _log.warn(String.format(
                    "[SLOW_OPERATION] %s - Duration: %dms, TotalCalls: %d, SlowCalls: %d, Context: %s",
                    operation, duration, totalCalls.get(), slowCalls.get(), context
                ));
            }
        }
    }

    /**
     * 開始監控一個操作
     * @param operation 操作名稱
     * @param context 上下文信息（如角色名、NPC ID等）
     * @return PerformanceTracker 可用於結束監控
     */
    public static PerformanceTracker startTracking(String operation, String context) {
        return new PerformanceTracker(operation, context);
    }

    /**
     * 性能追蹤器 - 使用 try-with-resources 或手動 close()
     */
    public static class PerformanceTracker implements AutoCloseable {
        private final String operation;
        private final String context;
        private final long startTime;
        private final OperationMetrics metrics;

        PerformanceTracker(String operation, String context) {
            this.operation = operation;
            this.context = context;
            this.startTime = System.currentTimeMillis();
            this.metrics = _metrics.computeIfAbsent(operation, OperationMetrics::new);
        }

        @Override
        public void close() {
            long duration = System.currentTimeMillis() - startTime;
            metrics.record(duration, context);
        }

        /**
         * 手動結束追蹤（如果不使用 try-with-resources）
         */
        public void end() {
            close();
        }
    }

    /**
     * 獲取性能統計報告
     */
    public static String getPerformanceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Performance Monitor Report ===\n");

        for (OperationMetrics metrics : _metrics.values()) {
            long totalCalls = metrics.totalCalls.get();
            if (totalCalls > 0) {
                long totalTime = metrics.totalTime.get();
                long avgTime = totalTime / totalCalls;
                long slowCalls = metrics.slowCalls.get();
                double slowPercent = (slowCalls * 100.0) / totalCalls;

                sb.append(String.format(
                    "%s: Calls=%d, Avg=%dms, Slow=%d(%.2f%%), LastSlowContext=%s\n",
                    metrics.operation, totalCalls, avgTime, slowCalls, slowPercent,
                    metrics.lastSlowCallContext
                ));
            }
        }

        return sb.toString();
    }

    /**
     * 清理所有統計數據
     */
    public static void reset() {
        _metrics.clear();
        _log.info("[PerformanceMonitor] All metrics reset");
    }

    /**
     * 記錄資料庫操作性能
     */
    public static PerformanceTracker trackDB(String operation, String tableName) {
        return startTracking("DB_" + operation, "Table=" + tableName);
    }

    /**
     * 記錄網路操作性能
     */
    public static PerformanceTracker trackNetwork(String operation, String target) {
        return startTracking("NET_" + operation, "Target=" + target);
    }

    /**
     * 記錄技能操作性能
     */
    public static PerformanceTracker trackSkill(String skillName, String charName) {
        return startTracking("SKILL_" + skillName, "Char=" + charName);
    }

    /**
     * 記錄物品操作性能
     */
    public static PerformanceTracker trackItem(String operation, String itemType) {
        return startTracking("ITEM_" + operation, "Type=" + itemType);
    }
}