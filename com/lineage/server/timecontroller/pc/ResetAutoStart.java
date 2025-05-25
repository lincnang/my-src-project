package com.lineage.server.timecontroller.pc;

import com.lineage.config.ThreadPoolSetNew;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * PC 掛機自動重置
 *
 * @author 台灣JAVA技術老爹
 */
public class ResetAutoStart extends TimerTask {
    private static final Log _log = LogFactory.getLog(ResetAutoStart.class);
    private ScheduledFuture<?> _timer;

    /**
     * 檢查掛機時間
     *
     */
    private static void check(final L1PcInstance tgpc, final Integer time) {
        if (time > 0) {//還有掛機時間
            tgpc.setRestartAutoStartSec(time);
        } else {
            tgpc.setRestartAutoStartSec(0);
            if (tgpc.hasSkillEffect(4020)) {
                tgpc.killSkillEffectTimer(4020);
            }
            tgpc.setRestartAuto(ThreadPoolSetNew.RESTART_AUTO);
            //            tgpc.setIsAuto(true);
            //            AutoAttack2020_1 auto = new AutoAttack2020_1(tgpc);
            //            auto.begin();
            tgpc.startPcAI();
            tgpc.sendPackets(new S_ServerMessage("\\ff自動練功重置完成。"));
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
            if (all.isEmpty()) {
                return;
            }
            for (final L1PcInstance tgpc : all) {
                int time = tgpc.getRestartAutoStartSec();
                if (time > 0) {
                    time--;//時間-1秒
                    if (!tgpc.isDead()) {//角色未死亡
                        check(tgpc, time);
                    }
                    TimeUnit.MILLISECONDS.sleep(5);
                }
            }
        } catch (final Exception e) {
            _log.error("PC 掛機處理時間軸異常重啟", e);
            PcOtherThreadPool.get().cancel(_timer, false);
            final ResetAutoStart autoTimer = new ResetAutoStart();
            autoTimer.start();
        }
    }
}
