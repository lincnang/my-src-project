package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1ItemStatus;

public class S_ItemStatus extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ItemStatus(L1ItemInstance item) {
        if (item == null) {
            return;
        }
        buildPacket(item);
    }

    public S_ItemStatus(L1ItemInstance item, long count) {
        writeC(S_CHANGE_ITEM_DESC_EX);
        writeD(item.getId());
        writeS(item.getNumberedViewName(count));
        int out_count = (int) Math.min(count, 2000000000L);
        writeD(out_count);
        if (!item.isIdentified()) {
            writeC(0);
        } else {
            L1ItemStatus itemInfo = new L1ItemStatus(item);
            byte[] status = itemInfo.getStatusBytes(false).getBytes();
            writeC(status.length);
            for (byte b : status) {
                writeC(b);
            }
        }
    }

    private void buildPacket(L1ItemInstance item) {
        writeC(S_CHANGE_ITEM_DESC_EX);
        writeD(item.getId());
        writeS(item.getViewName());
        int count = (int) Math.min(item.getCount(), 2000000000L);
        writeD(count);
        if (!item.isIdentified()) {
            writeC(0);
        } else {
            L1ItemStatus itemInfo = new L1ItemStatus(item);
            byte[] status = itemInfo.getStatusBytes(false).getBytes();
            writeC(status.length);
            for (byte b : status) {
                writeC(b);
            }
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
