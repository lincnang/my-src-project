package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_EnterGame extends ServerBasePacket {
    private byte[] _byte = null;

    public S_EnterGame(L1PcInstance pc) {
        writeC(S_ENTER_WORLD_CHECK);
        writeC(3); // 語系
        if (pc.getClanid() > 0) {
            writeD(pc.getClanMemberId());
        } else {
            writeC(0x53);
            writeC(0x01);
            writeC(0x00);
            writeC(0x8b);
        }
        writeC(0x9c);
        writeC(0x1f);
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
 * Location: C:\Users\kenny\Desktop\伊薇380\ Qualified Name:
 * com.lineage.server.serverpackets.S_EnterGame JD-Core Version: 0.6.2
 */