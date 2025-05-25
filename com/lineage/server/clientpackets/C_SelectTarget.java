package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldPet;

public class C_SelectTarget extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            int petId = readD();
            //int type = readC();
            int targetId = readD();
            L1PetInstance pet = WorldPet.get().get(Integer.valueOf(petId));
            if (pet == null) {
                return;
            }
            L1Character target = (L1Character) World.get().findObject(targetId);
            if (target == null) {
                return;
            }
            boolean isCheck = false;
            if ((target instanceof L1PcInstance)) {
                L1PcInstance tgpc = (L1PcInstance) target;
                if (tgpc.checkNonPvP(tgpc, pet)) {
                    return;
                }
                isCheck = true;
            } else if ((target instanceof L1DeInstance)) {
                isCheck = true;
            } else if ((target instanceof L1PetInstance)) {
                isCheck = true;
            } else if ((target instanceof L1SummonInstance)) {
                isCheck = true;
            } else {
                if ((target instanceof L1CnInstance)) {
                    return;
                }
                if ((target instanceof L1GamblingInstance)) {
                    return;
                }
                if ((target instanceof L1GamInstance)) {
                    return;
                }
                if ((target instanceof L1IllusoryInstance)) {
                    return;
                }
                if (target instanceof L1SkinInstance) {
                    over();
                    return;
                }
            }
            if ((isCheck) && (target.isSafetyZone())) {
                return;
            }
            pet.setMasterSelectTarget(target);
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
 * com.lineage.server.clientpackets.C_SelectTarget JD-Core Version: 0.6.2
 */