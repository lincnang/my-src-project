package com.lineage.server.timecontroller.other.ins;

import com.lineage.config.ConfigAutoAll;
import com.lineage.item_etcitem.SkillScroll;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.lineage.server.model.skill.L1SkillId.BLOODY_SOUL;

/**
 * 自動補魔 -> 第二組 -> 魂體
 */
public class SkillSoundMp2 extends TimerTask {
    private static Logger _log = Logger.getLogger(SkillSoundMp2.class.getName());
    private final L1PcInstance _pc;

    public SkillSoundMp2(final L1PcInstance pc) {
        _pc = pc;
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                if (!_pc.isAutoHpAll()) {
                    return;
                }
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
                if (!_pc.isSkillMastery(BLOODY_SOUL)) {
                    return;
                }
                // 自動魂體
                if (_pc.isAutoMp2()) {
                    if (((_pc.getMaxMp() / 100) * _pc.getTextMp2()) < _pc.getCurrentMp()) {
                        return;
                    }
                    if (_pc.getInventory().getWeight240() >= 197) { // 重量過重
                        return;
                    }
                    if (_pc.getCurrentHp() > 51) {
                        if (ConfigAutoAll.AutoIsBloody) {
                            _pc.sendPackets(new S_SkillSound(_pc.getId(), 2178));
                            _pc.broadcastPacketAll(new S_SkillSound(_pc.getId(), 2178));
                            _pc.setCurrentHp(_pc.getCurrentHp() - 50);
                            _pc.setCurrentMp(_pc.getCurrentMp() + 15);
                        } else {
                            SkillScroll.DoMySkill1(_pc, BLOODY_SOUL);
                        }
                    }
                }
            }
        } catch (final Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
