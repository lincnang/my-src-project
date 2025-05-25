package com.lineage.server.serverpackets;

import com.lineage.config.ConfigAlt;
import com.lineage.server.model.Instance.L1MerchantInstance;

public class S_NPCPack_M extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NPCPack_M(L1MerchantInstance npc) {
        writeC(S_PUT_OBJECT);
        writeH(npc.getX());
        writeH(npc.getY());
        writeD(npc.getId());
        writeH(npc.getGfxId());
        writeC(npc.getStatus());
        writeC(npc.getHeading());
        writeC(npc.getChaLightSize());
        writeC(npc.getMoveSpeed());
        writeD((int) npc.getExp());
        writeH(npc.getTempLawful());
        if (!ConfigAlt.NPCid) {
            writeS(npc.getNameId());
        } else {
            writeS(npc.getNameId() + "[" + npc.getNpcId() + "]");
        }
        writeS(null);
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
 * com.lineage.server.serverpackets.S_NPCPack_M JD-Core Version: 0.6.2
 */