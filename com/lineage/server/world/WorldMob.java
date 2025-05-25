package com.lineage.server.world;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界MOB暫存區<BR>
 *
 * @author dexc
 */
public class WorldMob {
    private static final Log _log = LogFactory.getLog(WorldMob.class);
    private static WorldMob _instance;
    private final ConcurrentHashMap<Integer, L1MonsterInstance> _isMob;
    private Collection<L1MonsterInstance> _allMobValues;

    private WorldMob() {
        _isMob = new ConcurrentHashMap<>();
    }

    public static WorldMob get() {
        if (_instance == null) {
            _instance = new WorldMob();
        }
        return _instance;
    }

    /**
     * 全部怪物
     *
     */
    public Collection<L1MonsterInstance> all() {
        try {
            final Collection<L1MonsterInstance> vs = _allMobValues;
            return (vs != null) ? vs : (_allMobValues = Collections.unmodifiableCollection(_isMob.values()));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 怪物清單
     *
     */
    public ConcurrentHashMap<Integer, L1MonsterInstance> map() {
        return _isMob;
    }

    /**
     * 加入怪物清單
     *
     */
    public void put(final Integer key, final L1MonsterInstance value) {
        try {
            _isMob.put(key, value);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移出怪物清單
     *
     */
    public void remove(final Integer key) {
        try {
            _isMob.remove(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 19格範圍NPC物件
     *
     * @param src 原始物件
     */
    public ArrayList<L1MonsterInstance> getVisibleMob(final L1MonsterInstance src) {
        final L1Map map = src.getMap();
        final Point pt = src.getLocation();
        final ArrayList<L1MonsterInstance> result = new ArrayList<>();
        for (final L1MonsterInstance element : all()) {
            // for (final L1MonsterInstance element : this._isMob.values()) {
            if (element.equals(src)) {
                continue;
            }
            if (map != element.getMap()) {
                continue;
            }
            // 19格內NPC
            if (pt.isInScreen(element.getLocation())) {
                result.add(element);
            }
        }
        return result;
    }

    public ArrayList<L1MonsterInstance> getVisibleMob(L1Object src) {
        L1Map map = src.getMap();
        Point pt = src.getLocation();
        ArrayList<L1MonsterInstance> result = new ArrayList<>();
        for (L1MonsterInstance element : all()) {
            if (!element.equals(src)) {
                if (map == element.getMap()) {
                    if (src.get_showId() == element.get_showId()) {
                        if (pt.isInScreen(element.getLocation())) {
                            result.add(element);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 19格範圍內相同NPC物件數量
     *
     * @param src 原始物件
     */
    public int getVisibleCount(final L1MonsterInstance src) {
        final L1Map map = src.getMap();
        final Point pt = src.getLocation();
        int count = 0;
        for (final L1MonsterInstance element : all()) {
            // for (final L1MonsterInstance element : this._isMob.values()) {
            if (element.equals(src)) {
                continue;
            }
            if (map != element.getMap()) {
                continue;
            }
            // 19格內NPC
            if (pt.isInScreen(element.getLocation())) {
                if (src.getNpcId() == element.getNpcId()) {
                    count++;
                }
            }
        }
        return count;
    }
}
