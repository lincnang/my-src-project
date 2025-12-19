package com.lineage.server.Controller;

import com.lineage.server.clientpackets.AcceleratorChecker;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.monitor.L1PcMonitor;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.templates.L1Skills;

import java.util.concurrent.TimeUnit;

public class L1DarknessMonitor extends L1PcMonitor {
    public L1DarknessMonitor(final int oId) {
        super(oId);
    }

    @Override
    public void execTask(final L1PcInstance pc) {
        try {
            // 獲取技能資訊
            L1Skills skill = SkillsTable.get().getTemplate(L1SkillId.HAND_DARKNESS);
            // 預設間隔時間（毫秒）
            int defaultInterval = 500;
            // 如果有設定技能的 reuseDelay，可以使用它來調整間隔
            // 這裡使用 reuseDelay 的百分比作為間隔調整（例如：reuseDelay/100）
            int skillInterval = (skill != null && skill.getReuseDelay() > 0) ?
                Math.max(100, skill.getReuseDelay()) : defaultInterval;

            while (pc.hasSkillEffect(L1SkillId.HAND_DARKNESS) || pc.hasSkillEffect(L1SkillId.Phantom_Blade)) {
                pc.HandDarknessMove();
                // 使用技能設定的間隔或預設的移動速度間隔
                final int moveInterval = Math.min(skillInterval, pc.speed_Attack().getRightInterval(AcceleratorChecker.ACT_TYPE.MOVE));
                TimeUnit.MILLISECONDS.sleep(moveInterval);
            }
        } catch (final Exception ignored) {
        }
    }
}
