package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ServerQuestMobTable;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QuestMobSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(QuestMobSet.class);
    public static boolean START = false;

    public static EventExecutor get() {
        return new QuestMobSet();
    }

    @Override
    public void execute(L1Event event) {
        try {
            START = true;
            ServerQuestMobTable.get();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
