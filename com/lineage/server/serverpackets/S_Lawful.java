package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_Lawful extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Lawful(L1PcInstance pc) {
        buildPacket(pc);
    }

    public S_Lawful(int objid) {
        writeC(S_CHANGE_ALIGNMENT);
        writeD(objid);
        writeH(-32768);
        writeH(-32768);
        writeH(-32768);
    }

    private void buildPacket(L1PcInstance pc) {
        writeC(S_CHANGE_ALIGNMENT);
        writeD(pc.getId());
        writeH(pc.getLawful());
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
 * com.lineage.server.serverpackets.S_Lawful JD-Core Version: 0.6.2
 */