package com.lineage.server.templates;

import java.sql.Timestamp;

public class L1Event {
    private int _eventid;
    private String _eventname;
    private String _eventclass;
    private boolean _eventstart;
    private String _eventother;
    private String _eventother2;
    private Timestamp _next_time; // 下一個判斷時間 by terry0412

    public int get_eventid() {
        return _eventid;
    }

    public void set_eventid(int eventid) {
        _eventid = eventid;
    }

    public String get_eventname() {
        return _eventname;
    }

    public void set_eventname(String eventname) {
        _eventname = eventname;
    }

    public String get_eventclass() {
        return _eventclass;
    }

    public void set_eventclass(String eventclass) {
        _eventclass = eventclass;
    }

    public boolean is_eventstart() {
        return _eventstart;
    }

    public void set_eventstart(boolean eventstart) {
        _eventstart = eventstart;
    }

    public String get_eventother() {
        return _eventother;
    }

    public void set_eventother(String eventother2) {
        _eventother = eventother2;
    }

    public String get_eventother2() {
        return _eventother2;
    }

    public void set_eventother2(String eventother2) {
        _eventother2 = eventother2;
    }

    /**
     * 下一個判斷時間 by terry0412
     *
     * @return the _next_time
     */
    public Timestamp get_next_time() {
        return this._next_time;
    }

    /**
     * 下一個判斷時間 by terry0412
     *
     * @param next_time the _next_time to set
     */
    public void set_next_time(final Timestamp next_time) {
        this._next_time = next_time;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Event JD-Core Version: 0.6.2
 */