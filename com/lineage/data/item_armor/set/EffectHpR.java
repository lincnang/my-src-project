package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

public class EffectHpR implements ArmorSetEffect {
    private final int _add;

    public EffectHpR(int add) {
        _add = add;
    }

    public void giveEffect(L1PcInstance pc) {
        pc.addHpr(_add);
    }

    public void cancelEffect(L1PcInstance pc) {
        pc.addHpr(-_add);
    }

    public int get_mode() {
        return _add;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.set.EffectHpR JD-Core Version: 0.6.2
 */