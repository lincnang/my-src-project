package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;

public class C_CnItem extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
