package com.lineage.server.world;

import com.lineage.server.model.Instance.L1DeInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class WorldDe {
    private static final Log _log = LogFactory.getLog(WorldDe.class);
    private static WorldDe _instance;
    private final ConcurrentHashMap<String, L1DeInstance> _isDe;
    private Collection<L1DeInstance> _allDe;

    private WorldDe() {
        _isDe = new ConcurrentHashMap<String, L1DeInstance>();
    }

    public static WorldDe get() {
        if (_instance == null) {
            _instance = new WorldDe();
        }
        return _instance;
    }

    public Collection<L1DeInstance> all() {
        try {
            Collection<L1DeInstance> vs = _allDe;
            return (vs != null) ? vs : (_allDe = Collections.unmodifiableCollection(_isDe.values()));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public ConcurrentHashMap<String, L1DeInstance> map() {
        return _isDe;
    }

    public L1DeInstance getDe(String key) {
        try {
            return (L1DeInstance) _isDe.get(key);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public void put(String key, L1DeInstance value) {
        try {
            _isDe.put(key, value);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void remove(String key) {
        try {
            _isDe.remove(key);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.world.WorldDe JD-Core Version: 0.6.2
 */