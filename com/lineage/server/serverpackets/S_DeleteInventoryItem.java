package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

public class S_DeleteInventoryItem extends ServerBasePacket {
    private byte[] _byte = null;

    public S_DeleteInventoryItem(L1ItemInstance item) {
        writeC(S_REMOVE_INVENTORY);
        writeD(item.getId());
    }

    public S_DeleteInventoryItem(int objid) {
        writeC(S_REMOVE_INVENTORY);
        writeD(objid);
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
 * com.lineage.server.serverpackets.S_DeleteInventoryItem JD-Core Version: 0.6.2
 */