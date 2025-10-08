package com.lineage.server.timecontroller.pc;

import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldIllusionist;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class HprTimerIllusionist extends TimerTask {
    private static final Log _log = LogFactory.getLog(HprTimerIllusionist.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            Collection<?> allPc = WorldIllusionist.get().all();
            if (allPc.isEmpty()) {
                return;
            }
            HprTimerCrown.executorHpr(allPc);
        } catch (Exception e) {
            _log.error("Pc(幻術) HP自然回復時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            HprTimerIllusionist hprIllusionist = new HprTimerIllusionist();
            hprIllusionist.start();
        }
    }
}