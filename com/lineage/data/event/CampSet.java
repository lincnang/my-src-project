package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.datatables.lock.CharacterC1Reading;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CampSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(CampSet.class);
    public static boolean CAMPSTART = false;

    public static EventExecutor get() {
        return new CampSet();
    }

    public void execute(L1Event event) {
        try {
            CAMPSTART = true;
            C1_Name_Table.get().load();
            C1_Name_Type_Table.get().load();
            CharacterC1Reading.get().load();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.CampSet JD-Core Version: 0.6.2
 */