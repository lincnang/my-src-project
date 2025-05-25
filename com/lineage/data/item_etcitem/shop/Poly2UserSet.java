package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.S_MatizCloudia;

public class Poly2UserSet extends ItemExecutor {
    //private static final Log _log = LogFactory.getLog(Poly2UserSet.class);
    private int _polyid = 0;
    private int _time = 1800;

    public static ItemExecutor get() {
        return new Poly2UserSet();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (_polyid == 0) {
            if (pc.isElf()) {
                _polyid = 12314;
            }
            if (pc.isDarkelf()) {
                _polyid = 12280;
            }
            if (pc.isKnight() || pc.isCrown() || pc.isWarrior() || pc.isDragonKnight()) {
                _polyid = 12283;
            }
            if (pc.isWizard() || pc.isIllusionist()) {
                _polyid = 12286;
            }
        }
        if (pc.cL == 0) {
            pc.sendPackets(new S_MatizCloudia(1, 2));
            pc.cL = 2;
        } else if (pc.cL == 1) {
            pc.sendPackets(new S_MatizCloudia(1, 3));
            pc.cL = 3;
            return;
        }
        pc.getInventory().removeItem(item, 1);
        L1PolyMorph.doPoly(pc, _polyid, _time, 1);
    }
}
