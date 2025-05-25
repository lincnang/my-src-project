package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_ChatOut extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ChatOut(int objid) {
        buildPacket(objid);
    }

    public S_ChatOut(L1PcInstance pc) {
        buildPacket(pc.getId());
    }

    private void buildPacket(int objid) {
        writeD(objid);
        writeD(0);
        writeD(0);
        writeD(0);
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
 * com.lineage.server.serverpackets.S_ChatOut JD-Core Version: 0.6.2
 */