package com.lineage.server.serverpackets;

public class S_Light extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Light(int objid, int type) {
        buildPacket(objid, type);
    }

    private void buildPacket(int objid, int type) {
        writeC(S_CHANGE_LIGHT);
        writeD(objid);
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
 * com.lineage.server.serverpackets.S_Light JD-Core Version: 0.6.2
 */