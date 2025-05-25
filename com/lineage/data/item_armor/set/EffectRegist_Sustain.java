package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

public class EffectRegist_Sustain implements ArmorSetEffect {
    private final int _add;

    public EffectRegist_Sustain(int add) {
        _add = add;
    }

    public void giveEffect(L1PcInstance pc) {
        pc.addRegistSustain(_add);
    }

    public void cancelEffect(L1PcInstance pc) {
        pc.addRegistSustain(-_add);
    }

    public int get_mode() {
        return _add;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.set.EffectRegist_Sustain JD-Core Version: 0.6.2
 */