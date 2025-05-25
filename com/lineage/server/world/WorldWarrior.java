package com.lineage.server.world;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class WorldWarrior {
    private static final Log _log = LogFactory.getLog(WorldWarrior.class);
    private static WorldWarrior _instance;
    private final ConcurrentHashMap<Integer, L1PcInstance> _isWarrior;
    private Collection<L1PcInstance> _allPlayer;

    private WorldWarrior() {
        _isWarrior = new ConcurrentHashMap<>();
    }

    public static WorldWarrior get() {
        if (_instance == null) {
            _instance = new WorldWarrior();
        }
        return _instance;
    }

    public Collection<L1PcInstance> all() {
        try {
            final Collection<L1PcInstance> vs = _allPlayer;
            return (vs != null) ? vs : (_allPlayer = Collections.unmodifiableCollection(_isWarrior.values()));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public ConcurrentHashMap<Integer, L1PcInstance> map() {
        return _isWarrior;
    }

    public void put(final Integer key, final L1PcInstance value) {
        try {
            _isWarrior.put(key, value);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void remove(final Integer key) {
        try {
            _isWarrior.remove(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
