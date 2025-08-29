package com.lineage.server.utils;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 玩家活動監控器
 * 用於追蹤玩家的各種活動，防止誤判閒置
 */
public class ActivityMonitor {
    private static final Log _log = LogFactory.getLog(ActivityMonitor.class);
    private static ActivityMonitor _instance;
    
    // 玩家活動記錄
    private final ConcurrentHashMap<String, PlayerActivity> playerActivities = new ConcurrentHashMap<>();
    
    // 活動類型
    public enum ActivityType {
        MOVE("移動"),           // 角色移動
        CHAT("聊天"),           // 聊天對話
        SKILL("技能"),          // 使用技能
        ITEM("道具"),           // 使用道具
        COMBAT("戰鬥"),         // 戰鬥相關
        TRADE("交易"),          // 交易操作
        PARTY("組隊"),          // 組隊操作
        GUILD("血盟"),          // 血盟操作
        LOGIN("登入"),          // 登入遊戲
        LOGOUT("登出"),         // 登出遊戲
        SAVE("儲存"),           // 儲存角色
        OTHER("其他");          // 其他活動
        
        private final String description;
        
        ActivityType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private ActivityMonitor() {
        // 啟動定期清理任務
        startCleanupTask();
    }
    
    public static ActivityMonitor getInstance() {
        if (_instance == null) {
            synchronized (ActivityMonitor.class) {
                if (_instance == null) {
                    _instance = new ActivityMonitor();
                }
            }
        }
        return _instance;
    }
    
    /**
     * 記錄玩家活動
     */
    public void recordActivity(L1PcInstance player, ActivityType activityType) {
        if (player == null || player.getNetConnection() == null) {
            return;
        }
        
        try {
            String accountName = player.getAccountName();
            if (accountName != null && !accountName.isEmpty()) {
                PlayerActivity activity = playerActivities.computeIfAbsent(accountName, 
                    k -> new PlayerActivity(accountName));
                
                activity.recordActivity(activityType);
                
                // 更新連線活動時間
                ClientExecutor executor = player.getNetConnection();
                if (executor != null) {
                    executor.touchActivity();
                    _log.debug("更新玩家活動時間: " + accountName + " - " + activityType.getDescription());
                }
            }
        } catch (Exception e) {
            _log.debug("記錄玩家活動時發生錯誤", e);
        }
    }
    
    /**
     * 記錄玩家活動（重載方法）
     */
    public void recordActivity(String accountName, ActivityType activityType) {
        if (accountName == null || accountName.isEmpty()) {
            return;
        }
        
        try {
            PlayerActivity activity = playerActivities.computeIfAbsent(accountName, 
                k -> new PlayerActivity(accountName));
            
            activity.recordActivity(activityType);
            _log.debug("記錄玩家活動: " + accountName + " - " + activityType.getDescription());
            
        } catch (Exception e) {
            _log.debug("記錄玩家活動時發生錯誤", e);
        }
    }
    
    /**
     * 獲取玩家活動統計
     */
    public String getActivityStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== 玩家活動統計 ===\n");
        stats.append("總監控玩家數: ").append(playerActivities.size()).append("\n");
        
        // 統計各類型活動
        ConcurrentHashMap<ActivityType, Integer> activityCounts = new ConcurrentHashMap<>();
        for (PlayerActivity activity : playerActivities.values()) {
            for (ActivityType type : ActivityType.values()) {
                int count = activity.getActivityCount(type);
                if (count > 0) {
                    activityCounts.merge(type, count, Integer::sum);
                }
            }
        }
        
        if (!activityCounts.isEmpty()) {
            stats.append("\n活動類型統計:\n");
            activityCounts.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach(entry -> {
                    stats.append("  ").append(entry.getKey().getDescription())
                          .append(": ").append(entry.getValue()).append(" 次\n");
                });
        }
        
        // 顯示最活躍的玩家
        stats.append("\n最活躍玩家 (前5名):\n");
        playerActivities.values().stream()
            .sorted((a, b) -> Integer.compare(b.getTotalActivityCount(), a.getTotalActivityCount()))
            .limit(5)
            .forEach(activity -> {
                stats.append("  ").append(activity.accountName)
                      .append(": ").append(activity.getTotalActivityCount()).append(" 次活動\n");
            });
        
        stats.append("==================\n");
        return stats.toString();
    }
    
    /**
     * 啟動定期清理任務
     */
    private void startCleanupTask() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ActivityMonitor-Cleanup");
            t.setDaemon(true);
            return t;
        });
        
        // 每小時清理一次離線玩家的活動記錄
        scheduler.scheduleAtFixedRate(() -> {
            try {
                cleanupOfflinePlayers();
            } catch (Exception e) {
                _log.error("清理離線玩家活動記錄時發生錯誤", e);
            }
        }, 1, 1, TimeUnit.HOURS);
        
        _log.info("玩家活動監控器已啟動");
    }
    
    /**
     * 清理離線玩家的活動記錄
     */
    private void cleanupOfflinePlayers() {
        int beforeSize = playerActivities.size();
        
        // 這裡可以添加檢查玩家是否在線的邏輯
        // 目前簡單地保留所有記錄，避免誤刪
        
        _log.debug("活動記錄清理完成，當前記錄數: " + playerActivities.size());
    }
    
    /**
     * 玩家活動記錄類
     */
    private static class PlayerActivity {
        final String accountName;
        final ConcurrentHashMap<ActivityType, Integer> activityCounts;
        long lastActivityTime;
        
        PlayerActivity(String accountName) {
            this.accountName = accountName;
            this.activityCounts = new ConcurrentHashMap<>();
            this.lastActivityTime = System.currentTimeMillis();
        }
        
        void recordActivity(ActivityType activityType) {
            activityCounts.merge(activityType, 1, Integer::sum);
            lastActivityTime = System.currentTimeMillis();
        }
        
        int getActivityCount(ActivityType activityType) {
            return activityCounts.getOrDefault(activityType, 0);
        }
        
        int getTotalActivityCount() {
            return activityCounts.values().stream().mapToInt(Integer::intValue).sum();
        }
        
        long getLastActivityTime() {
            return lastActivityTime;
        }
    }
}
