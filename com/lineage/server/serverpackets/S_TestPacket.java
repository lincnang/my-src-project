package com.lineage.server.serverpackets;

import java.util.StringTokenizer;

/**
 * 紅騎士團攻城訊息
 */
public class S_TestPacket extends ServerBasePacket {
    public static final int a = 103;
    private static final String S_TestPack = "S_TestPack";
    private byte[] _byte = null;

    /**
     * 紅騎士團攻城訊息
     */
    public S_TestPacket(int type, int gfxid, int messageCode, String color) {
        writeC(S_EXTENDED_PROTOBUF);
        writeC(type);
        switch (type) {
            case a:
                writeC(0x00);
                writeC(0x08);
                writeBit(gfxid * 2);
                writeC(0x10);
                writeBit(messageCode * 2);
                writeC(0x1a);
                writeC(0x03);
                StringTokenizer st = new StringTokenizer(color.toString());
                while (st.hasMoreTokens()) {
                    writeC(Integer.parseInt(st.nextToken(), 16));
                }
                writeC(0x20);
                writeC(0x14);
                writeH(0x00);
                break;
        }
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
        return S_TestPack;
    }
}