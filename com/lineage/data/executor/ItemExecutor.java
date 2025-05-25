package com.lineage.data.executor;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.BinaryOutputStream;

public abstract class ItemExecutor {
    private String[] as;

    public abstract void execute(int[] paramArrayOfInt, L1PcInstance paramL1PcInstance, L1ItemInstance paramL1ItemInstance);

    public String[] get_set() {
        return as;
    }

    public void set_set(String[] set) {
        as = set;
    }

    public BinaryOutputStream itemStatus(L1ItemInstance item) {
        return null;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.executor.ItemExecutor JD-Core Version: 0.6.2
 */