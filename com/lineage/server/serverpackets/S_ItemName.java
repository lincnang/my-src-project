package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

public class S_ItemName extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ItemName(L1ItemInstance item) {
        if (item == null) {
            return;
        }
        writeC(S_CHANGE_ITEM_DESC);
        writeD(item.getId());
        writeS(item.getViewName());
    }

    public S_ItemName(L1ItemInstance item, int id) {
        if (item == null) {
            return;
        }
        writeC(S_CHANGE_ITEM_DESC);
        writeD(item.getId());
        writeS(item.getViewName() + " ($" + id + ")");
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
 * com.lineage.server.serverpackets.S_ItemName JD-Core Version: 0.6.2
 */