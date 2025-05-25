package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1BoxInstance;

/**
 * @author terry0412
 */
public class S_BoxPack extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * @param egg
     */
    public S_BoxPack(final L1BoxInstance box) {
        this.buildPacket(box);
    }

    private void buildPacket(final L1BoxInstance box) {
        this.writeC(S_PUT_OBJECT);
        this.writeH(box.getX());
        this.writeH(box.getY());
        this.writeD(box.getId());
        this.writeH(box.getGfxId());
        this.writeC(box.getOpenStatus()); // 寶箱開啟狀態
        this.writeC(0x00);
        this.writeC(0x00);
        this.writeC(0x00);
        this.writeD(0x00000001);
        this.writeH(0x0000);
        this.writeS(null);
        this.writeS(null);
        this.writeC(0x00); // 狀態
        this.writeD(0x00000000);
        this.writeS(null);
        this.writeS(null);
        this.writeC(0x00); // 物件分類
        this.writeC(0xff); // HP
        this.writeC(0x00);
        this.writeC(0x00);
        this.writeC(0x00);
        this.writeC(0xff);
        this.writeC(0xff);
        this.writeC(0x00); // added by terry0412
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
