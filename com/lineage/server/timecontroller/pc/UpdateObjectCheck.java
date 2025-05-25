package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdateObjectCheck {
    private static final Log _log = LogFactory.getLog(UpdateObjectCheck.class);

    public static boolean check(L1PcInstance tgpc) {
        try {
            if (tgpc == null) {
                return false;
            }
            if (tgpc.isOutGame()) {
                return false;
            }
            if (tgpc.isTeleport()) {
                return false;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }
}