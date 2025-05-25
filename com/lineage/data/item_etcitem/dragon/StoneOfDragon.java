package com.lineage.data.item_etcitem.dragon;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

public class StoneOfDragon extends ItemExecutor {
    public static ItemExecutor get() {
        return new StoneOfDragon();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if ((pc.getMapId() >= 1005) && (pc.getMapId() <= 1023)) {
            if (!pc.hasSkillEffect(50373)) {
                pc.addHitup(3);
                pc.addDmgup(3);
                pc.addBowHitup(3);
                pc.addBowDmgup(3);
                pc.addSp(3);
                pc.sendPackets(new S_SPMR(pc));
                pc.setSkillEffect(50373, 3600000);
                pc.sendPackets(new S_InventoryIcon(2430, true, 1316, 1316, 3600));
                pc.getInventory().removeItem(item, 1L);
            } else {
                int time = pc.getSkillEffectTimeSec(50373);
                pc.sendPackets(new S_ServerMessage("龍之石 剩餘時間(秒):" + time));
            }
        } else {
            pc.sendPackets(new S_SystemMessage("只能在屠龍副本裡使用。"));
        }
    }
}