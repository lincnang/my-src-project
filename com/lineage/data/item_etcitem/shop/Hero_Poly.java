package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;

public class Hero_Poly extends ItemExecutor {
    private int _PolyTime;
    private int _RemoveItem;

    public static ItemExecutor get() {
        return new Hero_Poly();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.hasSkillEffect(67)) {
            pc.removeSkillEffect(67);
        }
        if ((pc.get_sex() == 0) && (pc.isCrown())) {
            L1PolyMorph.doPoly(pc, 13715, this._PolyTime, 1);
        } else if ((pc.get_sex() == 1) && (pc.isCrown())) {
            L1PolyMorph.doPoly(pc, 13717, this._PolyTime, 1);
        } else if ((pc.get_sex() == 0) && (pc.isKnight())) {
            L1PolyMorph.doPoly(pc, 13719, this._PolyTime, 1);
        } else if ((pc.get_sex() == 1) && (pc.isKnight())) {
            L1PolyMorph.doPoly(pc, 13721, this._PolyTime, 1);
        } else if ((pc.get_sex() == 0) && (pc.isElf())) {
            L1PolyMorph.doPoly(pc, 13723, this._PolyTime, 1);
        } else if ((pc.get_sex() == 1) && (pc.isElf())) {
            L1PolyMorph.doPoly(pc, 13725, this._PolyTime, 1);
        } else if ((pc.get_sex() == 0) && (pc.isWizard())) {
            L1PolyMorph.doPoly(pc, 13727, this._PolyTime, 1);
        } else if ((pc.get_sex() == 1) && (pc.isWizard())) {
            L1PolyMorph.doPoly(pc, 13729, this._PolyTime, 1);
        } else if ((pc.get_sex() == 0) && (pc.isDarkelf())) {
            L1PolyMorph.doPoly(pc, 13731, this._PolyTime, 1);
        } else if ((pc.get_sex() == 1) && (pc.isDarkelf())) {
            L1PolyMorph.doPoly(pc, 13733, this._PolyTime, 1);
        } else if ((pc.get_sex() == 0) && (pc.isDragonKnight())) {
            L1PolyMorph.doPoly(pc, 13735, this._PolyTime, 1);
        } else if ((pc.get_sex() == 1) && (pc.isDragonKnight())) {
            L1PolyMorph.doPoly(pc, 13737, this._PolyTime, 1);
        } else if ((pc.get_sex() == 0) && (pc.isIllusionist())) {
            L1PolyMorph.doPoly(pc, 13739, this._PolyTime, 1);
        } else if ((pc.get_sex() == 1) && (pc.isIllusionist())) {
            L1PolyMorph.doPoly(pc, 13741, this._PolyTime, 1);
        } else if ((pc.get_sex() == 0) && (pc.isWarrior())) {
            L1PolyMorph.doPoly(pc, 13743, this._PolyTime, 1);
        } else if ((pc.get_sex() == 1) && (pc.isWarrior())) {
            L1PolyMorph.doPoly(pc, 13745, this._PolyTime, 1);
        }
        if (this._RemoveItem == 1) {
            pc.getInventory().removeItem(item, 1L);
        }
    }

    public void set_set(String[] set) {
        try {
            this._PolyTime = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            this._RemoveItem = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
    }
}