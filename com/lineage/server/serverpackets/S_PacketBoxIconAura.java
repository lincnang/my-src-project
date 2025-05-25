package com.lineage.server.serverpackets;

public class S_PacketBoxIconAura extends ServerBasePacket {
    public static final int ICON_AURA = 22;
    public static final int ICON_OS = 125;
    public static final int ICON_E3 = 227;
    private byte[] _byte = null;

    public S_PacketBoxIconAura(int iconid, int time) {
        writeC(S_EVENT);
        writeC(ICON_AURA);
        writeC(iconid);
        writeH(time);
    }

    public S_PacketBoxIconAura(int type, int time, int value, int test) {
        writeC(S_EVENT);
        writeC(ICON_AURA);
        writeC(type);
        writeH(time);
        writeH(test);
        writeH(value);
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
 * com.lineage.server.serverpackets.S_PacketBoxIconAura JD-Core Version: 0.6.2
 */