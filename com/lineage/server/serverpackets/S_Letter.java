package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

public class S_Letter extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Letter(L1ItemInstance item) {
    }

    public S_Letter() {
        writeD(0);
        writeH(615);
        writeH(0);
        writeS("123");
        writeS("456");
        writeByte(null);
        writeByte(null);
        writeC(0);
        writeS("info");
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
 * Location: C:\Users\kenny\Desktop\伊薇380\ Qualified Name:
 * com.lineage.server.serverpackets.S_Letter JD-Core Version: 0.6.2
 */