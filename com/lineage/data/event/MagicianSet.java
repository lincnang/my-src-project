package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MagicianSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(MagicianSet.class);
    public static int ITEM_ID;
    public static int ITEM_COUNT;

    public static EventExecutor get() {
        return new MagicianSet();
    }

    public void execute(L1Event event) {
        try {
            String[] set = event.get_eventother().split(",");
            ITEM_ID = Integer.parseInt(set[0]);
            ITEM_COUNT = Integer.parseInt(set[1]);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.MagicianSet JD-Core Version: 0.6.2
 */