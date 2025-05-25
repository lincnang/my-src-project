package com.lineage.server.world;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界人物暫存區(區分職業)<BR>
 * 黑妖
 *
 * @author dexc
 */
public class WorldDarkelf {
    private static final Log _log = LogFactory.getLog(WorldDarkelf.class);
    private static WorldDarkelf _instance;
    private final ConcurrentHashMap<Integer, L1PcInstance> _isDarkelf;
    private Collection<L1PcInstance> _allPlayer;

    private WorldDarkelf() {
        _isDarkelf = new ConcurrentHashMap<>();
    }

    public static WorldDarkelf get() {
        if (_instance == null) {
            _instance = new WorldDarkelf();
        }
        return _instance;
    }

    /**
     * 全部黑妖玩家
     *
     */
    public Collection<L1PcInstance> all() {
        try {
            final Collection<L1PcInstance> vs = _allPlayer;
            return (vs != null) ? vs : (_allPlayer = Collections.unmodifiableCollection(_isDarkelf.values()));
            // return Collections.unmodifiableCollection(_isCrown.values());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 黑妖玩家清單
     *
     */
    public ConcurrentHashMap<Integer, L1PcInstance> map() {
        return _isDarkelf;
    }

    /**
     * 加入黑妖玩家清單
     *
     */
    public void put(final Integer key, final L1PcInstance value) {
        try {
            _isDarkelf.put(key, value);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移出黑妖玩家清單
     *
     */
    public void remove(final Integer key) {
        try {
            _isDarkelf.remove(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
