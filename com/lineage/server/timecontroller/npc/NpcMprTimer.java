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
import java.util.concurrent.TimeUnit;

/**
 * Npc MP自然回復時間軸(對怪物)
 *
 * @author dexc
 */
public class NpcMprTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(NpcMprTimer.class);
    private ScheduledFuture<?> _timer;

    /**
     * 判斷是否執行回復
     *
     */
    private static void mpUpdate(final L1MonsterInstance mob) {
        int mprInterval = mob.getNpcTemplate().get_mprinterval();
        // 無特別指定時間 每10秒回復一次
        if (mprInterval <= 0) {
            mprInterval = 10;
        }
        final long nowtime = System.currentTimeMillis() / 1000;// 現在時間換算為秒
        long LastMprTime = mob.getLastMprTime();// 怪物上次回血時間(秒)
        if (nowtime - LastMprTime >= mprInterval) {
            // 無特別指定回復量 每次回復2
            int mpr = mob.getNpcTemplate().get_mpr();
            if (mpr <= 0) {
                mpr = 2;
            }
            mprInterval(mob, mpr);
            mob.setLastMprTime(nowtime);// 更新怪物上次回魔時間
        }
    }

    /**
     * 執行回復MP
     *
     */
    private static void mprInterval(final L1MonsterInstance mob, final int mpr) {
        try {
            if (mob.isMpRegenerationX()) {
                mob.setCurrentMp(mob.getCurrentMp() + mpr);
            }
        } catch (final Exception e) {
            _log.error("Npc 執行回復MP發生異常", e);
            mob.deleteMe();
        }
    }

    public void start() {
        final int timeMillis = 5000;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            final Collection<L1MonsterInstance> allMob = WorldMob.get().all();
            // 不包含元素
            if (allMob.isEmpty()) {
                return;
            }
            for (final Iterator<L1MonsterInstance> iter = allMob.iterator(); iter.hasNext(); ) {
                final L1MonsterInstance mob = iter.next();
                // HP是否具備回復條件
                if (mob.isMpR()) {
                    mpUpdate(mob);
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            }
        } catch (final Exception e) {
            _log.error("Npc MP自然回復時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final NpcMprTimer npcMprTimer = new NpcMprTimer();
            npcMprTimer.start();
        }
    }
}
