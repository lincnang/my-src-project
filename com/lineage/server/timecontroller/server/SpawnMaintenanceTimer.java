package com.lineage.server.timecontroller.server;

import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

/**
 * 生怪系統維護定時器
 * 定期清理和優化生怪系統，避免長時間運行的累積問題
 */
public class SpawnMaintenanceTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(SpawnMaintenanceTimer.class);
    private ScheduledFuture<?> _timer;
    private static SpawnMaintenanceTimer _instance;
    
    public static SpawnMaintenanceTimer getInstance() {
        if (_instance == null) {
            _instance = new SpawnMaintenanceTimer();
        }
        return _instance;
    }
    
    public void start() {
        // 每30分鐘執行一次維護
        final long timeMillis = 30 * 60 * 1000L;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
        _log.info("生怪系統維護定時器啟動，每30分鐘執行一次");
    }
    
    @Override
    public void run() {
        try {
            long startTime = System.currentTimeMillis();
            
            // 強制垃圾回收
            System.gc();
            
            long endTime = System.currentTimeMillis();
            _log.info("生怪系統維護完成，耗時: " + (endTime - startTime) + "ms");
            
        } catch (Exception e) {
            _log.error("生怪系統維護時發生異常", e);
        }
    }
    
    public void stop() {
        if (_timer != null) {
            GeneralThreadPool.get().cancel(_timer, false);
            _timer = null;
        }
    }
}

