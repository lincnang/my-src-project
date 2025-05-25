package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.WorldDarkelf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class UpdateObjectDTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(UpdateObjectDTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = PcOtherThreadPool.get().scheduleAtFixedRate(this, 350L, 350L);
    }

    public void run() {
        try {
            Collection<?> allPc = WorldDarkelf.get().all();
            if (allPc.isEmpty()) {
                return;
            }
            for (Object o : allPc) {
                L1PcInstance tgpc = (L1PcInstance) o;
                if (UpdateObjectCheck.check(tgpc)) {
                    tgpc.updateObject();
                }
            }
        } catch (Exception e) {
            _log.error("Pc 可見物更新處理時間軸(黑妖)異常重啟", e);
            PcOtherThreadPool.get().cancel(_timer, false);
            UpdateObjectDTimer objectDTimer = new UpdateObjectDTimer();
            objectDTimer.start();
        }
    }
}