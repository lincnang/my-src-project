package com.lineage.data.item_armor.set;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 套裝效果:變身名字編號
 */
public class EffectPolyDesc implements ArmorSetEffect {
    private int _polyDesc; // 變身名字編號

    /**
     * 套裝效果:變身名字編號
     */
    public EffectPolyDesc(final int polyDesc) {
        this._polyDesc = polyDesc;
    }

    @Override
    public void giveEffect(final L1PcInstance pc) {
    }

    @Override
    public void cancelEffect(final L1PcInstance pc) {
    }

    @Override
    public int get_mode() {
        return this._polyDesc;
    }
}
