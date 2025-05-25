package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1HierarchInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件封包 - 招喚獸
 *
 * @author dexc
 */
public class S_NPCPack_Hierarch extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NPCPack_Hierarch(L1HierarchInstance pet) {
        writeC(87);
        writeH(pet.getX());
        writeH(pet.getY());
        writeD(pet.getId());
        writeH(pet.getGfxId());
        writeC(pet.getStatus());
        writeC(pet.getHeading());
        writeC(0);
        writeC(pet.getMoveSpeed());
        writeD(0);
        writeH(0);
        writeS(pet.getNameId());
        writeS(pet.getTitle());
        writeC(0);
        writeD(0);
        writeS(null);
        StringBuilder stringBuilder = new StringBuilder();
        if (pet.getMaster() != null) {
            if ((pet.getMaster() instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) pet.getMaster();
                if (pc.isProtector()) {//主人是守護者狀態
                    stringBuilder.append("**守護者**");
                } else {
                    stringBuilder.append(pc.getViewName());
                }
                // stringBuilder.append(pet.getMaster().getName());
            }
        } else if ((pet.getMaster() instanceof L1NpcInstance)) {
            L1NpcInstance npc = (L1NpcInstance) pet.getMaster();
            stringBuilder.append(npc.getNameId());
        }
        writeS(stringBuilder.toString());
        writeC(0);
        writeC(255);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(255);
        writeC(255);
    }

    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = getBytes();
        }
        return this._byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
