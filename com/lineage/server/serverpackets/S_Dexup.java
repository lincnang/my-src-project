package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_Dexup extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Dexup(L1PcInstance pc, int type, int time) {
        writeC(S_MAGE_DEXTERITY);
        writeH(time);
        writeC(pc.getDex());
        writeC(type);
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
 * com.lineage.server.serverpackets.S_Dexup JD-Core Version: 0.6.2
 */