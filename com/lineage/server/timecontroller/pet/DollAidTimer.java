package com.lineage.server.timecontroller.pet;

import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldDoll;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DollAidTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(DollAidTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        int timeMillis = 10000;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            Collection<L1DollInstance> allDoll = WorldDoll.get().all();
            if (allDoll.isEmpty()) {
                return;
            }
            for (L1DollInstance doll : allDoll) {
                if (doll.is_power_doll()) {
                    doll.startDollSkill();// 娃娃輔助技能處理
                }
                TimeUnit.MILLISECONDS.sleep(50L);
            }
        } catch (Exception e) {
            _log.error("魔法娃娃處理時間軸(輔助技能)異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            DollAidTimer dollTimer = new DollAidTimer();
            dollTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pet.DollAidTimer JD-Core Version: 0.6.2
 */