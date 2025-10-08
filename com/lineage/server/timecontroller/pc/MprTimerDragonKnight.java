package com.lineage.server.timecontroller.pc;

import com.lineage.server.world.WorldDragonKnight;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class MprTimerDragonKnight extends TimerTask {
    private static final Log _log = LogFactory.getLog(MprTimerDragonKnight.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            Collection<?> allPc = WorldDragonKnight.get().all();
            if (allPc.isEmpty()) {
                return;
            }
            MprTimerCrown.executorMpr(allPc);
        } catch (Exception e) {
            _log.error("Pc(龍騎) MP自然回復時間軸異常重啟", e);
            com.lineage.server.thread.GeneralThreadPool.get().cancel(_timer, false);
            MprTimerDragonKnight mprDragonKnight = new MprTimerDragonKnight();
            mprDragonKnight.start();
        }
    }
}