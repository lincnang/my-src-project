package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseResetSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(BaseResetSet.class);
    public static int RETAIN = 0;

    public static EventExecutor get() {
        return new BaseResetSet();
    }

    public void execute(L1Event event) {
        try {
            String[] set = event.get_eventother().split(",");
            try {
                RETAIN = Integer.parseInt(set[0]);
            } catch (Exception localException1) {
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.BaseResetSet JD-Core Version: 0.6.2
 */