package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;

/**
 * 物件封包 寵物
 *
 * @author kenny
 */
public class S_NPCPack_Pet extends ServerBasePacket {
    private static final int STATUS_POISON = 1;
    private byte[] _byte = null;

    public S_NPCPack_Pet(L1PetInstance pet, L1PcInstance pc) {
        buildPacket(pet, pc);
    }

    private void buildPacket(L1PetInstance pet, L1PcInstance pc) {
        writeC(S_PUT_OBJECT);
        writeH(pet.getX());
        writeH(pet.getY());
        writeD(pet.getId());
        writeH(pet.getGfxId());
        writeC(pet.getStatus());
        writeC(pet.getHeading());
        writeC(pet.getChaLightSize());
        writeC(pet.getMoveSpeed());
        writeD((int) pet.getExp());
        writeH(pet.getTempLawful());
        writeS(pet.getName());
        writeS(pet.getTitle());
        int status = 0;
        if ((pet.getPoison() != null) && (pet.getPoison().getEffectId() == 1)) {
            status |= STATUS_POISON;
        }
        writeC(status);
        writeD(0);
        writeS(null);
        StringBuilder stringBuilder = new StringBuilder();
        if (pet.getMaster() != null) {
            if ((pet.getMaster() instanceof L1PcInstance)) {
                L1PcInstance master = (L1PcInstance) pet.getMaster();
                if (master != null) {
                    if (master.get_other().get_color() != 0) {
                        stringBuilder.append(master.get_other().color());
                    }
                    stringBuilder.append(master.getViewName());
                }
            }
        } else {
            stringBuilder.append("");
        }
        writeS(stringBuilder.toString());
        writeC(0);
        if ((pet.getMaster() != null) && (pet.getMaster().getId() == pc.getId())) {
            writeC(100 * pet.getCurrentHp() / pet.getMaxHp());
        } else {
            writeC(255);
        }
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(255);
        writeC(255);
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_NPCPack_Pet JD-Core Version: 0.6.2
 */