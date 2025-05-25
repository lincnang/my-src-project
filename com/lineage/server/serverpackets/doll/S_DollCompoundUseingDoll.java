package com.lineage.server.serverpackets.doll;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * @author kyo
 */
public class S_DollCompoundUseingDoll extends ServerBasePacket {
    private byte[] _byte = null;

    public S_DollCompoundUseingDoll(final int objid) {
        this.writeC(S_EXTENDED_PROTOBUF);
        this.writeH(0x0080);
        if (objid > 0) {
            this.writeInt32(1, objid);
        }
        this.randomShort();
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return "[S] " + this.getClass().getSimpleName();
    }
}
