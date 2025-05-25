package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

public class EffectStat_Cha implements ArmorSetEffect {
    private final int _add;

    public EffectStat_Cha(int add) {
        _add = add;
    }

    public void giveEffect(L1PcInstance pc) {
        pc.addCha(_add);
    }

    public void cancelEffect(L1PcInstance pc) {
        pc.addCha(-_add);
    }

    public int get_mode() {
        return _add;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.set.EffectStat_Cha JD-Core Version: 0.6.2
 */