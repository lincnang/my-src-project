package com.lineage.server.serverpackets;

public class S_Fishing extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Fishing(int objectId, int motionNum, int x, int y) {
        buildPacket(objectId, motionNum, x, y);
    }

    private void buildPacket(int objectId, int motionNum, int x, int y) {
        writeC(S_ACTION);
        writeD(objectId);
        writeC(motionNum);
        writeH(x);
        writeH(y);
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
 * com.lineage.server.serverpackets.S_Fishing JD-Core Version: 0.6.2
 */