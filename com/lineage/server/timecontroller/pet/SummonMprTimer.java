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

public class SummonMprTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(SummonMprTimer.class);
    private static int _time = 0;
    private ScheduledFuture<?> _timer;

    public void start() {
        _time = 0;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            _time += 1;
            Collection<?> allPet = WorldSummons.get().all();
            if (allPet.isEmpty()) {
                return;
            }
            for (Object o : allPet) {
                L1SummonInstance summon = (L1SummonInstance) o;
                if (MprPet.mpUpdate(summon, _time)) {
                    TimeUnit.MILLISECONDS.sleep(5L);
                }
            }
        } catch (Exception e) {
            _log.error("Summon MP自然回復時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            SummonMprTimer summonMprTimer = new SummonMprTimer();
            summonMprTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pet.SummonMprTimer JD-Core Version: 0.6.2
 */