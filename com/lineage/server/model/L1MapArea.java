package com.lineage.server.model;

import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.types.Rectangle;

public class L1MapArea extends Rectangle {
    private L1Map _map = L1Map.newNull();

    public L1MapArea(int left, int top, int right, int bottom, int mapId) {
        super(left, top, right, bottom);
        _map = L1WorldMap.get().getMap((short) mapId);
    }

    public L1Map getMap() {
        return _map;
    }

    public void setMap(L1Map map) {
        _map = map;
    }

    public int getMapId() {
        return _map.getId();
    }

    public boolean contains(L1Location loc) {
        return (_map.getId() == loc.getMap().getId()) && (super.contains(loc));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1MapArea JD-Core Version: 0.6.2
 */