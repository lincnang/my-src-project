package com.lineage.server.database;

import com.lineage.DatabaseFactory;
import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 數據庫健康監控器 - 長時間運行版本
 * 專門用於監控長時間運行的服務器數據庫連接狀態
 */
public class DatabaseHealthMonitor {
    
    private static final Log _log = LogFactory.getLog(DatabaseHealthMonitor.class);
    private static DatabaseHealthMonitor _instance;
    
    private ScheduledFuture<?> _monitorTask;
    private volatile boolean _isRunning = false;
    
    // 監控參數 - 針對長時間運行優化
    private static final int MONITOR_INTERVAL_SECONDS = 15; // 每15秒檢查一次
    private static final int MAX_CONSECUTIVE_FAILURES = 5;  // 最大連續失敗次數
    private static final int RECONNECT_DELAY_SECONDS = 30; // 重連延遲時間
    
    // 統計信息
    private final AtomicInteger _totalChecks = new AtomicInteger(0);
    private final AtomicInteger _successfulChecks = new AtomicInteger(0);
    private final AtomicInteger _failedChecks = new AtomicInteger(0);
    private final AtomicInteger _reconnectAttempts = new AtomicInteger(0);
    
    private DatabaseHealthMonitor() {
        // 私有構造函數
    }
    
    public static DatabaseHealthMonitor getInstance() {
        if (_instance == null) {
            _instance = new DatabaseHealthMonitor();
        }
        return _instance;
    }
    
    /**
     * 啟動數據庫健康監控
     */
    public void startMonitoring() {
        if (_isRunning) {
            _log.warn("數據庫健康監控已經在運行中");
            return;
        }
        
        _isRunning = true;
        _monitorTask = GeneralThreadPool.get().scheduleAtFixedRate(
            new DatabaseHealthChecker(), 
            MONITOR_INTERVAL_SECONDS * 1000L, 
            MONITOR_INTERVAL_SECONDS * 1000L
        );
        
        _log.info("數據庫健康監控已啟動，檢查間隔: " + MONITOR_INTERVAL_SECONDS + "秒");
    }
    
    /**
     * 停止數據庫健康監控
     */
    public void stopMonitoring() {
        if (!_isRunning) {
            return;
        }
        
        _isRunning = false;
        if (_monitorTask != null) {
            _monitorTask.cancel(false);
            _monitorTask = null;
        }
        
        _log.info("數據庫健康監控已停止");
    }
    
    /**
     * 獲取監控統計信息
     */
    public String getStatistics() {
        int total = _totalChecks.get();
        int success = _successfulChecks.get();
        int failed = _failedChecks.get();
        int reconnects = _reconnectAttempts.get();
        
        double successRate = total > 0 ? (double) success / total * 100 : 0;
        
        return String.format(
            "數據庫監控統計 - 總檢查: %d, 成功: %d, 失敗: %d, 重連: %d, 成功率: %.2f%%",
            total, success, failed, reconnects, successRate
        );
    }
    
    /**
     * 檢查數據庫連接健康狀態
     */
    public boolean checkDatabaseHealth() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        
        try {
            con = DatabaseFactory.get().getConnection();
            
            pstm = con.prepareStatement("SELECT 1, NOW() as current_time, @@version as mysql_version");
            rs = pstm.executeQuery();
            
            if (rs.next()) {
                String currentTime = rs.getString("current_time");
                String mysqlVersion = rs.getString("mysql_version");
                _log.debug("數據庫連接健康檢查通過 - 時間: " + currentTime + ", 版本: " + mysqlVersion);
                return true;
            } else {
                _log.warn("數據庫連接健康檢查失敗：查詢無結果");
                return false;
            }
            
        } catch (Exception e) {
            _log.error("數據庫連接健康檢查失敗: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(rs, pstm, con);
        }
    }
    
