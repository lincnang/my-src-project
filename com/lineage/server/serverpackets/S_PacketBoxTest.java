package com.lineage.server.serverpackets;

public class S_PacketBoxTest extends ServerBasePacket {
    private byte[] _byte = null;

    public S_PacketBoxTest() {
        writeC(S_EVENT);
        writeC(82);
        writeC(150);
        writeC(0);
        writeC(0);
        writeC(0);
    }

    public S_PacketBoxTest(byte ocid, int time) {
        writeC(S_EVENT);
        writeC(ocid);
        writeC(time);
        writeC(0);
        writeC(0);
        writeC(0);
    }

    public S_PacketBoxTest(int type, int time) {
        writeC(S_EVENT);
        writeC(type);
        writeH(time);
        writeH(0);
    }

    public S_PacketBoxTest(int value, String[] clanName) {
        writeC(S_EVENT);
        writeC(value);
        for (int i = 0; i < value; i++) {
            writeS(clanName[i]);
        }
    }

    public S_PacketBoxTest(int time) {
        writeC(S_EVENT);
        writeC(79);
        writeC(2);
        writeS("TEMP");
        writeS("AASS");
    }

    public S_PacketBoxTest(byte[] bs) {
        for (byte outbs : bs) {
            writeC(outbs);
        }
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
 * com.lineage.server.serverpackets.S_PacketBoxTest JD-Core Version: 0.6.2
 */