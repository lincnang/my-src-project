package com.lineage.server.serverpackets;

public final class S_DragonDoor extends ServerBasePacket {
    private byte[] _byte = null;

    public S_DragonDoor(int itemobjid, int a, int b, int c, int d) {
        writeC(S_EVENT);
        writeC(102);
        writeD(itemobjid);
        writeC(a);
        writeC(b);
        writeC(c);
        writeC(d);
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_DragonDoor JD-Core Version: 0.6.2
 */