package com.lineage.server.model;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

/**
 * 詛咒麻痺處理 (木乃伊狀態)
 * 包含麻痺前的延遲階段與正式麻痺階段的計時器控制
 */
public class L1CurseParalysis extends L1Paralysis {
    private static final Log _log = LogFactory.getLog(L1CurseParalysis.class);
    private final L1Character _target;
    private final int _delay;
    private final int _time;
    private volatile Thread _timer;
    private volatile boolean _isCured = false;

    private L1CurseParalysis(L1Character cha, int delay, int time, int mode) {
        _target = cha;
        _delay = delay;
        _time = time;
        curse(mode);
    }

    public static boolean curse(L1Character cha, int delay, int time, int mode) {
        if ((!(cha instanceof L1PcInstance)) && (!(cha instanceof L1MonsterInstance))) {
            return false;
        }
        if ((cha.hasSkillEffect(1010)) || (cha.hasSkillEffect(1011))) {
            return false;
        }
        cha.setParalaysis(new L1CurseParalysis(cha, delay, time, mode));
        return true;
    }

    private void curse(int mode) {
        if ((_target instanceof L1PcInstance)) {
            L1PcInstance player = (L1PcInstance) _target;
            switch (mode) {
                case 1:
                    player.sendPackets(new S_ServerMessage(212));
                    break;
                case 2:
                    player.sendPackets(new S_ServerMessage(291));
            }
        }
        _target.setPoisonEffect(2);
        _timer = new ParalysisDelayTimer();
        GeneralThreadPool.get().execute(_timer);
    }

    public int getEffectId() {
        return 2;
    }

    public void cure() {
        _isCured = true;
        _target.setPoisonEffect(0);
        _target.setParalaysis(null);
        if (_timer != null) {
            _timer.interrupt();
        }
        if (_target instanceof L1PcInstance) {
            L1PcInstance player = (L1PcInstance) _target;
            if (!player.isDead()) {
                player.sendPackets(new S_Paralysis(1, false, 0));
            }
        }
        _target.setParalyzed(false);
    }

    private class ParalysisDelayTimer extends Thread {
        private ParalysisDelayTimer() {
        }

        public void run() {
            _target.setSkillEffect(1010, 0);
            try {
                TimeUnit.MILLISECONDS.sleep(_delay);
            } catch (InterruptedException e) {
                _target.killSkillEffectTimer(1010);
                ModelError.isError(L1CurseParalysis._log, e.getLocalizedMessage(), e);
                return;
            }
            if (_isCured) return;

            if ((_target instanceof L1PcInstance)) {
                L1PcInstance player = (L1PcInstance) _target;
                if (!player.isDead()) {
                    player.sendPackets(new S_Paralysis(1, true, _time));
                }
            }

            if (_isCured) {
                if ((_target instanceof L1PcInstance)) {
                    L1PcInstance player = (L1PcInstance) _target;
                    player.sendPackets(new S_Paralysis(1, false, 0));
                }
                return;
            }

            _target.setParalyzed(true);
            _timer = new L1CurseParalysis.ParalysisTimer();
            GeneralThreadPool.get().execute(_timer);
            if (isInterrupted()) {
                _timer.interrupt();
            }
        }
    }

    private class ParalysisTimer extends Thread {
        private ParalysisTimer() {
        }

        public void run() {
            if (_isCured) return;
            _target.killSkillEffectTimer(1010);
            _target.setSkillEffect(1011, 0);
            try {
                TimeUnit.MILLISECONDS.sleep(_time);
            } catch (InterruptedException e) {
                ModelError.isError(L1CurseParalysis._log, e.getLocalizedMessage(), e);
            }
            _target.killSkillEffectTimer(1011);
            if ((_target instanceof L1PcInstance)) {
                L1PcInstance player = (L1PcInstance) _target;
                if (!player.isDead()) {
                    player.sendPackets(new S_Paralysis(1, false, 0));
                }
            }
            _target.setParalyzed(false);
            cure();
        }
    }
}
