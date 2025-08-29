package com.lineage.echo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 网络连接统计类
 * 用于监控和分析Connection Reset等网络问题
 */
public class NetworkStats {
    private static final Log _log = LogFactory.getLog(NetworkStats.class);
    private static NetworkStats _instance;
    
    // 统计计数器
    private final AtomicLong connectionResetCount = new AtomicLong(0);
    private final AtomicLong connectionAbortCount = new AtomicLong(0);
    private final AtomicLong brokenPipeCount = new AtomicLong(0);
    private final AtomicLong totalConnectionCount = new AtomicLong(0);
    private final AtomicLong successfulConnectionCount = new AtomicLong(0);
    
    // 按IP统计断线次数
    private final ConcurrentHashMap<String, AtomicLong> disconnectionsByIP = new ConcurrentHashMap<>();
    
    // 按时间段统计
    private final ConcurrentHashMap<String, AtomicLong> hourlyDisconnections = new ConcurrentHashMap<>();
    
    private NetworkStats() {
        // 启动定期统计报告
        startPeriodicReport();
    }
    
    public static NetworkStats getInstance() {
        if (_instance == null) {
            synchronized (NetworkStats.class) {
                if (_instance == null) {
                    _instance = new NetworkStats();
                }
            }
        }
        return _instance;
    }
    
    /**
     * 记录新连接
     */
    public void recordNewConnection(String clientIP) {
        totalConnectionCount.incrementAndGet();
        _log.debug("新连接记录 - IP: " + clientIP + " 总连接数: " + totalConnectionCount.get());
    }
    
    /**
     * 记录成功连接
     */
    public void recordSuccessfulConnection(String clientIP) {
        successfulConnectionCount.incrementAndGet();
        _log.debug("成功连接记录 - IP: " + clientIP + " 成功连接数: " + successfulConnectionCount.get());
    }
    
    /**
     * 增加Connection Reset计数
     */
    public void incrementConnectionResetCount() {
        connectionResetCount.incrementAndGet();
    }
    
    /**
     * 增加Connection Abort计数
     */
    public void incrementConnectionAbortCount() {
        connectionAbortCount.incrementAndGet();
    }
    
    /**
     * 增加Broken Pipe计数
     */
    public void incrementBrokenPipeCount() {
        brokenPipeCount.incrementAndGet();
    }
    
    /**
     * 记录IP断线
     */
    public void recordIPDisconnection(String clientIP) {
        if (clientIP != null && !clientIP.isEmpty()) {
            disconnectionsByIP.computeIfAbsent(clientIP, k -> new AtomicLong(0)).incrementAndGet();
            
            // 记录按小时统计
            String hourKey = getCurrentHourKey();
            hourlyDisconnections.computeIfAbsent(hourKey, k -> new AtomicLong(0)).incrementAndGet();
        }
    }
    
    /**
     * 获取Connection Reset次数
     */
    public long getConnectionResetCount() {
        return connectionResetCount.get();
    }
    
    /**
     * 获取Connection Abort次数
     */
    public long getConnectionAbortCount() {
        return connectionAbortCount.get();
    }
    
    /**
     * 获取Broken Pipe次数
     */
    public long getBrokenPipeCount() {
        return brokenPipeCount.get();
    }
    
    /**
     * 获取总连接数
     */
    public long getTotalConnectionCount() {
        return totalConnectionCount.get();
    }
    
    /**
     * 获取成功连接数
     */
    public long getSuccessfulConnectionCount() {
        return successfulConnectionCount.get();
    }
    
    /**
     * 获取连接成功率
     */
    public double getConnectionSuccessRate() {
        long total = totalConnectionCount.get();
        if (total == 0) return 0.0;
        return (double) successfulConnectionCount.get() / total * 100.0;
    }
    
    /**
     * 获取某IP的断线次数
     */
    public long getDisconnectionCountByIP(String clientIP) {
        AtomicLong count = disconnectionsByIP.get(clientIP);
        return count != null ? count.get() : 0;
    }
    
    /**
     * 获取当前小时的统计键
     */
    private String getCurrentHourKey() {
        long currentTime = System.currentTimeMillis();
        long hour = currentTime / (1000 * 60 * 60); // 小时级别的键
        return String.valueOf(hour);
    }
    
    /**
     * 生成统计报告
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n=== 网络连接统计报告 ===\n");
        report.append("总连接数: ").append(totalConnectionCount.get()).append("\n");
        report.append("成功连接数: ").append(successfulConnectionCount.get()).append("\n");
        report.append("连接成功率: ").append(String.format("%.2f%%", getConnectionSuccessRate())).append("\n");
        report.append("Connection Reset: ").append(connectionResetCount.get()).append("\n");
        report.append("Connection Abort: ").append(connectionAbortCount.get()).append("\n");
        report.append("Broken Pipe: ").append(brokenPipeCount.get()).append("\n");
        
        // 显示断线最多的前5个IP
        report.append("\n断线最多的IP (前5名):\n");
        disconnectionsByIP.entrySet().stream()
            .sorted(Map.Entry.<String, AtomicLong>comparingByValue((a, b) -> Long.compare(b.get(), a.get())))
            .limit(5)
            .forEach(entry -> {
                report.append("  ").append(entry.getKey())
                      .append(": ").append(entry.getValue().get()).append(" 次\n");
            });
        
        report.append("========================\n");
        return report.toString();
    }
    
    /**
     * 启动定期统计报告
     */
    private void startPeriodicReport() {
        // 每30分钟输出一次统计报告
        Thread reportThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30 * 60 * 1000); // 30分钟
                    
                    // 只有在有连接活动时才输出报告
                    if (totalConnectionCount.get() > 0) {
                        _log.info(generateReport());
                    }
                    
                    // 清理过期的小时统计数据（保留24小时）
                    cleanupOldHourlyStats();
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    _log.error("生成网络统计报告时发生错误", e);
                }
            }
        });
        
        reportThread.setDaemon(true);
        reportThread.setName("NetworkStats-Reporter");
        reportThread.start();
    }
    
    /**
     * 清理过期的小时统计数据
     */
    private void cleanupOldHourlyStats() {
        long currentHour = System.currentTimeMillis() / (1000 * 60 * 60);
        long cutoffHour = currentHour - 24; // 保留24小时的数据
        
        hourlyDisconnections.entrySet().removeIf(entry -> {
            try {
                long hour = Long.parseLong(entry.getKey());
                return hour < cutoffHour;
            } catch (NumberFormatException e) {
                return true; // 移除无效的键
            }
        });
    }
    
    /**
     * 重置所有统计数据
     */
    public void reset() {
        connectionResetCount.set(0);
        connectionAbortCount.set(0);
        brokenPipeCount.set(0);
        totalConnectionCount.set(0);
        successfulConnectionCount.set(0);
        disconnectionsByIP.clear();
        hourlyDisconnections.clear();
        
        _log.info("网络统计数据已重置");
    }
    
    /**
     * 检查某IP是否异常（断线次数过多）
     */
    public boolean isIPSuspicious(String clientIP) {
        long disconnectionCount = getDisconnectionCountByIP(clientIP);
        // 如果某IP在短时间内断线超过10次，标记为异常
        return disconnectionCount > 10;
    }
}
