package com.lineage.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GameServerFullException extends Exception {
    private static final Log _log = LogFactory.getLog(GameServer.class);
    private static final long serialVersionUID = 1L;

    public GameServerFullException() {
    }

    public GameServerFullException(String string) {
        super(string);
        _log.error(string);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.GameServerFullException JD-Core Version: 0.6.2
 */