package com.lineage.server.timecontroller.pet;

import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldSummons;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SummonTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(SummonTimer.class);
    private ScheduledFuture<?> _timer;

    private static void outSummon(L1SummonInstance summon) {
        try {
            if (summon != null) {
                if (summon.destroyed()) {
                    return;
                }
                if (summon.tamed()) {
                    summon.liberate();
                } else {
                    summon.Death(null);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 60000L, 60000L);
    }

    public void run() {
        try {
            Collection<?> allPet = WorldSummons.get().all();
            if (allPet.isEmpty()) {
                return;
            }
            for (Object o : allPet) {
                L1SummonInstance summon = (L1SummonInstance) o;
                int time = summon.get_time() - 60;
                if (time <= 0) {
                    outSummon(summon);
                } else {
                    summon.set_time(time);
                }
                TimeUnit.MILLISECONDS.sleep(50L);
            }
        } catch (Exception e) {
            _log.error("召喚獸處理時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            SummonTimer summon_Timer = new SummonTimer();
            summon_Timer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pet.SummonTimer JD-Core Version: 0.6.2
 */