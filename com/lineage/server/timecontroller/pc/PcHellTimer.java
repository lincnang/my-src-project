package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.utils.collections.Maps;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * PC 地獄模式處理 時間軸
 *
 * @author dexc 修改 by 聖子默默
 */
public class PcHellTimer extends TimerTask {
    public static final Map<L1PcInstance, Integer> _HELL_PC_LIST = Maps.newConcurrentHashMap();
    private static final Log _log = LogFactory.getLog(PcHellTimer.class);
    private ScheduledFuture<?> _timer;

    /**
     * 檢查地獄模式時間
     *
     */
    private static void check(final L1PcInstance player, final Integer time) {
        if (time > 0) {// 還有處罰時間
            // 更新
            player.setHellTime(time);
        } else {
            // 時間到
            player.setHellTime(0);
            // 未斷線移除狀態
            if (player.getNetConnection() != null) {
                outPc(player);
            }
        }
    }

    /**
     * 離開地獄模式
     *
     */
    private static void outPc(final L1PcInstance player) {
        try {
            if (player != null) {
                player.endHell();
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        final int timeMillis = 1000;
        _timer = PcOtherThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            final Collection<L1PcInstance> all = World.get().getAllPlayers();
            // 不包含元素
            if (_HELL_PC_LIST.isEmpty()) {
                return;
            }
            for (final L1PcInstance hellPc : _HELL_PC_LIST.keySet()) {
                if (!hellPc.isDead()) {// 角色未死亡
                    continue;
                }
                if (hellPc.isOutGame()) {
                    _HELL_PC_LIST.remove(hellPc);
                    continue;
                }
                int time = hellPc.getHellTime();
                // 非地獄狀態
                if (time <= 0) {
                    continue;
                }
                time--;// 時間-1秒
                check(hellPc, time);
                TimeUnit.MILLISECONDS.sleep(1);
            }
        } catch (final Exception e) {
            _log.error("PC 地獄模式處理時間軸異常重啟", e);
            PcOtherThreadPool.get().cancel(_timer, false);
            final PcHellTimer hellTimer = new PcHellTimer();
            hellTimer.start();
        }
    }
}
