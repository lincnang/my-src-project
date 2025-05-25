package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.List;

/**
 * 物品清單
 *
 * @author daien
 */
public class S_PowerItemList extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 物品清單
     *
     */
    public S_PowerItemList(L1PcInstance pc, int objid, List<L1ItemInstance> items) {
        writeC(S_RETRIEVE_LIST);
        writeD(objid);
        writeH(items.size());
        //writeC(10);
        writeC(12);
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
        items.clear();
    }

    public S_PowerItemList(int objid, List<L1ItemInstance> items) {
        writeC(S_RETRIEVE_LIST);
        writeD(objid);
        writeH(items.size());
        writeC(12);
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
        items.clear();
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
