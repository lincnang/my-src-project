package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1SkinInstance;

public class S_NPCPack_Skin extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NPCPack_Skin(L1SkinInstance pet) {
        writeC(S_PUT_OBJECT);
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
        writeS(null);
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