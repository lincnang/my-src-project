package com.lineage.server.model;

import com.lineage.server.model.Instance.L1PcInstance;

import java.util.ArrayList;

public final class L1Apprentice {
    private final ArrayList<L1PcInstance> totalList;
    private L1PcInstance _master;

    public L1Apprentice(L1PcInstance master, L1PcInstance[] mentorList) {
        _master = master;
        totalList = new ArrayList<L1PcInstance>(4);
        for (L1PcInstance apprentice : mentorList) {
            totalList.add(apprentice);
        }
    }

    public final L1PcInstance getMaster() {
        return _master;
    }

    public final void setMaster(L1PcInstance master) {
        _master = master;
    }

    public final ArrayList<L1PcInstance> getTotalList() {
        return totalList;
    }

    public final boolean addApprentice(L1PcInstance l1char) {
        if (checkSize()) {
            return totalList.add(l1char);
        }
        return false;
    }

    public final boolean checkSize() {
        return totalList.size() < 4;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1Apprentice JD-Core Version: 0.6.2
 */