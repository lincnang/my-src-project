package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ItemBuffTable;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 道具狀態系統
 */
public class ItemBuffSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(ItemBuffSet.class);
    public static boolean START = false;

    public static EventExecutor get() {
        return new ItemBuffSet();
    }

    @Override
    public void execute(final L1Event event) {
        try {
            ItemBuffSet.START = true;
            ItemBuffTable.get(); // 道具狀態系統資料
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
