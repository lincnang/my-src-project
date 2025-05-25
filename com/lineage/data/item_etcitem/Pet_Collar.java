package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;

public class Pet_Collar extends ItemExecutor {
    public static ItemExecutor get() {
        return new Pet_Collar();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.getInventory().checkItem(41160)) {
            if (withdrawPet(pc, item.getId())) {
                pc.getInventory().consumeItem(41160, 1L);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }

    private boolean withdrawPet(L1PcInstance pc, int itemObjectId) {
        if (!pc.getMap().isTakePets()) {
            pc.sendPackets(new S_ServerMessage(563));
            return false;
        }
        int petCost = 0;
        int petCount = 0;
        int divisor = 6;
        Object[] petList = pc.getPetList().values().toArray();
        /*
         * if (petList.length > 2) { pc.sendPackets(new S_ServerMessage(489));
         * return false; }
         */
        for (Object pet : petList) {
            if (((pet instanceof L1PetInstance)) && (((L1PetInstance) pet).getItemObjId() == itemObjectId)) {
                return false;
            }
            petCost += ((L1NpcInstance) pet).getPetcost();
        }
        int charisma = pc.getCha();
        if (pc.isCrown()) {
            charisma += 6;
        } else if (pc.isElf()) {
            charisma += 12;
        } else if (pc.isWizard()) {
            charisma += 6;
        } else if (pc.isDarkelf()) {
            charisma += 6;
        } else if (pc.isDragonKnight()) {
            charisma += 6;
        } else if (pc.isIllusionist()) {
            charisma += 6;
        } else if (pc.isWarrior()) {
            charisma += 6;
        }
        /*
         * charisma -= petCost; petCount = charisma / 6; if (petCount <= 0) {
         * pc.sendPackets(new S_ServerMessage(489)); return false; }
         */
        L1Pet l1pet = PetReading.get().getTemplate(itemObjectId);
        /*
         * if (l1pet != null) { L1Npc npcTemp =
         * NpcTable.get().getTemplate(l1pet.get_npcid()); L1PetInstance pet =
         * new L1PetInstance(npcTemp, pc, l1pet); pet.setPetcost(6); }
         */
        if (l1pet != null) {
            final int npcId = l1pet.get_npcid();
            charisma -= petCost;
            if ((npcId == 45313) || (npcId == 45710 // 虎男、真虎男
            ) || (npcId == 45711) || (npcId == 45712)) { // 高麗幼犬、高麗犬
                divisor = 12;
            } else {
                divisor = 6;
            }
            petCount = charisma / divisor;
            if (petCount <= 0) {
                // 489：你無法一次控制那麼多寵物。
                pc.sendPackets(new S_ServerMessage(489)); // 引取多。
                return false;
            }
            final L1Npc npcTemp = NpcTable.get().getTemplate(npcId);
            final L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
            pet.setPetcost(divisor);
        }
        return true;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Pet_Collar JD-Core Version: 0.6.2
 */