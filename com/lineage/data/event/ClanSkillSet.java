package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.ClanShowTimer;
import com.lineage.server.timecontroller.event.ClanSkillTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClanSkillSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(ClanSkillSet.class);
    public static boolean START = false;

    public static EventExecutor get() {
        return new ClanSkillSet();
    }

    public void execute(L1Event event) {
        try {
            if (ClanSkillDBSet.START) {
                _log.info("警告!活動設置:111項血盟技能已啟動狀態下無法同時啟動伊薇版血盟技能");
            } else {
                START = true;
                ClanSkillTimer useTimer = new ClanSkillTimer();
                useTimer.start();
            }
            ClanShowTimer clanShowTimer = new ClanShowTimer();
            clanShowTimer.start();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
