package com.add.Tsai.Astrology;

/**
 * 星盤任務編號
 *
 * @author hero
 */
public class AstrologyQuest {
    /**
     * obj 編號
     */
    private final int _objId;
    /**
     * 星盤編號
     */
    private final int _astrologyId;
    /**
     * 任務數量
     */
    private final int _num;

    /**
     * @param objId       obj 編號
     * @param astrologyId 任務編號
     * @param num         任務數量
     */
    public AstrologyQuest(int objId, int astrologyId, int num) {
        _objId = objId;
        _astrologyId = astrologyId;
        _num = num;
    }

    public int getId() {
        return _objId;
    }

    public int getAstrologyId() {
        return _astrologyId;
    }

    public int getNum() {
        return _num;
    }
}