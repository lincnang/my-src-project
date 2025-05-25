package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Object;

public class S_RemoveObject extends ServerBasePacket {
    private byte[] _byte = null;

    public S_RemoveObject(L1Object obj) {
        writeC(S_REMOVE_OBJECT);
        writeD(obj.getId());
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
 * com.lineage.server.serverpackets.S_RemoveObject JD-Core Version: 0.6.2
 */