package com.lineage.server.serverpackets;

import java.io.IOException;

public class S_GameMallItemMoney extends ServerBasePacket {
    private byte[] _byte = null;

    public S_GameMallItemMoney(final long count) {
        writeC(S_VOICE_CHAT);
        writeC(37);
        writeD((int) count);
    }

    @Override
    public byte[] getContent() throws IOException {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }
}