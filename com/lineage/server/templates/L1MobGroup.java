package com.lineage.server.templates;

import com.lineage.server.utils.collections.Lists;

import java.util.Collections;
import java.util.List;

public class L1MobGroup {
    private final int _id;
    private final int _leaderId;
    private final List<L1NpcCount> _minions = Lists.newArrayList();
    private final boolean _isRemoveGroupIfLeaderDie;

    public L1MobGroup(int id, int leaderId, List<L1NpcCount> minions, boolean isRemoveGroupIfLeaderDie) {
        _id = id;
        _leaderId = leaderId;
        _minions.addAll(minions);
        _isRemoveGroupIfLeaderDie = isRemoveGroupIfLeaderDie;
    }

    public int getId() {
        return _id;
    }

    public int getLeaderId() {
        return _leaderId;
    }

    public List<L1NpcCount> getMinions() {
        return Collections.unmodifiableList(_minions);
    }

    public boolean isRemoveGroupIfLeaderDie() {
        return _isRemoveGroupIfLeaderDie;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1MobGroup JD-Core Version: 0.6.2
 */