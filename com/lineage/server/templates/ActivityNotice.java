package com.lineage.server.templates;

/**
 * 活動
 *
 * @author dexc
 */
public class ActivityNotice {
    int id;
    String title;
    int locx;
    int locy;
    int mapid;
    int marterial;
    int marterial_count;
    int isShow;
    int week;
    int hour;
    int minute;
    int second;
    int endtime;
    int boss_spawnid;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getisShow() {
        return this.isShow;
    }

    public void setisShow(final int isShow) {
        this.isShow = isShow;
    }

    public int getLocx() {
        return this.locx;
    }

    public void setLocx(final int locx) {
        this.locx = locx;
    }

    public int getLocy() {
        return this.locy;
    }

    public void setLocy(final int locy) {
        this.locy = locy;
    }

    public int getMapid() {
        return this.mapid;
    }

    public void setMapid(final int mapid) {
        this.mapid = mapid;
    }

    public int getMarterial() {
        return this.marterial;
    }

    public void setMarterial(final int marterial) {
        this.marterial = marterial;
    }

    public int getMarterial_count() {
        return this.marterial_count;
    }

    public void setMarterial_count(final int marterial_count) {
        this.marterial_count = marterial_count;
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

    public int getendtime() {
        return this.endtime;
    }

    public void setendtime(final int endtime) {
        this.endtime = endtime;
    }

    public int getboss_spawnid() {
        return this.boss_spawnid;
    }

    public void setboss_spawnid(final int boss_spawnid) {
        this.boss_spawnid = boss_spawnid;
    }
}
