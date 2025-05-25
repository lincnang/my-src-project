package com.lineage.server.timecontroller.npc;

import com.lineage.server.datatables.lock.SpawnBossReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1Spawn;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NpcExistTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(NpcExistTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        int timeMillis = 600 * 1000;// 10分鐘
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            Map<Integer, L1Spawn> BossSpawn = SpawnBossReading.get().get_bossSpawnTable();
            for (L1Spawn spawn : BossSpawn.values()) {
                if (spawn.get_existTime() > 0) {// 具有BOSS存在時間限制(分)
                    long existTime = (long) spawn.get_existTime() * 60 * 1000;// 存在時間限制轉換成毫秒
                    long spawnTime = spawn.get_nextSpawnTime().getTimeInMillis();// 出生時間轉換成毫秒
                    long nowTime = System.currentTimeMillis();// 現在時間
                    L1NpcInstance npc = spawn.getNpcTemp();
                    if (existTime + spawnTime < nowTime) {// 已超過存在時間限制
                        npc.deleteMe();
                    }
                }
                TimeUnit.MILLISECONDS.sleep(50L);
            }
        } catch (Exception e) {
            _log.error("BOSS存在時間限制時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            NpcExistTimer npcexistTimer = new NpcExistTimer();
            npcexistTimer.start();
        }
    }
}
