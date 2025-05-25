package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Location;
import com.lineage.server.types.Point;

/**
 * 產生動畫(地點)
 *
 * @author dexc
 */
public class S_EffectLocation extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 產生動畫(地點)
     *
     * @param pt    - Point
     * @param gfxId - 動畫編號
     */
    public S_EffectLocation(final Point pt, final int gfxId) {
        this(pt.getX(), pt.getY(), gfxId);
    }

    /**
     * 產生動畫(地點)
     *
     * @param loc   - 座標資料
     * @param gfxId - 動畫編號
     */
    public S_EffectLocation(final L1Location loc, final int gfxId) {
        this(loc.getX(), loc.getY(), gfxId);
    }

    /**
     * 產生動畫(地點)
     *
     * @param x     - 座標資料X
     * @param y     - 座標資料Y
     * @param gfxId - 動畫編號
     */
    public S_EffectLocation(final int x, final int y, final int gfxId) {
        // 0000: 52 8a 82 2b 80 b1 18 20 R..+...
        this.writeC(S_EFFECT_LOC);
        this.writeH(x);
        this.writeH(y);
        this.writeH(gfxId);
    }

    /**
     * 測試用
     *
     * @param opid 封包編號
     */
    public S_EffectLocation(final int opid, final L1Location loc) {
        this.writeC(opid);
        this.writeH(loc.getX());
        this.writeH(loc.getY());
        this.writeH(4842);
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
