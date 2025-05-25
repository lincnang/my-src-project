package com.lineage.server.serverpackets;

public class S_PacketBoxWisdomPotion extends ServerBasePacket {
    public static final int WISDOM_POTION = 57;
    private byte[] _byte = null;

    public S_PacketBoxWisdomPotion(int time) {
        writeC(S_EVENT);
        writeC(0x39);
        writeC(0x2c);
        writeH(time);
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
 * com.lineage.server.serverpackets.S_PacketBoxWisdomPotion JD-Core Version:
 * 0.6.2
 */