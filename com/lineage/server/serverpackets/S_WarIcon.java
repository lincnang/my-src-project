package com.lineage.server.serverpackets;

/**
 * 紅騎士團徽章
 */
public class S_WarIcon extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 紅騎士團徽章
     * <p>
     * type= 1:狼頭  2:蝴蝶
     */
    public S_WarIcon(int objId, int type) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(65);
        writeCC(1, objId);
        writeCC(2, type);
        writeH(0);
    }

    /**
     * 刪除紅騎士團徽章
     */
    public S_WarIcon(int objId) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(65);
        writeCC(1, objId);
        writeCC(2, 3L);
        writeH(0);
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
        return this.getClass().getSimpleName();
    }
}
