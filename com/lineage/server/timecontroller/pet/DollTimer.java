package com.lineage.server.timecontroller.pet;

import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldDoll;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DollTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(DollTimer.class);
    private static Random _random = new Random();
    private ScheduledFuture<?> _timer;

    /**
     * 收回魔法娃娃
     *
     */
    private static void outDoll(L1DollInstance doll) {
        try {
            if (doll != null) {
                if (doll.destroyed()) {
                    return;
                }
                doll.deleteDoll();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        int timeMillis = 30 * 1000;// 30秒
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            Collection<?> allDoll = WorldDoll.get().all();
            if (allDoll.isEmpty()) {
                return;
            }
            for (Iterator<?> iter = allDoll.iterator(); iter.hasNext(); ) {
                L1DollInstance doll = (L1DollInstance) iter.next();
                int time = doll.get_time() - 30;// 減少30秒
                if (time <= 0) {
                    outDoll(doll);
                } else {
                    doll.set_time(time);
                    if (doll.isDead()) {
                        continue;
                    }
                    checkAction(doll);
                }
                TimeUnit.MILLISECONDS.sleep(50L);
            }
        } catch (Exception e) {
            _log.error("魔法娃娃處理時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            DollTimer dollTimer = new DollTimer();
            dollTimer.start();
        }
    }

    /**
     * 特殊動作執行
     *
     */
    private void checkAction(L1DollInstance doll) {
        try {
            if ((doll.getX() == doll.get_olX()) && (doll.getY() == doll.get_olY())) {
                int actionCode;
                if (_random.nextBoolean()) {
                    actionCode = 66;
                } else {
                    actionCode = 67;
                }
                doll.broadcastPacketAll(new S_DoActionGFX(doll.getId(), actionCode));
            }
            doll.set_olX(doll.getX());
            doll.set_olY(doll.getY());
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pet.DollTimer JD-Core Version: 0.6.2
 */