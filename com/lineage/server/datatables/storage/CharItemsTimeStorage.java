package com.lineage.server.datatables.storage;

import java.sql.Timestamp;

public abstract interface CharItemsTimeStorage {
    public abstract void load();

    public abstract void addTime(int paramInt, Timestamp paramTimestamp);

    public abstract void updateTime(int paramInt, Timestamp paramTimestamp);

    public abstract boolean isExistTimeData(int itemr_obj_id);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.CharItemsTimeStorage JD-Core Version:
 * 0.6.2
 */