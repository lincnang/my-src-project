package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1Bank;

import java.util.Map;

public abstract interface AccountBankStorage {
    public abstract void load();

    public abstract L1Bank get(String paramString);

    public abstract Map<String, L1Bank> map();

    public abstract void create(String paramString, L1Bank paramL1Bank);

    public abstract void updatePass(String paramString1, String paramString2);

    public abstract void updateAdena(String paramString, long paramLong);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.AccountBankStorage JD-Core Version:
 * 0.6.2
 */