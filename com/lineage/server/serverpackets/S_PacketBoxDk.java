package com.lineage.server.serverpackets;

public class S_PacketBoxDk extends ServerBasePacket {
    public static final int LV1 = 1;
    public static final int LV2 = 2;
    public static final int LV3 = 3;
    private byte[] _byte = null;

    public S_PacketBoxDk(int lv) {
        writeC(S_EVENT);
        writeC(75);
        writeC(lv);
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
 * com.lineage.server.serverpackets.S_PacketBoxDk JD-Core Version: 0.6.2
 */