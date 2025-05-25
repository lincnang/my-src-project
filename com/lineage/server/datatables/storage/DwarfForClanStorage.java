package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract interface DwarfForClanStorage {
    public abstract void load();

    public abstract CopyOnWriteArrayList<L1ItemInstance> loadItems(String paramString);

    public abstract void delUserItems(String paramString);

    public abstract boolean getUserItems(String paramString, int paramInt1, int paramInt2);

    public abstract void insertItem(String paramString, L1ItemInstance paramL1ItemInstance);

    public abstract void updateItem(L1ItemInstance paramL1ItemInstance);

    public abstract void deleteItem(String paramString, L1ItemInstance paramL1ItemInstance);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.DwarfForClanStorage JD-Core Version:
 * 0.6.2
 */