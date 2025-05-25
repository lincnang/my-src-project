package com.lineage.server.serverpackets;

/**
 * 服務器登入訊息(使用string.tbl)
 *
 * @author dexc
 */
public class S_CommonInfo extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 服務器登入訊息(使用string.tbl)
     *
     * @param skillid
     */
    public S_CommonInfo(final int type, final String[] info) {
        buildPacket(type, info);
    }

    private void buildPacket(final int type, final String[] info) {
        writeC(S_NEWS);
        writeH(type);
        if (info == null) {
            writeC(0x00);
        } else {
            writeC(info.length);
            for (int i = 0; i < info.length; i++) {
                writeS(info[i]);
            }
        }
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