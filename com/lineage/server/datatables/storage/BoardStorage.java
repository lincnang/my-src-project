package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Board;

import java.util.Map;

public abstract interface BoardStorage {
    public abstract void load();

    public abstract Map<Integer, L1Board> getBoardMap();

    public abstract L1Board[] getBoardTableList();

    public abstract L1Board getBoardTable(int paramInt);

    public abstract int getMaxId();

    public abstract void writeTopic(L1PcInstance paramL1PcInstance, String paramString1, String paramString2, String paramString3);

    public abstract void deleteTopic(int paramInt);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.BoardStorage JD-Core Version: 0.6.2
 */