package com.lineage.server.serverpackets;

public class S_CharSynAck extends ServerBasePacket {
    public static final int SYN = 10;
    public static final int ACK = 64;
    private byte[] _byte = null;

    public S_CharSynAck(int type) {
        buildPacket(type);
    }

    private void buildPacket(int type) {
        // writeC(S_OPCODE_CHARSYNACK);
        writeC(type);
        if (type == SYN) {
            writeC(2);
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(8);
            writeC(0);
        } else {
            writeD(0);
            writeH(0);
        }
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
 * com.lineage.server.serverpackets.S_CharSynAck JD-Core Version: 0.6.2
 */