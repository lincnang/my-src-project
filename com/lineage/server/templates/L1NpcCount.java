package com.lineage.server.templates;

public class L1NpcCount {
    private final int _id;
    private final int _count;

    public L1NpcCount(int id, int count) {
        _id = id;
        _count = count;
    }

    public int getId() {
        return _id;
    }

    public int getCount() {
        return _count;
    }

    public boolean isZero() {
        return (_id == 0) && (_count == 0);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1NpcCount JD-Core Version: 0.6.2
 */