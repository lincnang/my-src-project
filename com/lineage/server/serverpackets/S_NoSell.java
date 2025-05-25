package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

public class S_NoSell extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NoSell(L1NpcInstance npc) {
        buildPacket(npc);
    }

    private void buildPacket(L1NpcInstance npc) {
        writeC(S_HYPERTEXT);
        writeD(npc.getId());
        writeS("nosell");
        writeC(1);
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
 * com.lineage.server.serverpackets.S_NoSell JD-Core Version: 0.6.2
 */