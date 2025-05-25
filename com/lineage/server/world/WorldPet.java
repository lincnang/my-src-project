package com.lineage.server.world;

import com.lineage.server.model.Instance.L1PetInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界寵物暫存區<BR>
 *
 * @author dexc
 */
public class WorldPet {
    private static final Log _log = LogFactory.getLog(WorldPet.class);
    private static WorldPet _instance;
    private final ConcurrentHashMap<Integer, L1PetInstance> _isPet;
    private Collection<L1PetInstance> _allPetValues;

    private WorldPet() {
        _isPet = new ConcurrentHashMap<>();
    }

    public static WorldPet get() {
        if (_instance == null) {
            _instance = new WorldPet();
        }
        return _instance;
    }

    /**
     * 全部寵物
     *
     */
    public Collection<L1PetInstance> all() {
        try {
            final Collection<L1PetInstance> vs = _allPetValues;
            return (vs != null) ? vs : (_allPetValues = Collections.unmodifiableCollection(_isPet.values()));
            // return Collections.unmodifiableCollection(_isCrown.values());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 寵物清單
     *
     */
    public ConcurrentHashMap<Integer, L1PetInstance> map() {
        return _isPet;
    }

    /**
     * 指定寵物數據
     *
     */
    public L1PetInstance get(final Integer key) {
        try {
            return _isPet.get(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 加入寵物清單
     *
     */
    public void put(final Integer key, final L1PetInstance value) {
        try {
            _isPet.put(key, value);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移出寵物清單
     *
     */
    public void remove(final Integer key) {
        try {
            _isPet.remove(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
