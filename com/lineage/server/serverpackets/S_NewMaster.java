package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

public class S_NewMaster extends ServerBasePacket {
    private byte[] _byte = null;

    public S_NewMaster(String name, L1NpcInstance npc) {
        buildPacket(name, npc);
    }

    public S_NewMaster(L1NpcInstance npc) {
        writeC(S_MASTER);
        writeD(npc.getId());
        writeS("");
    }

    private void buildPacket(String name, L1NpcInstance npc) {
        writeC(S_MASTER);
        writeD(npc.getId());
        writeS(name);
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
 * com.lineage.server.serverpackets.S_NewMaster JD-Core Version: 0.6.2
 */