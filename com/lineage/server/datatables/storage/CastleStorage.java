package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1Castle;

import java.util.Map;

public abstract interface CastleStorage {
    public abstract void load();

    public abstract Map<Integer, L1Castle> getCastleMap();

    public abstract L1Castle[] getCastleTableList();

    public abstract L1Castle getCastleTable(int paramInt);

    public abstract void updateCastle(L1Castle paramL1Castle);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.CastleStorage JD-Core Version: 0.6.2
 */