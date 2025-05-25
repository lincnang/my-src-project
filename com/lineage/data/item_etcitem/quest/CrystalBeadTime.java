package com.lineage.data.item_etcitem.quest;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.IllusionistLv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.L1SpawnUtil;

public class CrystalBeadTime extends ItemExecutor {
    public static ItemExecutor get() {
        return new CrystalBeadTime();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.getMapId() != 67) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (pc.getQuest().isStart(IllusionistLv45_1.QUEST.get_id())) {
            L1Location loc = pc.getLocation().randomLocation(5, false);
            pc.sendPacketsXR(new S_EffectLocation(loc, 7004), 8);
            L1SpawnUtil.spawnX(80140, loc, pc.get_showId());
        }
        pc.getInventory().removeItem(item, 1L);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.quest.CrystalBeadTime JD-Core Version: 0.6.2
 */