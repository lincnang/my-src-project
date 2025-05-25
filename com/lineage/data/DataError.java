package com.lineage.data;

import com.lineage.config.Config;
import org.apache.commons.logging.Log;

public class DataError {
    private static boolean _debug = Config.DEBUG;

    public static void isError(Log log, String string, Exception e) {
        if (_debug) {
            log.error(string, e);
        }
    }
}
