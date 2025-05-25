package com.lineage.server.serverpackets;

import com.lineage.server.datatables.T_RankTable;

import java.io.IOException;

public class S_RankedMine extends ServerBasePacket {
    private byte[] _byte = null;

    public S_RankedMine(final int level, final int clna, final int weapon, final int gold, final int consumption, final int kill) {
        writeC(S_EVENT);
        writeC(166);
        writeC(0);
        writeD(T_RankTable._basedTime);
        writeD(level);
        writeD(clna);
        writeD(weapon);
        writeD(gold);
        writeD(consumption);
        writeD(kill);
    }

    @Override
    public byte[] getContent() throws IOException {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }
}