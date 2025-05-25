package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;

public abstract interface ServerGmCommandStorage {
    public abstract void create(L1PcInstance paramL1PcInstance, String paramString);

    public abstract void createTradeControl(int objId, String pcName);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.ServerGmCommandStorage JD-Core Version:
 * 0.6.2
 */