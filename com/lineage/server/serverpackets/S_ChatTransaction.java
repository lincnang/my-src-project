package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class S_ChatTransaction extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ChatTransaction(L1PcInstance pc, String chat) {
        buildPacket(pc, chat);
    }

    public S_ChatTransaction(L1NpcInstance npc, String chat) {
        writeC(S_MESSAGE);
        writeC(12);
        writeS("[" + npc.getNameId() + "] " + chat);
    }

    private void buildPacket(L1PcInstance pc, String chat) {
        writeC(S_MESSAGE);
        writeC(12);
        writeS("[" + pc.getName() + "] " + chat);
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
 * com.lineage.server.serverpackets.S_ChatTransaction JD-Core Version: 0.6.2
 */