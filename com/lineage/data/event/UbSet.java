package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.UBSpawnTable;
import com.lineage.server.datatables.UBTable;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.UbTime;

public class UbSet extends EventExecutor {
    public static EventExecutor get() {
        return new UbSet();
    }

    public void execute(L1Event event) {
        UBTable.getInstance().load();
        UBSpawnTable.getInstance().load();
        UbTime ubTimeContoroller = new UbTime();
        ubTimeContoroller.start();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.UbSet JD-Core Version: 0.6.2
 */