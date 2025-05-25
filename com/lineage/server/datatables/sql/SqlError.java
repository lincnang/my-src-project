package com.lineage.server.datatables.sql;

import com.lineage.config.Config;
import org.apache.commons.logging.Log;

public class SqlError {
    private static boolean _debug = Config.DEBUG;

    public static void isError(Log log, String string, Exception e) {
        if (_debug) {
            log.error(string, e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.SqlError JD-Core Version: 0.6.2
 */