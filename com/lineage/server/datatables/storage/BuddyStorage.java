package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1BuddyTmp;

import java.util.ArrayList;

public abstract interface BuddyStorage {
    public abstract void load();

    public abstract ArrayList<L1BuddyTmp> userBuddy(int paramInt);

    public abstract void addBuddy(int paramInt1, int paramInt2, String paramString);

    public abstract void removeBuddy(int paramInt, String paramString);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.BuddyStorage JD-Core Version: 0.6.2
 */