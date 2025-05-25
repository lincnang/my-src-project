package com.lineage.server.serverpackets;

public class S_DeleteCharOK extends ServerBasePacket {
    public static final int DELETE_CHAR_NOW = 5;
    public static final int DELETE_CHAR_AFTER_7DAYS = 81;
    private byte[] _byte = null;

    public S_DeleteCharOK(int type) {
        writeC(S_DELETE_CHARACTER_CHECK);
        writeC(type);
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
 * com.lineage.server.serverpackets.S_DeleteCharOK JD-Core Version: 0.6.2
 */