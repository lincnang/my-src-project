package com.lineage.server.serverpackets;

public class S_Weather extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Weather(int weather) {
        buildPacket(weather);
    }

    private void buildPacket(int weather) {
        writeC(S_WEATHER);
        writeC(weather);
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
 * com.lineage.server.serverpackets.S_Weather JD-Core Version: 0.6.2
 */