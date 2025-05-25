package com.lineage.server.serverpackets;

import java.io.IOException;

public class S_OnlineGift extends ServerBasePacket {
    private byte[] _byte = null;

    public S_OnlineGift(final int giftIndex, final int time, final int type) {
        writeC(S_EVENT);
        writeC(150);
        writeC(type);
        writeC(giftIndex);
        writeD(time);
    }

    public S_OnlineGift(final int giftIndex) {
        writeC(S_EVENT);
        writeC(151);
        writeC(giftIndex);
        writeH(5555);
    }

    @Override
    public byte[] getContent() throws IOException {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }
}