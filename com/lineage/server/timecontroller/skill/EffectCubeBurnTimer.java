package com.lineage.server.timecontroller.skill;

import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1EffectType;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldEffect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EffectCubeBurnTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(EffectCubeBurnTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 500L, 500L);
    }

    public void run() {
        try {
            Collection<?> allNpc = WorldEffect.get().all();
            if (allNpc.isEmpty()) {
                return;
            }
            for (Object o : allNpc) {
                L1EffectInstance effect = (L1EffectInstance) o;
                if (effect.effectType() == L1EffectType.isCubeBurn) {
                    EffectCubeExecutor.get().cubeBurn(effect);
                    TimeUnit.MILLISECONDS.sleep(1L);
                }
            }
        } catch (Exception e) {
            _log.error("Npc L1Effect幻術師技能(立方：燃燒)狀態送出時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            EffectCubeBurnTimer cubeBurnTimer = new EffectCubeBurnTimer();
            cubeBurnTimer.start();
        }
    }
}