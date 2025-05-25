package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.DungeonTable;
import com.lineage.server.model.Instance.L1PcInstance;

public class C_EnterPortal extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while ((pc.isDead()) || (pc.isTeleport())) {
                return;
            }
            int locx = readH();
            int locy = readH();
            if (pc.isTeleport()) {
                return;
            }
            DungeonTable.get().dg(locx, locy, pc.getMap().getId(), pc);
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
 * com.lineage.server.clientpackets.C_EnterPortal JD-Core Version: 0.6.2
 */