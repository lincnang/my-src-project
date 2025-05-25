package com.lineage.server.serverpackets;

public class S_PacketBoxSelect extends ServerBasePacket {
    public static final int LOGOUT = 42;
    private byte[] _byte = null;

    public S_PacketBoxSelect() {
        writeC(S_EVENT);
        writeC(42);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
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
 * com.lineage.server.serverpackets.S_PacketBoxSelect JD-Core Version: 0.6.2
 */