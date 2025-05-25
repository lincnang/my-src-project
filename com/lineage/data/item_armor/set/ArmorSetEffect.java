package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

public abstract interface ArmorSetEffect {
    public abstract void giveEffect(L1PcInstance paramL1PcInstance);

    public abstract void cancelEffect(L1PcInstance paramL1PcInstance);

    public abstract int get_mode();
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.set.ArmorSetEffect JD-Core Version: 0.6.2
 */