package com.lineage.server.serverpackets;

public class S_PacketBoxItemLv extends ServerBasePacket {
    public static final int MSG_LEVEL_OVER = 12;
    private byte[] _byte = null;

    public S_PacketBoxItemLv(int minLv, int maxLv) {
        writeC(S_EVENT);
        writeC(12);
        writeC(minLv);
        writeC(maxLv);
    }

    public S_PacketBoxItemLv(int opid) {
        writeC(opid);
        writeC(12);
        writeC(10);
        writeC(1249);
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
 * com.lineage.server.serverpackets.S_PacketBoxItemLv JD-Core Version: 0.6.2
 */