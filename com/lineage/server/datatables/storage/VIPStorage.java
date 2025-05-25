package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;

import java.sql.Timestamp;
import java.util.Map;

public abstract interface VIPStorage {
    public abstract void load();

    public abstract Map<Integer, Timestamp> map();

    public abstract Timestamp getOther(L1PcInstance paramL1PcInstance);

    public abstract void storeOther(int paramInt, Timestamp paramTimestamp);

    public abstract void delOther(int paramInt);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.VIPStorage JD-Core Version: 0.6.2
 */