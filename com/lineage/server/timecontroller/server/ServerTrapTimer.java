package com.lineage.server.timecontroller.server;

import com.lineage.server.model.Instance.L1TrapInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldTrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerTrapTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(ServerTrapTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 5000L, 5000L);
    }

    public void run() {
        try {
            Map<Integer, L1TrapInstance> allTrap = WorldTrap.get().map();
            if (allTrap.isEmpty()) {
                return;
            }
            for (Object iter : allTrap.values().toArray()) {
                L1TrapInstance trap = (L1TrapInstance) iter;
                if (!trap.isEnable()) {
                    trap.set_stop(trap.get_stop() + 1);
                    if (trap.get_stop() >= trap.getSpan()) {
                        trap.resetLocation();
                        TimeUnit.MILLISECONDS.sleep(50L);
                    }
                }
            }
        } catch (Exception io) {
            _log.error("陷阱召喚處理時間軸異常重啟", io);
            GeneralThreadPool.get().cancel(_timer, false);
            ServerTrapTimer trapTimer = new ServerTrapTimer();
            trapTimer.start();
        }
    }
}
