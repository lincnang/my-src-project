package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;

public class MazuSet extends EventExecutor {
    public static EventExecutor get() {
        return new MazuSet();
    }

    public void execute(L1Event event) {
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.MazuSet JD-Core Version: 0.6.2
 */