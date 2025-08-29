package com.lineage.echo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;

/**
 * 连接监控器
 * 监控客户端连接状态，记录断线原因，提供诊断信息
 */
public class ConnectionMonitor {
    private static final Log _log = LogFactory.getLog(ConnectionMonitor.class);
    private static ConnectionMonitor _instance;
    
    // 活跃连接映射
    private final ConcurrentHashMap<String, ConnectionInfo> activeConnections = new ConcurrentHashMap<>();
    
    // 断线记录队列（最近1000条）
    private final ConcurrentLinkedQueue<DisconnectionRecord> recentDisconnections = new ConcurrentLinkedQueue<>();
    
    // 定时任务执行器
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    private ConnectionMonitor() {
        startMonitoring();
    }
    
    public static ConnectionMonitor getInstance() {
        if (_instance == null) {
            synchronized (ConnectionMonitor.class) {
                if (_instance == null) {
                    _instance = new ConnectionMonitor();
                }
            }
        }
        return _instance;
    }
    
    /**
     * 记录新连接
     */
    public void recordConnection(String clientId) {
        ConnectionInfo info = new ConnectionInfo(clientId, System.currentTimeMillis());
        activeConnections.put(clientId, info);
        
        // 更新网络统计
        String ip = extractIPFromClientId(clientId);
        if (ip != null) {
            NetworkStats.getInstance().recordNewConnection(ip);
        }
        
        _log.debug("记录新连接: " + clientId + " 当前活跃连接数: " + activeConnections.size());
    }
    
    /**
     * 记录断线
     */
    public void recordDisconnection(String clientId, String reason, String details) {
        ConnectionInfo info = activeConnections.remove(clientId);
        
        if (info != null) {
            long connectionDuration = System.currentTimeMillis() - info.connectTime;
            DisconnectionRecord record = new DisconnectionRecord(
                clientId, reason, details, connectionDuration, System.currentTimeMillis()
            );
            
            // 添加到断线记录队列
            recentDisconnections.offer(record);
            
            // 保持队列大小不超过1000
            while (recentDisconnections.size() > 1000) {
                recentDisconnections.poll();
            }
            
            // 更新网络统计
            String ip = extractIPFromClientId(clientId);
            if (ip != null) {
                NetworkStats.getInstance().recordIPDisconnection(ip);
            }
            
            _log.debug("记录断线: " + clientId + " 原因: " + reason + " 连接时长: " + (connectionDuration / 1000) + "秒");
        }
    }
    
    /**
     * 获取活跃连接数
     */
    public int getActiveConnectionCount() {
        return activeConnections.size();
    }
    
    /**
     * 获取最近断线记录数量
     */
    public int getRecentDisconnectionCount() {
        return recentDisconnections.size();
    }
    
    /**
     * 生成连接监控报告
     */
    public String generateMonitorReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n=== 连接监控报告 ===\n");
        report.append("当前活跃连接数: ").append(activeConnections.size()).append("\n");
        report.append("最近断线记录数: ").append(recentDisconnections.size()).append("\n");
        
        // 统计最近断线原因
        ConcurrentHashMap<String, Integer> reasonCount = new ConcurrentHashMap<>();
        for (DisconnectionRecord record : recentDisconnections) {
            reasonCount.merge(record.reason, 1, Integer::sum);
        }
        
        report.append("\n最近断线原因统计:\n");
        reasonCount.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .forEach(entry -> {
                report.append("  ").append(entry.getKey())
                      .append(": ").append(entry.getValue()).append(" 次\n");
            });
        
        // 显示最近10条断线记录
        report.append("\n最近10条断线记录:\n");
        recentDisconnections.stream()
            .skip(Math.max(0, recentDisconnections.size() - 10))
            .forEach(record -> {
                report.append("  ").append(new Date(record.disconnectTime))
                      .append(" - ").append(record.clientId)
                      .append(" - ").append(record.reason)
                      .append(" (").append(record.connectionDuration / 1000).append("秒)\n");
            });
        
