package com.lineage.server.serverpackets;

/**
 * 表情
 *
 * @author kyo
 */
public class S_Expression extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 表情
     *
     */
    public S_Expression(final int id, final int value) {
        this.writeC(S_EXTENDED_PROTOBUF);
        this.writeH(0x0140);
        this.writeInt32(1, id);
        this.writeInt32(2, 0x02);
        this.writeInt32(3, value);
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
