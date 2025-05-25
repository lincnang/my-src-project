package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ItemUpdateTable;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ItemUpdateSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(ItemUpdateSet.class);
    public static boolean MODE = false;

    public static EventExecutor get() {
        return new ItemUpdateSet();
    }

    public void execute(L1Event event) {
        try {
            String[] set = event.get_eventother().split(",");
            MODE = Boolean.parseBoolean(set[0]);
            ItemUpdateTable.get().load();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.ItemUpdateSet JD-Core Version: 0.6.2
 */