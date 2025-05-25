package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_Exp extends ServerBasePacket {
    private byte[] _byte = null;

    public S_Exp(L1PcInstance pc) {
        writeC(S_EXP);//S_OPCODE_EXP
        writeC(pc.getLevel());
        writeExp(pc.getExp());
        writeC(1);
    }

    public S_Exp() {
        writeC(S_EXP);
        writeC(59);
        writeD(414931028);
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
 * com.lineage.server.serverpackets.S_Exp JD-Core Version: 0.6.2
 */