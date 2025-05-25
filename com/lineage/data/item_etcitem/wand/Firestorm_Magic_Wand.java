package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.world.World;

import java.util.ArrayList;

public class Firestorm_Magic_Wand extends ItemExecutor {
    public static ItemExecutor get() {
        return new Firestorm_Magic_Wand();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item.getChargeCount() == 0) {
            pc.getInventory().deleteItem(item);
            return;
        }
        pc.sendPacketsX8(new S_DoActionGFX(pc.getId(), 17));
        int useCount = item.getChargeCount() - 1;
        if (useCount > 0) {
            item.setChargeCount(useCount);
            pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
        } else {
            pc.getInventory().deleteItem(item);
        }
        ArrayList<L1Object> list = World.get().getVisibleObjects(pc, 5);
        if (list == null) {
            return;
        }
        for (L1Object object : list) {
            if (object instanceof L1MonsterInstance) {
                L1MonsterInstance dota = (L1MonsterInstance) object;
                if ((dota.getNpcId() >= 190098 && dota.getNpcId() <= 190110) || dota.getNpcId() == 190112) {// 中央寺廟
                    dota.broadcastPacketAll(new S_DoActionGFX(dota.getId(), 2));
                    dota.receiveDamage(pc, 250);
                }
            }
        }
        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 1819));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.wand.Lightning_Magic_Wand JD-Core Version:
 * 0.6.2
 */