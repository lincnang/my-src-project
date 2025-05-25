package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

public class EffectStat_Con implements ArmorSetEffect {
    private final int _add;

    public EffectStat_Con(int add) {
        _add = add;
    }

    public void giveEffect(L1PcInstance pc) {
        pc.addCon(_add);
        pc.sendDetails();
    }

    public void cancelEffect(L1PcInstance pc) {
        pc.addCon(-_add);
        pc.sendDetails();
    }

    public int get_mode() {
        return _add;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.set.EffectStat_Con JD-Core Version: 0.6.2
 */