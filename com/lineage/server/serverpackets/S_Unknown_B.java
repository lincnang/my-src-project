package com.lineage.server.serverpackets;

/**
 * 未知 B 人物列表之前
 *
 * @author dexc
 */
public class S_Unknown_B extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 未知 B 人物列表之前
     *
     */
    public S_Unknown_B() {
        /*
         * Server op: 43 0000: 2b 0a 02 00 00 00 2b 7f +.....+
         */
        writeC(S_VOICE_CHAT);
        writeC(10);
        writeC(2);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(43);
        writeC(127);
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