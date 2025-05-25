package com.lineage.server.serverpackets;

public class S_PacketBoxThirdSpeed extends ServerBasePacket {
    public static final int CAKE = 60;
    private byte[] _byte = null;

    public S_PacketBoxThirdSpeed(int time) {
        writeC(S_EVENT);
        writeC(60);
        writeC(time >> 2);
        writeC(8);
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
 * com.lineage.server.serverpackets.S_PacketBoxThirdSpeed JD-Core Version: 0.6.2
 */