package com.lineage.server.utils;

// ConnectionMonitor 已移除
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.thread.GeneralThreadPool;
import java.util.concurrent.ScheduledFuture;

/**
 * 斷線問題診斷工具
 * 監控和分析玩家斷線原因，提供改善建議
 */
public class DisconnectionDiagnostics {
    
    private static final Log _log = LogFactory.getLog(DisconnectionDiagnostics.class);
    private static DisconnectionDiagnostics _instance;
    private ScheduledFuture<?> task1;
    private ScheduledFuture<?> task2;
    
    private DisconnectionDiagnostics() { }
    
    public static DisconnectionDiagnostics getInstance() {
        if (_instance == null) {
            synchronized (DisconnectionDiagnostics.class) {
                if (_instance == null) {
                    _instance = new DisconnectionDiagnostics();
                }
            }
        }
        return _instance;
    }
    
    /**
     * 啟動斷線監控
     */
    public void startMonitoring() {
        // 每5分鐘檢查一次斷線統計（無外部統計則僅輸出心跳）
        task1 = GeneralThreadPool.get().scheduleAtFixedRate(new java.util.TimerTask(){
            @Override public void run(){ analyzeDisconnections(); }
        }, 5 * 60 * 1000L, 5 * 60 * 1000L);

        // 每30分鐘生成簡要報告
        task2 = GeneralThreadPool.get().scheduleAtFixedRate(new java.util.TimerTask(){
            @Override public void run(){ generateDetailedReport(); }
        }, 30 * 60 * 1000L, 30 * 60 * 1000L);
        
        _log.info("斷線診斷監控已啟動");
    }
    
    /**
     * 分析斷線情況（無外部 ConnectionMonitor，僅輸出基礎訊息）
     */
    private void analyzeDisconnections() {
        try {
            String stats = ""; // 無外部統計來源
            if (stats.contains("斷線率")) {
                String[] lines = stats.split("\n");
                for (String line : lines) {
                    if (line.contains("斷線率:") && line.contains("%")) {
                        String rateStr = line.substring(line.indexOf(":") + 1, line.indexOf("%"))
                                .trim();
                        try {
                            double rate = Double.parseDouble(rateStr);
                            if (rate > 30) {
                                _log.warn("⚠️ 斷線率異常: " + rate + "% - 建議檢查網路設定");
                                provideSuggestions(rate);
                            }
                        } catch (NumberFormatException e) {
                            // 忽略解析錯誤
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error("分析斷線情況時發生錯誤", e);
        }
    }
    
    /**
     * 提供改善建議
     */
    private void provideSuggestions(double disconnectRate) {
        StringBuilder suggestions = new StringBuilder();
        suggestions.append("=== 斷線問題改善建議 ===\n");
        
        if (disconnectRate > 50) {
            suggestions.append("🚨 嚴重斷線問題:\n");
            suggestions.append("  1. 檢查網路設備和頻寬\n");
            suggestions.append("  2. 檢查防火牆設定\n");
            suggestions.append("  3. 考慮增加伺服器資源\n");
        } else if (disconnectRate > 30) {
            suggestions.append("⚠️ 中等斷線問題:\n");
            suggestions.append("  1. 檢查Socket緩衝區設定\n");
            suggestions.append("  2. 調整超時參數\n");
            suggestions.append("  3. 檢查封包處理效率\n");
        }
        
        suggestions.append("當前設定:\n");
        suggestions.append("  - 握手超時: 90秒\n");
        suggestions.append("  - 遊戲超時: 15分鐘\n");
        suggestions.append("  - 閒置超時: 60分鐘\n");
        suggestions.append("  - 緩衝區大小: 2MB\n");
        suggestions.append("  - 異常封包容忍: 20次\n");
        
        _log.warn(suggestions.toString());
    }
    
    /**
     * 生成簡要報告（無外部統計來源）
     */
    private void generateDetailedReport() {
        try {
            String fullStats = ""; // 無外部統計來源
            _log.info("=== 連線健康報告（無外部統計） ===");
            _log.info(fullStats);
            
            // 檢查記憶體使用情況
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            long usedMemoryMB = usedMemory / 1024 / 1024;
            long maxMemoryMB = maxMemory / 1024 / 1024;
            double memoryUsage = (double) usedMemory / maxMemory * 100;
            
            _log.info("記憶體使用情況: " + usedMemoryMB + "MB / " + maxMemoryMB + "MB (" +
                     String.format("%.1f", memoryUsage) + "%)");
            
            if (memoryUsage > 80) {
                _log.warn("⚠️ 記憶體使用率過高，可能影響連線穩定性");
            }
            
            // 檢查活躍線程數
            int threadCount = Thread.activeCount();
            _log.info("活躍線程數: " + threadCount);
            
            if (threadCount > 1000) {
                _log.warn("⚠️ 線程數量過多，可能導致效能問題");
            }
        } catch (Exception e) {
            _log.error("生成詳細報告時發生錯誤", e);
        }
    }
    
    /**
     * 停止監控
     */
    public void stopMonitoring() {
        try {
            if (task1 != null) GeneralThreadPool.get().cancel(task1, false);
        } catch (Exception ignored) { }
        try {
            if (task2 != null) GeneralThreadPool.get().cancel(task2, false);
        } catch (Exception ignored) { }
        _log.info("斷線診斷監控已停止");
    }
    
    /**
     * 手動觸發診斷
     */
    public void runDiagnostics() {
        _log.info("手動執行斷線診斷...");
        analyzeDisconnections();
        generateDetailedReport();
    }
}
