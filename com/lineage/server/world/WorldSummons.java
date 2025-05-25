package com.lineage.server.world;

import com.lineage.server.model.Instance.L1SummonInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界召喚獸暫存區<BR>
 *
 * @author dexc
 */
public class WorldSummons {
    private static final Log _log = LogFactory.getLog(WorldSummons.class);
    private static WorldSummons _instance;
    private final ConcurrentHashMap<Integer, L1SummonInstance> _isSummons;
    private Collection<L1SummonInstance> _allSummonValues;

    private WorldSummons() {
        _isSummons = new ConcurrentHashMap<>();
    }

    public static WorldSummons get() {
        if (_instance == null) {
            _instance = new WorldSummons();
        }
        return _instance;
    }

    /**
     * 全部召喚獸
     *
     */
    public Collection<L1SummonInstance> all() {
        try {
            final Collection<L1SummonInstance> vs = _allSummonValues;
            return (vs != null) ? vs : (_allSummonValues = Collections.unmodifiableCollection(_isSummons.values()));
            // return Collections.unmodifiableCollection(_isCrown.values());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 召喚獸清單
     *
     */
    public ConcurrentHashMap<Integer, L1SummonInstance> map() {
        return _isSummons;
    }

    /**
     * 加入召喚獸清單
     *
     */
    public void put(final Integer key, final L1SummonInstance value) {
        try {
            _isSummons.put(key, value);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移出召喚獸清單
     *
     */
    public void remove(final Integer key) {
        try {
            _isSummons.remove(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
