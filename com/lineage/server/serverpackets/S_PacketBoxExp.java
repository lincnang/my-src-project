package com.lineage.server.serverpackets;

/**
 * 受到殷海薩的祝福，增加了些許的狩獵經驗值
 *
 * @author DaiEn
 */
public class S_PacketBoxExp extends ServerBasePacket {
    /**
     * 1,550：受到殷海薩的祝福，增加了些許的狩獵經驗值。
     */
    public static final int LEAVES = 0x52;
    private byte[] _byte = null;

    /**
     * 受到殷海薩的祝福，增加了些許的狩獵經驗值
     *
     * @param exp 經驗值增加率
     */
    public S_PacketBoxExp(final int exp) {
        writeC(S_EVENT);
        writeC(LEAVES);
        writeD(exp);
        writeD(7700);
        writeD(0);
    }

    /**
     * 解除 受到殷海薩的祝福，增加了些許的狩獵經驗值
     */
    public S_PacketBoxExp() {
        writeC(S_EVENT);
        writeC(LEAVES);
        writeD(0);
        writeD(7700);
        writeD(0);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
