package com.lineage.server.serverpackets;

public class S_PinkName extends ServerBasePacket {
    private byte[] _byte = null;

    public S_PinkName(int objecId, int time) {
        writeC(S_CRIMINAL);
        writeD(objecId);
        writeC(time);
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
 * com.lineage.server.serverpackets.S_PinkName JD-Core Version: 0.6.2
 */