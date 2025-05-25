package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_HireSoldier extends ServerBasePacket {
    private byte[] _byte = null;

    public S_HireSoldier(L1PcInstance pc) {
        writeH(0);
        writeH(0);
        writeH(0);
        writeS(pc.getName());
        writeD(0);
        writeH(0);
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
 * com.lineage.server.serverpackets.S_HireSoldier JD-Core Version: 0.6.2
 */