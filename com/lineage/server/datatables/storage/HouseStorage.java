package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1House;

import java.util.Map;

public abstract interface HouseStorage {
    public abstract void load();

    public abstract Map<Integer, L1House> getHouseTableList();

    public abstract L1House getHouseTable(int paramInt);

    public abstract void updateHouse(L1House paramL1House);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.HouseStorage JD-Core Version: 0.6.2
 */