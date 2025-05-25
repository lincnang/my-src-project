package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.Teleportation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C_Teleport extends ClientBasePacket {
    @SuppressWarnings("unused")
    private static final Log _log = LogFactory.getLog(C_Teleport.class);

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            Teleportation.teleportation(pc);
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
 * com.lineage.server.clientpackets.C_Teleport JD-Core Version: 0.6.2
 */