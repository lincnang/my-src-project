package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 昇華系統
 *
 * @author Admin
 */
public class SubItemSet extends EventExecutor {

    private static final Log _log = LogFactory.getLog(SubItemSet.class);

    public static boolean START = false;

    public static int ARMORHOLE = 0;

    /**
     *
     */
    private SubItemSet() {
        // TODO Auto-generated constructor stub
    }

    public static EventExecutor get() {
        return new SubItemSet();
    }

    @Override
    public void execute(final L1Event event) {
        try {
            START = true;

            final String[] set = event.get_eventother().split(",");

            ARMORHOLE = Integer.parseInt(set[0]);
            if (ARMORHOLE > 100) {
                ARMORHOLE = 100;
            }

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
