package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.List;

public class S_RetrievePowerItem extends ServerBasePacket {
    private byte[] _byte = null;

    public S_RetrievePowerItem(L1PcInstance pc, int objid, List<L1ItemInstance> items) {
        writeC(S_RETRIEVE_LIST);
        writeD(objid);
        writeH(items.size());
        writeC(10);
        for (L1ItemInstance item : items) {
            int itemobjid = item.getId();
            writeD(itemobjid);
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
 * com.lineage.server.serverpackets.S_RetrievePowerItem JD-Core Version: 0.6.2
 */