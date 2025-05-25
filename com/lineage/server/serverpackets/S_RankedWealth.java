package com.lineage.server.serverpackets;

import com.lineage.server.datatables.T_RankTable;

import java.io.IOException;
import java.util.ArrayList;

public class S_RankedWealth extends ServerBasePacket {
    private byte[] _byte = null;

    public S_RankedWealth(final ArrayList<String> wealthGoldRanked) {
        writeC(S_EVENT);
        writeC(166);
        writeC(4);
        writeD(T_RankTable._basedTime);
        writeD(wealthGoldRanked.size());
        for (String s : wealthGoldRanked) {
            writeS(s);
        }
    }

    @Override
    public byte[] getContent() throws IOException {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }
}