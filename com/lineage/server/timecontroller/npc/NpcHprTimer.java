package com.lineage.server.timecontroller.npc;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldMob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

/**
 * Npc HP自然回復時間軸(對怪物)
 *
 * @author dexc
 */
public class NpcHprTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(NpcHprTimer.class);
    private ScheduledFuture<?> _timer;

    /**
     * 判斷是否執行回復
     *
     */
    private static void hpUpdate(final L1MonsterInstance mob) {
        int hprInterval = mob.getNpcTemplate().get_hprinterval();
        // 無特別指定時間 每10秒回復一次
        if (hprInterval <= 0) {
            hprInterval = 10;
        }
        final long nowtime = System.currentTimeMillis() / 1000;// 現在時間換算為秒
        long LastHprTime = mob.getLastHprTime();// 怪物上次回血時間(秒)
        if (nowtime - LastHprTime >= hprInterval) {
            // 無特別指定回復量 每次回復2
            int hpr = mob.getNpcTemplate().get_hpr();
            if (hpr <= 0) {
                hpr = 2;
            }
            hprInterval(mob, hpr);
            mob.setLastHprTime(nowtime);// 更新怪物上次回血時間
        }
    }

    /**
     * 執行回復HP
     *
     */
    private static void hprInterval(final L1MonsterInstance mob, final int hpr) {
        try {
            if (mob.isHpRegenerationX()) {
                mob.setCurrentHp(mob.getCurrentHp() + hpr);
            }
        } catch (final Exception e) {
            _log.error("Npc 執行回復HP發生異常", e);
            mob.deleteMe();
        }
    }

    public void start() {
        final int timeMillis = 5000;// 5秒
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        //System.out.println("怪物回血魔執行中:"+format.format(new Date()));
        try {
            final Collection<L1MonsterInstance> allMob = WorldMob.get().all();
            // 不包含元素
            if (allMob.isEmpty()) {
                _log.error("Npc HP自然回復時間軸異常重啟 allMob.isEmpty()");
                return;
            }
            for (final L1MonsterInstance mob : allMob) {
                // HP是否具備回復條件
                if (mob.isHpR()) {
                    hpUpdate(mob);
                    //TimeUnit.MILLISECONDS.sleep(50);
                }
            }
        } catch (final Exception e) {
            _log.error("Npc HP自然回復時間軸異常重啟", e);
            if (_timer != null) {
                GeneralThreadPool.get().cancel(_timer, false);
            }
            final NpcHprTimer npcHprTimer = new NpcHprTimer();
            npcHprTimer.start();
        }
    }
}
