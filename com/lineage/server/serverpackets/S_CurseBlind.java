package com.lineage.server.serverpackets;

public class S_CurseBlind extends ServerBasePacket {
    private byte[] _byte = null;

    public S_CurseBlind(int type) {
        buildPacket(type);
    }

    private void buildPacket(int type) {
        writeC(S_BLIND);
        writeH(type);
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
 * com.lineage.server.serverpackets.S_CurseBlind JD-Core Version: 0.6.2
 */