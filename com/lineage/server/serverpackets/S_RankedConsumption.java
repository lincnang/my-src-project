package com.lineage.server.serverpackets;

import com.lineage.server.datatables.T_RankTable;

import java.io.IOException;
import java.util.ArrayList;

public class S_RankedConsumption extends ServerBasePacket {
    private byte[] _byte = null;

    public S_RankedConsumption(final ArrayList<String> wealthConsumptionRanked) {
        writeC(S_EVENT);
        writeC(166);
        writeC(5);
        writeD(T_RankTable._basedTime);
        writeD(wealthConsumptionRanked.size());
        for (int i = 0; i < wealthConsumptionRanked.size(); i++) {
            writeS(wealthConsumptionRanked.get(i));
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