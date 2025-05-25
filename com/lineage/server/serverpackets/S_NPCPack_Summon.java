package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;

/**
 * 物件封包 召喚獸
 *
 * @author kenny
 */
public class S_NPCPack_Summon extends ServerBasePacket {
    private static final int STATUS_POISON = 1;
    private byte[] _byte = null;

    public S_NPCPack_Summon(L1SummonInstance pet, L1PcInstance pc) {
        buildPacket(pet, pc, true);
    }

    public S_NPCPack_Summon(L1SummonInstance pet, L1PcInstance pc, boolean isCheckMaster) {
        buildPacket(pet, pc, isCheckMaster);
    }

    private void buildPacket(L1SummonInstance pet, L1PcInstance pc, boolean isCheckMaster) {
        writeC(S_PUT_OBJECT);
        writeH(pet.getX());
        writeH(pet.getY());
        writeD(pet.getId());
        writeH(pet.getGfxId());
        writeC(pet.getStatus());
        writeC(pet.getHeading());
        writeC(pet.getChaLightSize());
        writeC(pet.getMoveSpeed());
        writeD(0);
        writeH(0);
        writeS(pet.getNameId());
        writeS(pet.getTitle());
        int status = 0;
        if ((pet.getPoison() != null) && (pet.getPoison().getEffectId() == 1)) {
            status |= STATUS_POISON;
        }
        writeC(status);
        writeD(0);
        writeS(null);
        StringBuilder stringBuilder = new StringBuilder();
        if ((isCheckMaster) && (pet.isExsistMaster())) {
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
        if ((pet.getMaster() instanceof L1NpcInstance)) {
            L1NpcInstance npc = (L1NpcInstance) pet.getMaster();
            stringBuilder.append(npc.getNameId());
        }
        writeS(stringBuilder.toString());
        writeC(0);
        if (!(pet.getMaster() instanceof L1NpcInstance)) {
            if ((pet.getMaster() != null) && (pet.getMaster().getId() == pc.getId())) {
                int percent = pet.getMaxHp() != 0 ? 100 * pet.getCurrentHp() / pet.getMaxHp() : 100;
                writeC(percent);
            } else {
                writeC(255);
            }
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
 * com.lineage.server.serverpackets.S_NPCPack_Summon JD-Core Version: 0.6.2
 */