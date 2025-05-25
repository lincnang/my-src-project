package com.lineage.server.serverpackets;

/**
 * @author Roy
 * @category 魔法武器封包修訂
 */
public class S_PacketBoxWeapon extends ServerBasePacket {
    private byte[] _byte = null;

    public S_PacketBoxWeapon(final int gfxid, final int time) {
        this.writeC(S_EVENT);
        this.writeC(154);
        this.writeH(time);
        this.writeH(gfxid);
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
