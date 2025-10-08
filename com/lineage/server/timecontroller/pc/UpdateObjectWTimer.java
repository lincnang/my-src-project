package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldWizard;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class UpdateObjectWTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(UpdateObjectWTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 350L, 350L);
    }

    public void run() {
        try {
            Collection<?> allPc = WorldWizard.get().all();
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
            _log.error("Pc 可見物更新處理時間軸(法師)異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            UpdateObjectWTimer objectWTimer = new UpdateObjectWTimer();
            objectWTimer.start();
        }
    }
}