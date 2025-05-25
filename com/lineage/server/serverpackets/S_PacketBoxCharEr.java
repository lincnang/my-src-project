package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 角色迴避
 *
 * @author admin
 */
public class S_PacketBoxCharEr extends ServerBasePacket {
    /**
     * 更新角色的迴避率
     *
     */
    public S_PacketBoxCharEr(final L1PcInstance pc) {
        writeC(S_EVENT);
        writeC(S_PacketBox.UPDATE_ER);
        writeC(pc.getEr());
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return "[S] " + this.getClass().getSimpleName() + " [S->C 發送封包盒子(更新角色的迴避率)]";
    }
}
