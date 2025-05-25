package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;

public class C_Unkonwn extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            //_log.info("未處理封包: " + (decrypt[0] & 0xFF) + " (" + getNow_YMDHMS() + " 核心管理者紀錄用!)");
            //_log.info(PacketPrint.get().printData(decrypt, decrypt.length));
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_Unkonwn JD-Core Version: 0.6.2
 */