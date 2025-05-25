package com.lineage.server.serverpackets;

public class S_Drawal extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Drawal(int objectId, long count) {
        writeC(S_WITHDRAW);
        writeD(objectId);
        writeD((int) Math.min(count, 2000000000L));
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
 * com.lineage.server.serverpackets.S_Drawal JD-Core Version: 0.6.2
 */