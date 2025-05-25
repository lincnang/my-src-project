package com.lineage.server.serverpackets;

public class S_HouseMap extends ServerBasePacket {
    private byte[] _byte = null;

    public S_HouseMap(int objectId, String house_number) {
        buildPacket(objectId, house_number);
    }

    private void buildPacket(int objectId, String house_number) {
        int number = Integer.valueOf(house_number).intValue();
        writeC(S_AGIT_MAP);
        writeD(objectId);
        writeD(number);
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
 * com.lineage.server.serverpackets.S_HouseMap JD-Core Version: 0.6.2
 */