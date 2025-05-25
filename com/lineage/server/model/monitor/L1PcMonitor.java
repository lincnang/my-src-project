package com.lineage.server.model.monitor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;

public abstract class L1PcMonitor implements Runnable {
    protected int _id;

    public L1PcMonitor(int oId) {
        _id = oId;
    }

    public final void run() {
        L1PcInstance pc = (L1PcInstance) World.get().findObject(_id);
        if ((pc == null) || (pc.getNetConnection() == null)) {
            return;
        }
        execTask(pc);
    }

    public abstract void execTask(L1PcInstance paramL1PcInstance);
}
