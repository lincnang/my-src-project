package com.lineage.server.serverpackets;

public class S_ToGmMessage extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ToGmMessage(String info) {
        writeC(S_SAY_CODE);
        writeC(0);
        writeD(0);
        writeS("\\fY" + info);
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
 * com.lineage.server.serverpackets.S_ToGmMessage JD-Core Version: 0.6.2
 */