package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1Gambling;

public abstract interface GamblingStorage {
    public abstract void load();

    public abstract L1Gambling getGambling(String paramString);

    public abstract L1Gambling getGambling(int paramInt);

    public abstract void add(L1Gambling paramL1Gambling);

    public abstract void updateGambling(int paramInt1, int paramInt2);

    public abstract int[] winCount(int paramInt);

    public abstract int maxId();
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.GamblingStorage JD-Core Version: 0.6.2
 */