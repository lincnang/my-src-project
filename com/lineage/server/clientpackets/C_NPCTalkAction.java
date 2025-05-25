package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.world.World;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

public abstract class C_NPCTalkAction extends ClientBasePacket {
    private static final String C_NPC_TALK_ACTION = "[C] C_NPCTalkAction";
    private static Logger _log = Logger.getLogger(C_NPCTalkAction.class.getName());

    public C_NPCTalkAction(byte[] decrypt, ClientExecutor client) throws FileNotFoundException, Exception {
        read(decrypt);
        int objectId = readD();
        String action = readS();
        L1PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        L1Object obj = World.get().findObject(objectId);
        if (obj == null) {
            _log.warning("object not found, oid " + objectId);
            return;
        }
        try {
            L1NpcInstance npc = (L1NpcInstance) obj;
            npc.onFinalAction(activeChar, action);
        } catch (ClassCastException ignored) {
        }
    }

    @Override
    public String getType() {
        return C_NPC_TALK_ACTION;
    }
}
