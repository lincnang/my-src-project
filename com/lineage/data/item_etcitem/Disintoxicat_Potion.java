package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

public class Disintoxicat_Potion extends ItemExecutor {
    public static ItemExecutor get() {
        return new Disintoxicat_Potion();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
        } else {
            L1BuffUtil.cancelAbsoluteBarrier(pc);
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 192));
            pc.getInventory().removeItem(item, 1L);
            pc.curePoison();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Disintoxicat_Potion JD-Core Version: 0.6.2
 */