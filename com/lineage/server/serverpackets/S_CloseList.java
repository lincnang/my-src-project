package com.lineage.server.serverpackets;

public class S_CloseList extends ServerBasePacket {
    private byte[] _byte = null;

    public S_CloseList(int objid) {
        writeC(S_HYPERTEXT);
        writeD(objid);
        writeS("");
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
