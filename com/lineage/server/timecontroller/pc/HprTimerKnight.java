package com.lineage.server.timecontroller.pc;

import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.WorldKnight;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class HprTimerKnight extends TimerTask {
    private static final Log _log = LogFactory.getLog(HprTimerKnight.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = PcOtherThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            Collection<?> allPc = WorldKnight.get().all();
            if (allPc.isEmpty()) {
                return;
            }
            HprTimerCrown.executorHpr(allPc);
        } catch (Exception e) {
            _log.error("Pc(騎士) HP自然回復時間軸異常重啟", e);
            PcOtherThreadPool.get().cancel(_timer, false);
            HprTimerKnight hprKnight = new HprTimerKnight();
            hprKnight.start();
        }
    }
}