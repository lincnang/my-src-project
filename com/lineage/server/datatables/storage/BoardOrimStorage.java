package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1Rank;

import java.util.List;

public abstract interface BoardOrimStorage {
    public abstract void load();

    public abstract List<L1Rank> getTotalList();

    public abstract int writeTopic(int paramInt, String paramString, List<String> paramList);

    public abstract void renewPcName(String paramString1, String paramString2);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.BoardOrimStorage JD-Core Version: 0.6.2
 */