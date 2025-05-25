package com.lineage.server.serverpackets;

import java.io.IOException;

public class S_VipTime extends ServerBasePacket {
    private byte[] _byte = null;

    public S_VipTime(final int vipLevel, final long startTime, final long endTime) {
        writeC(S_VOICE_CHAT);
        writeC(72);
        writeD(vipLevel);
        writeExp(startTime / 1000L);
        writeExp(endTime / 1000L);
        writeH((int) ((endTime - startTime) / 1000L));
    }

    @Override
    public byte[] getContent() throws IOException {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }
}