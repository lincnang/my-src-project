package com.lineage.server.serverpackets;

public class S_War extends ServerBasePacket {
    private byte[] _byte = null;

    public S_War(int type, String clan_name1, String clan_name2) {
        buildPacket(type, clan_name1, clan_name2);
    }

    private void buildPacket(int type, String clan_name1, String clan_name2) {
        writeC(S_WAR);
        writeC(type);
        writeS(clan_name1);
        writeS(clan_name2);
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
 * com.lineage.server.serverpackets.S_War JD-Core Version: 0.6.2
 */