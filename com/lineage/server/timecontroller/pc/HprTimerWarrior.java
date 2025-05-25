package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldWarrior;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class HprTimerWarrior extends TimerTask {
    private static final Log _log = LogFactory.getLog(HprTimerWarrior.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        final int timeMillis = 1000;// 1秒
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            final Collection<L1PcInstance> allPc = WorldWarrior.get().all();
            // 不包含元素
            if (allPc.isEmpty()) {
                return;
            }
            HprTimerCrown.executorHpr(allPc);
        } catch (final Exception e) {
            _log.error("Pc(戰士) HP自然回復時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final HprTimerWarrior hprWarrior = new HprTimerWarrior();
            hprWarrior.start();
        }
    }
}
