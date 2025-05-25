package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FeatureItemSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(FeatureItemSet.class);
    public static boolean POWER_START = false;

    public static EventExecutor get() {
        return new FeatureItemSet();
    }

    public void execute(L1Event event) {
        try {
            String[] set = event.get_eventother().split(",");
            POWER_START = Boolean.parseBoolean(set[0]);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.FeatureItemSet JD-Core Version: 0.6.2
 */