package com.lineage.server.Controller;

import com.lineage.server.clientpackets.AcceleratorChecker;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.monitor.L1PcMonitor;
import com.lineage.server.model.skill.L1SkillId;

import java.util.concurrent.TimeUnit;

public class L1DarknessMonitor extends L1PcMonitor {
    public L1DarknessMonitor(final int oId) {
        super(oId);
    }

    @Override
    public void execTask(final L1PcInstance pc) {
        try {
            while (pc.hasSkillEffect(L1SkillId.HAND_DARKNESS) || pc.hasSkillEffect(L1SkillId.Phantom_Blade)) {
                pc.HandDarknessMove();
                final int interval = pc.speed_Attack().getRightInterval(AcceleratorChecker.ACT_TYPE.MOVE);
                TimeUnit.MILLISECONDS.sleep(interval);
            }
        } catch (final Exception ignored) {
        }
    }
}
