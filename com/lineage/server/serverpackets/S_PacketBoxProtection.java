package com.lineage.server.serverpackets;

public class S_PacketBoxProtection extends ServerBasePacket {
    public static final int JUSTICE_L1 = 0;
    public static final int JUSTICE_L2 = 1;
    public static final int JUSTICE_L3 = 2;
    public static final int EVIL_L1 = 3;
    public static final int EVIL_L2 = 4;
    public static final int EVIL_L3 = 5;
    public static final int ENCOUNTER = 6;
    private byte[] _byte = null;

    public S_PacketBoxProtection(int model, int type) {
        writeC(S_EVENT);
        writeC(114);
        writeD(model);
        writeD(type);
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
 * com.lineage.server.serverpackets.S_PacketBoxProtection JD-Core Version: 0.6.2
 */