package com.lineage.server.model;

import com.lineage.config.Config;
import org.apache.commons.logging.Log;

public class ModelError {
    private static boolean _debug = Config.DEBUG;

    public static void isError(Log log, String string, Exception e) {
        if (_debug) {
            log.error(string, e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.ModelError JD-Core Version: 0.6.2
 */