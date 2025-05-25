package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StonePowerSet extends EventExecutor {  //src039
    private static final Log _log = LogFactory.getLog(StonePowerSet.class);
    public static boolean START = false;
    public static int HOLER = 0;
    public static int ARMORHOLE = 0;
    public static int WEAPONHOLE = 0;

    private StonePowerSet() {
    }

    public static EventExecutor get() {
        return new StonePowerSet();
    }

    @Override
    public void execute(final L1Event event) {
        try {
            START = true;
            final String[] set = event.get_eventother().split(",");
            HOLER = Integer.parseInt(set[0]);
            ARMORHOLE = Integer.parseInt(set[1]);
            if (ARMORHOLE > 5) {
                ARMORHOLE = 5;
            }
            WEAPONHOLE = Integer.parseInt(set[2]);
            if (WEAPONHOLE > 5) {
                WEAPONHOLE = 5;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
