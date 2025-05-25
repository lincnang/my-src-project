package com.lineage.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigErrorException extends Exception {
    private static final Log _log = LogFactory.getLog(ConfigErrorException.class);
    private static final long serialVersionUID = 1L;

    public ConfigErrorException() {
    }

    public ConfigErrorException(String string) {
        _log.error(string);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.config.ConfigErrorException JD-Core Version: 0.6.2
 */