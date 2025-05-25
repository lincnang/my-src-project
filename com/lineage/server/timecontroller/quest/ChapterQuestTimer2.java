package com.lineage.server.timecontroller.quest;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChapterQuestTimer2 extends TimerTask {
    private static final Log _log = LogFactory.getLog(ChapterQuestTimer2.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
    }

    public void run() {
        try {
            Collection<L1QuestUser> allQuest = WorldQuest.get().all();
            if (allQuest.isEmpty()) {
                return;
            }
            for (L1QuestUser quest : allQuest) {
                if (quest.get_orimR() != null) {
                    L1PcInstance leader = quest.get_orimR().party.getLeader();
                    if ((leader.getX() == 32799) && (leader.getY() == 32808)) {
                        if (leader.get_actionId() == 66) {
                            quest.get_orimR().attack();
                            leader.set_actionId(0);
                        } else if (leader.get_actionId() == 69) {
                            quest.get_orimR().defense();
                            leader.set_actionId(0);
                        }
                    } else if ((leader.get_actionId() == 66) || (leader.get_actionId() == 69)) {
                        leader.set_actionId(0);
                    }
                    if (quest.get_orimR().portal != null) {
                        for (L1PcInstance member : quest.pcList()) {
                            quest.get_orimR().teleport(member, quest.get_orimR().getCabinLocation());
                        }
                    }
                    quest.get_orimR().calcScore();
                    TimeUnit.MILLISECONDS.sleep(1L);
                }
            }
        } catch (Exception e) {
            _log.error("副本任務檢查時間軸<海戰副本>異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            ChapterQuestTimer2 questTimer = new ChapterQuestTimer2();
            questTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.quest.ChapterQuestTimer2 JD-Core Version:
 * 0.6.2
 */