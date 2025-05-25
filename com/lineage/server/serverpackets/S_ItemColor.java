package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

public class S_ItemColor extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ItemColor(L1ItemInstance item) {
        if (item == null) {
            return;
        }
        buildPacket(item);
    }

    public S_ItemColor(L1ItemInstance item, int id) {
        writeC(S_CHANGE_ITEM_BLESS);
        writeD(item.getId());
        writeC(id);
    }

    private void buildPacket(L1ItemInstance item) {
        writeC(S_CHANGE_ITEM_BLESS);
        writeD(item.getId());
        writeC(item.getBless());
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
 * com.lineage.server.serverpackets.S_ItemColor JD-Core Version: 0.6.2
 */