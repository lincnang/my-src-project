package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_PetInventory;
import com.lineage.server.world.WorldPet;

public class C_PetMenu extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc == null)
                ;
            while ((pc.isGhost()) || (pc.isTeleport()) || (pc.isPrivateShop())) {
                return;
            }
            int petId = readD();
            L1PetInstance pet = WorldPet.get().get(Integer.valueOf(petId));
            if (pet == null) {
                return;
            }
            if (pc.getPetList().get(Integer.valueOf(petId)) == null) {
                return;
            }
            pc.sendPackets(new S_PetInventory(pet));
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
 * com.lineage.server.clientpackets.C_PetMenu JD-Core Version: 0.6.2
 */