package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1TrapInstance;

public class S_Trap extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Trap(L1TrapInstance trap, String name) {
        writeC(S_PUT_OBJECT);
        writeH(trap.getX());
        writeH(trap.getY());
        writeD(trap.getId());
        writeH(7);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeD(0);
        writeH(0);
        writeS(name);
        writeS(null);
        writeC(0);
        writeD(0);
        writeS(null);
        writeS(null);
        writeC(0);
        writeC(255);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(255);
        writeC(255);
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
 * com.lineage.server.serverpackets.S_Trap JD-Core Version: 0.6.2
 */