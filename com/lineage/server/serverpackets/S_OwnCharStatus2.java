package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_OwnCharStatus2 extends ServerBasePacket {
    private byte[] _byte = null;

    public S_OwnCharStatus2(L1PcInstance pc) {
        if (pc == null) {
            return;
        }
        buildPacket(pc);
    }

    public S_OwnCharStatus2(L1PcInstance pc, int str) {
        writeC(S_ABILITY_SCORES);
        writeC(str);
        writeC(pc.getInt());
        writeC(pc.getWis());
        writeC(pc.getDex());
        writeC(pc.getCon());
        writeC(pc.getCha());
        writeC(pc.getInventory().getWeight240());
    }

    private void buildPacket(L1PcInstance pc) {
        writeC(S_ABILITY_SCORES);
        writeC(pc.getStr());
        writeC(pc.getInt());
        writeC(pc.getWis());
        writeC(pc.getDex());
        writeC(pc.getCon());
        writeC(pc.getCha());
        writeC(pc.getInventory().getWeight240());
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
 * com.lineage.server.serverpackets.S_OwnCharStatus2 JD-Core Version: 0.6.2
 */