    /**
     * 檢查登入數據庫連接健康狀態
     */
    public boolean checkLoginDatabaseHealth() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            
            pstm = con.prepareStatement("SELECT 1, NOW() as current_time, @@version as mysql_version");
            rs = pstm.executeQuery();
            
            if (rs.next()) {
                String currentTime = rs.getString("current_time");
                String mysqlVersion = rs.getString("mysql_version");
                _log.debug("登入數據庫連接健康檢查通過 - 時間: " + currentTime + ", 版本: " + mysqlVersion);
                return true;
            } else {
                _log.warn("登入數據庫連接健康檢查失敗：查詢無結果");
                return false;
            }
            
        } catch (Exception e) {
            _log.error("登入數據庫連接健康檢查失敗: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(rs, pstm, con);
        }
    }
    
    /**
     * 關閉資源
     */
    private void closeResources(ResultSet rs, PreparedStatement pstm, Connection con) {
        try {
            if (rs != null) rs.close();
        } catch (Exception e) {
            _log.debug("關閉ResultSet時發生錯誤", e);
        }
        
        try {
            if (pstm != null) pstm.close();
        } catch (Exception e) {
            _log.debug("關閉PreparedStatement時發生錯誤", e);
        }
        
        try {
            if (con != null) con.close();
        } catch (Exception e) {
            _log.debug("關閉Connection時發生錯誤", e);
        }
    }
    
    /**
     * 數據庫健康檢查任務
     */
    private class DatabaseHealthChecker implements Runnable {
        
        private int _consecutiveFailures = 0;
        private long _lastFailureTime = 0;
        
        @Override
        public void run() {
            if (!_isRunning) {
                return;
            }
            
            _totalChecks.incrementAndGet();
            
            try {
                boolean gameDbHealthy = checkDatabaseHealth();
                boolean loginDbHealthy = checkLoginDatabaseHealth();
                
                if (gameDbHealthy && loginDbHealthy) {
                    _successfulChecks.incrementAndGet();
                    
                    if (_consecutiveFailures > 0) {
                        _log.info("數據庫連接已恢復正常，連續失敗次數重置");
                        _consecutiveFailures = 0;
                        _lastFailureTime = 0;
                    }
                    
                    // 每100次成功檢查輸出一次統計信息
                    if (_successfulChecks.get() % 100 == 0) {
                        _log.info(getStatistics());
                    }
                    
                } else {
                    _failedChecks.incrementAndGet();
                    _consecutiveFailures++;
                    _lastFailureTime = System.currentTimeMillis();
                    
                    _log.warn("數據庫連接健康檢查失敗，連續失敗次數: " + _consecutiveFailures);
                    
                    if (_consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                        _log.error("數據庫連接連續失敗 " + MAX_CONSECUTIVE_FAILURES + " 次，嘗試重連...");
                        
                        // 嘗試重連
                        attemptReconnect();
                    }
                }
                
            } catch (Exception e) {
                _log.error("數據庫健康檢查任務執行失敗", e);
                _failedChecks.incrementAndGet();
            }
        }
        
        /**
         * 嘗試重連數據庫
         */
        private void attemptReconnect() {
            _reconnectAttempts.incrementAndGet();
            
            try {
                _log.info("等待 " + RECONNECT_DELAY_SECONDS + " 秒後嘗試重連...");
                Thread.sleep(RECONNECT_DELAY_SECONDS * 1000);
                
                // 嘗試重新獲取連接
                boolean gameDbReconnected = checkDatabaseHealth();
                boolean loginDbReconnected = checkLoginDatabaseHealth();
                
                if (gameDbReconnected && loginDbReconnected) {
                    _log.info("數據庫重連成功！");
                    _consecutiveFailures = 0;
                    _lastFailureTime = 0;
                } else {
                    _log.error("數據庫重連失敗，將繼續監控...");
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                _log.warn("重連等待被中斷");
            } catch (Exception e) {
                _log.error("重連過程中發生錯誤", e);
            }
        }
    }
}
