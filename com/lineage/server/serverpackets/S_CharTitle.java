package com.lineage.server.serverpackets;

public class S_CharTitle extends ServerBasePacket {
    private byte[] _byte = null;

    public S_CharTitle(int objid, StringBuilder title) {
        writeC(S_TITLE);
        writeD(objid);
        writeS(title.toString());
    }

    public S_CharTitle(int objid) {
        writeC(S_TITLE);
        writeD(objid);
        writeS("");
    }

    public S_CharTitle(int objid, String title) {
        writeC(S_TITLE);
        writeD(objid);
        writeS(title);
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
 * com.lineage.server.serverpackets.S_CharTitle JD-Core Version: 0.6.2
 */