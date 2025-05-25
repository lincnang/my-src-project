package com.lineage.server.world;

import com.lineage.server.model.Instance.L1DollInstance2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界魔法娃娃暫存區<BR>
 *
 * @author dexc
 */
public class WorldDoll2 {
    private static final Log _log = LogFactory.getLog(WorldDoll2.class);
    private static WorldDoll2 _instance;
    private final ConcurrentHashMap<Integer, L1DollInstance2> _isDoll;
    private Collection<L1DollInstance2> _allDollValues;

    private WorldDoll2() {
        _isDoll = new ConcurrentHashMap<Integer, L1DollInstance2>();
    }

    public static WorldDoll2 get() {
        if (_instance == null) {
            _instance = new WorldDoll2();
        }
        return _instance;
    }

    /**
     * 全部魔法娃娃
     *
     */
    public Collection<L1DollInstance2> all() {
        try {
            final Collection<L1DollInstance2> vs = _allDollValues;
            return (vs != null) ? vs : (_allDollValues = Collections.unmodifiableCollection(_isDoll.values()));
            // return Collections.unmodifiableCollection(_isCrown.values());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 魔法娃娃清單
     *
     */
    public ConcurrentHashMap<Integer, L1DollInstance2> map() {
        return _isDoll;
    }

    /**
     * 指定魔法娃娃數據
     *
     */
    public L1DollInstance2 get(final Integer key) {
        try {
            return _isDoll.get(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 加入魔法娃娃清單
     *
     */
    public void put(final Integer key, final L1DollInstance2 value) {
        try {
            _isDoll.put(key, value);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移出魔法娃娃清單
     *
     */
    public void remove(final Integer key) {
        try {
            _isDoll.remove(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
