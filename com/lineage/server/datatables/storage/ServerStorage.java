package com.lineage.server.datatables.storage;

public abstract interface ServerStorage {
    public abstract void load();

    public abstract int minId();

    public abstract int maxId();

    public abstract void isStop();
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.ServerStorage JD-Core Version: 0.6.2
 */