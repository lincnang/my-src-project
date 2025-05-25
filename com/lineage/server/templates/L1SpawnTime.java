package com.lineage.server.templates;

import com.lineage.server.utils.TimePeriod;

import java.sql.Time;
import java.sql.Timestamp;

public class L1SpawnTime {
    private final int _spawnId;
    private final Time _timeStart;
    private final Time _timeEnd;
    private final TimePeriod _timePeriod;
    private final Timestamp _periodStart;
    private final Timestamp _periodEnd;
    // 怪物出生訊息 (可使用%s對應怪物名稱) by terry0412
    private final String _spawnMsg;
    // 每週出生日期 (使用西曆格式, 1=星期日, 空值則為全星期都套用) by terry0412
    private final String _weekDays;
    private boolean _isDeleteAtEndTime;

    private L1SpawnTime(L1SpawnTimeBuilder builder) {
        _spawnId = builder._spawnId;
        _timeStart = builder._timeStart;
        _timeEnd = builder._timeEnd;
        _timePeriod = new TimePeriod(_timeStart, _timeEnd);
        _periodStart = builder._periodStart;
        _periodEnd = builder._periodEnd;
        _isDeleteAtEndTime = builder._isDeleteAtEndTime;
        _spawnMsg = builder._spawnMsg;
        _weekDays = builder._weekDays;
    }

    public boolean isDeleteAtEndTime() {
        return _isDeleteAtEndTime;
    }

    public int getSpawnId() {
        return _spawnId;
    }

    public Time getTimeStart() {
        return _timeStart;
    }

    public Time getTimeEnd() {
        return _timeEnd;
    }

    public Timestamp getPeriodStart() {
        return _periodStart;
    }

    public Timestamp getPeriodEnd() {
        return _periodEnd;
    }

    public TimePeriod getTimePeriod() {
        return _timePeriod;
    }

    // 怪物出生訊息 (可使用%s對應怪物名稱) by terry0412
    public String getSpawnMsg() {
        return _spawnMsg;
    }

    // 每週出生日期 (使用西曆格式, 1=星期日, 空值則為全星期都套用) by terry0412
    public String getWeekDays() {
        return _weekDays;
    }

    public static class L1SpawnTimeBuilder {
        private final int _spawnId;
        private Time _timeStart;
        private Time _timeEnd;
        private Timestamp _periodStart;
        private Timestamp _periodEnd;
        private boolean _isDeleteAtEndTime;
        // 怪物出生訊息 (可使用%s對應怪物名稱) by terry0412
        private String _spawnMsg;
        // 每週出生日期 (使用西曆格式, 1=星期日, 空值則為全星期都套用) by terry0412
        private String _weekDays;

        public L1SpawnTimeBuilder(int spawnId) {
            _spawnId = spawnId;
        }

        public L1SpawnTime build() {
            return new L1SpawnTime(this);
        }

        public void setTimeStart(Time timeStart) {
            _timeStart = timeStart;
        }

        public void setTimeEnd(Time timeEnd) {
            _timeEnd = timeEnd;
        }

        public void setPeriodStart(Timestamp periodStart) {
            _periodStart = periodStart;
        }

        public void setPeriodEnd(Timestamp periodEnd) {
            _periodEnd = periodEnd;
        }

        public void setDeleteAtEndTime(boolean f) {
            _isDeleteAtEndTime = f;
        }

        // 怪物出生訊息 (可使用%s對應怪物名稱) by terry0412
        public void setSpawnMsg(final String msg) {
            _spawnMsg = msg;
        }

        // 每週出生日期 (使用西曆格式, 1=星期日, 空值則為全星期都套用) by terry0412
        public void setWeekDays(final String days) {
            _weekDays = days;
        }
    }
}
