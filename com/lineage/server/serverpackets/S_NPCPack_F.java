package com.lineage.server.serverpackets;

import com.lineage.config.ConfigAlt;
import com.lineage.server.datatables.SceneryTable;
import com.lineage.server.model.Instance.L1FieldObjectInstance;

public class S_NPCPack_F extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NPCPack_F(L1FieldObjectInstance npc) {
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
        String sceneryHtml = SceneryTable.get().get_sceneryHtml(npc.getId());
        if (sceneryHtml != null) {
            writeS(sceneryHtml);
        } else {
            writeS(null);
        }
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
 * com.lineage.server.serverpackets.S_NPCPack_F JD-Core Version: 0.6.2
 */