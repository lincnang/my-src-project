package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;

public class S_Resurrection extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Resurrection(L1PcInstance target, L1Character use, int type) {
        writeC(S_RESURRECT);
        writeD(target.getId());
        writeC(type);
        writeD(use.getId());
        writeD(target.getClassId());
    }

    public S_Resurrection(L1Character target, L1Character use, int type) {
        writeC(S_RESURRECT);
        writeD(target.getId());
        writeC(type);
        writeD(use.getId());
        writeD(target.getGfxId());
    }

    public S_Resurrection(L1PcInstance target, int opid, int type) {
        writeC(opid);
        writeD(target.getId());
        writeC(type);
        writeD(target.getId());
        writeD(target.getClassId());
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
 * com.lineage.server.serverpackets.S_Resurrection JD-Core Version: 0.6.2
 */