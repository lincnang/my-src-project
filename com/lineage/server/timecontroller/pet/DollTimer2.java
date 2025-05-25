package com.lineage.server.timecontroller.pet;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1DollInstance2;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldDoll2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 魔法娃娃處理時間軸
 *
 * @author dexc
 */
public class DollTimer2 extends TimerTask {
    private static final Log _log = LogFactory.getLog(DollTimer2.class);
    private static Random _random = new Random();
    private ScheduledFuture<?> _timer;

    /**
     * 移除魔法娃娃
     *
     */
    private static void outDoll(final L1DollInstance2 doll) {
        try {
            if (doll != null) {
                if (doll.destroyed()) {
                    return;
                }
                doll.deleteDoll2();
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        final int timeMillis = 60 * 1000;// 60秒
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            final Collection<L1DollInstance2> allDoll = WorldDoll2.get().all();
            // 不包含元素
            if (allDoll.isEmpty()) {
                return;
            }
            for (final Iterator<L1DollInstance2> iter = allDoll.iterator(); iter.hasNext(); ) {
                final L1DollInstance2 doll = iter.next();
                final int time = doll.get_time() - 60;
                // time -= 60;
                if (time <= 0) {
                    outDoll(doll);
                } else {
                    doll.set_time(time);
                    if (doll.isDead()) {
                        continue;
                    }
                    checkAction(doll);
                }
                TimeUnit.MILLISECONDS.sleep(50);
            }
        } catch (final Exception e) {
            _log.error("魔法娃娃處理時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final DollTimer2 dollTimer = new DollTimer2();
            dollTimer.start();
        }
    }

    /**
     * 娃娃動作
     *
     */
    private void checkAction(final L1DollInstance2 doll) {
        try {
            if (doll.getX() == doll.get_olX() && doll.getY() == doll.get_olY()) {
                int run = 0;
                run = _random.nextInt(100) + 1;
                int actionCode = ActionCodes.ACTION_Think; // 66
                if (run > 20 && run < 40) {
                    actionCode = ActionCodes.ACTION_Aggress; // 67
                }
                if (run > 40 && run < 60) {
                    actionCode = 68; // 67
                }
                if (run > 60 && run < 80) {
                    actionCode = 98; // 67
                }
                if (run > 80 && run < 100) {
                    actionCode = 99; // 67
                }
                doll.broadcastPacketAll(new S_DoActionGFX(doll.getId(), actionCode));
            }
            doll.set_olX(doll.getX());
            doll.set_olY(doll.getY());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
