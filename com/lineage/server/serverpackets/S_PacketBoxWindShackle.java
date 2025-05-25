package com.lineage.server.serverpackets;

public class S_PacketBoxWindShackle extends ServerBasePacket {
    public static final int WIND_SHACKLE = 44;
    private byte[] _byte = null;

    public S_PacketBoxWindShackle(int objectId, int time) {
        int buffTime = time >> 2;
        writeC(S_EVENT);
        writeC(44);
        writeD(objectId);
        writeH(buffTime);
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
 * com.lineage.server.serverpackets.S_PacketBoxWindShackle JD-Core Version:
 * 0.6.2
 */