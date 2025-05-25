package com.lineage.server.templates;

/**
 * 指定地圖指定時間傳走玩家
 *
 * @author dexc
 */
public class L1NpcTeleportOut {
    int id;
    int week;
    int hour;
    int minute;
    int second;
    int backlocx;
    int backlocy;
    int backmapid;
    String msg;

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getweek() {
        return this.week;
    }

    public void setweek(final int week) {
        this.week = week;
    }

    public int gethour() {
        return this.hour;
    }

    public void sethour(final int hour) {
        this.hour = hour;
    }

    public int getminute() {
        return this.minute;
    }

    public void setminute(final int minute) {
        this.minute = minute;
    }

    public int getsecond() {
        return this.second;
    }

    public void setsecond(final int second) {
        this.second = second;
    }

    public int getLocx() {
        return this.backlocx;
    }

    public void setLocx(final int locx) {
        this.backlocx = locx;
    }

    public int getLocy() {
        return this.backlocy;
    }

    public void setLocy(final int locy) {
        this.backlocy = locy;
    }

    public int getMapid() {
        return this.backmapid;
    }

    public void setMapid(final int mapid) {
        this.backmapid = mapid;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }
}
