package com.lineage.server.timecontroller.npc;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * NPC(BOSS)召喚時間時間軸
 *
 * @author dexc
 */
public class NpcSpawnBossTimer extends TimerTask {
    public static final Map<L1NpcInstance, Long> MAP = new ConcurrentHashMap<L1NpcInstance, Long>();
    private static final Log _log = LogFactory.getLog(NpcSpawnBossTimer.class);
    private ScheduledFuture<?> _timer;
    /*
     * private static final ArrayList<L1NpcInstance> REMOVE = new
     * ArrayList<L1NpcInstance>();
     */

    /**
     * 召喚BOSS
     *
     * @param npc
     */
    private static void spawn(L1NpcInstance npc) {
        try {
            npc.getSpawn().executeSpawnTask(npc.getSpawnNumber(), npc.getId());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        final int timeMillis = 60 * 1000;// 1分鐘
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            // 不包含元素
            if (MAP.isEmpty()) {
                return;
            }
            for (final L1NpcInstance npc : MAP.keySet()) {
                final Long time = MAP.get(npc);
                long t = time - 60;
                if (time > 0) {
                    // 更新時間
                    MAP.put(npc, t);
                } else {
                    // 召喚
                    spawn(npc);
                    MAP.remove(npc);
                }
                TimeUnit.MILLISECONDS.sleep(50);
            }
        } catch (final Exception e) {
            _log.error("NPC(BOSS)召喚時間時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final NpcSpawnBossTimer bossTimer = new NpcSpawnBossTimer();
            bossTimer.start();
        } finally {
            // ListMapUtil.clear(REMOVE);
        }
    }
}
