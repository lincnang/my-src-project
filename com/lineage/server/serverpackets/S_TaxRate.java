package com.lineage.server.serverpackets;

public class S_TaxRate extends ServerBasePacket {
    private byte[] _byte = null;

    public S_TaxRate(int objecId) {
        writeC(S_TAX);
        writeD(objecId);
        writeC(10);
        writeC(50);
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
 * com.lineage.server.serverpackets.S_TaxRate JD-Core Version: 0.6.2
 */