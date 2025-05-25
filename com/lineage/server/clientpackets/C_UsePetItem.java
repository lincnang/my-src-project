package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.PetItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.serverpackets.S_PetEquipment;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1PetItem;
import com.lineage.server.world.WorldPet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * 要求使用寵物道具
 *
 * @author kenny
 */
public class C_UsePetItem extends ClientBasePacket {
    @SuppressWarnings("unused")
    private static final Log _log = LogFactory.getLog(C_UsePetItem.class);

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            int data = readC();
            int petId = readD();
            int listNo = readC();
            L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            L1PetInstance pet = WorldPet.get().get(Integer.valueOf(petId));
            if (pet == null) {
                return;
            }
            if (pc.getPetList().get(Integer.valueOf(petId)) == null) {
                return;
            }
            switch (pet.getNpcId()) {// 未進化的寵物
                case 45034:
                case 45039:
                case 45040:
                case 45042:
                case 45043:
                case 45044:
                case 45046:
                case 45047:
                case 45048:
                case 45049:
                case 45053:
                case 45054:
                case 45313:
                case 45711:
                case 46042:
                case 46044:
                case 91148:
                case 97022:
                case 97023:
                    return;
            }
            L1Inventory inventory = pet.getInventory();
            List<L1ItemInstance> itemList = inventory.getItems();
            if (itemList.size() <= 0) {
                return;
            }
            L1ItemInstance item = (L1ItemInstance) itemList.get(listNo);
            if (item == null) {
                return;
            }
            int itemId = item.getItemId();
            L1PetItem petItem = PetItemTable.get().getTemplate(itemId);
            if (petItem != null) {
                if (petItem.isWeapom()) {
                    pet.usePetWeapon(pet, item);
                    pc.sendPackets(new S_PetEquipment(data, pet, listNo)); // 裝備時更新寵物資訊
                } else {
                    pet.usePetArmor(pet, item);
                    pc.sendPackets(new S_PetEquipment(data, pet, listNo)); // 裝備時更新寵物資訊
                }
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_UsePetItem JD-Core Version: 0.6.2
 */