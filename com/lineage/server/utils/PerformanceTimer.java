package com.lineage.server.utils;

import com.lineage.config.Config;

import java.util.Calendar;
import java.util.TimeZone;

public class PerformanceTimer {
    private long _begin;

    public PerformanceTimer() {
        _begin = System.currentTimeMillis();
    }

    public static Calendar getRealTime() {
        TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
        Calendar cal = Calendar.getInstance(_tz);
        return cal;
    }

    public void reset() {
        _begin = System.currentTimeMillis();
    }

    public long get() {
        return System.currentTimeMillis() - _begin;
    }
}
