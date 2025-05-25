package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物品名單(個人倉庫)
 *
 * @author dexc
 */
public class S_RetrieveList extends ServerBasePacket {
    public boolean NonValue = false;
    private byte[] _byte = null;
    private byte[] status = null;

    /**
     * 物品名單(個人倉庫)
     *
     */
    public S_RetrieveList(final int objid, final L1PcInstance pc) {
        if (pc.getInventory().getSize() < 180) {
            final int size = pc.getDwarfInventory().getSize();
            if (size > 0) {
                this.writeC(S_RETRIEVE_LIST);
                this.writeD(objid);
                this.writeH(size);
                this.writeC(0x03); // 個人倉庫
                for (final Object itemObject : pc.getDwarfInventory().getItems()) {
                    final L1ItemInstance item = (L1ItemInstance) itemObject;
                    this.writeD(item.getId());
                    int i = item.getItem().getUseType();
                    if (i < 0) {
                        i = 0;
                    }
                    this.writeC(i);
                    this.writeH(item.get_gfxid());
                    this.writeC(item.getBless());
                    this.writeD((int) Math.min(item.getCount(), 2000000000));
                    this.writeC(item.isIdentified() ? 0x01 : 0x00);
                    this.writeS(item.getViewName());
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

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
