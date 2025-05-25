package com.lineage.data.event.SoulQueen;

/**
 * 噬魂塔副本
 *
 * @author l1j-jp
 */
public class SoulTowerNpcModel {
    int _type;
    int _point_index;
    int _npcid;
    int _locx;
    int _locy;
    int _heading;

    public int getType() {
        return _type;
    }

    public void setType(final int type) {
        _type = type;
    }

    public int getPoint_index() {
        return _point_index;
    }

    public void setPoint_index(final int point_index) {
        _point_index = point_index;
    }

    public int getNpcid() {
        return _npcid;
    }

    public void setNpcid(final int npcid) {
        _npcid = npcid;
    }

    public int getLocx() {
        return _locx;
    }

    public void setLocx(final int locx) {
        _locx = locx;
    }

    public int getLocy() {
        return _locy;
    }

    public void setLocy(final int locy) {
        _locy = locy;
    }

    public int getHeading() {
        return _heading;
    }

    public void setHeading(final int heading) {
        _heading = heading;
    }
}
