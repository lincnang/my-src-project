package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillIconBlessOfEva;
import com.lineage.server.serverpackets.S_SkillSound;

public class Water_Essence extends ItemExecutor {
    public static ItemExecutor get() {
        return new Water_Essence();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        useBlessOfEva(pc, itemId);
        pc.getInventory().removeItem(item, 1L);
    }

    private void useBlessOfEva(L1PcInstance pc, int item_id) {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }
        L1BuffUtil.cancelAbsoluteBarrier(pc);
        int time = 0;
        if (item_id == 40032) {
            time = 1800;
        } else if (item_id == 40041) {
            time = 300;
        } else if (item_id == 41344) {
            time = 2100;
        } else if (item_id == 42024) {
            time = 7200;
        } else {
            return;
        }
        if (pc.hasSkillEffect(1003)) {
            int timeSec = pc.getSkillEffectTimeSec(1003);
            time += timeSec;
            if (time > 3600) {
                time = 3600;
            }
        }
        pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), time));
        pc.sendPacketsX8(new S_SkillSound(pc.getId(), 190));
        pc.setSkillEffect(1003, time * 1000);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Water_Essence JD-Core Version: 0.6.2
 */