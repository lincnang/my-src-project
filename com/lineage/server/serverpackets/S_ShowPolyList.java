package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

/**
 * NPC對話視窗(變身清單)
 *
 * @author dexc
 */
public class S_ShowPolyList extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * NPC對話視窗(變身清單)
     *
     * @param objid
     */
    public S_ShowPolyList(final int objid) {
        this.writeC(S_HYPERTEXT);
        this.writeD(objid);
        this.writeS("monlist");
    }

    /**
     * NPC對話視窗(變身清單)
     *
     * @param target
     */
    public S_ShowPolyList(final L1Character target) {
        this.writeC(S_HYPERTEXT);
        this.writeD(target.getId());
        this.writeS("monlist");
    }

    /**
     * 自訂變身清單
     *
     * @param objid
     */
    public S_ShowPolyList(int objid, String htmlid) {
        this.writeC(S_HYPERTEXT);
        this.writeD(objid);
        this.writeS(htmlid);
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
