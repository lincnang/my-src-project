package com.lineage.server.serverpackets;

public class S_PacketBoxLoc extends ServerBasePacket {
    public static final int SEND_LOC = 111;
    private byte[] _byte = null;

    public S_PacketBoxLoc(String name, int map, int x, int y, int zone) {
        writeC(S_EVENT);
        writeC(111);
        writeS(name);
        writeH(map);
        writeH(x);
        writeH(y);
        writeD(zone);
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
 * com.lineage.server.serverpackets.S_PacketBoxLoc JD-Core Version: 0.6.2
 */