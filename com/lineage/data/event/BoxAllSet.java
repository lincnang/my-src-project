package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BoxAllSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(BoxAllSet.class);
    public static boolean START = false;

    private BoxAllSet() {
    }

    public static EventExecutor get() {
        return new BoxAllSet();
    }

    public void execute(L1Event event) {
        try {
            START = true;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
