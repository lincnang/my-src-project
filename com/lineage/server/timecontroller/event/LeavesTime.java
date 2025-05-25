package com.lineage.server.timecontroller.event;

import com.lineage.data.event.LeavesSet;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxExp;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LeavesTime extends TimerTask {
    private static final Log _log = LogFactory.getLog(LeavesTime.class);
    private ScheduledFuture<?> _timer;

    private static boolean check(L1PcInstance tgpc) {
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
            if (tgpc.isDead()) {
                return false;
            }
            if (!tgpc.isSafetyZone()) {
                return false;
            }
            if (tgpc.isTeleport()) {
                return false;
            }
            if (tgpc.get_other().get_leaves_time_exp() >= LeavesSet.MAXEXP) {
                return false;
            }
            if (tgpc.getHpRegenState() == 1) {
                return false;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    public void start() {
        int timeMillis = 60000;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            Collection<?> all = World.get().getAllPlayers();
            if (all.isEmpty()) {
                return;
            }
            for (Object o : all) {
                L1PcInstance tgpc = (L1PcInstance) o;
                if (check(tgpc)) {
                    int time = tgpc.get_other().get_leaves_time();
                    tgpc.get_other().set_leaves_time(time + 1);
                    if (tgpc.get_other().get_leaves_time() >= LeavesSet.TIME) {// 達到DB設定休息時間
                        tgpc.get_other().set_leaves_time(0);// 休息時間歸零
                        int exp = tgpc.get_other().get_leaves_time_exp();
                        int addexp = exp + LeavesSet.EXP;
                        tgpc.get_other().set_leaves_time_exp(addexp);
                        tgpc.sendPackets(new S_PacketBoxExp(tgpc.get_other().get_leaves_time_exp() / LeavesSet.EXP));
                    }
                    TimeUnit.MILLISECONDS.sleep(100L);
                }
            }
        } catch (Exception e) {
            _log.error("殷海薩的祝福-休息系統時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            LeavesTime leavesTime = new LeavesTime();
            leavesTime.start();
        }
    }
}
