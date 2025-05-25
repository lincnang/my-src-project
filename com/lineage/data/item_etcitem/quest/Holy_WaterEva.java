package com.lineage.data.item_etcitem.quest;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

public class Holy_WaterEva extends ItemExecutor {
    public static ItemExecutor get() {
        return new Holy_WaterEva();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if ((pc.hasSkillEffect(1013)) || (pc.hasSkillEffect(1014))) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (pc.hasSkillEffect(1014)) {
            pc.removeSkillEffect(1014);
        }
        pc.setSkillEffect(1015, 900000);
        pc.sendPacketsX8(new S_SkillSound(pc.getId(), 190));
        pc.sendPackets(new S_ServerMessage(1140));
        pc.getInventory().removeItem(item, 1L);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.quest.Holy_WaterEva JD-Core Version: 0.6.2
 */