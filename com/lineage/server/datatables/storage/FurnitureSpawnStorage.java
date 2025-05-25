package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1FurnitureInstance;

public abstract interface FurnitureSpawnStorage {
    public abstract void load();

    public abstract void insertFurniture(L1FurnitureInstance paramL1FurnitureInstance);

    public abstract void deleteFurniture(L1FurnitureInstance paramL1FurnitureInstance);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.FurnitureSpawnStorage JD-Core Version:
 * 0.6.2
 */