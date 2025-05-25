package com.lineage.server.world;

import com.lineage.server.model.L1War;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 世界戰爭暫存區<BR>
 *
 * @author dexc
 */
public class WorldWar {
    private static final Log _log = LogFactory.getLog(WorldWar.class);
    private static WorldWar _instance;
    private final CopyOnWriteArrayList<L1War> _allWars;
    private List<L1War> _allWarList;

    private WorldWar() {
        this._allWars = new CopyOnWriteArrayList<>(); // 全部戰爭
    }

    public static WorldWar get() {
        if (_instance == null) {
            _instance = new WorldWar();
        }
        return _instance;
    }

    /**
     * 加入戰爭清單
     *
     */
    public void addWar(final L1War war) {
        try {
            if (!this._allWars.contains(war)) {
                this._allWars.add(war);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移出戰爭清單
     *
     */
    public void removeWar(final L1War war) {
        try {
            if (this._allWars.contains(war)) {
                this._allWars.remove(war);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 全部戰爭
     *
     */
    public List<L1War> getWarList() {
        try {
            final List<L1War> vs = this._allWarList;
            return (vs != null) ? vs : (this._allWarList = Collections.unmodifiableList(this._allWars));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 是否為對戰中血盟
     *
     */
    public boolean isWar(String clanname, String tgclanname) {
        try {
            for (final L1War war : _allWars) {
                final boolean isInWar = war.checkClanInWar(clanname);
                if (isInWar) {
                    final boolean isInWarTg = war.checkClanInWar(tgclanname);
                    if (isInWarTg) {
                        return true;
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }
}
