package com.lineage.server.timecontroller.other.ins;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;

import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 自動回城
 */
public class SkillSoundBackHome extends TimerTask {
    private static final Random _random = new Random();
    private static Logger _log = Logger.getLogger(SkillSoundBackHome.class.getName());
    private final L1PcInstance _pc;

    public SkillSoundBackHome(final L1PcInstance pc) {
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
            if (!_pc.getMap().isUsableItem()) {
                return;
            }
            if (!_pc.getMap().isEscapable()) {
                return;
            }
            if (!_pc.isAutoHpAll()) {
                return;
            }
            if (!_pc.isAutoBackHome()) {
                return;
            }
            if (_pc.isSafetyZone()) {
                return;
            }
            synchronized (this) {
                // 自動回城
                if (_pc.isAutoBackHome()) {
                    if (((_pc.getMaxHp() / 100) * _pc.getTextBh()) < _pc.getCurrentHp()) {
                        return;
                    }
                    int r = _random.nextInt(80);
                    if (r <= 20) {
                        L1Teleport.teleport(_pc, 33438, 32810, (short) 4, _pc.getHeading(), true);
                    } else if (r <= 40) {
                        L1Teleport.teleport(_pc, 33421, 32814, (short) 4, _pc.getHeading(), true);
                    } else if (r <= 60) {
                        L1Teleport.teleport(_pc, 33433, 32799, (short) 4, _pc.getHeading(), true);
                    } else if (r <= 80) {
                        L1Teleport.teleport(_pc, 33429, 32829, (short) 4, _pc.getHeading(), true);
                    }
                    //int addhp = (_pc.getMaxHp() / 100) * (_pc.getText4() + 1);
                    //_pc.setCurrentHp(addhp);
                }
            }
        } catch (final Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
