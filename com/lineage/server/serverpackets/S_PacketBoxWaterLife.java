package com.lineage.server.serverpackets;

public class S_PacketBoxWaterLife extends ServerBasePacket {
    private byte[] _byte = null;

    public S_PacketBoxWaterLife() {
        writeC(S_EVENT);
        writeC(59);
        writeH(0);
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
 * com.lineage.server.serverpackets.S_PacketBoxWaterLife JD-Core Version: 0.6.2
 */