package com.lineage.server.serverpackets;

public class S_SellHouse extends ServerBasePacket {
    private byte[] _byte = null;

    public S_SellHouse(int objectId, String houseNumber) {
        buildPacket(objectId, houseNumber);
    }

    private void buildPacket(int objectId, String houseNumber) {
        writeC(S_HYPERTEXT_INPUT);
        writeD(objectId);
        writeD(0);
        writeD(100000);
        writeD(100000);
        writeD(2000000000);
        writeH(0);
        writeS("agsell");
        writeS("agsell " + houseNumber);
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
 * com.lineage.server.serverpackets.S_SellHouse JD-Core Version: 0.6.2
 */