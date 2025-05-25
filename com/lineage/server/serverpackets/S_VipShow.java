package com.lineage.server.serverpackets;

import java.io.IOException;

public class S_VipShow extends ServerBasePacket {
    private byte[] _byte = null;

    public S_VipShow(final int objId, final int vipLevel) {
        writeC(S_VOICE_CHAT);
        writeC(73);
        writeD(objId);
        writeD(vipLevel);
    }

    @Override
    public byte[] getContent() throws IOException {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }
}