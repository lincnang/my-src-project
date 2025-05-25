package com.lineage.server.serverpackets;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 選項(Yes/No)
 *
 * @author loli
 */
public class S_Message_YN extends ServerBasePacket {
    // 訊息編序 7.6
    public static AtomicInteger _MessageNumber = new AtomicInteger(1);
    // 交易編序
    private static AtomicInteger _sequentialNumber = new AtomicInteger(1);
    private byte[] _byte = null;

    /**
     * 選項(Yes/No)
     *
     */
    public S_Message_YN(final int type) {
        writeC(S_ASK);
        writeH(0x0000);
        writeD(0x00000000);
        writeH(type);
    }

    /**
     * 選項(Yes/No)<BR>
     * 交易
     *
     */
    public S_Message_YN(final String name) {
        writeC(S_ASK);
        writeH(0x0000);
        writeD(_sequentialNumber.incrementAndGet());
        writeH(0x00fc);
        writeS(name);
    }

    /**
     * 選項(Yes/No)
     *
     */
    public S_Message_YN(final int type, final String msg) {
        writeC(S_ASK);
        writeH(0x0000);
        writeD(0x00000000);
        writeH(type);
        writeS(msg);
    }

    /**
     * 選項(Yes/No)
     *
     */
    public S_Message_YN(final int type, final String msg1, final String msg2) {
        writeC(S_ASK);
        writeH(0x0000);
        writeD(0x00000000);
        writeH(type);
        writeS(msg1);
        writeS(msg2);
    }

    // 7.6
    public S_Message_YN(final int mode, final String msg1, final String msg2, final String msg3) {
        writeC(S_ASK);
        writeH(0x0000);
        writeD(_MessageNumber.incrementAndGet());
        writeH(mode);
        writeS(msg1);
        writeS(msg2);
        writeS(msg3);
    }

    // 7.6
    public S_Message_YN(final int mode, final int value) {
        writeC(S_ASK);
        writeH(0x0000);
        writeD(_MessageNumber.incrementAndGet());
        writeH(mode);
        writeS(value + "");
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
        return getClass().getSimpleName();
    }
}
