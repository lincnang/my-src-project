package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Town;

public abstract interface TownStorage {
    public abstract void load();

    public abstract L1Town[] getTownTableList();

    public abstract L1Town getTownTable(int paramInt);

    public abstract boolean isLeader(L1PcInstance paramL1PcInstance, int paramInt);

    public abstract void addSalesMoney(int paramInt1, int paramInt2);

    public abstract void updateTaxRate();

    public abstract void updateSalesMoneyYesterday();

    public abstract String totalContribution(int paramInt);

    public abstract void clearHomeTownID();

    public abstract int getPay(int paramInt);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.TownStorage JD-Core Version: 0.6.2
 */