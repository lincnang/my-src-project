package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public abstract interface LogEnchantStorage {
    public abstract void failureEnchant(L1PcInstance paramL1PcInstance, L1ItemInstance paramL1ItemInstance);

    /**
     * 衝裝贖回系統
     *
     */
    public abstract void resetEnchant(L1PcInstance pc, L1ItemInstance item);
}
