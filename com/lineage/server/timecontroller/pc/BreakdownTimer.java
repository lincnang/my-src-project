package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class BreakdownTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(BreakdownTimer.class);
    private ScheduledFuture<?> _timer;
    private int _timeMillis;
    private Random _random;

    public void start(int timeMillis, Random random) {
        _timeMillis = timeMillis;
        _random = random;
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
                if (_random != null) {
                    int gfxid = _random.nextInt(3);
                    tgpc.sendPackets(new S_Poison(tgpc.getId(), gfxid));
                    tgpc.broadcastPacketAll(new S_Poison(tgpc.getId(), gfxid));
                } else {
                    tgpc.sendPackets(new S_Poison(tgpc.getId(), 0));
                    tgpc.broadcastPacketAll(new S_Poison(tgpc.getId(), 0));
                }
            }
        } catch (Exception e) {
            _log.error("時間軸異常重啟.時間軸異常重啟.時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            BreakdownTimer breakdownTimer = new BreakdownTimer();
            breakdownTimer.start(_timeMillis, _random);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pc.BreakdownTimer JD-Core Version: 0.6.2
 */