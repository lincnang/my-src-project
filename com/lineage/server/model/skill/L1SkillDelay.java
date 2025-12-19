package com.lineage.server.model.skill;

import com.lineage.server.model.L1Character;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1SkillDelay {
    private static final Log _log = LogFactory.getLog(L1SkillDelay.class);

    public static void onSkillUse(L1Character cha, int time) {
        try {
            cha.setSkillDelay(true);
            GeneralThreadPool.get().schedule(new SkillDelayTimer(cha), time);
        } catch (Exception e) {
            _log.error("設置技能延遲時發生錯誤: " + e.getLocalizedMessage(), e);
            // 如果設置失敗，確保清除延遲標記
            cha.setSkillDelay(false);
        }
    }

    static class SkillDelayTimer implements Runnable {
        private L1Character _cha;

        public SkillDelayTimer(L1Character cha) {
            _cha = cha;
        }

        public void run() {
            try {
                stopDelayTimer();
            } catch (Exception e) {
                _log.error("清除技能延遲時發生錯誤: " + e.getLocalizedMessage(), e);
            }
        }

        public void stopDelayTimer() {
            _cha.setSkillDelay(false);
        }
    }
}
