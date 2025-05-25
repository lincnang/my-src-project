package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

public class Effect_MagicCritical_chance implements ArmorSetEffect {
    private final int _add;

    public Effect_MagicCritical_chance(int add) {
        _add = add;
    }

    public void giveEffect(L1PcInstance pc) {
        pc.addOriginalMagicCritical(_add);
    }

    public void cancelEffect(L1PcInstance pc) {
        pc.addOriginalMagicCritical(-_add);
    }

    public int get_mode() {
        return _add;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_armor.set.Effect_Magic_modifier_dmg JD-Core Version:
 * 0.6.2
 */