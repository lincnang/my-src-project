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

public class EffectCubeHarmonizeTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(EffectCubeHarmonizeTimer.class);
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
            for (Iterator<?> iter = allNpc.iterator(); iter.hasNext(); ) {
                L1EffectInstance effect = (L1EffectInstance) iter.next();
                if (effect.effectType() == L1EffectType.isCubeHarmonize) {
                    EffectCubeExecutor.get().cubeBurn(effect);
                    TimeUnit.MILLISECONDS.sleep(1L);
                }
            }
        } catch (Exception e) {
            _log.error("Npc L1Effect幻術師技能(立方：和諧)狀態送出時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            EffectCubeHarmonizeTimer cubeHarmonizeTimer = new EffectCubeHarmonizeTimer();
            cubeHarmonizeTimer.start();
        }
    }
}
