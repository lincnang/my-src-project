package com.lineage.server.templates;

import java.sql.Timestamp;

public class L1Inn {
    private int _npcId;
    private int _roomNumber;
    private int _keyId;
    private int _lodgerId;
    private boolean _hall;
    private Timestamp _dueTime;

    public int getKeyId() {
        return _keyId;
    }

    public void setKeyId(int i) {
        _keyId = i;
    }

    public int getInnNpcId() {
        return _npcId;
    }

    public void setInnNpcId(int i) {
        _npcId = i;
    }

    public int getRoomNumber() {
        return _roomNumber;
    }

    public void setRoomNumber(int i) {
        _roomNumber = i;
    }

    public int getLodgerId() {
        return _lodgerId;
    }

    public void setLodgerId(int i) {
        _lodgerId = i;
    }

    public boolean isHall() {
        return _hall;
    }

    public void setHall(boolean hall) {
        _hall = hall;
    }

    public Timestamp getDueTime() {
        return _dueTime;
    }

    public void setDueTime(Timestamp i) {
        _dueTime = i;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Inn JD-Core Version: 0.6.2
 */