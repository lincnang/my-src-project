package com.lineage.server.world;

import com.lineage.server.model.Instance.L1NpcInstance;
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
 * 世界NPC暫存區<BR>
 *
 * @author dexc
 */
public class WorldNpc {
    private static final Log _log = LogFactory.getLog(WorldNpc.class);
    private static WorldNpc _instance;
    private final ConcurrentHashMap<Integer, L1NpcInstance> _isNpc;
    private Collection<L1NpcInstance> _allNpcValues;

    private WorldNpc() {
        _isNpc = new ConcurrentHashMap<>();
    }

    public static WorldNpc get() {
        if (_instance == null) {
            _instance = new WorldNpc();
        }
        return _instance;
    }

    /**
     * 全部NPC
     *
     */
    public Collection<L1NpcInstance> all() {
        try {
            final Collection<L1NpcInstance> vs = _allNpcValues;
            return (vs != null) ? vs : (_allNpcValues = Collections.unmodifiableCollection(_isNpc.values()));
            // return Collections.unmodifiableCollection(_isCrown.values());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * NPC清單
     *
     */
    public ConcurrentHashMap<Integer, L1NpcInstance> map() {
        return _isNpc;
    }

    /**
     * 加入NPC清單
     *
     */
    public void put(final Integer key, final L1NpcInstance value) {
        try {
            _isNpc.put(key, value);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移出NPC清單
     *
     */
    public void remove(final Integer key) {
        try {
            _isNpc.remove(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 找尋Npc Oid
     *
     */
    public L1NpcInstance find(final Integer key) {
        try {
            for (L1NpcInstance npc : _isNpc.values()) {
                if (npc.getNpcId() == key) {
                    return npc;
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 19格範圍NPC物件
     *
     * @param src 原始物件
     */
    public ArrayList<L1NpcInstance> getVisibleMob(final L1NpcInstance src) {
        final L1Map map = src.getMap();
        final Point pt = src.getLocation();
        final ArrayList<L1NpcInstance> result = new ArrayList<>();
        for (final L1NpcInstance element : all()) {
            // for (final L1NpcInstance element : this._isMob.values()) {
            if (element.equals(src)) {
                continue;
            }
            if (map != element.getMap()) {
                continue;
            }
            // 13格內NPC
            if (pt.isInScreen(element.getLocation())) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * 19格範圍內相同NPC物件數量
     *
     * @param src 原始物件
     */
    public int getVisibleCount(final L1NpcInstance src) {
        final L1Map map = src.getMap();
        final Point pt = src.getLocation();
        int count = 0;
        for (final L1NpcInstance element : all()) {
            // for (final L1NpcInstance element : this._isMob.values()) {
            if (element.equals(src)) {
                continue;
            }
            if (map != element.getMap()) {
                continue;
            }
            // 13格內NPC
            if (pt.isInScreen(element.getLocation())) {
                if (src.getNpcId() == element.getNpcId()) {
                    count++;
                }
            }
        }
        return count;
    }
}
