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

/**
 * 數據庫連接監控器
 * 用於監控數據庫連接狀態，檢測連接問題並自動重連
 */
public class DatabaseMonitor {
    
    private static final Log _log = LogFactory.getLog(DatabaseMonitor.class);
    private static DatabaseMonitor _instance;
    
    private ScheduledFuture<?> _monitorTask;
    private volatile boolean _isRunning = false;
    
    // 監控參數
    private static final int MONITOR_INTERVAL_SECONDS = 30; // 每30秒檢查一次
    
    private DatabaseMonitor() {
        // 私有構造函數
    }
    
    public static DatabaseMonitor getInstance() {
        if (_instance == null) {
            _instance = new DatabaseMonitor();
        }
        return _instance;
    }
    
    /**
     * 啟動數據庫監控
     */
    public void startMonitoring() {
        if (_isRunning) {
            _log.warn("數據庫監控已經在運行中");
            return;
        }
        
        _isRunning = true;
        _monitorTask = GeneralThreadPool.get().scheduleAtFixedRate(
            new DatabaseHealthChecker(), 
            MONITOR_INTERVAL_SECONDS * 1000L, 
            MONITOR_INTERVAL_SECONDS * 1000L
        );
        
        _log.info("數據庫監控已啟動，檢查間隔: " + MONITOR_INTERVAL_SECONDS + "秒");
    }
    
    /**
     * 停止數據庫監控
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
        
        _log.info("數據庫監控已停止");
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
            
            pstm = con.prepareStatement("SELECT 1");
            rs = pstm.executeQuery();
            
            if (rs.next()) {
                _log.debug("數據庫連接健康檢查通過");
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
            
            pstm = con.prepareStatement("SELECT 1");
            rs = pstm.executeQuery();
            
            if (rs.next()) {
                _log.debug("登入數據庫連接健康檢查通過");
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
        private static final int MAX_CONSECUTIVE_FAILURES = 3;
        
        @Override
        public void run() {
            if (!_isRunning) {
                return;
            }
            
            try {
                boolean gameDbHealthy = checkDatabaseHealth();
                boolean loginDbHealthy = checkLoginDatabaseHealth();
                
                if (gameDbHealthy && loginDbHealthy) {
                    if (_consecutiveFailures > 0) {
                        _log.info("數據庫連接已恢復正常，連續失敗次數重置");
                        _consecutiveFailures = 0;
                    }
                } else {
                    _consecutiveFailures++;
                    _log.warn("數據庫連接健康檢查失敗，連續失敗次數: " + _consecutiveFailures);
                    
                    if (_consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                        _log.error("數據庫連接連續失敗 " + MAX_CONSECUTIVE_FAILURES + " 次，可能需要手動檢查");
                        // 這裡可以添加告警機制，如發送郵件或短信
                    }
                }
                
            } catch (Exception e) {
                _log.error("數據庫健康檢查任務執行失敗", e);
            }
        }
    }
}
