package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_Teleport extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Teleport(L1PcInstance pc) {
        writeC(S_REQUEST_SUMMON);
        writeC(0x00);
        writeC(0x40);
        writeD(pc.getId());
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
 * com.lineage.server.serverpackets.S_Teleport JD-Core Version: 0.6.2
 */