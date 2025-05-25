package com.lineage.server.timecontroller.pet;

import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DollMprTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(DollMprTimer.class);
    private ScheduledFuture<?> _timer;

    private static boolean checkErr(L1PcInstance tgpc) {
        try {
            if (tgpc == null) {
                return false;
            }
            if (tgpc.getOnlineStatus() == 0) {
                return false;
            }
            if (tgpc.getNetConnection() == null) {
                return false;
            }
            if (tgpc.isTeleport()) {
                return false;
            }
            if (!tgpc.getMpRegeneration()) {
                return false;
            }
            if (tgpc.getCurrentMp() >= tgpc.getMaxMp()) {
                return false;
            }
            int newtime = tgpc.get_doll_mpr_time() - 1;
            tgpc.set_doll_mpr_time(newtime);
            if (newtime <= 0) {
                tgpc.set_doll_mpr_time(tgpc.get_doll_mpr_time_src());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            Collection<L1PcInstance> all = World.get().getAllPlayers();
            if (all.isEmpty()) {
                return;
            }
            for (Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext(); ) {
                L1PcInstance tgpc = (L1PcInstance) iter.next();
                if (tgpc.get_doll_mpr_time_src() > 0) {
                    if (checkErr(tgpc)) {
                        int newMp = tgpc.getCurrentMp() + tgpc.get_doll_mpr();
                        tgpc.setCurrentMp(newMp);
                        if (!tgpc.getDolls().isEmpty()) {
                            for (L1DollInstance doll : tgpc.getDolls().values()) {
                                doll.show_action(3);
                            }
                        }
                        TimeUnit.MILLISECONDS.sleep(50L);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("魔法娃娃處理時間軸(MPR)異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            DollMprTimer dollTimer = new DollMprTimer();
            dollTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pet.DollMprTimer JD-Core Version: 0.6.2
 */