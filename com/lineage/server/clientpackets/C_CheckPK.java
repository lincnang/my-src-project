package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;

public class C_CheckPK extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            //String count = String.valueOf(pc.get_PKcount());
            //pc.sendPackets(new S_SystemMessage("您目前的PK次數為: " + count));
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
 * com.lineage.server.clientpackets.C_CheckPK JD-Core Version: 0.6.2
 */