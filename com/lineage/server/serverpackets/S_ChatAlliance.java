package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_ChatAlliance extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ChatAlliance(L1PcInstance pc, String chat) {
        buildPacket(pc, chat);
    }

    private void buildPacket(L1PcInstance pc, String chat) {
        writeC(S_SAY);
		/*writeC(4);
		writeS("{{" + pc.getName() + "}} " + chat);*/
        writeC(13);
        writeD(pc.getId());
        writeS("\\fV[" + pc.getName() + "] " + chat);
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
