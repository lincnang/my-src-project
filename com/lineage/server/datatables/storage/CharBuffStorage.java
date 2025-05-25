package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;

public abstract interface CharBuffStorage {
    public abstract void load();

    public abstract void saveBuff(L1PcInstance paramL1PcInstance);

    public abstract void buff(L1PcInstance paramL1PcInstance);

    public abstract void deleteBuff(L1PcInstance paramL1PcInstance);

    public abstract void deleteBuff(int paramInt);

    public abstract void deleteBuff_skill(int skillid);

    public abstract void deleteBuff_skill_buff(int objid);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.CharBuffStorage JD-Core Version: 0.6.2
 */