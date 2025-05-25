package com.lineage.server.serverpackets;

public class S_Deposit extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Deposit(int objecId) {
        writeC(S_DEPOSIT);
        writeD(objecId);
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
 * com.lineage.server.serverpackets.S_Deposit JD-Core Version: 0.6.2
 */