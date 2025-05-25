package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

public class S_NpcChatGlobal extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NpcChatGlobal(L1NpcInstance npc, String chat) {
        buildPacket(npc, chat);
    }

    private void buildPacket(L1NpcInstance npc, String chat) {
        writeC(S_SAY_CODE);
        writeC(3);
        writeD(npc.getId());
        writeS("[" + npc.getNameId() + "] " + chat);
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_NpcChatGlobal JD-Core Version: 0.6.2
 */