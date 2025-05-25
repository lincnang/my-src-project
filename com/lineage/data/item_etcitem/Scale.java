package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Scale extends ItemExecutor {
    public static ItemExecutor get() {
        return new Scale();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        usePolyScale(pc, itemId);
        pc.getInventory().removeItem(item, 1L);
    }

    private void usePolyScale(L1PcInstance pc, int itemId) {
        int awakeSkillId = pc.getAwakeSkillId();
        if ((awakeSkillId == 185) || (awakeSkillId == 190) || (awakeSkillId == 195)) {
            pc.sendPackets(new S_ServerMessage(1384));
            return;
        }
        int polyId = 0;
        if (itemId == 41154) {
            polyId = 3101;
        } else if (itemId == 41155) {
            polyId = 3126;
        } else if (itemId == 41156) {
            polyId = 3888;
        } else if (itemId == 41157) {
            polyId = 3784;
        }
        L1PolyMorph.doPoly(pc, polyId, 600, 1);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Scale JD-Core Version: 0.6.2
 */