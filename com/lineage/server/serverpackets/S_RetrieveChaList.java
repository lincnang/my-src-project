package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.Iterator;

public class S_RetrieveChaList extends ServerBasePacket {
    public boolean NonValue = false;
    private byte[] _byte = null;
    private byte[] status = null;

    public S_RetrieveChaList(int objid, L1PcInstance pc) {
        if (pc.getInventory().getSize() < 180) {
            int size = pc.getDwarfForChaInventory().getSize();
            if (size > 0) {
                writeC(S_RETRIEVE_LIST);
                writeD(objid);
                writeH(size);
                writeC(18);
                for (Object itemObject : pc.getDwarfForChaInventory().getItems()) {
                    L1ItemInstance item = (L1ItemInstance) itemObject;
                    writeD(item.getId());
                    int i = item.getItem().getUseType();
                    if (i < 0) {
                        i = 0;
                    }
                    writeC(i);
                    writeH(item.get_gfxid());
                    writeC(item.getBless());
                    writeD((int) Math.min(item.getCount(), 2000000000L));
                    writeC(item.isIdentified() ? 1 : 0);
                    writeS(item.getViewName());
                    if (!item.isIdentified()) {
                        this.writeC(0);
                    } else {
                        status = item.getStatusBytes();
                        this.writeC(status.length);
                        for (byte b : status) {
                            this.writeC(b);
                        }
                    }
                }
                this.writeH(0x001e);
                this.writeD(0x00);
                this.writeH(0x00);
                this.writeH(0x08);
            } else {
                NonValue = true;
            }
        } else {
            pc.sendPackets(new S_ServerMessage(263));
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
 * com.lineage.server.serverpackets.S_RetrieveList JD-Core Version: 0.6.2
 */