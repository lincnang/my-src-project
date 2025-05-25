package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.lock.MaryReading;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MarySet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(MarySet.class);
    public static boolean START = false;

    public static EventExecutor get() {
        return new MarySet();
    }

    public void execute(L1Event event) {
        try {
            START = true;
            MaryReading.get().load();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.MarySet JD-Core Version: 0.6.2
 */