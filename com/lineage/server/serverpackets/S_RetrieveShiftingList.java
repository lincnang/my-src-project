package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.List;

public class S_RetrieveShiftingList extends ServerBasePacket {
    private byte[] _byte = null;

    public S_RetrieveShiftingList(L1PcInstance pc, List<L1ItemInstance> items) {
        writeC(S_RETRIEVE_LIST);
        writeD(pc.getId());
        writeH(items.size());
        writeC(2);
        for (L1ItemInstance item : items) {
            int itemid = item.getId();
            writeD(itemid);
            writeC(0);
            writeH(item.get_gfxid());
            writeC(item.getBless());
            writeD(1);
            writeC(item.isIdentified() ? 1 : 0);
            writeS(item.getViewName());
        }
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
 * com.lineage.server.serverpackets.S_RetrieveShiftingList JD-Core Version:
 * 0.6.2
 */