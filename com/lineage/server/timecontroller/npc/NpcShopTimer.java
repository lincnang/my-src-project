package com.lineage.server.timecontroller.npc;

import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldDe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NpcShopTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(NpcShopTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        int timeMillis = 8000;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            Collection<L1DeInstance> allDe = WorldDe.get().all();
            if (allDe.isEmpty()) {
                return;
            }
            for (L1DeInstance de : allDe) {
                if ((!de.destroyed()) || (!de.isDead())) {
                    if (de.getCurrentHp() > 0) {
                        if (de.isShop()) {
                            de.shopChat();
                        }
                        if (de.get_chat() != null) {
                            de.globalChat();
                        }
                        TimeUnit.MILLISECONDS.sleep(50L);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Npc 虛擬商店時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            NpcShopTimer shopTimer = new NpcShopTimer();
            shopTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.npc.NpcShopTimer JD-Core Version: 0.6.2
 */