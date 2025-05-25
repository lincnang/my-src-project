package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;

public class C_Disconnect extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        over();
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_Disconnect JD-Core Version: 0.6.2
 */