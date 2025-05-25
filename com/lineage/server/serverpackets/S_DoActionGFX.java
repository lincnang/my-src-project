package com.lineage.server.serverpackets;

public class S_DoActionGFX extends ServerBasePacket {
    public static int ACTION_MAGIC = 22;
    private byte[] _byte = null;

    public S_DoActionGFX(int objectId, int actionId) {
        writeC(S_ACTION);
        writeD(objectId);
        writeC(actionId);
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
 * com.lineage.server.serverpackets.S_DoActionGFX JD-Core Version: 0.6.2
 */