package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.WorldNpc;

public class C_Board extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            int objId = readD();
            L1NpcInstance npc = (L1NpcInstance) WorldNpc.get().map().get(Integer.valueOf(objId));
            if (npc == null) {
                return;
            }
            L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            if (npc.ACTION != null) {
                npc.ACTION.talk(pc, npc);
            }
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
 * com.lineage.server.clientpackets.C_Board JD-Core Version: 0.6.2
 */