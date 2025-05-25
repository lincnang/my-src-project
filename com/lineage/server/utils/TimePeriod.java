package com.lineage.server.utils;

import com.lineage.server.model.gametime.L1GameTime;

import java.sql.Time;

public class TimePeriod {
    private final Time _timeStart;
    private final Time _timeEnd;

    public TimePeriod(Time timeStart, Time timeEnd) {
        if (timeStart.equals(timeEnd)) {
            throw new IllegalArgumentException("timeBegin must not equals timeEnd");
        }
        _timeStart = timeStart;
        _timeEnd = timeEnd;
    }

    private boolean includes(L1GameTime time, Time timeStart, Time timeEnd) {
        Time when = time.toTime();
        return (timeStart.compareTo(when) <= 0) && (timeEnd.compareTo(when) > 0);
    }

    public boolean includes(L1GameTime time) {
        return _timeStart.after(_timeEnd) ? true : includes(time, _timeEnd, _timeStart) ? false : includes(time, _timeStart, _timeEnd);
    }

    private final boolean includes(final Time time, final Time timeStart, final Time timeEnd) {
        return (timeStart.compareTo(time) <= 0) && (0 < timeEnd.compareTo(time));
    }

    public final boolean includes(final Time time) {
        return this._timeStart.after(this._timeEnd) ? !this.includes(time, this._timeEnd, this._timeStart) : this.includes(time, this._timeStart, this._timeEnd);
    }
}
