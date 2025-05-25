package com.lineage.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class IdFactoryNpc {
    private static final Log _log = LogFactory.getLog(IdFactoryNpc.class);
    private static IdFactoryNpc _instance;
    private Object _monitor;
    private AtomicInteger _nextId;

    public IdFactoryNpc() {
        try {
            _monitor = new Object();
            _nextId = new AtomicInteger(2000000000);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public static IdFactoryNpc get() {
        if (_instance == null) {
            _instance = new IdFactoryNpc();
        }
        return _instance;
    }

    public int nextId() {
        synchronized (_monitor) {
            return _nextId.getAndIncrement();
        }
    }

    public int maxId() {
        synchronized (_monitor) {
            return _nextId.get();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.IdFactoryNpc JD-Core Version: 0.6.2
 */