package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.NewServerTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NewServerSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(NewServerSet.class);

    public static EventExecutor get() {
        return new NewServerSet();
    }

    public void execute(L1Event event) {
        try {
            int time = Integer.parseInt(event.get_eventother());
            NewServerTime chatTime = new NewServerTime();
            chatTime.start(time);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.NewServerSet JD-Core Version: 0.6.2
 */