package com.lineage.server.model.skill;

import com.lineage.server.model.L1Character;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ScheduledFuture;

public class L1SkillTimerTimerImpl implements L1SkillTimer, Runnable {
    private static final Log _log = LogFactory.getLog(L1SkillTimerTimerImpl.class);
    private final L1Character _cha;
    private final int _skillId;
    private ScheduledFuture<?> _future;
    private int _remainingTime;

    public L1SkillTimerTimerImpl(L1Character cha, int skillId, int timeMillis) {
        _cha = cha;
        _skillId = skillId;
        _remainingTime = (timeMillis / 1000);
    }

    public void run() {
        if (--_remainingTime <= 0) {
            _cha.removeSkillEffect(_skillId);
        }
    }

    public void begin() {
        _future = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void end() {
        kill();
        try {
            L1SkillStop.stopSkill(_cha, _skillId);
        } catch (Throwable e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void kill() {
        try {
            if (_future != null) {
                _future.cancel(false);
                _future = null;
            }
        } catch (Throwable e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int getRemainingTime() {
        return _remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        _remainingTime = remainingTime;
    }
}
