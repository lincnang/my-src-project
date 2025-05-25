package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.QuestTable;
import com.lineage.server.datatables.QuesttSpawnTable;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.quest.ChapterQuestTimer2;
import com.lineage.server.timecontroller.quest.QuestTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QuestSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(QuestSet.class);
    public static boolean ISQUEST = false;

    public static EventExecutor get() {
        return new QuestSet();
    }

    public void execute(L1Event event) {
        try {
            ISQUEST = true;
            QuestTable.get().load();
            if (QuestTable.get().size() > 0) {
                QuesttSpawnTable.get().load();
            }
            QuestTimer questTimer = new QuestTimer();
            questTimer.start();
            ChapterQuestTimer2 questTimer2 = new ChapterQuestTimer2();
            questTimer2.start();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.QuestSet JD-Core Version: 0.6.2
 */