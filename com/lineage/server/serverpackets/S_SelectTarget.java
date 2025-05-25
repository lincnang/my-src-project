package com.lineage.server.serverpackets;

public class S_SelectTarget extends ServerBasePacket {
    private byte[] _byte = null;

    public S_SelectTarget(int ObjectId) {
        writeC(S_SLAVE_CONTROL);
        writeD(ObjectId);
        writeC(0);
        writeC(0);
        writeC(2);
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
 * com.lineage.server.serverpackets.S_SelectTarget JD-Core Version: 0.6.2
 */