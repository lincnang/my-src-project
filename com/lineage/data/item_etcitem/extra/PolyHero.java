package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PolyHero extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(PolyHero.class);
    private int _polyid = -1;
    private int _time = 1800;

    private PolyHero() {
    }

    public static ItemExecutor get() {
        return new PolyHero();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (_polyid == -1) {
            final int itemId = item.getItemId();
            _log.error("自定義變身卷軸 設定錯誤: " + itemId + " 沒有變身代號!");
            return;
        }
        final L1ItemInstance adena = pc.getInventory().checkItemX(40308, 50000);
        if (adena != null) {
            pc.getInventory().removeItem(adena, 50000);
        } else {
            pc.sendPackets(new S_ServerMessage(189));
            return;
        }
        L1PolyMorph.doPoly(pc, _polyid, _time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
    }

    @Override
    public void set_set(String[] set) {
        try {
            _polyid = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
        try {
            _time = Integer.parseInt(set[2]);
        } catch (Exception e) {
        }
    }
}
