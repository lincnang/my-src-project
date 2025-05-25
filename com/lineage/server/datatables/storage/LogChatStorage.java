package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;

public abstract interface LogChatStorage {
    public abstract void isTarget(L1PcInstance paramL1PcInstance1, L1PcInstance paramL1PcInstance2, String paramString, int paramInt);

    public abstract void noTarget(L1PcInstance paramL1PcInstance, String paramString, int paramInt);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.LogChatStorage JD-Core Version: 0.6.2
 */