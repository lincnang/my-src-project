package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.util.Iterator;

public class Reactivating_Reel extends ItemExecutor {
    public static ItemExecutor get() {
        return new Reactivating_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int targObjId = data[0];
        L1Character target = (L1Character) World.get().findObject(targObjId);
        if (target == null) {
            return;
        }
        if (target.getId() == pc.getId()) {
            return;
        }
        if ((target.getCurrentHp() > 0) && (!target.isDead())) {
            return;
        }
        pc.getInventory().removeItem(item, 1L);
        if (target.isDead()) {
            Iterator<?> localIterator;
            if ((target instanceof L1PcInstance)) {
                L1PcInstance targetPc = (L1PcInstance) target;
                if (World.get().getVisiblePlayer(targetPc, 0).size() > 0) {
                    localIterator = World.get().getVisiblePlayer(targetPc, 0).iterator();
                    while (localIterator.hasNext()) {
                        L1PcInstance visiblePc = (L1PcInstance) localIterator.next();
                        if (!visiblePc.isDead()) {
                            pc.sendPackets(new S_ServerMessage(592));
                            return;
                        }
                    }
                }
                if (pc.getMap().isUseResurrection()) {
                    targetPc.setTempID(pc.getId());
                    if (item.getItem().getBless() != 0) {
                        targetPc.sendPackets(new S_Message_YN(321));
                    } else {
                        targetPc.sendPackets(new S_Message_YN(322));
                    }
                }
            } else if (((target instanceof L1NpcInstance)) && (!(target instanceof L1TowerInstance))) {
                L1NpcInstance npc = (L1NpcInstance) target;
                if (npc.getNpcTemplate().isCantResurrect()) {
                    return;
                }
                if (((npc instanceof L1PetInstance)) && (World.get().getVisiblePlayer(npc, 0).size() > 0)) {
                    for (L1PcInstance visiblePc : World.get().getVisiblePlayer(npc, 0)) {
                        if (!visiblePc.isDead()) {
                            pc.sendPackets(new S_ServerMessage(592));
                            return;
                        }
                    }
                }
                if (npc.isDead()) {
                    npc.resurrect(npc.getMaxHp() / 4);
                    npc.setResurrect(true);
                    npc.onNpcAI();
                }
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Reactivating_Reel JD-Core Version: 0.6.2
 */