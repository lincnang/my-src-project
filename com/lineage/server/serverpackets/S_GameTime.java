package com.lineage.server.serverpackets;

import com.lineage.server.model.gametime.L1GameTimeClock;

public class S_GameTime extends ServerBasePacket {
    public S_GameTime(int time) {
        buildPacket(time);
    }

    public S_GameTime() {
        int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
        buildPacket(time);
    }

    private void buildPacket(int time) {
        writeC(S_TIME);
        writeD(time);
    }

    public byte[] getContent() {
        return getBytes();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_GameTime JD-Core Version: 0.6.2
 */