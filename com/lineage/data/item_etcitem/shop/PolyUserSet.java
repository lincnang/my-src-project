package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PolyUserSet extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(PolyUserSet.class);
    private int _polyid = -1;
    private int _time = 1800;

    public static ItemExecutor get() {
        return new PolyUserSet();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int awakeSkillId = pc.getAwakeSkillId();
        if ((awakeSkillId == 185) || (awakeSkillId == 190) || (awakeSkillId == 195)) {
            pc.sendPackets(new S_ServerMessage(1384));
            return;
        }
        if (_polyid == -1) {
            int itemId = item.getItemId();
            _log.error("自定義變身卷軸 設定錯誤: " + itemId + " 沒有變身代號!");
            return;
        }
        pc.getInventory().removeItem(item, 1L);
        L1PolyMorph.doPoly(pc, _polyid, _time, 1);
    }

    public void set_set(String[] set) {
        try {
            _polyid = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            _time = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.shop.PolyUserSet JD-Core Version: 0.6.2
 */