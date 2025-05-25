package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.WorldNpc;

public class C_BoardWrite extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while ((pc.isDead()) || (pc.isTeleport())) {
                return;
            }
            int objId = readD();
            L1NpcInstance npc = (L1NpcInstance) WorldNpc.get().map().get(objId);
            if (npc == null) {
                return;
            }
            String title = readS();
            if (title.length() > 16) {
                pc.sendPackets(new S_ServerMessage(166, "標題過長"));
                return;
            }
            String content = readS();
            if (title.length() > 1000) {
                pc.sendPackets(new S_ServerMessage(166, "內容過長"));
                return;
            }
            if (npc.ACTION != null) {
                pc.set_board_title(title);
                pc.set_board_content(content);
                npc.ACTION.action(pc, npc, "w", 0L);
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
 * com.lineage.server.clientpackets.C_BoardWrite JD-Core Version: 0.6.2
 */