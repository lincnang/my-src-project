package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1ShopS;

import java.util.HashMap;
import java.util.Map;

public abstract interface DwarfShopStorage {
    public abstract void load();

    public abstract int get_id();

    public abstract void set_id(int paramInt);

    public abstract HashMap<Integer, L1ShopS> allShopS();

    public abstract Map<Integer, L1ItemInstance> allItems();

    public abstract L1ShopS getShopS(int paramInt);

    public abstract HashMap<Integer, L1ShopS> getShopSMap(int paramInt);

    public abstract void insertItem(int paramInt, L1ItemInstance paramL1ItemInstance, L1ShopS paramL1ShopS);

    public abstract void updateShopS(L1ShopS paramL1ShopS);

    public abstract void deleteItem(int paramInt);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.DwarfShopStorage JD-Core Version: 0.6.2
 */