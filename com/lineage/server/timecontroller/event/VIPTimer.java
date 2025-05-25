package com.lineage.server.timecontroller.event;

import com.lineage.server.datatables.lock.VIPReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class VIPTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(VIPTimer.class);
    private ScheduledFuture<?> _timer;

    private static void checkVIP(Integer objid) {
        try {
            L1Object target = World.get().findObject(objid.intValue());
            if (target != null) {
                boolean isOut = false;
                if (!isOut) {
                    return;
                }
                if ((target instanceof L1PcInstance)) {
                    L1PcInstance tgpc = (L1PcInstance) target;
                    L1Teleport.teleport(tgpc, 33080, 33392, (short) 4, 5, true);
                    tgpc.sendPackets(new S_ServerMessage("VIP時間終止"));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        int timeMillis = 60000;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            Map<Integer, Timestamp> map = VIPReading.get().map();
            if (map.isEmpty()) {
                return;
            }
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            for (Integer objid : map.keySet()) {
                Timestamp time = (Timestamp) map.get(objid);
                if (time.before(ts)) {
                    VIPReading.get().delOther(objid.intValue());
                    checkVIP(objid);
                }
                TimeUnit.MILLISECONDS.sleep(5L);
            }
        } catch (Exception e) {
            _log.error("VIP計時時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            VIPTimer timer = new VIPTimer();
            timer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.event.VIPTimer JD-Core Version: 0.6.2
 */