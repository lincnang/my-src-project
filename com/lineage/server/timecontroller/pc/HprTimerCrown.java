package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.WorldCrown;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HprTimerCrown extends TimerTask {
    private static final Log _log = LogFactory.getLog(HprTimerCrown.class);
    private ScheduledFuture<?> _timer;

    static void executorHpr(Collection<?> allPc) throws InterruptedException {
        for (Object o : allPc) {
            L1PcInstance player = (L1PcInstance) o;
            HprExecutor hpr = HprExecutor.get();
            if (hpr.check(player)) {
                hpr.checkRegenHp(player);
                TimeUnit.MILLISECONDS.sleep(1L);
            }
        }
    }

    public void start() {
        _timer = PcOtherThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            Collection<?> allPc = WorldCrown.get().all();
            if (allPc.isEmpty()) {
                return;
            }
            executorHpr(allPc);
        } catch (Exception e) {
            _log.error("Pc(王族) HP自然回復時間軸異常重啟", e);
            PcOtherThreadPool.get().cancel(_timer, false);
            HprTimerCrown hprCrown = new HprTimerCrown();
            hprCrown.start();
        }
    }
}