package com.lineage.server.serverpackets;

public class S_Liquor extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Liquor(int objecId) {
        writeC(S_DRUNKEN);
        writeD(objecId);
        writeC(1);
    }

    public S_Liquor(int objecId, int type) {
        writeC(S_DRUNKEN);
        writeD(objecId);
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
 * com.lineage.server.serverpackets.S_Liquor JD-Core Version: 0.6.2
 */