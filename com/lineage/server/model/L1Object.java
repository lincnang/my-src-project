package com.lineage.server.model;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;

import java.io.Serializable;

public class L1Object implements Serializable {
    private static final long serialVersionUID = 1L;
    private final L1Location _loc = new L1Location();
    private int _id = 0;
    private int _showId = -1;

    public short getMapId() {
        return (short) _loc.getMap().getId();
    }

    /* 新手任務 */
    public int mo5912fu() {
        return this._loc.getMap().getId();
    }

    /* 新手任務 */
    public void mo5905cC(int mapId) {
        _loc.setMap(L1WorldMap.get().getMap(mapId));
    }

    public L1Map getMap() {
        return _loc.getMap();
    }

    public void setMap(short mapId) {
        _loc.setMap(L1WorldMap.get().getMap(mapId));
    }

    public void setMap(L1Map map) {
        if (map == null) {
            throw new NullPointerException();
        }
        _loc.setMap(map);
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public int getX() {
        return _loc.getX();
    }

    public void setX(int x) {
        _loc.setX(x);
    }

    public int getY() {
        return _loc.getY();
    }

    public void setY(int y) {
        _loc.setY(y);
    }

    public L1Location getLocation() {
        return _loc;
    }

    public void setLocation(L1Location loc) {
        _loc.setX(loc.getX());
        _loc.setY(loc.getY());
        _loc.setMap(loc.getMapId());
    }

    public void setLocation(int x, int y, int mapid) {
        _loc.setX(x);
        _loc.setY(y);
        _loc.setMap(mapid);
    }

    public double getLineDistance(L1Object obj) {
        return getLocation().getLineDistance(obj.getLocation());
    }

    public int getTileLineDistance(L1Object obj) {
        return getLocation().getTileLineDistance(obj.getLocation());
    }

    public int getTileDistance(L1Object obj) {
        return getLocation().getTileDistance(obj.getLocation());
    }

    public void onPerceive(L1PcInstance perceivedFrom) {
    }

    public void onAction(L1PcInstance actionFrom) {
    }

    public void onTalkAction(L1PcInstance talkFrom) {
    }

    public int get_showId() {
        return _showId;
    }

    public void set_showId(int showId) {
        _showId = showId;
    }

    /* 新手任務 */
    public int mo5914fw() {
        return this._id;
    }

    /* 新手任務 */
    public void mo5906cD(int id) {
        this._id = id;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1Object JD-Core Version: 0.6.2
 */