package com.lineage.server.timecontroller.pet;

import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldPet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PetMprTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(PetMprTimer.class);
    private static int _time = 0;
    private ScheduledFuture<?> _timer;

    public void start() {
        _time = 0;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            _time += 1;
            Collection<?> allPet = WorldPet.get().all();
            if (allPet.isEmpty()) {
                return;
            }
            for (Object o : allPet) {
                L1PetInstance pet = (L1PetInstance) o;
                if (MprPet.mpUpdate(pet, _time)) {
                    TimeUnit.MILLISECONDS.sleep(5L);
                }
            }
        } catch (Exception e) {
            _log.error("Pet MP自然回復時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            PetMprTimer petMprTimer = new PetMprTimer();
            petMprTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pet.PetMprTimer JD-Core Version: 0.6.2
 */