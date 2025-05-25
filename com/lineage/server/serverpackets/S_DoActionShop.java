package com.lineage.server.serverpackets;

public class S_DoActionShop extends ServerBasePacket {
    public S_DoActionShop(int object, byte[] message) {
        writeC(S_ACTION);
        writeD(object);
        writeC(70);
        writeByte(message);
    }

    public S_DoActionShop(int object, String message) {
        writeC(S_ACTION);
        writeD(object);
        writeC(70);
        writeS(message);
    }

    public byte[] getContent() {
        return getBytes();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_DoActionShop JD-Core Version: 0.6.2
 */