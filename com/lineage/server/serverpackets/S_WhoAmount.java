package com.lineage.server.serverpackets;

public class S_WhoAmount extends ServerBasePacket {
    private byte[] _byte = null;

    public S_WhoAmount(String amount) {
        writeC(S_MESSAGE_CODE);
        writeH(81);
        writeC(1);
        writeS(amount);
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
 * com.lineage.server.serverpackets.S_WhoAmount JD-Core Version: 0.6.2
 */