package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.WorldCrown;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MprTimerCrown extends TimerTask {
    private static final Log _log = LogFactory.getLog(MprTimerCrown.class);
    private ScheduledFuture<?> _timer;

    static void executorMpr(Collection<?> allPc) throws InterruptedException {
        for (Object o : allPc) {
            L1PcInstance player = (L1PcInstance) o;
            MprExecutor mpr = MprExecutor.get();
            if (mpr.check(player)) {
                mpr.checkRegenMp(player);
                TimeUnit.MILLISECONDS.sleep(1L);
            }
        }
    }

    public void start() {
        _timer = com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            Collection<?> allPc = WorldCrown.get().all();
            if (allPc.isEmpty()) {
                return;
            }
            executorMpr(allPc);
        } catch (Exception e) {
            _log.error("Pc(王族) MP自然回復時間軸異常重啟", e);
            com.lineage.server.thread.GeneralThreadPool.get().cancel(_timer, false);
            MprTimerCrown mprCrown = new MprTimerCrown();
            mprCrown.start();
        }
    }
}