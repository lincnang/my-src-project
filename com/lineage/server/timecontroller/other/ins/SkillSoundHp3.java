package com.lineage.server.timecontroller.other.ins;

import com.lineage.item_etcitem.SkillScroll;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 自動喝藥 -> 第三組
 */
public class SkillSoundHp3 extends TimerTask {
    private static Logger _log = Logger.getLogger(SkillSoundHp3.class.getName());
    private final L1PcInstance _pc;

    public SkillSoundHp3(final L1PcInstance pc) {
        _pc = pc;
    }

    @Override
    public void run() {
        try {
            if (_pc.isDead()) {
                return;
            }
            if (_pc.isParalyzedX()) {
                return;
            }
            // 是否鬼魂/傳送/商店
            if (_pc.isParalyzedX1(_pc)) {
                return;
            }
            if (!_pc.getMap().isUsableSkill()) {
                return;
            }
            if (!_pc.isAutoHpAll()) {
                return;
            }
            if (!_pc.isAutoHp3()) {
                return;
            }
            synchronized (this) {
                // 第三組自動喝藥
                if (_pc.isAutoHp3()) {
                    if (((_pc.getMaxHp() / 100) * _pc.getTextHp3()) < _pc.getCurrentHp()) {
                        return;
                    }
                    if (_pc.getInventory().getWeight240() >= 197) { // 重量過重
                        return;
                    }
                    if (_pc.getAutoItemId3() == 700025 && _pc.isSkillMastery(EXTRA_HEAL)) {
                        SkillScroll.DoMySkill1(_pc, EXTRA_HEAL);// 初級治愈術 20231229修改
                    } else if (_pc.getAutoItemId3() == 700026 && _pc.isSkillMastery(EXTRA_HEAL)) {
                        SkillScroll.DoMySkill1(_pc, EXTRA_HEAL);// 中級治愈術
                    } else if (_pc.getAutoItemId3() == 700027 && _pc.isSkillMastery(GREATER_HEAL)) {
                        SkillScroll.DoMySkill1(_pc, GREATER_HEAL);// 高級治愈術
                    } else if (_pc.getAutoItemId3() == 700028 && _pc.isSkillMastery(FULL_HEAL)) {
                        SkillScroll.DoMySkill1(_pc, FULL_HEAL);// 全部治愈術
                    } else if (_pc.getAutoItemId3() == 700029 && _pc.isSkillMastery(NATURES_BLESSING)) {
                        SkillScroll.DoMySkill1(_pc, NATURES_BLESSING);// 生命的祝福
                    }
                }
            }
        } catch (final Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
