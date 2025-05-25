package com.lineage.server.datatables.storage;

import java.sql.Timestamp;

public abstract interface IpStorage {
    public abstract void load();

    public abstract void add(String paramString1, String paramString2);

    public abstract void remove(String paramString);

    public abstract void setUnbanTime(Timestamp time);

    public abstract void checktime(String key);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.IpStorage JD-Core Version: 0.6.2
 */