package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;

public abstract interface CharShiftingStorage {
    public abstract void newShifting(L1PcInstance paramL1PcInstance, int paramInt1, String paramString, int paramInt2, L1Item paramL1Item, L1ItemInstance paramL1ItemInstance, int paramInt3);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.CharShiftingStorage JD-Core Version:
 * 0.6.2
 */