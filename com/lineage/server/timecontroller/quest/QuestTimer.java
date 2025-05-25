package com.lineage.server.timecontroller.quest;

import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class QuestTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(QuestTimer.class);
    private ScheduledFuture<?> _timer;

    private static void setQuest(L1QuestUser quest) {
        try {
            switch (quest.get_time()) {
                case 60:
                case 120:
                case 180:
                case 240:
                case 300:
                case 600:
                case 900:
                case 1800:
                case 3600:
                    quest.sendPackets(new S_HelpMessage("\\fV注意!退出副本後無法再進入。"));
                    quest.sendPackets(new S_HelpMessage("\\fV副本任務-剩餘時間：" + quest.get_time() / 60 + "分"));
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 10:
                case 15:
                case 30:
                    quest.sendPackets(new S_HelpMessage("\\fV副本任務-剩餘時間：" + quest.get_time() + "秒"));
            }
            quest.set_time(quest.get_time() - 1);
            if (quest.get_time() == 0) {
                quest.sendPackets(new S_ServerMessage("副本任務-時間結束"));
                quest.set_time(-1);
                quest.endQuest();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

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
                if (quest.get_time() > -1) {
                    setQuest(quest);
                    TimeUnit.MILLISECONDS.sleep(50L);
                }
            }
        } catch (Exception e) {
            _log.error("副本任務可執行時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            QuestTimer questTimer = new QuestTimer();
            questTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.quest.QuestTimer JD-Core Version: 0.6.2
 */