        report.append("==================\n");
        return report.toString();
    }
    
    /**
     * 启动监控任务
     */
    private void startMonitoring() {
        // 每10分钟清理过期连接
        scheduler.scheduleAtFixedRate(this::cleanupExpiredConnections, 10, 10, TimeUnit.MINUTES);
        
        // 每小时生成监控报告
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (activeConnections.size() > 0 || recentDisconnections.size() > 0) {
                    _log.info(generateMonitorReport());
                }
            } catch (Exception e) {
                _log.error("生成连接监控报告时发生错误", e);
            }
        }, 1, 1, TimeUnit.HOURS);
    }
    
    /**
     * 清理过期连接
     */
    private void cleanupExpiredConnections() {
        long currentTime = System.currentTimeMillis();
        long expireTime = 2 * 60 * 60 * 1000; // 2小时过期
        
        activeConnections.entrySet().removeIf(entry -> {
            boolean expired = (currentTime - entry.getValue().connectTime) > expireTime;
            if (expired) {
                _log.debug("清理过期连接: " + entry.getKey());
            }
            return expired;
        });
    }
    
    /**
     * 从客户端ID提取IP地址
     */
    private String extractIPFromClientId(String clientId) {
        try {
            if (clientId == null || clientId.isEmpty()) {
                return "unknown";
            }
            
            // 尝试多种格式提取IP
            if (clientId.contains("@")) {
                String[] parts = clientId.split("@");
                if (parts.length > 1) {
                    String ipPart = parts[1].trim();
                    if (isValidIP(ipPart)) {
                        return ipPart;
                    }
                }
            }
            
            // 如果包含IP地址格式，直接提取
            if (clientId.matches(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*")) {
                String[] parts = clientId.split("\\s+");
                for (String part : parts) {
                    if (isValidIP(part)) {
                        return part;
                    }
                }
            }
            
            // 如果都失败，返回clientId的前20个字符
            return clientId.length() > 20 ? clientId.substring(0, 20) + "..." : clientId;
            
        } catch (Exception e) {
            _log.debug("无法从clientId提取IP: " + clientId, e);
        }
        return "unknown";
    }
    
    /**
     * 验证IP地址格式
     */
    private boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        try {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }
            
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 获取连接统计信息
     */
    public String getConnectionStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== 连接统计信息 ===\n");
        stats.append("当前活跃连接数: ").append(activeConnections.size()).append("\n");
        stats.append("最近断线记录数: ").append(recentDisconnections.size()).append("\n");
        
        // 计算断线率
        int totalConnections = activeConnections.size() + recentDisconnections.size();
        if (totalConnections > 0) {
            double disconnectRate = (double) recentDisconnections.size() / totalConnections * 100;
            stats.append("断线率: ").append(String.format("%.2f", disconnectRate)).append("%\n");
        } else {
            stats.append("断线率: 0.00%\n");
        }
        
        // 统计断线原因
        ConcurrentHashMap<String, Integer> reasonCount = new ConcurrentHashMap<>();
        for (DisconnectionRecord record : recentDisconnections) {
            reasonCount.merge(record.reason, 1, Integer::sum);
        }
        
        if (!reasonCount.isEmpty()) {
            stats.append("\n断线原因统计:\n");
            reasonCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach(entry -> {
                    stats.append("  ").append(entry.getKey())
                          .append(": ").append(entry.getValue()).append(" 次\n");
                });
        }
        
        // 显示最近5条断线记录
        if (!recentDisconnections.isEmpty()) {
            stats.append("\n最近5条断线记录:\n");
            recentDisconnections.stream()
                .skip(Math.max(0, recentDisconnections.size() - 5))
                .forEach(record -> {
                    stats.append("  ").append(new Date(record.disconnectTime))
                          .append(" - ").append(record.reason)
                          .append(" (").append(record.connectionDuration / 1000).append("秒)\n");
                });
        }
        
        stats.append("==================\n");
        return stats.toString();
    }
    
    /**
     * 关闭监控器
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        _log.info("连接监控器已关闭");
    }
    
    /**
     * 连接信息类
     */
    private static class ConnectionInfo {
        final String clientId;
        final long connectTime;
        
        ConnectionInfo(String clientId, long connectTime) {
            this.clientId = clientId;
            this.connectTime = connectTime;
        }
    }
    
    /**
     * 断线记录类
     */
    private static class DisconnectionRecord {
        final String clientId;
        final String reason;
        final String details;
        final long connectionDuration;
        final long disconnectTime;
        
        DisconnectionRecord(String clientId, String reason, String details, 
                          long connectionDuration, long disconnectTime) {
            this.clientId = clientId;
            this.reason = reason;
            this.details = details;
            this.connectionDuration = connectionDuration;
            this.disconnectTime = disconnectTime;
        }
    }
}