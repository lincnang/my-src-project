package com.add.Tsai;

import com.add.Tsai.Astrology.*;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 玩家數據清理管理器
 * 負責清理玩家離線後的緩存數據，防止記憶體洩漏
 * 
 * @author 系統優化
 */
public class PlayerDataCleanupManager {
    private static final Log _log = LogFactory.getLog(PlayerDataCleanupManager.class);
    private static PlayerDataCleanupManager _instance;
    
    // 定期清理服務
    private final ScheduledExecutorService cleanupService;
    
    // 記錄玩家最後活動時間
    private final ConcurrentHashMap<Integer, Long> playerLastActivity = new ConcurrentHashMap<>();
    
    // 清理延遲時間（毫秒）- 玩家離線30分鐘後清理數據
    private static final long CLEANUP_DELAY_MS = 30 * 60 * 1000L;
    
    private PlayerDataCleanupManager() {
        cleanupService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "PlayerDataCleanup");
            t.setDaemon(true);
            return t;
        });
        
        // 每5分鐘執行一次清理檢查
        cleanupService.scheduleAtFixedRate(this::performCleanupCheck, 5, 5, TimeUnit.MINUTES);
        
        _log.info("玩家數據清理管理器已啟動");
    }
    
    public static PlayerDataCleanupManager getInstance() {
        if (_instance == null) {
            synchronized (PlayerDataCleanupManager.class) {
                if (_instance == null) {
                    _instance = new PlayerDataCleanupManager();
                }
            }
        }
        return _instance;
    }
    
    /**
     * 玩家上線時調用，更新活動時間
     */
    public void onPlayerLogin(L1PcInstance pc) {
        if (pc != null) {
            playerLastActivity.put(pc.getId(), System.currentTimeMillis());
            _log.debug("玩家上線，更新活動時間: " + pc.getName() + " (ID: " + pc.getId() + ")");
        }
    }
    
    /**
     * 玩家活動時調用，更新活動時間
     */
    public void updatePlayerActivity(L1PcInstance pc) {
        if (pc != null) {
            playerLastActivity.put(pc.getId(), System.currentTimeMillis());
        }
    }
    
    /**
     * 玩家離線時調用，標記為離線但不立即清理
     */
    public void onPlayerLogout(L1PcInstance pc) {
        if (pc != null) {
            // 不立即清理，等待定期清理檢查
            _log.debug("玩家離線，等待清理: " + pc.getName() + " (ID: " + pc.getId() + ")");
        }
    }
    
    /**
     * 立即清理指定玩家的所有緩存數據
     */
    public void cleanupPlayerData(int playerId) {
        try {
            // 清理星盤相關數據
            AstrologyCmd.cleanupPlayerData(playerId);
            AttonAstrologyCmd.cleanupPlayerData(playerId);
            SilianAstrologyCmd.cleanupPlayerData(playerId);
            GritAstrologyCmd.cleanupPlayerData(playerId);
            YishidiAstrologyCmd.cleanupPlayerData(playerId);
            
            // 移除活動記錄
            playerLastActivity.remove(playerId);
            
            _log.info("已清理玩家數據: ID=" + playerId);
            
        } catch (Exception e) {
            _log.error("清理玩家數據時發生錯誤: ID=" + playerId, e);
        }
    }
    
    /**
     * 定期清理檢查
     */
    private void performCleanupCheck() {
        try {
            long currentTime = System.currentTimeMillis();
            int cleanedCount = 0;
            
            // 檢查所有玩家的最後活動時間
            for (ConcurrentHashMap.Entry<Integer, Long> entry : playerLastActivity.entrySet()) {
                int playerId = entry.getKey();
                long lastActivity = entry.getValue();
                
                // 如果超過清理延遲時間，執行清理
                if (currentTime - lastActivity > CLEANUP_DELAY_MS) {
                    cleanupPlayerData(playerId);
                    cleanedCount++;
                }
            }
            
            if (cleanedCount > 0) {
                _log.info("定期清理完成，清理了 " + cleanedCount + " 個玩家的數據");
            }
            
        } catch (Exception e) {
            _log.error("定期清理檢查時發生錯誤", e);
        }
    }
    
    /**
     * 強制清理所有離線玩家數據
     */
    public void forceCleanupAllOfflinePlayers() {
        try {
            int totalCleaned = 0;
            
            // 清理所有記錄的玩家數據
            for (Integer playerId : playerLastActivity.keySet()) {
                cleanupPlayerData(playerId);
                totalCleaned++;
            }
            
            _log.info("強制清理完成，共清理 " + totalCleaned + " 個玩家的數據");
            
        } catch (Exception e) {
            _log.error("強制清理時發生錯誤", e);
        }
    }
    
    /**
     * 獲取當前緩存的玩家數量
     */
    public int getCachedPlayerCount() {
        return playerLastActivity.size();
    }
    
    /**
     * 關閉清理服務
     */
    public void shutdown() {
        if (cleanupService != null && !cleanupService.isShutdown()) {
            cleanupService.shutdown();
            try {
                if (!cleanupService.awaitTermination(30, TimeUnit.SECONDS)) {
                    cleanupService.shutdownNow();
                }
            } catch (InterruptedException e) {
                cleanupService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        _log.info("玩家數據清理管理器已關閉");
    }
}
