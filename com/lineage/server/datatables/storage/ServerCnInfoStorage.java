package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;

public abstract interface ServerCnInfoStorage {
    public abstract void create(L1PcInstance pc, L1Item itemtmp, long count, boolean mode);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.ServerCnInfoStorage JD-Core Version:
 * 0.6.2
 */