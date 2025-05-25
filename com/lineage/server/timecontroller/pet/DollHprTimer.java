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

public class DollHprTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(DollHprTimer.class);
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
            if (!tgpc.getHpRegeneration()) {
                return false;
            }
            if (tgpc.getCurrentHp() >= tgpc.getMaxHp()) {
                return false;
            }
            int newtime = tgpc.get_doll_hpr_time() - 1;
            tgpc.set_doll_hpr_time(newtime);
            if (newtime <= 0) {
                tgpc.set_doll_hpr_time(tgpc.get_doll_hpr_time_src());
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
            for (L1PcInstance tgpc : all) {
                if (tgpc.get_doll_hpr_time_src() > 0) {
                    if (checkErr(tgpc)) {
                        int newHp = tgpc.getCurrentHp() + tgpc.get_doll_hpr();
                        tgpc.setCurrentHp(newHp);
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
            _log.error("魔法娃娃處理時間軸(HPR)異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            DollHprTimer dollTimer = new DollHprTimer();
            dollTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pet.DollHprTimer JD-Core Version: 0.6.2
 */