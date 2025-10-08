package com.lineage.server.world;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1TrapInstance;
import com.lineage.server.model.L1Location;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界陷阱暫存區<BR>
 *
 * @author dexc
 */
public class WorldTrap {
    private static final Log _log = LogFactory.getLog(WorldTrap.class);
    
    /**
     * 線程安全的單例 Holder
     */
    private static class Holder {
        private static final WorldTrap INSTANCE = new WorldTrap();
    }
    
    private final ConcurrentHashMap<Integer, L1TrapInstance> _isTrap;

    private WorldTrap() {
        _isTrap = new ConcurrentHashMap<>();
    }

    /**
     * 獲取 WorldTrap 單例實例（線程安全）
     */
    public static WorldTrap get() {
        return Holder.INSTANCE;
    }

    /**
     * NPC清單（線程安全）
     *
     */
    public ConcurrentHashMap<Integer, L1TrapInstance> map() {
        return _isTrap;
    }

    /**
     * 加入NPC清單
     *
     */
    public void put(final Integer key, final L1TrapInstance value) {
        try {
            _isTrap.put(key, value);
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
            _isTrap.remove(key);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 重置所有陷阱
     */
    public void resetAllTraps() {
        for (final Object iter : _isTrap.values().toArray()) {
            final L1TrapInstance trap = (L1TrapInstance) iter;
            trap.resetLocation();
            trap.enableTrap();
        }
    }

    /**
     * 踩到陷阱的處理
     *
     */
    public void onPlayerMoved(final L1PcInstance pc) {
        final L1Location loc = pc.getLocation();
        for (final Object iter : _isTrap.values().toArray()) {
            final L1TrapInstance trap = (L1TrapInstance) iter;
            if (trap.isEnable() && loc.equals(trap.getLocation())) {
                trap.onTrod(pc);
                trap.disableTrap();
            }
        }
    }

    /**
     * 偵測陷阱的處理
     *
     */
    public void onDetection(final L1PcInstance pc) {
        final L1Location loc = pc.getLocation();
        for (final Object iter : _isTrap.values().toArray()) {
            final L1TrapInstance trap = (L1TrapInstance) iter;
            if (trap.isEnable() && loc.isInScreen(trap.getLocation())) {
                trap.onDetection(pc);
                trap.disableTrap();
            }
        }
    }
}
