package com.lineage.server.templates;

public class L1Drop {
    int _mobId;
    int _itemId;
    int _min;
    int _max;
    int _chance;

    public L1Drop(int mobId, int itemId, int min, int max, int chance) {
        _mobId = mobId;
        _itemId = itemId;
        _min = min;
        _max = max;
        _chance = chance;
    }

    public int getChance() {
        return _chance;
    }

    public int getItemid() {
        return _itemId;
    }

    public int getMax() {
        return _max;
    }

    public int getMin() {
        return _min;
    }

    public int getMobid() {
        return _mobId;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Drop JD-Core Version: 0.6.2
 */